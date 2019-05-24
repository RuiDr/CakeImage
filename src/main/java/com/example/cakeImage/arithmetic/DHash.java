package com.example.cakeImage.arithmetic;


import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.awt.image.BufferedImage;

import static com.example.cakeImage.arithmetic.Phash.ImageToGray;
import static com.example.cakeImage.arithmetic.Phash.bufImagetoMat;

/**
 * @ Author     ：CrazyCake
 * @ Date       ：Created in 1:52 2019/5/11
 * @ Description：dHash算法
 * @ Modified By：
 * @Version: 1.0$
 */
public class DHash {
    static String  fileName= PictureProcessin.path+"images/";

    static BufferedImage image = null;
    Phash phash = new Phash();

    public DHash(BufferedImage image) {
        this.image = image;
    }
    public String DHashGen() {
        String fingerPrint = "";
        //    1.缩小图片：收缩到9*8的大小，一遍它有72的像素点
        image = phash.resize(image, 8, 9);
        //    2.转化为灰度图：把缩放后的图片转化为256阶的灰度图。（具体算法见平均哈希算法步骤）
        image = ImageToGray(image);
        //    3.计算差异值：dHash算法工作在相邻像素之间，这样每行9个像素之间产生了8个不同的差异，一共8行，则产生了64个差异值

        return fingerPrint;
    }
    //    4.获得指纹：如果左边的像素比右边的更亮，则记录为1，否则为0.
    public String fingerPrint(BufferedImage image, int m, int n) {
        String fingerPrint = "";
        double[][] iMatrix = new double[m][n];
        Mat mat1 = bufImagetoMat(image, image.getType(), CvType.CV_32F);
        for (int i=0;i<n;i++){
            double [] b=mat1.get(i,0);
            for (int j=0;j<m;j++){
                iMatrix[i][j]=b[j];
            }
        }
        for (int i=0;i<n;i++){
            for (int j=0;j<m-1;j++){
                if(iMatrix[i][j]>iMatrix[i][j+1]){
                    fingerPrint+=1;
                }else {
                    fingerPrint+=0;
                }
            }
        }
        return fingerPrint;
    }

}
