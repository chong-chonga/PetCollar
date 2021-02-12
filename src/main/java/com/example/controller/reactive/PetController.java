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
@RequestMapping("/users")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }


    @GetMapping("/{username}/pets")
    public ReactiveResponse getUserPets(@PathVariable("username") String username,
                                        @RequestBody PetRequest request) {
        request.setUsername(username);
        request.setRequestType(PetRequestType.GET_USER_PETS);
        return petService.getPetResponse(request);
    }

    @PostMapping("/{username}/pets")
    public ReactiveResponse addPet(@PathVariable String username,
                                   @RequestBody PetRequest request) {
        request.setUsername(username);
        request.setRequestType(PetRequestType.ADD_PET);
        return petService.getPetResponse(request);
    }

    @PatchMapping("/{username}/pets/{petName}/introduction")
    public ReactiveResponse modifyPetIntroduction(@PathVariable String username,
                                                  @PathVariable String petName,
                                                  @RequestBody PetRequest request) {
        request.setUsername(username);
        request.setPetName(petName);
        request.setRequestType(PetRequestType.MODIFY_PET_INTRODUCTION);
        return petService.getPetResponse(request);
    }

}
