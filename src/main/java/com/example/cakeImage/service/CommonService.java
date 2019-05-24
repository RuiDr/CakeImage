package com.example.cakeImage.service;

import com.example.cakeImage.entity.Ahash;

import com.example.cakeImage.mapper.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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


    public int addImages(Ahash imagesInfo) {
        return commonmapper.addImages(imagesInfo);
    }
}
