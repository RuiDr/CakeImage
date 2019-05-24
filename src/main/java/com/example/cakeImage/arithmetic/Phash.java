package com.example.cakeImage.arithmetic;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
/**
 * @ Author     ：CrazyCake.
 * @ Date       ：Created in 10:20 2019/5/11
 * @ Description：pHash算法
 * @ Modified By：
 * @Version: 1.0$
 */
public class Phash {
    static String  fileName= PictureProcessin.path+"images/";
//    生成phash算法的指纹集
public static List<String> produceAllImagesPhash(int count){

    List<String >hashCodes=new ArrayList<>();
    String hashCode=null;
    for (int i=0;i<count;i++){
        hashCode=Phash.PHashGen(fileName  + (i + 1) + ".jpg");
        hashCodes.add(hashCode);
    }
    return hashCodes;
}

    public static String PHashGen(String imagePath){
        BufferedImage bufferedImage=null;
        String fingerPrint="";
        try{
            bufferedImage=ImageIO.read(new File(imagePath));
//            缩小尺寸
            bufferedImage=Phash.resize(bufferedImage,32,32);
//            灰度变换
            bufferedImage=ImageToGray(bufferedImage);
//            计算DCT
            double[][]dtc=Phash.DTC(bufferedImage,32);
//            缩小DCT
            double [][]reDtc=Phash.reDTC(dtc,8);
//            计算平均值
            double avg=Phash.avgDCT(reDtc,32);
//            计算hash值
            fingerPrint=Phash.generateHash(avg,reDtc,32);

        }catch (Exception e){
            e.printStackTrace();
        }
        return fingerPrint;
    }
    /*
       将图形24色灰度变换图形灰度化

       (0xAABBCCDD & 0xFF000000)>>24 得到的是AA两位的值 透明度
       (0xAABBCCDD & 0x00FF0000)>>16 得到的是BB两位的值 红色
       (0xAABBCCDD & 0x0000FF00)>>8 得到的是CC两位的值 绿色
       (0xAABBCCDD & 0x000000FF) 得到的是DD两位的值 蓝色
     */
    public static BufferedImage ImageToGray(BufferedImage  bufferedImage){

        int width=bufferedImage.getWidth();
        int height=bufferedImage.getHeight();
        BufferedImage result=new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
        for (int j=0;j<height;j++){
            for (int i=0;i<width;i++) {
//      返回： -6814716，此处可将整数转换成Color获取RGB值
                int argb = bufferedImage.getRGB(i, j);
//      16进制颜色码转换成RGB颜色值
                int a = argb >> 24 & 0xff;
                int r = argb >> 16 & 0xff;
                int g = argb >> 8 & 0xff;
                int b = argb & 0xff;
                int gray=(int )(0.3*r+0.59*g+0.11*b);
                int grayARGB=a<<24|gray<<16|gray<<8|gray;
                result.setRGB(i,j,grayARGB);
            }
        }
        return result;
    }
    /*
        将图片缩小为指定的尺寸
        image:原始图片
        width:图片宽
        heigth:图片高
     */
    public  static  BufferedImage resize(BufferedImage image,int width,int height){
//      创建指定大小，指定图形类型的bufferedImage对象
        BufferedImage resizedImage=new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
//      返回Graphics对象，从而允许通过该对象向bufferedImage中添加图形
        Graphics2D g=resizedImage.createGraphics();
        g.drawImage(image,0,0,width,height,null);
        g.dispose();
        return resizedImage;
    }
    /*
    计算hash生成指纹
    avg:所得dtc的平均值
    dct:缩小后的dtc
    算法:设置0/1的64位hash值,大于avg设为1,小于avg设为0,组合在一起构成64位的整数,即为图片的指纹
    n:矩阵的大小
     */
    public static String  generateHash(double avg,double[][]original,int n){
        String fingerPrint="";
        for (int i=0;i<n;i++){
            for (int j=0;j<n;j++){
                fingerPrint+=(original[i][j]>avg?"0":"1");
            }
        }
        return fingerPrint;
    }
    /*
    计算均值
    original:缩小后的dct
    n:dct的大小
     */
    public static double avgDCT(double[][]original,int n){
        double tatal=0;
        for (int i=0;i<n;i++){
            for (int j=0;j<n;j++){
                tatal+=original[i][j];
            }
        }
    //    减去第一项,因为直流数据可能有显著的不同,偏离平均值
        tatal-=original[0][0];
        double avg=tatal/(n*n-1);
        return avg;
    }

