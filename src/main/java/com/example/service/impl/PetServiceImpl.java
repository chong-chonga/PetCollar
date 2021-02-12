package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.authc.InvalidTokenException;
import com.example.authc.UnauthorizedRequestException;
import com.example.dao.CacheDao;
import com.example.mapper.PetMapper;
import com.example.mapper.UserMapper;
import com.example.pojo.Pet;
import com.example.pojo.User;
import com.example.request.PetRequest;
import com.example.response.PetRequestData;
import com.example.response.ReactiveResponse;
import com.example.response.ReactiveResponse.StatusCode;
import com.example.service.PetService;
import com.example.util.InvalidRequestException;
import com.example.util.RequestInfoFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Lexin Huang
 */
@Slf4j
@Service
public class PetServiceImpl extends ServiceImpl<PetMapper, Pet> implements PetService {

    private final PetMapper petMapper;

    private final CacheDao cacheDao;

    public PetServiceImpl(PetMapper petMapper,
                          UserMapper userMapper,
                          CacheDao cacheDao) {
        this.petMapper = petMapper;
        this.cacheDao = cacheDao;
    }


    @Override
    public ReactiveResponse getPetResponse(PetRequest request) {
        ReactiveResponse response = new ReactiveResponse();
        try{
            User user = getUserByToken(request.getToken());
            refreshTokenTime(request.getToken(), user);
            dispatchPetRequest(request, response, user);
        }catch (InvalidTokenException e){
            log.error(e.getMessage());
            response.setContent(StatusCode.TOKEN_NOT_EXISTS, "token无效!", new PetRequestData());
        }catch (UnauthorizedRequestException e){
            log.error(e.getMessage());
            response.setContent(StatusCode.UNAUTHORIZED, "您没有访问这个资源的权限!", new PetRequestData());
        }catch (InvalidRequestException e){
            log.error(e.getMessage());
            response.setContent(StatusCode.ITEM_NOT_OWNED, "您还没有该宠物!", new PetRequestData());
        }
        return response;
    }

    private User getUserByToken(String token) {
        if (Objects.isNull(token)) {
            throw new InvalidTokenException("Token不存在!");
        }
        User user = cacheDao.getUserCache(token);
        if(Objects.isNull(user)){
            throw new InvalidTokenException("Token不存在!");
        }
        return user;
    }

    /**
     * 刷新 token 的持续时间为 7 天
     * @param token 用户令牌
     * @param user 用户对象
     */
    @Deprecated
    private void refreshTokenTime(String token, User user) {
        cacheDao.setStringCache(user.getUsername(), token, 7L, TimeUnit.DAYS);
        cacheDao.setUserCache(token, user, 7L, TimeUnit.DAYS);
    }

    private void dispatchPetRequest(PetRequest request,
                                    ReactiveResponse response,
                                    User user) {
        doPermissionRequest(request, response, user);
    }

    private void doPermissionRequest(PetRequest request,
                                     ReactiveResponse response,
                                     User user) {
        PetRequestData data = new PetRequestData();
        if (!user.getUsername().equals(request.getUsername())) {
            throw new UnauthorizedRequestException(user.getUsername() + " 没有对 " + request.getUsername() + " 账户操作的权利!");
        } else {
            switch (request.getRequestType()) {
                case GET_USER_PETS:
                    doGetUserPets(response, data, user);
                    break;
                case ADD_PET:
                    doAddPet(request, response, data, user);
                    break;
                case MODIFY_PET_INTRODUCTION:
                    doModifyPetIntroduction(request, response, data, user);
                    break;
            }
        }
    }

    private void doGetUserPets(ReactiveResponse response,
                               PetRequestData data,
                               User user) {
        List<Pet> pets = getUserPetsByUserId(user.getUserId());
        if (Objects.isNull(pets)) {
            pets = new ArrayList<>();
        }
        data.configureData(pets, null, null);
        response.setContent(StatusCode.CORRECT, data);
    }

    private List<Pet> getUserPetsByUserId(Integer userId) {
        QueryWrapper<Pet> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return petMapper.selectList(queryWrapper);
    }


    private void doAddPet(PetRequest request,
                          ReactiveResponse response,
                          PetRequestData data,
                          User user) {
        if (!RequestInfoFormat.isNameFormatCorrect(request.getPetName())) {
            response.setContent(StatusCode.FORMAT_WRONG, "宠物名称必须在 4-16 个字符内, 由中英文,数字组成!", data);
        }else{
            if(!Objects.isNull(request.getPetIntroduction()) && 255 < request.getPetIntroduction().length()){
                response.setContent(StatusCode.FORMAT_WRONG, "宠物介绍必须在 1-255 个字符内!", data);
            }else{
                if (!exist(request.getPetName())) {
                    Pet pet = request.createPetToAdd(user.getUserId());
                    save(pet);
                    data.configureData(null, null, pet);
                    response.setContent(StatusCode.CORRECT, data);
                } else {
                    response.setContent(StatusCode.NAME_HAS_REGISTERED, data);
                }
            }
        }
    }

    private boolean exist(String petName) {
        QueryWrapper<Pet> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(true, "pet_name", petName);
        return null != petMapper.selectOne(queryWrapper);
    }


    private void doModifyPetIntroduction(PetRequest request,
                                         ReactiveResponse response,
                                         PetRequestData data,
                                         User user) throws NumberFormatException {
        if (Objects.isNull(request.getPetIntroduction()) || request.getPetIntroduction().length() > 255) {
            response.setContent(StatusCode.FORMAT_WRONG, "宠物介绍必须在 1-255 个字符内!", data);
        }else{
            Pet pet = getUserPetByName(request.getPetName(), user.getUserId());
            if(Objects.isNull(pet)) {
                throw new InvalidRequestException(user.getUsername() + " 没有名称为 " + request.getUsername() + " 的宠物");
            }
            updatePetIntroduction(pet, request.getPetIntroduction());
            data.configureData(null, null, pet);
            response.setContent(StatusCode.CORRECT, data);
        }
    }

    private Pet getUserPetByName(String petName, int userId) {
        QueryWrapper<Pet> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(true, "pet_name", petName)
                    .eq(true, "user_id", userId);
        return petMapper.selectOne(queryWrapper);
    }

    private void updatePetIntroduction(Pet pet, String newIntroduction) {
        pet.setPetIntroduction(newIntroduction);
        updateById(pet);
    }

}
