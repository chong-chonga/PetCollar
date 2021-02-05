package com.example.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Lexin Huang
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PetRequest {

    private String userId;

    private String token;

    private PetRequestType requestType;

    private String petName;

    private String petSpecies;

    private String petIntroduction = "~这只宠物还没有介绍哦~";

    private Boolean isPetOvert = false;


}
