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


    @GetMapping("/{userId}/pets/public")
    public ReactiveResponse getUserPets(@PathVariable String userId,
                                        @RequestBody PetRequest request){
        request.setUserId(userId);
        request.setRequestType(PetRequestType.SEARCH_USER_PETS);
        return petService.getPetResponse(request);
    }


    @PostMapping("/{userId}/pets")
    public ReactiveResponse addPet(@PathVariable String userId,
                                   @RequestBody PetRequest request){
        request.setUserId(userId);
        request.setRequestType(PetRequestType.ADD_PET);
        return petService.getPetResponse(request);
    }


    @PatchMapping("/{userId}/pets/{petId}/introduction")
    public ReactiveResponse modifyPetInfo(@PathVariable String userId,
                                          @PathVariable String petId,
                                          @RequestBody PetRequest request){
        request.setUserId(userId);
        request.setPetId(petId);
        request.setRequestType(PetRequestType.MODIFY_PET_INFO);
        return petService.getPetResponse(request);
    }

}
