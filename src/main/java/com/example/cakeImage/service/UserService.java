package com.example.cakeImage.service;

import com.example.cakeImage.mapper.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    public User user;

    public String login(String email,String password){
        return user.login(email,password);
    }
}
