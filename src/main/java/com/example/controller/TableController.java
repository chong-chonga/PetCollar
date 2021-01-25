package com.example.controller;

import com.example.service.PetService;
import com.example.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Lexin Huang
 */
@Controller
public class TableController {
    private UserService userService = new UserService();
    private PetService petService = new PetService();

    @GetMapping("/basic_table")
    public String basic_table(){
        return "table/basic_table";
    }

    @GetMapping("/admin/users_Info.html")
    public String dynamic_table(Model model){
        model.addAttribute("users", userService.allUsers());
        return "table/users_table";
    }

    @GetMapping("/editable_table")
    public String editable_table(){
        return "table/editable_table";
    }

    @GetMapping("/pricing_table")
    public String pricing_table(){
        return "table/pricing_table";
    }

    @GetMapping("/responsive_table")
    public String responsive_table(){
        return "table/responsive_table";
    }

    @GetMapping("/admin/petsInfo.html")
    public String pets_table(Model model){
        model.addAttribute("pets", petService.allPets());
        return "table/pets_table";
    }
}
