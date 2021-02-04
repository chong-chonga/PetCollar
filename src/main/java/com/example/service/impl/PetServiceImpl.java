package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.PetMapper;
import com.example.pojo.Pet;
import com.example.pojo.User;
import com.example.request.PetRequest;
import com.example.request.PetRequestType;
import com.example.response.PetInfoRequestData;
import com.example.response.ReactiveResponse;
import com.example.response.ReactiveResponse.StatusCode;
import com.example.service.CacheService;
import com.example.service.PetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

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

    public PetServiceImpl(PetMapper petMapper,
                          @Qualifier("redisCacheService") CacheService cacheService) {
        this.petMapper = petMapper;
        this.cacheService = cacheService;
    }

    @Override
    public ReactiveResponse getPetResponse(PetRequest request) {
        ReactiveResponse response = new ReactiveResponse();
        PetInfoRequestData data = new PetInfoRequestData();
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
                                      PetInfoRequestData data) {
        PetRequestType requestType = request.getRequestType();
        switch (requestType) {
            case GET:
                doGet(response, user, data);
                break;
            case PUT:
                doPut(request, response, user, data);
                break;
        }
    }

    private void doGet(ReactiveResponse response,
                       User user,
                       PetInfoRequestData data) {
        QueryWrapper<Pet> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(true,"user_id", user.getUserId());
        List<Pet> pets = petMapper.selectList(queryWrapper);
        data.setPets(pets);
        response.setContent(StatusCode.CORRECT, data);
    }


    private void doPut(PetRequest request,
                       ReactiveResponse response,
                       User user,
                       PetInfoRequestData data) {
        Pet pet = new Pet();
        pet.setPetName(request.getPetName());
        pet.setPetSpecies(request.getPetSpecies());
        pet.setIsPetOvert(request.getIsPetOvert());
        pet.setPetIntroduction(request.getPetIntroduction());
        pet.setUserId(user.getUserId());
        petMapper.insert(pet);
        response.setContent(StatusCode.CORRECT, data);
    }




}
