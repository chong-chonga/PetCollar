package com.example.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Lexin Huang
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Pet {
    private Integer petId;
    private String petPortraitPath;
    private String petName;
    private String petSpecies;
    private String petIntroduction;
    private Boolean isPetOvert;
    private Integer userId;
}
