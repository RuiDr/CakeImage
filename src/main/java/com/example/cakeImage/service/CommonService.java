package com.example.cakeImage.service;

import com.example.cakeImage.mapper.Common;
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
