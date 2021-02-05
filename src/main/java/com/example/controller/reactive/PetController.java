package com.example.controller.reactive;

import com.example.request.PetRequest;
import com.example.request.PetRequestType;
import com.example.response.ReactiveResponse;
import com.example.service.PetService;
import org.springframework.web.bind.annotation.*;

/**
 * @author Lexin Huang
 */
@RestController
@RequestMapping("/users")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }


    @GetMapping("/{userId}/pets")
    public ReactiveResponse getUserPets(@PathVariable String userId,
                                        @RequestBody PetRequest request){
        request.setUserId(userId);
        request.setRequestType(PetRequestType.GET_USER_PETS);
        return petService.getPetResponse(request);
    }

    @PostMapping("/{userId}/pets")
    public ReactiveResponse addPet(@PathVariable String userId,
                                   @RequestBody PetRequest request){
        request.setUserId(userId);
        request.setRequestType(PetRequestType.POST);
        return petService.getPetResponse(request);
    }


}
