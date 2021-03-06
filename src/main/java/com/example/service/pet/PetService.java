package com.example.service.pet;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.pojo.Pet;
import com.example.response.ReactiveResponse;
import com.example.response.data.pet.PetRequestData;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Lexin Huang
 */
public interface PetService extends IService<Pet> {
    ReactiveResponse<PetRequestData> getUserPetsResponse(String token);

    ReactiveResponse<PetRequestData> getAddPetResponse(String token, Pet pet);

    ReactiveResponse<PetRequestData> getModifyPetProfileResponse(String token, String petId, Pet newPetProfile);

    ReactiveResponse<PetRequestData> getUploadAvatarResponse(String token, String petId, MultipartFile image);

    ReactiveResponse<PetRequestData> getRemovePetResponse(String token, String petId);

}
