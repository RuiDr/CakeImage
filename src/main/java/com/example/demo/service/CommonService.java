package com.example.demo.service;

import com.example.demo.entity.Stu;
import com.example.demo.mapper.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommonService {
    @Autowired
    public Common commonmapper;
    public String login(String sno, String password){
        return commonmapper.login(sno,password);
    }
    public  int delById(String id,String password){
        return commonmapper.delById(id,password);
    }
}
