package com.example.dao;

import com.example.pojo.Pet;
import com.example.pojo.User;

import java.util.Arrays;
import java.util.List;

/**
 * @author Lexin Huang
 */
public class PetDao {
    public List<Pet> selectAll(){
        Pet pet1 = new Pet(11, null, "柴小犬", "柴犬","这是个贼听话的狗狗", true, 3);
        Pet pet2 = new Pet(3, null, "哈哈狗", "哈士奇", "一只憨憨", false, 2);
        return Arrays.asList(pet1, pet2);
    }
}
