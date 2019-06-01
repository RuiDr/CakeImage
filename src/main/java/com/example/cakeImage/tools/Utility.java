package com.example.cakeImage.tools;

import com.example.cakeImage.arithmetic.SimilarImageSearch;
import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {
    static String path = "G:\\java\\webprojects\\CakeImage\\src\\main\\resources\\static\\images\\";

    //    保存原图像到根目录下
    public static String tool(MultipartFile file, String filePath) {

        System.out.println("filePath is " + filePath);
        File filePa = new File(path);
        if (!filePa.exists()) {
            filePa.mkdir();
        }
        String fileNameA = "";
        if (file.isEmpty()) {
            return null;
        }
        String suf = file.getOriginalFilename().substring(file.getOriginalFilename().indexOf(".") + 1);
        fileNameA = "source" + "." + suf;
//        将原图片保存在根路径下
        try {
            FileUtils.writeByteArrayToFile(new File(path + fileNameA),
                    file.getBytes());
            System.out.println("path is " + path + fileNameA);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path + fileNameA;
    }

    // 计算汉明距离
    public static ArrayList<String> hammingDistance(String sourceCode, List<String> list) {
        ArrayList<String> list1 = new ArrayList<>();
        int distance = 0;
        for (int i = 0; i < list.size(); i++) {
            distance = SimilarImageSearch.hammingDistance(sourceCode, list.get(i));
//            图片有点相似
            if (distance < 10) {
                list1.add(path + i + ".jpg");
            }
        }
//        返回的图片编号的字符串形式
        return list1;
    }

    //    转成base64码
    public static String urlToBase64(String path) {
        String base64Img = "";
        try {
            File file = new File(path);
            FileInputStream is = new FileInputStream(file);
            byte[] data = new byte[is.available()];
            is.read(data);
            is.close();
            base64Img = Base64.getEncoder().encodeToString(data);
//             base64Img=Base64.getUrlEncoder().encodeToString(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return base64Img;
    }

    //    图片下载
    public static String download(String fileUrl) {
        String filepath = "E:" + File.separator + "eee.jpg";
        try {
            System.out.println("fileUr is " + fileUrl);
            URL url = new URL(fileUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5 * 1000);
            InputStream inputStream = null;
            if (connection.getResponseCode() == 200) {

                inputStream = connection.getInputStream();
                System.out.println(inputStream.available());
            }
            byte[] tmp = new byte[1024];
            int length;

            OutputStream outputStream = new FileOutputStream(filepath);
            while ((length = inputStream.read(tmp)) != -1) {
                System.out.println("length is " + length);
                outputStream.write(tmp, 0, length);
            }
            outputStream.close();
            inputStream.close();
        } catch (Exception e) {
            System.out.println();
            e.printStackTrace();
        }
        return filepath;
    }

    //  url判断
    public static boolean verifyUrl(String url) {

        // URL验证规则
        String regEx = "[a-zA-z]+://[^\\s]*";
        // 编译正则表达式
        Pattern pattern = Pattern.compile(regEx);
        // 忽略大小写的写法
        // Pattern pat = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(url);
        // 字符串是否与正则表达式相匹配
        boolean rs = matcher.matches();
        return rs;

    }


    public static boolean isExtreneValue(double[] values, double keyValue) {
////        极大值
//        for (double v:values){
//            if(keyValue<=v)
//                return false;
//        }
////        极小值
//        for (double v:values){
//            if(keyValue>=v)
//                return false;
//        }
//        return true;
//    }
        if (keyValue > values[0] + 0.001) {
            ///此处表示只可能是极大值
            for (double v : values) {
                ///此处用做差比较，防止double类型数字的不精确行
                if (keyValue <= v + 0.001) {
                    return false;///
                }
            }

            return true;

        } else if (keyValue < values[0] - 0.001) {
            //此处表示只可能是极小值
            for (double v : values) {
                ///此处用做差比较，防止double类型数字的不精确行
                if (keyValue >= v - 0.001) {
                    return false;///
                }
            }
            return true;

        } else {
            return false;
        }
    }
}
