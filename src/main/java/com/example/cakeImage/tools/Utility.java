package com.example.cakeImage.tools;

import com.example.cakeImage.arithmetic.SimilarImageSearch;
import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class Utility  {
    static String  path="G:\\java\\webprojects\\CakeImage\\src\\main\\resources\\static\\images\\";
//    保存原图像到根目录下
    public static String tool(MultipartFile file, String filePath) {

        System.out.println("filePath is "+filePath);
        File filePa=new File(path);
        if(!filePa.exists()){
            filePa.mkdir();
        }
        String fileNameA="";
        if(file.isEmpty()){
            return null;
        }
        String suf=file.getOriginalFilename().substring(file.getOriginalFilename().indexOf(".")+1);
        fileNameA="source"+"."+suf;
//        将原图片保存在根路径下
        try{
            FileUtils.writeByteArrayToFile(new File(path + fileNameA),
                    file.getBytes());
            System.out.println("path is "+path+fileNameA);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path+fileNameA;
    }
// 计算汉明距离
    public static ArrayList<String> hammingDistance(String sourceCode, List<String> list) {
        ArrayList<String >list1=new ArrayList<>();
        int distance=0;
        for (int i=0;i<list.size();i++){
            distance=SimilarImageSearch.hammingDistance(sourceCode,list.get(i));
//            图片有点相似
            if (distance<10){
                list1.add(path+i+".jpg");
            }
        }
//        返回的图片编号的字符串形式
        return list1;
    }

//    转成base64码
    public static String urlToBase64(String path){
        String base64Img="";
        try {
            File file = new File(path);
            FileInputStream is = new FileInputStream(file);
            byte[] data = new byte[is.available()];
            is.read(data);
            is.close();
             base64Img = Base64.getEncoder().encodeToString(data);
//             base64Img=Base64.getUrlEncoder().encodeToString(data);
        }catch (IOException e){
            e.printStackTrace();
        }
        return base64Img;
    }
}
