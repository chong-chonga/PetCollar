package com.example.service.impl.pet;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.authc.InvalidTokenException;
import com.example.dao.CacheDao;
import com.example.mapper.PetMapper;
import com.example.pojo.Pet;
import com.example.pojo.User;
import com.example.request.PetRequest;
import com.example.response.PetRequestData;
import com.example.response.ReactiveResponse;
import com.example.response.ReactiveResponse.Status;
import com.example.service.PetService;
import com.example.request.RequestException;
import com.example.request.RequestInfoFormat;
import com.example.service.impl.TooLongIntroductionException;
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
                          CacheDao cacheDao) {
        this.petMapper = petMapper;
        this.cacheDao = cacheDao;
    }


    @Override
    public ReactiveResponse getPetResponse(PetRequest request) {
        ReactiveResponse response = new ReactiveResponse();
        PetRequestData data = new PetRequestData();
        try {
            User user = getUserCache(request.getToken());
            putUserCache(request.getToken(), user);
            dispatchPetRequest(request, response, user, data);
        } catch (Exception e) {
            handle(e, response, data);
        }
        return response;
    }

    private void handle(Exception e, ReactiveResponse response, PetRequestData data) {
        log.info(e.getMessage());

        if(e instanceof InvalidTokenException ) {
            response.setContent(Status.INVALID_TOKEN, data);
        } else if (e instanceof RequestInfoFormat.NameFormatException) {
            response.setContent(Status.PET_NAME_FORMAT_WRONG, data);
        } else if (e instanceof TooLongIntroductionException) {
            response.setContent(Status.PET_INTRODUCTION_TOO_LONG, data);
        } else if(e instanceof RequestException) {
            response.setContent(Status.INVALID_ITEM, data);
        } else{
            log.error("存在未能捕获的异常! 信息:" + e.getMessage());
            response.setContent(Status.SERVER_ERROR, data);
        }
    }

    /**
     * 刷新 token 的持续时间为 7 天
     * @param token 用户令牌
     * @param user  用户对象
     */
    @Deprecated
    private void putUserCache(String token, User user) {
        cacheDao.set(user.getUsername(), token, 7L, TimeUnit.DAYS);
        cacheDao.set(token, user, 7L, TimeUnit.DAYS);
    }

    private User getUserCache(String token) {
        if (Objects.isNull(token)) {
            throw new InvalidTokenException("Token不存在!");
        }
        User user = cacheDao.getUserCache(token);
        if (Objects.isNull(user)) {
            throw new InvalidTokenException("Token不存在!");
        }
        return user;
    }


    private void dispatchPetRequest(PetRequest request,
                                    ReactiveResponse response,
                                    User user, PetRequestData data) throws RequestInfoFormat.NameFormatException {
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
            case DELETE_PET:
                doDeletePet(request, response, data, user);
        }
    }


    private void doDeletePet(PetRequest request,
                             ReactiveResponse response,
                             PetRequestData data, User user) {
        Pet pet = getUserPet(request.getPetName(), user.getUserId());
        if (Objects.isNull(pet)) {
            throw new RequestException(user.getUsername() + " 没有名称为 " + request.getPetName() + " 的宠物");
        }
        removeById(pet.getPetId());
        response.setContent(Status.SUCCESS, data);
    }

    private void doGetUserPets(ReactiveResponse response,
                               PetRequestData data,
                               User user) {
        List<Pet> pets = getUserPetsByUserId(user.getUserId());
        if (Objects.isNull(pets)) {
            pets = new ArrayList<>();
        }
        data.configureData(pets, null, null);
        response.setContent(Status.SUCCESS, data);
    }

    private List<Pet> getUserPetsByUserId(Integer userId) {
        QueryWrapper<Pet> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return petMapper.selectList(queryWrapper);
    }


    private void doAddPet(PetRequest request,
                          ReactiveResponse response,
                          PetRequestData data,
                          User user) throws RequestInfoFormat.NameFormatException {
        if (!RequestInfoFormat.isNameFormatCorrect(request.getPetName())) {
            throw new RequestInfoFormat.NameFormatException("宠物名称为" + request.getPetName() + "不合规范!");
        }
        int maxLen = 255;
        if (!Objects.isNull(request.getPetIntroduction()) && maxLen < request.getPetIntroduction().length()) {
            throw new TooLongIntroductionException("宠物介绍超过了 " + maxLen + " 个字符!");
        }
        if (!exist(request.getPetName())) {
            Pet pet = request.createPetToAdd(user.getUserId());
            save(pet);
            data.configureData(null, null, pet);
            response.setContent(Status.SUCCESS, data);
        } else {
            response.setContent(Status.PET_NAME_NOT_AVAILABLE, data);
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
                                         User user) throws RequestInfoFormat.NameFormatException {
        if (!Objects.isNull(request.getPetIntroduction()) && request.getPetIntroduction().length() > 255) {
            throw new RequestInfoFormat.NameFormatException("宠物名称为" + request.getPetName() + "不合规范!");
        }
        Pet pet = getUserPet(request.getPetName(), user.getUserId());
        if (!Objects.isNull(pet)) {
            updatePetIntroduction(pet, request.getPetIntroduction());
            data.configureData(null, null, pet);
            response.setContent(Status.SUCCESS, data);
        } else {
            response.setContent(Status.INVALID_ITEM, "您还没有该宠物, 请添加后再进行操作!", data);
        }

    }

    private Pet getUserPet(String petName, int userId) {
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
