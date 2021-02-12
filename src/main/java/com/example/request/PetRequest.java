package com.example.request;

import com.example.pojo.Pet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Objects;

/**
 * @author Lexin Huang
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PetRequest {

    private String username;

    private String token;

    private PetRequestType requestType;

    private String petName;

    private String petSpecies;

    private String petIntroduction;

    private Boolean isPetOvert = false;

    public Pet createPetToAdd(Integer ownerId){
        Pet pet = new Pet();
        pet.setPetName(this.petName);
        pet.setPetPortraitPath("http://www.petcollar.top:8082/images/headPortrait/pets/default.png");
        pet.setPetSpecies(this.petSpecies);
        pet.setOvert(this.isPetOvert);
        pet.setPetIntroduction(Objects.requireNonNullElse(this.petIntroduction, "~这只宠物还没有介绍哦~"));
        pet.setUserId(ownerId);
        return pet;
    }

}
