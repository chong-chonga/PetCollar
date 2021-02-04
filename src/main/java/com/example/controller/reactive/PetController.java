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
@RequestMapping("/pet-collar-system")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }


    @GetMapping("/pets")
    public ReactiveResponse getUserPets(@RequestBody PetRequest request){
        request.setRequestType(PetRequestType.GET);
        return petService.getPetResponse(request);
    }

    @PutMapping("/pets")
    public ReactiveResponse addPet(@RequestBody PetRequest request){
        request.setRequestType(PetRequestType.PUT);
        return petService.getPetResponse(request);
    }

}
