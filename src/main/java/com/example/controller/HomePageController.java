package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

/**
 * @author Lexin Huang
 */
@Controller
public class HomePageController {
    @GetMapping({"/main.html", "/index.html"})
    public String main(){
        return "main";
    }

    @GetMapping("/logOut")
    public String logOut(HttpSession session){
        session.removeAttribute("loginUser");
        return "redirect:/";
    }

    @GetMapping({"/", "/login"})
    public String index(){
        return "login";
    }
}
