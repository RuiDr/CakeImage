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

@Controller
public class ArithmeticController {

    @ResponseBody
    @RequestMapping("/perception")
    public String perception(@RequestParam( value = "file")MultipartFile file, String filePath, HttpServletRequest request, HttpSession session){

//        获取目标图片的地址
      String sourceImagePath=  Utility.tool(file,filePath);
      System.out.println("sourceImagePath is "+sourceImagePath);

//        样本集的所有图片的指纹

        List<String> list=new ArrayList<>();
        list=SimilarImageSearch.produceAllImages(10);

//获取目标图片的指纹

        String sourceCode = SimilarImageSearch.produceFingerPrint(sourceImagePath);
        System.out.println("sourceCode is "+sourceCode);


//     计算所有图片与目标图片的汉明距离
        ArrayList<String >ImagePath=new ArrayList<>();
       ImagePath= Utility.hammingDistance(sourceCode,list);
        System.out.println(ImagePath.toString());
        ModelAndView modelAndView=new ModelAndView();
//          String base64=  Utility.urlToBase64(sourceImagePath);
        System.out.print("base64 is "+file.getOriginalFilename());
      String fileName=file.getOriginalFilename();
      if (fileName!=null)
        session.setAttribute("fileName",fileName);
        if(ImagePath.size()!=0){
            modelAndView.addObject("ImagePath",ImagePath);
            session.setAttribute("ImagePath",ImagePath);

        }else{
        }

        return "/detail";
    }
}
