package com.lwj.house.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author Administrator
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    @GetMapping("/404")
    public String notFoundPage(Model model) {
        return "404";
    }

    @GetMapping("/403")
    public String accessError(Model model) {
        return "403";
    }

    @GetMapping("/500")
    public String internalError(Model model) {
        return "500";
    }

    @GetMapping("/logout")
    public String logoutPage(Model model) {
        return "logout";
    }
}
