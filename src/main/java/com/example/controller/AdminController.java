package com.example.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.pojo.User;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * @author Lexin Huang
 */
@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserService userService;

    @Controller
    @RequestMapping("/admin/user_overview")
    class UserOverviewController {

        @GetMapping("/users_info.html")
        public String usersInfo(@RequestParam("page_number") Integer pageNumber,
                         Model model) {
            Page<User> page = new Page<>(pageNumber, 5);
            Page<User> userPage = userService.page(page);
            model.addAttribute("userPage", userPage);

            return "table/dynamic_table";
        }
    }

    @Controller
    @RequestMapping("/admin/operation")
    class AdminOperationController {

        @GetMapping("/remove_user")
        public String removeUser(@RequestParam("user_id") Integer userId,
                                 @RequestParam("page_number") Integer pageNumber) {
            userService.removeById(userId);
            return "redirect:/admin/user_overview/users_info?page_number=" + pageNumber;
        }

    }


}
