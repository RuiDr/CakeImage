package com.example.cakeImage.controller;
import com.example.cakeImage.arithmetic.SimilarImageSearch;
import com.example.cakeImage.entity.Ahash;
import com.example.cakeImage.service.CommonService;
import com.example.cakeImage.service.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
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

//    判断用户采用的是哪种算法
    @GetMapping ("/method")
    public String method(@RequestParam("ids")String id, HttpSession session,HttpServletRequest request){

        System.out.println("hello method");
        String str="";
        if(id.contains("ahash")){
            str="ahash";
            session.setAttribute("method",str);
            return "/detail";
        }else if (id.contains("phash")){
            str="phash";
            session.setAttribute("method",str);

            return "/detail";
        }else if (id.contains("dhash")){
            str="dhash";
            session.setAttribute("method",str);
            return "/detail";
        }
        return "/index";
    }

}



