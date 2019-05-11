package com.example.cakeImage.controller;
import com.example.cakeImage.arithmetic.SimilarImageSearch;
import com.example.cakeImage.entity.ImagesInfo;
import com.example.cakeImage.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @ Author     ：CrazyCake.
 * @ Date       ：Created in 22:27 2019/4/26
 * @ Description：CommonController
 * @ Modified By：
 * @Version: 1.0$
 */
@Controller
public class CommonController {
    @Autowired
    public CommonService commonservice;

//    默认页面
    @RequestMapping(value = "/", method = {RequestMethod.POST, RequestMethod.GET})
    public String login(){


//        将文件中的图片生成指纹并保存到数据库中
        List<String> list=new ArrayList<>();
        list= SimilarImageSearch.produceAllImages(10);
        for (int i=0;i<list.size();i++){
            ImagesInfo imagesInfo=new ImagesInfo();
            String uuid=UUID.randomUUID().toString().substring(0, 4);
            imagesInfo.setId(uuid);
            imagesInfo.setAddress("images/"+(i+1)+".jpg");
            imagesInfo.setFinger(list.get(i));
            int key=commonservice.addImages(imagesInfo);
            System.out.println("第 "+(i+1)+"次插入图片 "+key);


        }
        System.out.println(list.toString());



        return "/index";
    }
//   登录界面
    @RequestMapping("/loginUser")
    public String loginPage(HttpServletRequest request, HttpSession session){


        String email = request.getParameter("email");
        String password = request.getParameter("password");
        System.out.println("你输入的用户名为：" + email);
        System.out.println("你输入的密码为：" + password);
        String tname = commonservice.login(email, password);
        System.out.println("del"+tname);
        session.setAttribute("tname", tname);
        if (tname == null) {
            return "/login";
        } else {
            return "/index";
        }
    }
   @GetMapping("/loginIndex")
    public String loginIndex() {
        return "/test";
    }

//    本地上传图片
    @GetMapping("/location")
    public String location(){
        System.out.println("hello location");
        return "/location";
    }

//    相似图片
    @GetMapping("/detail")
    public String detail(){

        System.out.println("hello detail");
        return "/detail";
    }

    //    照相机
    @GetMapping("/camera")
    public String camera(){
        System.out.println("hello camera");


        return "/camera";
    }

//    注册界面
    @GetMapping("/register")
    public String register(){
        return "/register";
    }

}
