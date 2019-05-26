package com.example.cakeImage.arithmetic;


import org.opencv.core.CvType;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.cakeImage.arithmetic.PhashArith.ImageToGray;
import static com.example.cakeImage.arithmetic.PhashArith.bufImagetoMat;

/**
 * @ Author     ：CrazyCake
 * @ Date       ：Created in 1:52 2019/5/11
 * @ Description：dHash算法
 * @ Modified By：
 * @Version: 1.0$
 */
public class DHashArith {
    static String  fileName= PictureProcessin.path+"images/";

    public static List<String> produceAllImagesDhash(int count){

        List<String >hashCodes=new ArrayList<>();
        String hashCode=null;
        for (int i=0;i<count;i++){

            hashCode= DHashArith.DHashGen(fileName  + (i + 1) + ".jpg");
            hashCodes.add(hashCode);
        }
        return hashCodes;
    }


    public static String DHashGen(String filePath) {
        BufferedImage bufferedImage=null;
        try {
            bufferedImage= ImageIO.read(new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String fingerPrint = "";
        //    1.缩小图片：收缩到9*8的大小，一遍它有72的像素点
        bufferedImage = PhashArith.resize(bufferedImage, 8, 9);
        //    2.转化为灰度图：把缩放后的图片转化为256阶的灰度图。（具体算法见平均哈希算法步骤）
        bufferedImage = ImageToGray(bufferedImage);
        //    3.计算差异值：dHash算法工作在相邻像素之间，这样每行9个像素之间产生了8个不同的差异，一共8行，则产生了64个差异值
        fingerPrint=fingerPrint(bufferedImage,8,9);
        return fingerPrint;
    }
    //    4.获得指纹：如果左边的像素比右边的更亮，则记录为1，否则为0.
    public static String fingerPrint(BufferedImage image, int m, int n) {
        int[] finger = new int[n*n];
        double[][] iMatrix = new double[m][n];
        iMatrix = bufImagetoMat(image, image.getType(), CvType.CV_32F);
//        for (int i=0;i<n;i++){
//            double [] b=mat1.get(i,0);
//            for (int j=0;j<m;j++){
//                iMatrix[i][j]=b[j];
//            }
//        }
        for (int i=0;i<n;i++){
            for (int j=0;j<m-1;j++){
                if(iMatrix[i][j]>iMatrix[i][j+1]){
                   finger[i*n+j]=1;
                }else {
                    finger[i*n+j]=0;
                }
            }
        }

        //      将二进制转换成十六进制
        StringBuffer hashCode=new StringBuffer();
        for (int i=0;i<finger.length-1;i+=4){
            int result=finger[i]*(int)Math.pow(2,3)+finger[i+1]*(int)Math.pow(2,2)+finger[i + 2] * (int) Math.pow(2, 1) + finger[i + 3];
            hashCode.append(SimilarImageSearch.binaryToHex(result));
        }
        return hashCode.toString();
    }
}
