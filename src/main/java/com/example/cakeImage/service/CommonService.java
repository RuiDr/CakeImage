package com.example.cakeImage.service;

import com.example.cakeImage.entity.Ahash;

import com.example.cakeImage.entity.Dhash;
import com.example.cakeImage.entity.Phash;
import com.example.cakeImage.mapper.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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


    public int addImagesAhash(Ahash imagesInfo) {
        return commonmapper.addImagesAhash(imagesInfo);
    }

    public int addImagesPhash(Phash phash) {
        return commonmapper.addImagesPhash(phash);
    }

    public int addImagesDhash(Dhash dhash) {
        return commonmapper.addImagesDhash(dhash);
    }

    public ArrayList<Ahash> findAhash() {
        return commonmapper.findAhash();
    }

    public ArrayList<Phash> findPhash() {
        return commonmapper.findPhash();
    }

    public ArrayList<Dhash> findDhash() {
        return commonmapper.findDhash();
    }


    public String AhashByAddress(String address) {
        return commonmapper.AhashByAddress(address);

    }

    public String PhashByAddress(String address) {
        return commonmapper.PhashByAddress(address);
    }

    public String DhashByAddress(String address) {
        return commonmapper.DhashByAddress(address);
    }
}
