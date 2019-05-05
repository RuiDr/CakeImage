package com.example.cakeImage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class ArithmeticController {

    @ResponseBody
    @RequestMapping("/perception")
    public String perception(  @RequestParam(value = "file", required = false)MultipartFile file,String filePath,HttpServletRequest request, HttpSession session){

        System.out.println("this is "+filePath);
        System.out.println("this is "+file.getOriginalFilename());

        return "/detail";
    }
}
