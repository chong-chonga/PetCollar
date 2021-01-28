package com.example.controller;

import com.example.pojo.User;
import com.example.service.UserService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.Objects;

/**
 * @author 悠一木碧
 */
@Controller("defaultLoginRegisterController")
public class LoginRegisterController {
    @Autowired
    private UserService userService;
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
    public String register(Model model, HttpSession session){
            User user = new User();
            user.setAccount("dasdasd");
            user.setPassword("123tfd123");
            user.setUserIntroduction("没有介绍");

            session.setAttribute("loginUser", user);
            return "redirect:/main.html";

    }



}
