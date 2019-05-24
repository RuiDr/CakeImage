package com.example.cakeImage.controller;
import com.example.cakeImage.arithmetic.Phash;
import com.example.cakeImage.arithmetic.SimilarImageSearch;
import com.example.cakeImage.entity.Ahash;
import com.example.cakeImage.service.CommonService;
import com.example.cakeImage.tools.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.ArrayList;

import java.util.List;

import java.util.UUID;

@Controller
public class ArithmeticController {

    @Autowired
    public CommonService commonservice;

    @ResponseBody
    @RequestMapping("/perception")
    public String perception(@RequestParam( value = "file")MultipartFile file, String filePath, HttpServletRequest request, HttpSession session){

//            向前端发送数据
        String method=(String) session.getAttribute("method");
        System.out.println("method is "+method);
        String sourceImagePath="";

        sourceImagePath=  Utility.tool(file,filePath);
//            获取原图像地址
        System.out.println("sourceImagePath is "+sourceImagePath);
//              list用于存放平均哈希算法的指纹集
        List<String> list=new ArrayList<>();

//            使用平均值哈希算法
        if(method.contains("ahash")){
            System.out.println("ahash 算法 ：");
//            使用平均值哈希算法获得指纹集
            list= SimilarImageSearch.produceAllImages(10);
            for (int i=0;i<list.size();i++){
                Ahash imagesInfo=new Ahash();
                String uuid= UUID.randomUUID().toString().substring(0, 4);
                imagesInfo.setId(uuid);
                imagesInfo.setAddress("images/"+(i+1)+".jpg");
                imagesInfo.setFinger(list.get(i));
                int key=commonservice.addImages(imagesInfo);
                System.out.println("第 "+(i+1)+"次插入图片 "+key);
            }

            System.out.println(list.toString());

//             获取目标图片的指纹
            String sourceCode = SimilarImageSearch.produceFingerPrint(sourceImagePath);
            System.out.println("sourceCode is "+sourceCode);

            session.setAttribute("source","images/source.jpg");
//            增强的哈希算法
        }else if(method.contains("phash")){
            System.out.println("phash 算法 ：");
            list=Phash.produceAllImagesPhash(10);
            for(int i=0;i<list.size();i++){
                System.out.println(""+list.get(i));
            }

//            差异值哈希算法
        }else if(method.contains("dhash")){



        }


        return "/detail";
    }
}
