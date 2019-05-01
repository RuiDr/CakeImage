package com.example.demo.controller;
import com.example.demo.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
    @RequestMapping("/loginPage")
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
        return "/location";
    }

//    相似图片
    @GetMapping("/detail")
    public String detail(){
        return "/detail";
    }

//    照相机
@GetMapping("/camera")
public String camera(){
    return "/camera";
}

}
