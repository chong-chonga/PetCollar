package com.example.response;

import com.example.pojo.Pet;
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
public class PetInfoRequestData extends ReactiveData {

    List<Pet> pets;

}
