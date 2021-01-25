package com.example.controller;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.Objects;

/**
 * @author 悠一木碧
 */
@Controller
public class LoginRegisterController {
    @PostMapping({"/user/login"})
    public String login(@RequestParam("username")String username,
                        @RequestParam("password")String password,
                        Model model, HttpSession session){
        if(Strings.isEmpty(username) || !Objects.equals("123456", password)){
            model.addAttribute("msg", "用户名或密码错误!");
            return "login";
        } else{
            session.setAttribute("loginUser", username);
            return "redirect:/main.html";
        }
    }
    @PostMapping("/user/register")
    public String register(@RequestParam("username")String username,
                           @RequestParam("password")String password,
                           @RequestParam("confirmPassword")String confirmPassword,
                           Model model, HttpSession session){
        boolean correctFormat = true;
        if(correctFormat){
            session.setAttribute("loginUser", username);
            return "redirect:/main.html";
        } else{
            return "login";
        }

    }



}
