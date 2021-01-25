package com.example.service;

import com.example.dao.PetDao;
import com.example.dao.UserDao;
import com.example.pojo.Pet;
import com.example.pojo.User;

import java.util.List;

/**
 * @author Lexin Huang
 */
public class PetService {
    private PetDao petDao = new PetDao();
    public List<Pet> allPets(){
        List<Pet> searchResult =petDao.selectAll();
        return searchResult;
    }
}
