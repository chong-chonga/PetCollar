package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.PetMapper;
import com.example.mapper.UserMapper;
import com.example.pojo.Pet;
import com.example.pojo.User;
import com.example.request.PetRequest;
import com.example.request.PetRequestType;
import com.example.response.PetRequestData;
import com.example.response.ReactiveResponse;
import com.example.response.ReactiveResponse.StatusCode;
import com.example.service.CacheService;
import com.example.service.PetService;
import com.example.util.RequestInfoFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Lexin Huang
 */
@Slf4j
@Service
public class PetServiceImpl extends ServiceImpl<PetMapper, Pet> implements PetService {

    private final PetMapper petMapper;

    private final CacheService cacheService;

    private final UserMapper userMapper;

    public PetServiceImpl(PetMapper petMapper,
                          UserMapper userMapper,
                          CacheService cacheService) {
        this.petMapper = petMapper;
        this.userMapper = userMapper;
        this.cacheService = cacheService;
    }


    @Override
    public ReactiveResponse getPetResponse(PetRequest request) {
        ReactiveResponse response = new ReactiveResponse();
        PetRequestData data = new PetRequestData();
        String token = request.getToken();
        User user = cacheService.getUserIfExist(token);
        if (!Objects.isNull(user)) {
            cacheService.refreshTokenTime(token, user);
            doDispatchPetRequest(request, response, user, data);
        } else {
            response.setContent(StatusCode.TOKEN_NOT_EXISTS, data);
        }
        return response;
    }

    private void doDispatchPetRequest(PetRequest request,
                                      ReactiveResponse response,
                                      User user,
                                      PetRequestData data) {
        PetRequestType requestType = request.getRequestType();
        try {
            switch (requestType) {
                case SEARCH_USER_PETS:
                    doSearchUserPets(request, response, data);
                    break;
                case ADD_PET:
                case MODIFY_PET_INFO:
                    doUpdate(requestType, request, response, data, user);
                    break;
            }
        } catch (NumberFormatException | ResourceAccessException e) {
            log.error(e.getMessage());
            response.setContent(StatusCode.RESOURCE_DOES_NOT_EXIST, data);
        }

    }

    private void doUpdate(PetRequestType requestType,
                          PetRequest request,
                          ReactiveResponse response,
                          PetRequestData data, User user) {
        if (!(user.getUserId().toString()).equals(request.getUserId())) {
            response.setContent(StatusCode.UNAUTHORIZED, data);
        } else {
            switch (requestType) {
                case ADD_PET:
                    doAddPet(request, response, data, user);
                    break;
                case MODIFY_PET_INFO:
                    doModifyPetInfo(request, response, data, user);
                    break;
            }
        }
    }

    private void doSearchUserPets(PetRequest request,
                                  ReactiveResponse response,
                                  PetRequestData data) {
        User userToSearch = userMapper.selectById(request.getUserId());
        if (Objects.isNull(userToSearch)) {
            throw new ResourceAccessException("请求的资源不存在!");
        }
        List<Pet> pets = getUserPublicPetsBy(userToSearch.getUserId());
        if (Objects.isNull(pets)) {
            pets = new ArrayList<>();
        }
        hidePetsLocation(pets);
        data.setPets(pets);
        data.setPetOwner(new PetRequestData.PetOwner(userToSearch));
        response.setContent(StatusCode.CORRECT, data);
    }

    private void hidePetsLocation(List<Pet> pets) {
        for (Pet pet : pets) {
            pet.setPetLatitude(null);
            pet.setPetLongitude(null);
        }
    }


    private void doAddPet(PetRequest request,
                          ReactiveResponse response,
                          PetRequestData data,
                          User user) {

        if (RequestInfoFormat.nameFormatCorrect(request.getPetName())) {
            if (!exist(request.getPetName())) {
                Pet pet = request.createPetToAdd(user.getUserId());
                petMapper.insert(pet);
                data.setPet(pet);
                response.setContent(StatusCode.CORRECT, data);
            } else {
                response.setContent(StatusCode.NAME_HAS_REGISTERED, data);
            }
        } else {
            response.setContent(StatusCode.NAME_FORMAT_WRONG, data);
        }

    }

    private boolean exist(String petName) {
        QueryWrapper<Pet> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(true, "pet_name", petName);
        return null != petMapper.selectOne(queryWrapper);
    }


    private void doModifyPetInfo(PetRequest request,
                                 ReactiveResponse response,
                                 PetRequestData data,
                                 User user) {
        if (null != request.getPetIntroduction() && request.getPetIntroduction().length() <= 255) {
            Pet pet = petMapper.selectById(request.getPetId());
            if(!Objects.isNull(pet)){
                pet.setPetIntroduction(request.getPetIntroduction());
                UpdateWrapper<Pet> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("pet_id", pet.getPetId())
                            .set("pet_introduction", pet.getPetIntroduction());
                update(updateWrapper);
                data.setPet(pet);
                response.setContent(StatusCode.CORRECT, data);
            }else{
                response.setContent(StatusCode.RESOURCE_DOES_NOT_EXIST, "您还没有这只宠物!", data);
            }
        } else {
            response.setContent(StatusCode.FORMAT_WRONG, "宠物介绍必须在1~255个字符内!", data);
        }
    }


    private List<Pet> getUserPublicPetsBy(Integer userId) {
        QueryWrapper<Pet> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(true, "user_id", userId)
                .eq(true, "is_pet_overt", true);
        return petMapper.selectList(queryWrapper);
    }


}
