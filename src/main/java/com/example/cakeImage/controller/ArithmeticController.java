package com.example.cakeImage.controller;
import com.example.cakeImage.arithmetic.SimilarImageSearch;
import com.example.cakeImage.tools.Utility;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.ArrayList;

import java.util.List;
import java.util.Map;

@Controller
public class ArithmeticController {

    @ResponseBody
    @RequestMapping("/perception")
    public String perception(@RequestParam( value = "file")MultipartFile file, String filePath,  Map<String , Object> map ,HttpServletRequest request, HttpSession session){

//        向前端发送数据

      String sourceImagePath=  Utility.tool(file,filePath);
      System.out.println("sourceImagePath is "+sourceImagePath);

//        样本集的所有图片的指纹


//获取目标图片的指纹

        String sourceCode = SimilarImageSearch.produceFingerPrint(sourceImagePath);
        System.out.println("sourceCode is "+sourceCode);


        session.setAttribute("source","images/source.jpg");

        return "/detail";
    }
}
