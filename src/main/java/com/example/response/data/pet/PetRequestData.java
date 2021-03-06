package com.example.response.data.pet;

import com.example.pojo.Pet;
import com.example.pojo.User;
import com.example.response.ReactiveResponse;
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
public class PetRequestData extends ReactiveResponse.ApiData {
    private PetOwner petOwner;
    private List<Pet> pets;
    private Pet pet;
    private String petPortraitPath;

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

}


