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
    @RequestMapping(value = "/", method = {RequestMethod.POST, RequestMethod.GET})
    public String login(){
        return "/login";
    }
    @RequestMapping(value = "/loginPage", method = {RequestMethod.POST, RequestMethod.GET})
    public String loginPage(HttpServletRequest request, HttpSession session){

        String tno = request.getParameter("tno");
        String password = request.getParameter("password");
        System.out.println("你输入的用户名为：" + tno);
        System.out.println("你输入的密码为：" + password);
        String tname = commonservice.login(tno, password);
        System.out.println("del"+tname);
        session.setAttribute("tname", tname);
        if (tname == null) {
            return "/login";
        } else {
            return "/test";
        }
    }
   @GetMapping("/loginIndex")
    public String loginIndex() {
        return "/test";
    }

}
