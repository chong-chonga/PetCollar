package com.example.response;

import com.example.pojo.Pet;
import com.example.pojo.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * @author Lexin Huang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@ToString
public class PetRequestData extends ReactiveData {
    private PetOwner petOwner;
    List<Pet> pets;
    Pet pet;

    @Data
    @ToString
    public static class PetOwner {
        private Integer userId;
        private String username;
        private String userIntroduction;

        public PetOwner(User user) {

            this.userId = user.getUserId();
            this.username = user.getUsername();
            this.userIntroduction = user.getUserIntroduction();
        }
    }

    public void configureData(List<Pet> pets,
                               PetRequestData.PetOwner petOwner,
                               Pet pet) {
        this.pets = pets;
        this.petOwner = petOwner;
        this.pet = pet;
    }
}


