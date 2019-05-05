package com.example.demo.controller;

import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class UserController {
    @Autowired
   public  UserService userService;

    @GetMapping("/userLogin")
    public String userLogin(HttpServletRequest request, HttpSession session){
        String email=request.getParameter("email");
        String password=request.getParameter("password");
        String username=userService.login(email,password);
        System.out.println("session.username is "+username);
        if(username!=null){
            session.setAttribute("username",username);
        }
       return "/index";
    }
}
