package com.example.cakeImage.controller;


import ch.qos.logback.core.util.FileUtil;
import com.example.cakeImage.arithmetic.SimilarImageSearch;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.*;
import java.lang.String.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ArithmeticController {

    @ResponseBody
    @RequestMapping("/perception")
    public void perception( @RequestParam( value = "file")MultipartFile file,String filePath,HttpServletRequest request, HttpSession session){

      String path="G:\\java\\webprojects\\CakeImage\\sourceImages\\";
      System.out.println("filePath is "+filePath);
      File filePa=new File(path);
      if(!filePa.exists()){
          filePa.mkdir();
      }
      String fileNameA="";
      if(file.isEmpty()){
          return;
      }
      String suf=file.getOriginalFilename().substring(file.getOriginalFilename().indexOf(".")+1);
      fileNameA="source"+"."+suf;
      try{
          FileUtils.writeByteArrayToFile(new File(path + fileNameA),
                  file.getBytes());

          System.out.println("path is "+path+fileNameA);
//          org.aspectj.util.FileUtil.canWriteFile(new File(path+fileNameA));

      } catch (Exception e) {
          e.printStackTrace();
      }


//        List<String> list=new ArrayList<>();
//
////        获取所有图片的指纹
//        list=SimilarImageSearch.produceAllImages(10);
//        System.out.println(list.toString());
//
////获取目标图片的指纹
//        try {
////            String basepath=filePath.replaceAll("\\\\", "/");
////            basepath=URLDecoder.decode(basepath,"utf-8");
//            String sourceCode = SimilarImageSearch.produceFingerPrint(filePath);
//            System.out.println(sourceCode);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
////        计算所有图片与目标图片的汉明距离
//
//        return "/detail";
    }
}
