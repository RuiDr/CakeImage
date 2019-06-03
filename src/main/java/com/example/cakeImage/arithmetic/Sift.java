package com.example.cakeImage.arithmetic;
import com.example.cakeImage.tools.Utility;
import org.opencv.core.*;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.FeatureDetector;
import org.opencv.highgui.Highgui;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.*;
import java.util.*;
import java.util.List;
/**
 * @ Author     ：CrazyCake.
 * @ Date       ：Created in 19:27 2019/5/11
 * @ Description：sift算法
 * @ Modified By：
 * @Version: 1.0$
 */
public class Sift {
    public Sift(){
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    BufferedImage image = null;
    static String  fileName= PictureProcessin.path+"images/";
//    public static void main(String[] args) {
//       List<MyPoint>list= getCharacterVectors(fileName+"1.jpg");
//    }


//保存该图片的特征点及特征向量集
    public static void saveCharacter(int count)throws Exception{
        for (int i=0;i<count;i++){
            File file = new File("G:\\java\\webprojects\\CakeImage\\siftdata\\"+(i+1)+".txt"); //存放数组数据的文件
            FileWriter out = new FileWriter(file);
            BufferedWriter bw=new BufferedWriter(out);
            List<MyPoint>list= getCharacterVectors(fileName+(i+1)+".jpg");
            if (list==null)
                continue;
          for (int j=0;j<list.size();j++) {
              double []a=list.get(j).getGrads();
                for (int k=0;k<a.length;k++){
                    bw.write(a[k] + "\t ");
                    bw.flush();
                }
              bw.write ( " \n");

          }
          System.out.println("数据读取保存成功 "+i+1);

        }
    }
//    读取一个文件
    public static List<MyPoint> readCharacterVectors(File file){
        List<MyPoint>list=new ArrayList<>();
        try {
            StringBuilder result = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

            String s=null;
            int i=0;
            while((s = bufferedReader.readLine())!=null){
                String[]temp=s.toString().replaceFirst(" ","").split("\\s+");
                double[]a=new double[temp.length];
                for (int k=0;k<temp.length;k++){
                    a[k]=Double.parseDouble(temp[k]);
                }
                MyPoint myPoint=new MyPoint();
                myPoint.setGrads(a);
                list.add(myPoint);

            }

//            for (int k=0;k<list.size();k++){
//                MyPoint myPoint=list.get(k);
//                for (int x=0;x<myPoint.getGrads().length;x++){
//                    System.out.print(" "+myPoint.getGrads()[x]);
//                }
//                System.out.println();
//            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    private static String isDouble(String s) {
        String str="";
        for (int i=0;i<s.length();i++){
            if (Character.isDigit(s.charAt(i))||s.charAt(i)=='.'){
                str+=s.charAt(i);
            }
        }
        return str;
    }

    //    获取该图片的特征点及特征向量
    public static List<MyPoint>  getCharacterVectors(String string) {
        BufferedImage bufferedImage=null;
            try{
                bufferedImage= ImageIO.read(new File(string));
                //            灰度变换
                bufferedImage=ImageToGray(bufferedImage);
//            image转换成二维数组
                double [][]source=BufferedImageToDouble(bufferedImage);
//            模糊后的高斯显示
                HashMap<Integer,double[][]>result=getGaussPyramid(source,3,1.6);
//            获取高斯差分金字塔
                HashMap<Integer,double[][]> dog=gaussToDog(result,6);
//          获取极值点
                HashMap<Integer, List<MyPoint>>keyPoints= getRoughKeyPoint(dog,6);
//          获取过滤后的极值点
                keyPoints=filterPoints(dog, keyPoints, 10,0.03);
//          获取该张图片的特征点
                List<MyPoint> vctors=getVectors(result, keyPoints);

                return vctors;
            }catch (Exception e){

            }
            return null;
    }
    private static double[][] getGauss(HashMap<Integer,double[][]>gauss,MyPoint point) {
        int num=6;
        int index=point.getOctave()+point.getS()*num;
        double[][]a=gauss.get(index);
        return a;
    }
    private static double[][] BufferedImageToDouble(BufferedImage bufferedImage) {
        int width=bufferedImage.getWidth();
        int height=bufferedImage.getHeight();
        double[][] result=new double[height][width];
        for(int j=0;j<height;j++){
            for(int i=0;i<width;i++){
                int rgb=bufferedImage.getRGB(i, j);
//                System.out.println("rgb is "+rgb);
                int grey=(rgb>>16)&0xFF;
//                System.out.println("grey is "+grey);
                result[j][i]=grey;

            }
        }
        return result ;
    }

    private static Mat BufferedImageMat(BufferedImage bufferedImage) {

        int width=bufferedImage.getWidth();
        int height=bufferedImage.getHeight();

       Mat mat=new Mat(width,height,CvType.CV_8U);
        for(int j=0;j<height;j++){
            for(int i=0;i<width;i++){
                int rgb=bufferedImage.getRGB(i, j);

                System.out.println("rgb is "+rgb);
                int grey=(rgb>>16)&0xFF;
                System.out.println("grey is "+grey);
                double []a=new double[3];
                a[0]=grey;
                a[1]=grey;
                a[2]=grey;
                mat.put(i,j,a);

            }
        }
        return mat ;
    }
    public static  void writeImageFile(BufferedImage bi) throws IOException {
        File outputfile = new File("saved.png");
        ImageIO.write(bi, "png", outputfile);
    }

    public static Mat bufferToMartix(BufferedImage image) {
        int width=image.getWidth();
        int height=image.getHeight();
        Mat mat = new Mat(height, width, CvType.CV_8UC3);
        byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        if (mat != null) {
            try {
                mat.put(0, 0, data);
            } catch (Exception e) {
                //throw new UnsupportedOperationException("byte is null");
                return null;
            }
        }
        return mat;
    }
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
    public Sift(BufferedImage image) {
        this.image = image;
    }
    /**
     * @ Author     ：CrazyCake
     * @ Date       ：Created in 15:46 2019/5/23
     * @ Description：
     * 将图像进行高斯模糊，先利用模糊函数计算高斯模板矩阵，然后进行卷积运算
     * @高斯模糊：是一种图像滤波器，它使用正态分布（高斯函数）计算模糊模板，并使用该模板与原图像做卷积运算，达到模糊图像的目的
     * @ Return     ：模糊后的图像信息矩阵
     */
    public static double[][] gaussTran(double[][] source, int index) {
//        获取大小
        int height = source.length;
        int width = source[0].length;
//        保存高斯过滤后的结果
        double[][] result = new double[height][width];
//        获取高斯模板，根据不同的i值获取不同的高斯模板
        double[][] template = GaussTemplate.getTemplate(index);
//        获取高斯模板维数
        int gt = template[0].length;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
//                进行模糊处理-----卷积运算
                double sum = 0.0;
                for (int m = 0; m < gt; m++) {
                    for (int n = 0; n < gt; n++) {
                        int x = j - gt / 2 + n;
                        int y = i - gt / 2 + m;
//                        判断是否越界
                        if (x >= 0 && x < width && y >= 0 && y < height) {
                            sum += source[y][x] * template[m][n];
                        }
                    }
                }
                result[i][j] = sum;
            }
        }
        return result;
    }
    /**
     * @ Author     ：CrazyCake
     * @ Date       ：Created in 21:51 2019/5/23
     * @ Description：构建高斯金字塔
     * @ Return     ：
     */
    public static HashMap<Integer, double[][]> getGaussPyramid(double[][] source, int n, double baseSigma) {
        //        存放结果
        HashMap<Integer, double[][]> gaussPyramid = new HashMap<>();
//        临时存储
        double[][] tempSource = source;
        int width = source[0].length;
        int height = source.length;
//        求取最小值
        int small = width > height ? height : width;
//        求金字塔层数    根据公式
        int octave=(int) (Math.log(small/20)/Math.log(2.0));//        每一组高斯图像数目为S=n+3,n取2，拥有特征值的图像张数
        int gaussS = n + 3;
//         获取原始矩阵

        double k = Math.pow(2, Math.sqrt(n));
        double []sig=new double[6];
        sig[0]=baseSigma;
        for (int i=0;i<gaussS;i++){
            double preSigma=baseSigma*Math.pow(2,(double)(i-1)/n);
            double nextSigma=preSigma*Math.pow(2,(double)1/n);
            sig[i]=Math.sqrt(nextSigma*nextSigma-preSigma*preSigma);
        }
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
//                System.out.println("模糊"+(end-start));
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
    public static double[][] getCapSimpleTmg(double[][] source) {
//        计算每一组高斯图的大小，隔点采样按采取偶数位上的点进行，否则大小计算有误！！！！！
        int width = (int) source[0].length / 2;
        int height = (int) source.length / 2;
//        存储采样结果
        double[][] result = new double[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int y = 2 * i;
                int x = 2 * j;
                result[i][j] = source[y][x];
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
    public static HashMap<Integer, double[][]> gaussToDog(HashMap<Integer, double[][]> gaussPyramid, int num) {
        HashMap<Integer, double[][]> dogPyramid = new HashMap<>();
//        获取高斯金字塔里面的图像
        Set<Integer> iset = gaussPyramid.keySet();
        int length = iset.size();
        for (int i = 0; i < length - 1; i++) {
            double[][] source1 = gaussPyramid.get(i);
            double[][] source2 = gaussPyramid.get(i + 1);
            int width = source1[0].length;
            int height = source1.length;
//            临时dog图像
            double[][] dogImg = new double[height][width];
//            做差分计算
            if (((i + 1) % num) != 0) {
//                如果不是每一组的最后一张图像
                for (int m = 0; m < height; m++) {
                    for (int n = 0; n < width; n++) {
                        dogImg[m][n] = source2[m][n] - source1[m][n];
                    }
                }
            }
//            存入god金字塔
            dogPyramid.put(i, dogImg);
        }
        return dogPyramid;
    }
    /**
     * @ Author     ：CrazyCake
     * @ Date       ：Created in 17:43 2019/5/27
     * @ Description：初步检测DoG的极值点
     * @ Param   num: 高斯金字塔 每层的图像数目
     * @ Return     ：HashMap<Integer,List<MyPoint>> integer是高斯金字塔内图像的索引
     */
    public static HashMap<Integer, List<MyPoint>> getRoughKeyPoint(HashMap<Integer, double[][]> dogPyramid, int num) {
        int n = 2;
        double T = 0.04;
        HashMap<Integer, List<MyPoint>> resultMap = new HashMap<>();
        Set<Integer> dogIndex = dogPyramid.keySet();
//        i=0,1,2,3,4,5，做差分后减1
        for (int i : dogIndex) {
//            对于dog金字塔每组的第一张和最后一张图像不进行求极值处理，原因是无法在高斯金字塔的方向上做极值,
            if (((i%num)!=0)&&((i%num)!=num-2)) {
                double[][] dogImage = dogPyramid.get(i);
//                获取该层图空间位置上的下一层和上一层
                double[][] dogImgeDown = dogPyramid.get(i - 1);
//                System.out.println("dogImgeDown is "+dogImgeDown.length+" 0:"+dogImgeDown[0].length);
                double[][] dogImageUp = dogPyramid.get(i + 1);

//                System.out.println("dogImgeUp is "+dogImageUp.length+" 0:"+dogImageUp[0].length);

                if (!((dogImage.length==dogImageUp.length)&&(dogImage.length==dogImgeDown.length))){
                    continue;
                }
                List<MyPoint> mpList = new ArrayList<>();
                int width = dogImgeDown[0].length;
                int height = dogImgeDown.length;
//                对每张dog图像求极值点
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        //                            图像的边缘点默认不是极值点
                        if ((x-1>0)&&((x+1)<width)&&((y-1)>0)&&((y+1)<height)) {
//                            比较上下尺度以及8领域的共26个点

                            double[] values = new double[26];
//                            关键点的值
                            double keyValue = dogImage[y][x];
//                            首先判断是不是噪点
//                            if (Math.abs(keyValue) > 0.5 * T / n) {
                            values[0]=dogImage[y-1][x-1];
                            values[1]=dogImage[y-1][x];
                            values[2]=dogImage[y-1][x+1];
                            values[3]=dogImage[y][x-1];
                            values[4]=dogImage[y][x+1];
                            values[5]=dogImage[y+1][x-1];
                            values[6]=dogImage[y+1][x];
                            values[7]=dogImage[y+1][x+1];

                            values[8]=dogImgeDown[y-1][x-1];///下一层
                            values[9]=dogImgeDown[y-1][x];
                            values[10]=dogImgeDown[y-1][x+1];
                            values[11]=dogImgeDown[y][x-1];
                            values[12]=dogImgeDown[y][x];
                            values[13]=dogImgeDown[y][x+1];
                            values[14]=dogImgeDown[y+1][x-1];
                            values[15]=dogImgeDown[y+1][x];
                            values[16]=dogImgeDown[y+1][x+1];

                            values[17]=dogImageUp[y-1][x-1];///上一层
                            values[18]=dogImageUp[y-1][x];
                            values[19]=dogImageUp[y-1][x+1];
                            values[20]=dogImageUp[y][x-1];
                            values[21]=dogImageUp[y][x];
                            values[22]=dogImageUp[y][x+1];
                            values[23]=dogImageUp[y+1][x-1];
                            values[24]=dogImageUp[y+1][x];
                            values[25]=dogImageUp[y+1][x+1];
                                boolean isKey = Utility.isExtreneValue(values, keyValue);
                                if (isKey) {
                                    MyPoint mp = new MyPoint();
                                    mp.setX(x);
                                    mp.setY(y);
                                    mp.setOctave(i / 6);
                                    mp.setS(i % 6);
                                    mpList.add(mp);
                                }
//                            }
                        }
                    }
                }///获得一张图的所有极值点，i表示的是第几张图，mpList表示的是该张图中所有的极值点
                resultMap.put(i, mpList);
            }
        }
        return resultMap;
    }
    /**
     * @ Author     ：CrazyCake
     * @ Date       ：Created in 23:14 2019/5/28
     * @ Description：image对应极值点的高斯金字塔，point对应的极值点，baseSigm对应的方差
     * @ Return     ：返回获得主方向的点
     */
//  image是极值点对应的高斯金字塔,point极值点

    /**
     * @ Author     ：CrazyCake
     * @ Date       ：Created in 23:43 2019/5/29
     * @ Description：精确极值点
     * @ Return     ：
     */
    public static MyPoint adjustLocation(HashMap<Integer, double[][]> dogPyramid, MyPoint kpoint, int MAX_STEP, int nLayer) {
//        获取关键点的下标
        int index = kpoint.getOctave() * nLayer + kpoint.getS();
//        获得组数
        int octave = kpoint.getOctave();
//        获取关键点的x,y坐标
        int x = kpoint.getX();
        int y = kpoint.getY();
//        获取组数
        int layer = kpoint.getS();
        double offL = 0;
        double offY = 0;
        double offX = 0;
//        在论文中设置了最大迭代次数为5
        for (int i = 0; i < MAX_STEP; i++) {
            double[][] img = dogPyramid.get(index);
            double[][] pre = dogPyramid.get(index - 1);
            double[][] next = dogPyramid.get(index + 1);
            int width = img[0].length;
            int height = img.length;
            if(pre!=null&&next!=null&&y>1&&x>1&&y<height-2&&x<width-2){
//               分别对三个分量求导的所构成的三行一列矩阵的参数矩阵
                double[] df_x = new double[3];
//                3*3的矩阵
                double[][] df_xx = new double[3][3];
                if((layer<1)||(layer>nLayer)||(x<1)||(x>width-1)||(y<1)||(y>height-1)){
                    ///如果越界，则返回null
                    return null;
                }

//                System.out.println("x="+x+ " y="+y);
                df_x[0]=(img[y][x+1]-img[y][x-1])/2;
                df_x[1]=(img[y+1][x]-img[y+1][x])/2;
                df_x[2]=(next[y][x]-pre[y][x])/2;

                double dxx = img[y][x + 1] + img[y][x + 1] - 2 * img[y][x];
                double dxy = (img[y + 1][x + 1] - img[y + 1][x - 1] - (img[y - 1][x + 1] - img[y - 1][x - 1])) / 4;
                double dxs = (next[y][x + 1] - next[y][x - 1] - (pre[y][x + 1] - pre[y][x - 1])) / 4;
                double dyy = img[y + 1][x] + img[y][x + 1] - 2 * img[y][x];
                double dys = (next[y + 1][x] - next[y - 1][x] - (pre[y + 1][x] - pre[y - 1][x])) / 4;
                double dss = next[y][x] - next[y][x] - 2 * img[y][x];

                df_xx[0][0] = dxx;
                df_xx[0][1] = dxy;
                df_xx[0][2] = dxs;
                df_xx[1][0] = dxy;
                df_xx[1][1] = dyy;
                df_xx[1][2] = dys;
                df_xx[2][0] = dxs;
                df_xx[2][1] = dys;
                df_xx[2][2] = dss;
//               新的极值点,负的三维矩阵的逆与参数矩阵的乘积
                double[] num = new double[3];
                num = getMultiply(df_xx, df_x);
//                求最新的极值
                offL = -num[2];
                offY = -num[1];
                offX = -num[0];
//              在论文中，当三个值都小于0.5时，则可退出
                if ((Math.abs(offL) < 0.5 && Math.abs(offY) < 0.5 && Math.abs(offX) < 0.5)) {
                    ///如果已经收敛
                    break;
                }

            }
//            超出迭代次数，舍弃该点
            if (i >= MAX_STEP) {

                return null;
            }
            kpoint.setX(x);
            kpoint.setY(y);
        }
        return kpoint;
    }
    /**
     * @ Author     ：CrazyCake
     * @ Date       ：Created in 0:09 2019/5/30
     * @ Description：三维矩阵与参数矩阵
     * @ Return     ：
     */
    public static double[] getMultiply(double[][] df_xx, double[] df_x) {
//        求取逆矩阵
        double[][] result = new double[3][3];
        result = getCover(df_xx);

//        求逆矩阵与参数矩阵的乘积
        double[] result1 = new double[3];
        result1[0] = df_xx[0][0] * df_x[0] + df_xx[0][1] * df_x[1] + df_xx[0][2] * df_x[2];
        result1[1] = df_xx[1][0] * df_x[0] + df_xx[1][1] * df_x[1] + df_xx[1][2] * df_x[2];
        result1[2] = df_xx[2][0] * df_x[0] + df_xx[2][1] * df_x[1] + df_xx[2][0] * df_x[2];
        return result1;
    }
    /**
     * @ Author     ：CrazyCake
     * @ Date       ：Created in 0:09 2019/5/30
     * @ Description：获取某张图像的所有特征向量集
     * @ Return     ：
     */
    private static  List<MyPoint> getVectors(HashMap<Integer,double[][]> gaussPyramid,HashMap<Integer, List<MyPoint>> keyPoints){

        List<MyPoint> testPoint=new ArrayList<MyPoint>();
        ////获取高斯金字塔里面的图像
        Set<Integer> iset=gaussPyramid.keySet();
        int length=iset.size();
        for(int i=0;i<length;i++){

            double[][] tempImage =gaussPyramid.get(i);///临时图像
            List<MyPoint> imagePoint=keyPoints.get(i);

            if(null!=imagePoint){

                ///获取特征点，并比较
                List<MyPoint> vector=getFeatureVector(tempImage, imagePoint,6,1.6,8);
                if(vector==null){
                    continue;
                }
                testPoint.addAll(vector);
            }

        }///end of for

        return testPoint;
    }
    /**
     * @ Author     ：CrazyCake
     * @ Date       ：Created in 0:19 2019/5/30
     * @ Description：求取逆矩阵
     * @ Return     ：
     */
    public static double[][] getCover(double[][] df_xx) {
//        求取逆矩阵
        double[][] result = new double[3][3];
        result[0][0] = df_xx[1][1] * df_xx[2][2] - df_xx[2][1] * df_xx[1][2];
        result[0][1] = df_xx[2][1] * df_xx[0][2] - df_xx[0][1] * df_xx[2][2];
        result[0][2] = df_xx[0][1] * df_xx[1][2] - df_xx[0][2] * df_xx[1][1];
        result[1][0] = df_xx[1][2] * df_xx[2][0] - df_xx[2][2] * df_xx[1][0];
        result[1][1] = df_xx[2][2] * df_xx[0][0] - df_xx[2][0] * df_xx[0][2];
        result[1][2] = df_xx[0][2] * df_xx[1][0] - df_xx[0][0] * df_xx[1][2];
        result[2][0] = df_xx[1][0] * df_xx[2][1] - df_xx[2][0] * df_xx[1][0];
        result[2][1] = df_xx[2][0] * df_xx[0][1] - df_xx[0][0] * df_xx[2][1];
        result[2][2] = df_xx[0][0] * df_xx[1][1] - df_xx[0][1] * df_xx[1][0];
        return result;
    }

    /**
     * @ Author     ：CrazyCake
     * @ Date       ：Created in 21:34 2019/5/27
     * @ Description：过滤关键点，得到更加稳定的特征点---去除对比度低（方差）和边缘点（hessian矩阵去边缘点）
     * @ Return     ：
     */
    public static HashMap<Integer, List<MyPoint>> filterPoints(HashMap<Integer, double[][]> dogPyramid, HashMap<Integer,
            List<MyPoint>> keyPoints, int r, double contrast) {
        HashMap<Integer, List<MyPoint>> resultMap = new HashMap<>();
        Set<Integer> pSet = keyPoints.keySet();
        for (int i : pSet) {
            List<MyPoint> points = keyPoints.get(i);
            List<MyPoint> resultPoints = new ArrayList<>();
//            获取对应的dog图像
            double[][] gaussImage = dogPyramid.get(i);
            for (MyPoint mp : points) {
                int x = mp.getX();
                int y = mp.getY();
//                对极值点的精确值进行判断
                if (adjustLocation(dogPyramid, mp, 5, 6) != null) {
                    double xy00 = gaussImage[y - 1][x - 1];
                    double xy01 = gaussImage[y - 1][x];
                    double xy02 = gaussImage[y - 1][x + 1];
                    double xy10 = gaussImage[y][x - 1];
                    double xy11 = gaussImage[y][x];
                    double xy12 = gaussImage[y][x + 1];
                    double xy20 = gaussImage[y + 1][x - 1];
                    double xy21 = gaussImage[y + 1][x];
                    double xy22 = gaussImage[y + 1][x + 1];
                    double dxx = xy10 + xy12 - 2 * xy11;
                    double dyy = xy01 + xy21 - 2 * xy11;
                    double dxy = (xy22 - xy20) - (xy02 - xy00);
//                hessian矩阵的对角线值和行列式
                    double trH = dxx + dyy;
                    double detH = dxx * dyy;
//                领域的均值
                    double avg = (xy00 + xy01 + xy02 + xy10 + xy11 + xy12 + xy20 + xy21 + xy22) / 9;
                    ///领域方差
                    double DX = (xy00 - avg) * (xy00 - avg) + (xy01 - avg) * (xy01 - avg) + (xy02 - avg) * (xy02 - avg) + (xy10 - avg) * (xy10 - avg) +
                            (xy11 - avg) * (xy11 - avg) + (xy12 - avg) * (xy12 - avg) + (xy20 - avg) * (xy20 - avg) + (xy21 - avg) * (xy21 - avg) + (xy22 - avg) * (xy22 - avg);
                    DX = DX / 9;

                    double threshold = (double) (r + 1) * (r + 1) / r;
                    if ((detH > 0 && (trH * trH) / detH < threshold) && (DX >= contrast)) {
//                    主曲率小于阈值，则不是需要剔除的边缘响应点；方差大于0.03的为高对比度
                        resultPoints.add(mp);
                    }
                }
            }
            resultMap.put(i, resultPoints);
        }
        return resultMap;
    }
    /**
     * @ Author     ：CrazyCake
     * @ Date       ：Created in 1:45 2019/5/29
     * @ Description：image:原始图像，point关键点，baseSigm：尺度
     * @ Return     ：
     */
    public static double[][] GetDescriptor(double[][] image, MyPoint point, double baseSigm) {
//      图像区域的半径radius,baseSigm是所在组内的尺度，d=4;
        int d = 4;
        double radius = (3 * baseSigm * Math.sqrt(2) * (d + 1) + 1) / 2;
//      将坐标移至关键点方向
        int x = point.getX();
        int y = point.getY();
        for (int i = (int) (x - radius); i < x + radius; i++) {
            for (int j = (int) (y - radius); j < y + radius; j++) {
//              获得主方向上的各点的新坐标
                int x1 = ((int) (Math.cos(baseSigm) * x - Math.sqrt(baseSigm) * y));
                int y1 = ((int) (Math.sin(baseSigm) * x + Math.cos(baseSigm) * y));
            }
        }
        return null;
    }
    //          二维数组转换成矩阵
    public static Mat ArrayToMat(double[][] source) {
        //        把二维数组转换成矩阵
        int width=source.length;
        int height=source.length;
        Mat mat = new Mat(width,width,CvType.CV_8UC1, new Scalar(0));
        for (int i = 0; i < source.length; i++) {
            for (int j = 0; j < source[0].length; j++) {
                mat.put(i, j, source[i][j]);
            }
        }
        return mat;
    }
    //    矩阵转换成二维数组
    public static double[][] MatToArray(Mat source) {
        int w = source.width();
        int l = source.height();
        double[][] mat = new double[l][w];
        for (int i = 0; i < source.height(); i++) {
            double[] a = source.get(i, 0);
            for (int j = 0; j < a.length; j++) {
                mat[i][j] = a[j];
            }
        }
        return mat;
    }
    //    求数组里面的最大值
    public static double[] MaxNum(double[] a) {
        double value[] = new double[2];
        value[0] = a[0];
        HashMap<Integer, Double> map = new HashMap<>();
        int key = 0;
        for (int i = 0; i < a.length; i++) {
            if (a[i] >= value[0]) {
                value[0] = a[i];
                value[1] = i;
            }
        }
        return value;
    }
//    获取相似集
public static boolean SiftGenerat( List<MyPoint>sourcePoints,List<MyPoint>filePoint){
    boolean isSimiliar=false;
    int count=0;
//    List<MyPoint>sourcePoints= getCharacterVectors(sourcePath);
//    List<MyPoint>filePoint=getCharacterVectors(fileName);

    for (int i=0;i<sourcePoints.size();i++){
//          获取第一个点
        MyPoint mp=new MyPoint();
        mp=sourcePoints.get(i);
//          该点的特征向量
        double[]a= mp.getGrads();
        ArrayList<Double>list=new ArrayList<>();
        for (int j=0;j<filePoint.size();j++){
            MyPoint mp1=new MyPoint();
            mp1=filePoint.get(j);
            double []b=mp1.getGrads();
//              计算欧式距离，并保存在ArrayList中。
            double distance= PointDistance(a,b);
            list.add(distance);
        }
        Collections.sort(list);
        if ((list.get(0)/list.get(1)<0.4)){
            count++;
        }
    }

    if (count>10)
        return true;
    else
        return false;

}
//      获取相似集
    public static boolean SiftGenerat(String sourcePath,String fileName ){
        boolean isSimiliar=false;
        int count=0;
      List<MyPoint>sourcePoints= getCharacterVectors(sourcePath);
      System.out.println("souce矩阵");
        for (int k=0;k<sourcePoints.size();k++){
            MyPoint myPoint=sourcePoints.get(k);
            for (int x=0;x<myPoint.getGrads().length;x++){
                System.out.print(" "+myPoint.getGrads()[x]);
            }
            System.out.println();
        }
      List<MyPoint>filePoint=getCharacterVectors(fileName);

      for (int i=0;i<sourcePoints.size();i++){
//          获取第一个点
          MyPoint mp=new MyPoint();
          mp=sourcePoints.get(i);
//          该点的特征向量
          double[]a= mp.getGrads();
          ArrayList<Double>list=new ArrayList<>();
          for (int j=0;j<filePoint.size();j++){
              MyPoint mp1=new MyPoint();
              mp1=filePoint.get(j);
              double []b=mp1.getGrads();
//              计算欧式距离，并保存在ArrayList中。
              double distance= PointDistance(a,b);
              list.add(distance);
          }
          Collections.sort(list);
          if ((list.get(0)*1.0/list.get(1)<0.4)){
              count++;
          }
      }

      if (count>10)
          return true;
      else
          return false;

    }
    private static double PointDistance(double[] a, double[] b) {
        double distance=0;
        for (int i=0,j=0;i<a.length&&j<b.length;i++,j++){
                distance+=Math.pow(a[i]-b[j],2);
        }
        return distance;
    }

    //    获取相似集
    public  ArrayList<String> isSimilar(int count,String sourcePath) {

        ArrayList<String >list=new ArrayList<>();
        for (int i=0;i<count;i++){

            try {
               boolean siftGenerat = SiftGenerat(sourcePath,fileName+(i + 1) + ".jpg");
               if (siftGenerat){
                   list.add("images/"+(i+1)+".jpg");
               }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return list;
    }
    /**
     * @ Author     ：CrazyCake
     * @ Date       ：Created in 19:35 2019/6/3
     * @ Description：
     * @ Return     ：
     */
    private static List<MyPoint> getFeatureVector(double[][] image,List<MyPoint> keyPoints,int nLayer,double baseSigma,int largestDistance){
        List<MyPoint> vectorList=new ArrayList<MyPoint>();///保存特征向量的list
        //int keyR=0.6*Math.pow(2, i+(double)j/gaussS);
        if(keyPoints.isEmpty()){
            return null;
        }
        int s=keyPoints.get(0).getS();
        int octave=keyPoints.get(0).getOctave();


        ///int n=0;///n用来记录特征点数
        for(MyPoint mp:keyPoints){
            int x=mp.getX();
            int y=mp.getY();
            int width=image[0].length;
            int height=image.length;
            double y2=image[y+1][x];
            double y1=image[y-1][x];
            double x2=image[y][x+1];
            double x1=image[y][x-1];

            //关键点梯度模值
//			double m=Math.sqrt((y2-y1)*(y2-y1)+(x2-x1)*(x2-x1));
            //关键点梯度方向,转为成0~360°之间的角度值
            double theta=Math.atan2(y2-y1, x2-x1)*(180/Math.PI)+180;

            //统计该特征点一定范围内的36个方向的模值和角度分布情况
            double[] keyTM=new double[45];
            double[] keyAngle=new double[45];
            double[]angleRatio=new double[45];////该变量用来记录主方向范围内（如0~10）的所有点的高斯核的比例总数，用keyAngle除以对应的比例就得到该主方向的高斯加权方向


            double max=0;///记录最大模值
            int index = 0;



            double s_oct=baseSigma*Math.pow(2, (double)mp.getS()/nLayer);
            double sigma=1.5*s_oct;////高斯模糊核
            double[][] gtemplate=getGaussTemplate(sigma);////用于对模值进行高斯加权
            int radius=(int) (3*sigma);///领域采样半径
            int diameter=2*radius;

            int gtCenter=gtemplate.length/2;
            if(x>=diameter&&x<width-diameter&&y>=diameter&&y<height-diameter){
                ///sigma=9
                for(int j=0;j<=2*radius;j++ ){
                    for(int i=0;i<=2*radius;i++){
						/*if((j==radius)&&(i==radius)){
							continue;
						}*/
                        double ty2=image[y-radius+j+1][x-radius+i];
                        double ty1=image[y-radius+j-1][x-radius+i];
                        double tx2=image[y-radius+j][x-radius+i+1];
                        double tx1=image[y-radius+j][x-radius+i-1];
                        //关梯度模值
                        double tM=Math.sqrt((ty2-ty1)*(ty2-ty1)+(tx2-tx1)*(tx2-tx1));
                        //梯度方向，转为成0~360°之间的角度值
                        double tTheta=Math.atan2(ty2-ty1, tx2-tx1)*(180/Math.PI)+180;
                        int section=(int) (tTheta/9);
                        if(360-Math.abs(tTheta)<0.0001){
                            ///如果角度为360°，则和零一样算在第一个一区间内
                            section=0;
                        }
                        keyTM[section]=keyTM[section]+tM*gtemplate[gtCenter-radius+j][gtCenter-radius+i];
                        keyAngle[section]=keyAngle[section]+tTheta*gtemplate[gtCenter-radius+j][gtCenter-radius+i];////按比例对主方向产生角度贡献;
                        angleRatio[section]+=gtemplate[gtCenter-radius+j][gtCenter-radius+i];
                    }
                }

                for(int key=0;key<keyTM.length;key++){
                    if(keyTM[key]>max){
                        max=keyTM[key];
                        index=key;
                    }
                }
                theta=keyAngle[index]/angleRatio[index];
            }






            ///对关键如果有多个辅方向，就复制成多个关键点
            for(int key=0;key<keyTM.length;key++){
                if(keyTM[key]>max*0.8){
                    ///大于最大值得80%都作为主方向之一，复制成一个关键点
                    theta=keyAngle[key]/angleRatio[key];////获得辅方向
                    //	System.out.println("theta:"+theta);

                    ///计算每个代数圆内的梯度方向分布
                    if(x>=largestDistance+1&&x<width-1-largestDistance&&y>=largestDistance+1&&y<height-1-largestDistance){

                        int secNum=15;//每个代数圆内多少扇形
                        int secAngle=360/secNum;///多大的角为一个扇形
                        double[] grads=new double[secNum*(largestDistance/2)];///保存多维向量的数组

                        double sum=0;
                        for(int j=y-largestDistance;j<=y+largestDistance;j++){
                            for(int i=x-largestDistance;i<=x+largestDistance;i++){

                                if((j==y)&&(i==x)){
                                    continue;
                                }

                                double ty2=image[j+1][i];
                                double ty1=image[j-1][i];
                                double tx2=image[j][i+1];
                                double tx1=image[j][i-1];

                                //tx1=tx1*Math.cos(theta)-txy1
                                //梯度模值
                                double tM=Math.sqrt((ty2-ty1)*(ty2-ty1)+(tx2-tx1)*(tx2-tx1));
                                sum=sum+tM;///；累加模值，便于后面归一化
                                //梯度方向，转为成0~360°之间的角度值
                                double tTheta=Math.atan2(ty2-ty1, tx2-tx1)*(180/Math.PI)+180;
                                ///减去关键点的方向，取得相对方向！！ 此处要不要取绝对值？？或者360-Math.abs(tTheta-theta)等？？
                                double absTheta=tTheta-theta;
                                ///因为得到的theta的结果在-pi到+pi之间

                                int section=(int) (absTheta/secAngle);
                                if(360-Math.abs(absTheta)<0.0001){
                                    ///如果角度为360°，则和零一样算在第一个一区间内
                                    section=0;
                                }
                                if(section<0){
                                    ///如果角度为负，应该加一个secNUm	转为正数
                                    section=section+secNum;
                                }
                                ///计算棋盘距离
                                int distance=Math.max(Math.abs(y-j),Math.abs(x-i));

                                if(distance<=2){
                                    ///如果在距离2的棋盘距离内
                                    ///梯度模值累加！！
                                    grads[section]=grads[section]+tM;
                                }else if(distance<=4){
                                    ///
                                    ///梯度模值累加！！
                                    grads[section+1*secNum]=grads[section+1*secNum]+tM;
                                }else if(distance<=6){
                                    ///梯度模值累加！！
                                    grads[section+2*secNum]=grads[section+2*secNum]+tM;
                                }else if(distance<=8){
                                    ///梯度模值累加！！
                                    grads[section+3*secNum]=grads[section+3*secNum]+tM;
                                }else if(distance<=10){
                                    ///梯度模值累加！！
                                    grads[section+4*secNum]=grads[section+4*secNum]+tM;
                                }
                            }
                        }////
                        ///归一化向量
                        grads=normalize(grads, sum);
                        ///存储到mypoint对象里
                        MyPoint rmp=new MyPoint(x, y, s, octave, theta, grads,false);
                        vectorList.add(rmp);
                    }
                }}
        }

        return vectorList;
    }

    private static double[][] getGaussTemplate(double sigma){
        //sigma=1.6;
        int width,height;
        width=(int) (6*sigma+1);
        if(width<6*sigma+1){
            ///6*sigma+1结果为小数，需要对矩阵维度加一
            width++;
        }
        height=width;

        double[][] template=new double[height][width];
        double sum=0.0;///用于归一化高斯模板

        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                double value;
                double index;//自然底数e的指数
                index=(i-height/2)*(i-height/2)+(j-width/2)*(j-width/2);
                index=-index/(2*sigma*sigma);
                value=(1/(2*sigma*Math.PI))*(Math.pow(Math.E, index));
                template[i][j]=value;//赋值给模板对应位置
                sum=sum+value;
            }
        }
        ///归一化模板
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                template[i][j]=template[i][j]/sum;//赋值给模板对应位置
            }
        }
        return template;
    }
//    归一化处理
    public static double[] normalize(double[] source,double sum){
        double[] result=new double[source.length];
        for(int i=0;i<source.length;i++){
            result[i]=source[i]/sum;
            if(result[i]<0.0001){
                result[i]=0;
            }
        }
        return result;
    }

}
