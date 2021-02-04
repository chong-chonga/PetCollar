package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.pojo.Pet;
import com.example.request.PetRequest;
import com.example.response.ReactiveResponse;

/**
 * @author Lexin Huang
 */
public interface PetService extends IService<Pet> {

    ReactiveResponse getPetResponse(PetRequest request);

}
