package com.lwj.house.web.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Administrator
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @GetMapping("/login")
    public String adminLoginPage() {
        return "user/login";
    }

    @GetMapping("/center")
    public String userCenterPage() {
        return "user/center";
    }
}