    /*
      缩小DCT
      original:原始dct
      n:缩小后的大小
     */
    public static double[][]reDTC(double [][] original,int n){
        double [][]newDtc=new double[n][n];
        for (int i=0;i<n;i++){
            for (int j=0;j<n;j++){
                newDtc[i][j]=original[i][j];
            }
        }
        return newDtc;
    }

    /*
     计算图形DCT
      image:原始图片
      n:原始图片大小
      return:变换后的矩阵数据
     */
    public static double[][] DTC(BufferedImage image,int n){
        double [][]iMatrix=new double[n][n];

        Mat mat= bufImagetoMat(image ,image.getType(),CvType.CV_32F);

        for (int i=0;i<n;i++){
            double[]a=mat.get(i,0);
            for (int j=0;j<n;j++){
                iMatrix[i][j]=a[j];
            }
        }
    //        求系数矩阵
        double [][]coefficient=Phash.findCoefficient(n);
    //        求系数矩阵的转置
        double[][]cofficientT=Phash.transposingMatrix(coefficient,n);
        double [][]temp=new double[n][n];
    //        矩阵相乘
        temp=Phash.matrixMultiply(coefficient,iMatrix,n);
        iMatrix=Phash.matrixMultiply(temp,cofficientT,n);
        return iMatrix;
    }
//    求离散变换的系数矩阵
    /*
        image转换成Mat
        imgType:图片类型
        matType:矩阵类型
     */
    public static Mat bufImagetoMat(BufferedImage original,int imgType,int matType){
        if (original==null){
            throw new IllegalArgumentException("original==null");
        }
       if(original.getType()!=imgType){
           BufferedImage image=new BufferedImage(original.getWidth(),original.getHeight(),imgType);
           Graphics2D g=image.createGraphics();
           try{
              g.setComposite(AlphaComposite.Src);
              g.drawImage(original,0,0,null);
           }finally {
               g.dispose();
           }
       }
       byte[] pixels=((DataBufferByte)original.getRaster().getDataBuffer()).getData();
       Mat mat=Mat.eye(original.getHeight(),original.getWidth(),matType);
       mat.put(0,0,pixels);
       return mat;
    }
    /*
    矩阵相乘
    coefficient:矩阵1
    iMatrix:矩阵2
    n:大小
     */
    public static double[][] matrixMultiply(double[][] coefficient, double[][] iMatrix, int n) {
        double nMatrix[][]=new double[n][n];
        double t=0.0;
        for (int i=0;i<n;i++){
            for (int j=0;j<n;j++){
                t=0;
                for (int k=0;k<n;k++){
                    t+=coefficient[i][k]*iMatrix[k][j];
                }
                nMatrix[i][j]=t;
            }
        }
        return nMatrix;
    }

    /*
      求矩阵的转置
      coefficient:原矩阵
      n:矩阵大小
     */
  public static double[][] transposingMatrix(double[][] coefficient, int n) {
        double nMatrix[][]=new double[n][n];
        for (int i=0;i<n;i++){
            for (int j=0;j<n;j++){
                nMatrix[i][j]=coefficient[j][i];
            }
        }
        return nMatrix;
    }

    /*求系数矩阵
      n:矩阵大小
     */
    public static double[][] findCoefficient(int n) {
        double[][]coe=new double[n][n];
        double sqrt=1.0/Math.sqrt(n);
        for (int i=0;i<n;i++){
            coe[0][i]=sqrt;
        }
        for(int i=1;i<n;i++){
            for (int j=0;j<n;j++){
                coe[i][j]=Math.sqrt(2.0/n) * Math.cos(i*Math.PI*(j+0.5)/(double)n);
            }
        }
        return coe;
    }
}
