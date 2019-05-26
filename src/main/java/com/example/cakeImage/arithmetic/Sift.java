package com.example.cakeImage.arithmetic;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import static org.opencv.imgproc.Imgproc.GaussianBlur;

/**
 * @ Author     ：CrazyCake.
 * @ Date       ：Created in 19:27 2019/5/11
 * @ Description：sift算法
 * @ Modified By：
 * @Version: 1.0$
 */
public class Sift {
    BufferedImage image=null;
    public Sift(BufferedImage image){
        this.image=image;
    }

    /**
     * @ Author     ：CrazyCake
     * @ Date       ：Created in 15:46 2019/5/23
     * @ Description：
     *      将图像进行高斯模糊，先利用模糊函数计算高斯模板矩阵，然后进行卷积运算
     * @高斯模糊：是一种图像滤波器，它使用正态分布（高斯函数）计算模糊模板，并使用该模板与原图像做卷积运算，达到模糊图像的目的
     *
     * @ Return     ：模糊后的图像信息矩阵
     */
    public double[][]gaussTran(double[][] source,int index){
//        获取大小
        int height=source.length;
        int width=source[0].length;
//        保存高斯过滤后的结果
        double [][]result=new double[height][width];
//        获取高斯模板，根据不同的i值获取不同的高斯模板
        double [][]template=GaussTemplate.getTemplate(index);
//        获取高斯模板维数
        int gt=template[0].length;
        for (int i=0;i<height;i++){
            for (int j=0;j<width;j++){
//                进行模糊处理-----卷积运算
                double sum=0.0;
                for (int m=0;m<gt;m++){
                    for (int n=0;n<gt;n++){
                        int x=j-gt/2+n;
                        int y=i-gt/2+m;
//                        判断是否越界
                        if(x>=0&&x<width&&y>=0&&y<height){
                            sum+=source[y][x]*template[m][n];
                        }
                    }
                }
                result[i][j]=sum;
            }
        }
        return  result;
    }
    /**
     * @ Author     ：CrazyCake
     * @ Date       ：Created in 21:51 2019/5/23
     * @ Description：构建高斯金字塔
     *
     *
     * @ Return     ：
     */
    public HashMap<Integer,double[][]>getGaussPyramid(double[][]source,int minSize,int s,double baseSigma){
        int width=source[0].length;
        int height=source.length;
//        求取最小值
        int small=width>height?height:width;
//        求金字塔层数    ?
        int octave=(int)(Math.log(small/minSize)/Math.log(2.0));
//        每一组高斯图像数目为S+3
        int gaussS=s+3;
        return null;
    }
}
