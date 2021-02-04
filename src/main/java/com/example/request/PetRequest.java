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

    private String token;

    private PetRequestType requestType;

    private String petName;

    private String petSpecies;

    private String petIntroduction;

    private Boolean isPetOvert;

}
