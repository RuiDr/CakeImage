package com.example.cakeImage.arithmetic;
import com.example.cakeImage.tools.Utility;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
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
     * @ Return     ：
     */
    public HashMap<Integer,double[][]>getGaussPyramid(double[][]source,int minSize,int s,double baseSigma){
        int width=source[0].length;
        int height=source.length;
//        求取最小值
        int small=width>height?height:width;
//        求金字塔层数    根据公式
        int octave=(int)(Math.log(small/minSize)-3);
//        每一组高斯图像数目为S=n+3,n取2，拥有特征值的图像张数
        int gaussS=s+3;
//        ？
        double []sig=new double[6];
        sig[0]=baseSigma;
        for (int i=0;i<gaussS;i++){
            double preSigma=baseSigma*Math.pow(2,(double)(i-1)/s);
            double nextSigma=preSigma*Math.pow(2,(double)1/s);
            sig[i]=Math.sqrt(nextSigma*nextSigma-preSigma*preSigma);
        }
//        存放结果
        HashMap<Integer,double[][]>gaussPyramid=new HashMap<>();
//        临时存储
        double[][]tempSource=gaussTran(source,0);
//        迭代生成一张张高斯图像
        for (int i=0;i<octave;i++){
            int j=0;//组内层数
            int index=0;//每张图片在数组(hashmap)里面的索引
            for (;j<gaussS;j++){
                if(0==j){
//                    第一张不进行模糊处理
                    index=i*gaussS+j;
//                    存入高斯金字塔
                    gaussPyramid.put(index,tempSource);
                    continue;
                }
//                计算得到下一张图片的组内尺度
//                sigma=sigma*Math.pow(2,(double)1/s);
                double start=System.currentTimeMillis();
//                进行高斯模糊
                tempSource=gaussTran(tempSource,j);
                double end=System.currentTimeMillis();
                System.out.println("模糊"+(end-start));
                index=i*gaussS+j;
//                存入高斯金字塔
                gaussPyramid.put(index,tempSource);
            }
//            选每一组的倒数第三张图片进行降采样，注意此时的j是6而不是5，所以减去3而不是2
             tempSource=getCapSimpleTmg(gaussPyramid.get(index-2));
        }
        return gaussPyramid;
    }
    /**
     * @ Author     ：CrazyCake
     * @ Date       ：Created in 16:02 2019/5/27
     * @ Description：进行降采样（隔点采样）
     * @ Return     ：
     */
    private double[][] getCapSimpleTmg(double[][] source) {
//        计算每一组高斯图的大小，隔点采样按采取偶数位上的点进行，否则大小计算有误！！！！！
        int width=(int)source[0].length/2;
        int height=(int)source.length/2;
//        存储采样结果
        double[][]result=new double[height][width];
        for (int i=0;i<height;i++){
            for (int j=0;j<width;j++){
                int y=2*i;
                int x=2*j;
                result[i][j]=source[y][x];
            }
        }
        return result;
    }
    /**
     * @ Author     ：CrazyCake
     * @ Date       ：Created in 16:12 2019/5/27
     * @ Description：获取高斯差分金字塔（DoG金字塔），近似高斯拉普拉斯函数，来获取非常稳定的极大极小值--特征点
     * @ Return     ：
     */
    public static HashMap<Integer,double[][]> gaussToDog(HashMap<Integer,double[][]> gaussPyramid,int num){
        HashMap<Integer,double[][]>dogPyramid=new HashMap<>();
//        获取高斯金字塔里面的图像
        Set<Integer>iset=gaussPyramid.keySet();
        int length=iset.size();
        for (int i=0;i<length-1;i++){
            double[][] source1=gaussPyramid.get(i);
            double [][] source2=gaussPyramid.get(i+1);
            int width=source1[0].length;
            int height=source1.length;
//            临时dog图像
            double[][] dogImg=new double[height][width];
//            做差分计算
            if(((i+1)%num)!=0){
//                如果不是每一组的最后一张图像
                for (int m=0;m<height;m++){
                    for (int n=0;n<width;n++){
                        dogImg[m][n]=source2[m][n]-source1[m][n];
                    }
                }
            }
//            存入god金字塔
            dogPyramid.put(i,dogImg);
        }
        return dogPyramid;
    }

    /**
     * @ Author     ：CrazyCake
     * @ Date       ：Created in 17:43 2019/5/27
     * @ Description：初步检测DoG的极值点 这个hashmap的key不是连续的数字，而是在高斯金字塔的图像索引值
     * @ Param   num: 高斯金字塔 每层的图像数目
     * @ Return     ：HashMap<Integer,List<MyPoint>> integer是高斯金字塔内图像的索引
     */
    public static HashMap<Integer, List<MyPoint>> getRoughKeyPoint(HashMap<Integer,double[][]> dogPyramid,int num) {
        HashMap<Integer,List<MyPoint>> resultMap=new HashMap<>();
        Set<Integer> dogIndex=dogPyramid.keySet();
        for (int i:dogIndex){
//            对于dog金字塔每组的第一张和最后一张图像不进行求极值处理，原因是无法在高斯金字塔的方向上做极值
            if(((i%num)!=0)&&((i%num)!=num-2)){
                double[][] dogImage=dogPyramid.get(i);
//                获取该层图空间位置上的下一层和上一层
                double[][] dogImgeDown=dogPyramid.get(i-1);
                double[][] dogImageUp=dogPyramid.get(i+1);
                List<MyPoint> mpList=new ArrayList<>();

                int width=dogImage[0].length;
                int height=dogImage.length;
//                对每张dog图像求极值点
                for (int y=0;y<height;y++){
                    for (int x=0;x<width;x++){
                        if ((x>0)&&x<width-1&&y>0&&y<height-1){
//                            图像的边缘点默认不是极值点
//                            比较上下尺度以及8领域的共26个点
                            double []values=new double[26];
//                            关键点的值
                            double keyValue=dogImage[y][x];

                            values[0]=dogImage[y-1][x-1];
                            values[1]=dogImage[y-1][x];
                            values[2]=dogImage[y-1][x+1];
                            values[3]=dogImage[y][x+1];
                            values[4]=dogImage[y+1][x+1];
                            values[5]=dogImage[y+1][x];
                            values[6]=dogImage[y+1][x-1];
                            values[7]=dogImage[y][x-1];
//                            下一层
                            values[8]=dogImgeDown[y-1][x-1];
                            values[9]=dogImgeDown[y-1][x];
                            values[10]=dogImgeDown[y-1][x+1];
                            values[11]=dogImgeDown[y][x+1];
                            values[12]=dogImgeDown[y+1][x+1];
                            values[13]=dogImgeDown[y+1][x];
                            values[14]=dogImgeDown[y+1][x-1];
                            values[15]=dogImgeDown[y][x-1];
                            values[16]=dogImgeDown[y][x];
//                            上一层
                            values[17]=dogImageUp[y-1][x-1];
                            values[18]=dogImageUp[y-1][x];
                            values[19]=dogImageUp[y-1][x+1];
                            values[20]=dogImageUp[y][x+1];
                            values[21]=dogImageUp[y+1][x+1];
                            values[22]=dogImageUp[y+1][x];
                            values[23]=dogImageUp[y+1][x-1];
                            values[24]=dogImageUp[y][x-1];
                            values[25]=dogImageUp[y][x];
                            boolean isKey= Utility.isExtreneValue(values,keyValue);
                            if (isKey){
                                MyPoint mp=new MyPoint();
                                mp.setX(x);
                                mp.setY(y);
                                mp.setOctave(i/6);
                                mp.setS(i%6);
                                mpList.add(mp);
                            }
                        }
                    }
                }///一张图遍历完毕
                resultMap.put(i,mpList);

            }

        }
        return resultMap;
    }
    /**
     * @ Author     ：CrazyCake
     * @ Date       ：Created in 21:34 2019/5/27
     * @ Description：过滤关键点，得到更加稳定的特征点---去除对比度低（方差）和边缘点（hessian矩阵去边缘点）
     * @ Return     ：
     */
    public static HashMap<Integer,List<MyPoint>> filterPoints(HashMap<Integer,double[][]>dogPyramid,HashMap<Integer,
            List<MyPoint>>keyPoints,int r,double contrast){
        HashMap<Integer,List<MyPoint>> resultMap=new HashMap<>();
        Set<Integer>pSet=keyPoints.keySet();
        for (int i:pSet){
            List<MyPoint>points=keyPoints.get(i);
            List<MyPoint>resultPoints=new ArrayList<>();
//            获取对应的dog图像
            double[][]gaussImage=dogPyramid.get(i);
            for (MyPoint mp:points){
                int x=mp.getX();
                int y=mp.getY();
                double xy00=gaussImage[y-1][x-1];
                double xy01=gaussImage[y-1][x];
                double xy02=gaussImage[y-1][x+1];
                double xy10=gaussImage[y][x-1];
                double xy11=gaussImage[y][x];
                double xy12=gaussImage[y][x+1];
                double xy20=gaussImage[y+1][x-1];
                double xy21=gaussImage[y+1][x];
                double xy22=gaussImage[y+1][x+1];

                double dxx=xy10+xy12-2*xy11;
                double dyy=xy01+xy21-2*xy11;
                double dxy=(xy22-xy20)-(xy02-xy00);
//                hessian矩阵的对角线值和行列式
                double trH=dxx+dyy;
                double detH=dxx*dyy;
//                领域的均值
                double avg=(xy00+xy01+xy02+xy10+xy11+xy12+xy20+xy21+xy22)/9;
                ///领域方差
                double DX=(xy00-avg)*(xy00-avg)+(xy01-avg)*(xy01-avg)+(xy02-avg)*(xy02-avg)+(xy10-avg)*(xy10-avg)+
                        (xy11-avg)*(xy11-avg)+(xy12-avg)*(xy12-avg)+(xy20-avg)*(xy20-avg)+(xy21-avg)*(xy21-avg)+(xy22-avg)*(xy22-avg);
                DX=DX/9;
            }
        }

    }
}
