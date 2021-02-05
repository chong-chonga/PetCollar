package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
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
                          @Qualifier("redisCacheService") CacheService cacheService) {
        this.petMapper = petMapper;
        this.cacheService = cacheService;
        this.userMapper = userMapper;
    }


    @Override
    public ReactiveResponse getPetResponse(PetRequest request) {
        ReactiveResponse response = new ReactiveResponse();
        PetRequestData data = new PetRequestData();
        String token = request.getToken();
        User user = cacheService.getUserIfExist(token);
        if(!Objects.isNull(user)){
            cacheService.refreshTokenTime(token, user);
            doDispatchPetRequest(request, response, user, data);
        }else{
            response.setContent(StatusCode.TOKEN_NOT_EXISTS, data);
        }
        return response;
    }


    private void doDispatchPetRequest(PetRequest request,
                                      ReactiveResponse response,
                                      User user,
                                      PetRequestData data) {
        PetRequestType requestType = request.getRequestType();
        try{
            switch (requestType) {
                case GET_USER_PETS:
                    doGetUserPets(request ,response, data);
                    break;
                case POST:
                    doPost(request, response, user, data);
                    break;
            }
        } catch (NumberFormatException | ResourceAccessException e ){
            log.error(e.getMessage());
            response.setContent(StatusCode.RESOURCE_DOES_NOT_EXIST, data);
        }

    }


    private void doGetUserPets(PetRequest request,
                               ReactiveResponse response,
                               PetRequestData data) {
        User user = userMapper.selectById(request.getUserId());
        if(Objects.isNull(user)){
            throw new ResourceAccessException("请求的资源不存在!");
        }
        List<Pet> pets = getUserPublicPetsBy(user.getUserId());
        if(Objects.isNull(pets)){
            pets = new ArrayList<>();
        }
        data.setPets(pets);
        data.setPetOwner(new PetRequestData.PetOwner(user));
        response.setContent(StatusCode.CORRECT, data);
    }


    private List<Pet> getUserPublicPetsBy(Integer userId){
        QueryWrapper<Pet> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(true,"user_id",userId)
                .eq(true, "is_pet_overt", true);
        return petMapper.selectList(queryWrapper);
    }



    private void doPost(PetRequest request,
                        ReactiveResponse response,
                        User user,
                        PetRequestData data) {
        if(!user.getUserId().toString().equals(request.getUserId())){
            response.setContent(StatusCode.UNAUTHORIZED, data);
        } else{
            Pet pet = new Pet();
            pet.setPetName(request.getPetName());
            pet.setPetSpecies(request.getPetSpecies());
            pet.setIsPetOvert(request.getIsPetOvert());
            pet.setPetIntroduction(request.getPetIntroduction());
            pet.setUserId(user.getUserId());
            petMapper.insert(pet);

            data.setPet(pet);
            response.setContent(StatusCode.CORRECT, data);
        }

    }



}
