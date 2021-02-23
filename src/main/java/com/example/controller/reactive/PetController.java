package com.example.controller.reactive;

import com.example.request.PetRequest;
import com.example.request.PetRequestType;
import com.example.response.ReactiveResponse;
import com.example.service.PetService;
import org.springframework.web.bind.annotation.*;

/**
 * 实践 REST URI
 * @author Lexin Huang
 */
@RestController
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }


    @GetMapping("/pets")
    public ReactiveResponse getUserPets(@RequestBody PetRequest request) {
        request.setRequestType(PetRequestType.GET_USER_PETS);
        return petService.getPetResponse(request);
    }

    @PostMapping("/pets")
    public ReactiveResponse addPet(@RequestBody PetRequest request) {
        request.setRequestType(PetRequestType.ADD_PET);
        return petService.getPetResponse(request);
    }

    @PatchMapping("/pets/{petName}/introduction")
    public ReactiveResponse modifyPetIntroduction(@PathVariable String petName,
                                                  @RequestBody PetRequest request) {
        request.setPetName(petName);
        request.setRequestType(PetRequestType.MODIFY_PET_INTRODUCTION);
        return petService.getPetResponse(request);
    }

    @DeleteMapping("/pets/{petName}")
    public ReactiveResponse deletePet(@PathVariable("petName")String petName,
                                      @RequestBody PetRequest request){
        request.setPetName(petName);
        request.setRequestType(PetRequestType.DELETE_PET);
        return petService.getPetResponse(request);
    }

}
