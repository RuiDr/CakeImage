package com.example.cakeImage.arithmetic;
import com.example.cakeImage.tools.Utility;
import org.opencv.core.*;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.FeatureDetector;
import org.opencv.highgui.Highgui;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
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


    public static void main(String[] args) {

//        String str="G:/java/webprojects/CakeImage/src/main/resources/static/images/1.jpg";
//       boolean a= SiftGenerat(str,"G:\\java\\webprojects\\CakeImage\\src\\main\\resources\\static\\images\\12.jpg");
//       System.out.println("isMatched"+a);


        BufferedImage bufferedImage=null;
        try {
            bufferedImage= ImageIO.read(new File("G:\\java\\1.jpg"));
            //            灰度变换
            bufferedImage=ImageToGray(bufferedImage);
//            image转换成二维数组
            double [][]source=BufferedImageToDouble(bufferedImage);
//            模糊后的高斯显示
            HashMap<Integer,double[][]>result=getGaussPyramid(source,0,3,1.6);

//            获取高斯差分金字塔
            HashMap<Integer,double[][]> dog=gaussToDog(result,6);

//            获得极值点
            HashMap<Integer, List<MyPoint>>keyPoints= getRoughKeyPoint(dog,6);
//获得精确的极值点
            keyPoints=  filterPoints(dog, keyPoints, 10,0.03);
            Iterator iter1 = keyPoints.entrySet().iterator();
            ArrayList<ArrayList<Double>>lastlist=new ArrayList<>();
            while (iter1.hasNext()) {
                Map.Entry entry = (Map.Entry) iter1.next();
                Object key = entry.getKey();
                List<MyPoint> value = (List<MyPoint>)entry.getValue();
                System.out.println("value Size is "+value.size());
                System.out.println("key is "+key + ": value is :"  );
                for(int i=0;i<value.size();i++) {
//                    获取关键点的高斯金字塔
                    double[][]a=getGauss(result,value.get(i));
                    ArrayList<Double> list=getFeatureVector(a,value.get(i),value.get(i).getTheta());
                    lastlist.add(list);
//                    获取多个方向
                    value.get(i).setGrads(list);
//                    获得描述子
                    HashMap<Integer,double[]> lastList= GetGescriptor(result,value.get(i),6,4);

                }
            }
//        获得描述子
            for (int i=0;i<lastlist.size();i++){
                System.out.println(i+"  "+lastlist.get(i).toString());
            }
            writeImageFile(bufferedImage);
            System.out.println(bufferedImage);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

//    是否匹配
    public  boolean SiftGenerat(String image,String image1){
//        获取特征向量
        Mat mat1=getMat(image);
        Mat mat=getMat(image1);
        System.out.println("m = " + mat.dump());
//
        Highgui.imwrite("G:\\java\\webprojects\\CakeImage\\src\\main\\resources\\static\\images\\mat.png",mat);
        Mat test_mat = Highgui.imread("G:\\java\\webprojects\\CakeImage\\src\\main\\resources\\static\\images\\mat.png");
        Highgui.imwrite("G:\\java\\webprojects\\CakeImage\\src\\main\\resources\\static\\images\\mat1.png",test_mat);
        boolean isMatch=isMatched(mat1,mat);
        return isMatch;
    }
//    将两幅图像的描述子，进行欧氏距离计算
    public  boolean isMatched(Mat mat,Mat mat1){
        int count=0;
        for (int i=0;i<mat.height();i++){
            ArrayList<Double>list=new ArrayList<>();
//            获取某一行数据
            Mat mat2=mat.rowRange(i,i+1);
            for (int j=0;j<mat1.height();j++){
                Mat mat3=mat1.rowRange(j,j+1);
//                计算欧式距离
                double len=Distance(mat2,mat3);
//                System.out.println("len is "+len);
                list.add(len);
            }

//            对list进行排序
            Collections.sort(list);
//            System.out.println("list is "+list.size());
            double a1=list.get(0);
//            System.out.println("a1 "+a1);
            double a2=list.get(1);
//            System.out.println("a2 "+a2);


            if ((a1/a2)<0.6){
//                表示匹配,统计
                count++;
            }
        }
        if (count>3){
            return true;
        }else
        {
            return false;
        }
////        获取长度
//        int aWidth=mat.width();
//        int aHeight=mat.height();
////        获取长度
//        int bWidth=mat.width();
//        int bHeight=mat.height();
//
////        为了方便计算，先将其转换成数组，方便计算
//        int[][]a=new int[aHeight][aWidth];
//
//        for (int i=0;i<mat.height();i++){
//            for (int j=0;j<mat.width();j++){
//                double []value=mat.get(i,j);
//                a[i][j]=(int) value[0];
//            }
//        }
//
//        int [][]b=new int[bHeight][bWidth];
//
//        for (int i=0;i<mat1.height();i++){
//            for (int j=0;j<mat1.width();j++){
//                double []value=mat1.get(i,j);
//                b[i][j]=(int) value[0];
//            }
//        }
//
////        计算欧式距离
//        for (int i=0;i<a.length;i++){
////            一个关键点的特征向量
//            for (int j=0,k=0;j<a[0].length;j++){
//
//            }
//        }


//

//        将

    }
    private static double Distance(Mat mat2, Mat mat3) {
        double distance=0;
        for (int i=0,j=0;i<mat2.width()&&j<mat3.width();i++,j++){
            distance+=Math.pow(mat2.get(0,i)[0]-mat3.get(0,j)[0],2);
        }
        return distance;
    }

    public static  MatOfKeyPoint getFeaturePoints(Mat mat){
        FeatureDetector fd = FeatureDetector.create(FeatureDetector.SIFT);
        MatOfKeyPoint mkp =new MatOfKeyPoint();
        fd.detect(mat, mkp);
        return mkp;
    }

    public static Mat getFeature(Mat mat){
        Mat desc = new Mat();
        MatOfKeyPoint mkp = getFeaturePoints(mat);
        DescriptorExtractor de = DescriptorExtractor.create(DescriptorExtractor.SIFT);
        de.compute(mat,mkp,desc );//提取sift特征
        return desc;
    }

    public  Mat BufImg2Mat (BufferedImage original, int imgType, int matType) {
        if (original == null) {
            throw new IllegalArgumentException("original == null");
        }

        // Don't convert if it already has correct type
        if (original.getType() != imgType) {

            // Create a buffered image
            BufferedImage image = new BufferedImage(original.getWidth(), original.getHeight(), imgType);

            // Draw the image onto the new buffer
            Graphics2D g = image.createGraphics();
            try {
                g.setComposite(AlphaComposite.Src);
                g.drawImage(original, 0, 0, null);
            } finally {
                g.dispose();
            }
        }

        byte[] pixels = ((DataBufferByte) original.getRaster().getDataBuffer()).getData();
        Mat mat = Mat.eye(original.getHeight(), original.getWidth(), matType);
        mat.put(0, 0, pixels);
        return mat;
    }

//    获取矩阵
    public  Mat  getMat(String string) {
//        string="G:\\java\\webprojects\\CakeImage\\src\\main\\resources\\static\\images\\12.jpg";
//        System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
//        try {
//            System.out.println("地址："+string);
//            Mat test_mat  =  BufferedImageMat(ImageIO.read(new File(string)));
////            Mat test_mat = Highgui.imread(string);
//            Mat desc = new Mat();
//            FeatureDetector fd = FeatureDetector.create(FeatureDetector.SIFT);
//            MatOfKeyPoint mkp =new MatOfKeyPoint();
//            fd.detect(test_mat, mkp);
//            DescriptorExtractor de = DescriptorExtractor.create(DescriptorExtractor.SIFT);
//            de.compute(test_mat,mkp,desc );//提取sift特征
//            System.out.println(desc.cols());
//            System.out.println(desc.rows());
//            return desc;
//        }catch (Exception e){
//            e.printStackTrace();
//        }


        BufferedImage bufferedImage=null;
        try {
            bufferedImage= ImageIO.read(new File("G:\\java\\1.jpg"));
            //            灰度变换
            bufferedImage=ImageToGray(bufferedImage);
//            image转换成二维数组
            double [][]source=BufferedImageToDouble(bufferedImage);
//            模糊后的高斯显示
            HashMap<Integer,double[][]>result=getGaussPyramid(source,0,3,1.6);
//            获取高斯差分金字塔
            HashMap<Integer,double[][]> dog=gaussToDog(result,6);

//            Iterator iter = dog.entrySet().iterator();
//            while (iter.hasNext()) {
//                Map.Entry entry = (Map.Entry) iter.next();
//                Object key = entry.getKey();
//                double[][] value = (double[][])entry.getValue();
//                System.out.println("key is "+key + ": value is :" );
//                for(int i=0;i<value.length;i++) {
//                    for(int j=0;j<value[0].length;j++) {
//                        System.out.print(" "+value[i][j]);
//                    }
//                    System.out.println();
//                }
//
//            }
            HashMap<Integer, List<MyPoint>>keyPoints= getRoughKeyPoint(dog,6);

          keyPoints=  filterPoints(dog, keyPoints, 10,0.03);
            Iterator iter = keyPoints.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                Object key = entry.getKey();
                List<MyPoint> value = (List<MyPoint>)entry.getValue();


                System.out.println("value Size is "+value.size());
                System.out.println("key is "+key + ": value is :"  );
                for(int i=0;i<value.size();i++) {
//                    获取关键点的高斯金字塔
                    double[][]a=getGauss(result,value.get(i));
                    ArrayList<Double> list=getFeatureVector(a,value.get(i),value.get(i).getTheta());
                    value.get(i).setGrads(list);
                    HashMap<Integer,double[]> lastList= GetGescriptor(result,value.get(i),6,4);
                    value.get(i).setList(lastList);

                    System.out.println("getTheTa is "+value.get(i).getTheta());
                    System.out.println("Octave is "+value.get(i).getOctave());
                    System.out.println("X is "+value.get(i).getX());
                    System.out.println("Y is "+value.get(i).getY());
                    System.out.println("list is "+value.get(i).getGrads());

                    System.out.println("list is "+value.get(i).getList());
                }

            }
//        获得描述子
            writeImageFile(bufferedImage);
            System.out.println(bufferedImage);

        }catch (Exception e){
            e.printStackTrace();
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
    public static HashMap<Integer, double[][]> getGaussPyramid(double[][] source, int minSize, int n, double baseSigma) {
        //        存放结果
        HashMap<Integer, double[][]> gaussPyramid = new HashMap<>();
//        临时存储
        double[][] tempSource = source;
        int width = source[0].length;
        int height = source.length;
//        求取最小值
        int small = width > height ? height : width;
//        求金字塔层数    根据公式
        int octave = (int) (Math.log(small) - 3);
//        每一组高斯图像数目为S=n+3,n取2，拥有特征值的图像张数
        int gaussS = n + 3;
//         获取原始矩阵

        double k = Math.pow(2, Math.sqrt(n));
////        使用高斯函数做卷积
//        for (int i = 0; i < octave; i++) {
//            int index = 0;
//            for (int j = 0; j < gaussS; j++) {
//                index = i * gaussS + j;
////                第一张不做模糊
//                if (j == 0) {
//                    gaussPyramid.put(index, tempSource);
//                    continue;
//                }
////                计算
//                Mat last = new Mat();
////                获得模糊后的矩阵
//                Mat mat = ArrayToMat(tempSource);
//                GaussianBlur(mat, last, new Size(11, 11), Math.pow(k, j) * baseSigma, 0, Core.BORDER_CONSTANT);
////                将高斯模糊后的值转换成double形式
//                double[][] a = MatToArray(last);
//                gaussPyramid.put(index, a);
//            }
////            降点采样，将前一组的倒数第三组作为后一组的开始
//            tempSource = getCapSimpleTmg(gaussPyramid.get(index - 2));
//        }
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
     * @ Description：初步检测DoG的极值点 这个hashmap的key不是连续的数字，而是在高斯金字塔的图像索引值
     * @ Param   num: 高斯金字塔 每层的图像数目
     * @ Return     ：HashMap<Integer,List<MyPoint>> integer是高斯金字塔内图像的索引
     */
    public static HashMap<Integer, List<MyPoint>> getRoughKeyPoint(HashMap<Integer, double[][]> dogPyramid, int num) {
        int n = 2;
        double T = 0.04;
        HashMap<Integer, List<MyPoint>> resultMap = new HashMap<>();
        Set<Integer> dogIndex = dogPyramid.keySet();
        for (int i : dogIndex) {
//            对于dog金字塔每组的第一张和最后一张图像不进行求极值处理，原因是无法在高斯金字塔的方向上做极值
            if (((i%num)!=0)&&((i%num)!=num-2)) {
                double[][] dogImage = dogPyramid.get(i);

                System.out.println("dogImage is "+dogImage.length+" 0:"+dogImage[0].length);
//                获取该层图空间位置上的下一层和上一层
                double[][] dogImgeDown = dogPyramid.get(i - 1);
                System.out.println("dogImgeDown is "+dogImgeDown.length+" 0:"+dogImgeDown[0].length);
                double[][] dogImageUp = dogPyramid.get(i + 1);

                System.out.println("dogImgeUp is "+dogImageUp.length+" 0:"+dogImageUp[0].length);

                if (!((dogImage.length==dogImageUp.length)&&(dogImage.length==dogImgeDown.length))){
                    continue;
                }

                List<MyPoint> mpList = new ArrayList<>();
                int width = dogImgeDown[0].length;
                int height = dogImgeDown.length;
//                对每张dog图像求极值点
                for (int y = 0; y < height-2; y++) {
                    for (int x = 0; x < width-2; x++) {
                        if ((x-1>0)&&((x+1)<width)&&((y-1)>0)&&((y+1)<height)) {
//                            图像的边缘点默认不是极值点
//                            比较上下尺度以及8领域的共26个点
//                            首先进行阈值化

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
                }///一张图遍历完毕
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
    public static ArrayList<Double> getFeatureVector(double[][] image, MyPoint point, double baseSigm) {
//           保存特征向量的list
        List<MyPoint> vectorList = new ArrayList<>();
//            获取关键点的x,y值

        double[] histogram = new double[36];
        int x = point.getX();
        int y = point.getY();
        double R = 1.5 * baseSigm;
        double i;
        double j;
        for (i = x - R; i < x + R; i++) {
            for (j = y + R; j > y - R; j--) {
                double y2 = image[y + 1][x];
                double y1 = image[y - 1][x];
                double x2 = image[y][x + 1];
                double x1 = image[y][x - 1];
//                    计算莫
                double m = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
//                    计算梯度方向,转换成360度
                double theta = Math.atan2((y2 - y1), (x2 - x1)) * (180 / Math.PI) + 180;
//                    统计各个方向的分布情况
                if (theta <= 10) {
                    histogram[0] += m;
                } else if (theta<=20) {
                    histogram[1] += m;
                } else if (theta<=30) {
                    histogram[2] += m;
                } else if (theta<=40) {
                    histogram[3] += m;
                } else if (theta<=50) {
                    histogram[4] += m;
                } else if ( theta<=60) {
                    histogram[5] += m;
                } else if (theta<=70) {
                    histogram[6] += m;
                } else if (theta<=80) {
                    histogram[7] += m;
                }else if (theta<=90) {
                    histogram[8] += m;
                } else if (theta<=100) {
                    histogram[9] += m;
                } else if (theta<=110) {
                    histogram[10] += m;
                } else if (theta<=120) {
                    histogram[11] += m;
                } else if ( theta<=130) {
                    histogram[12] += m;
                } else if (theta<=140) {
                    histogram[13] += m;
                } else if (theta<=150) {
                    histogram[14] += m;
                }else if (theta<=160) {
                    histogram[15] += m;
                } else if (theta<=170) {
                    histogram[16] += m;
                } else if (theta<=180) {
                    histogram[17] += m;
                } else if (theta<=190) {
                    histogram[18] += m;
                } else if ( theta<=200) {
                    histogram[19] += m;
                } else if (theta<=210) {
                    histogram[20] += m;
                } else if (theta<=220) {
                    histogram[21] += m;
                }else if (theta<=230) {
                    histogram[22] += m;
                } else if (theta<=240) {
                    histogram[23] += m;
                } else if (theta<=250) {
                    histogram[24] += m;
                } else if (theta<=260) {
                    histogram[25] += m;
                } else if ( theta<=270) {
                    histogram[26] += m;
                } else if (theta<=280) {
                    histogram[27] += m;
                } else if (theta<=290) {
                    histogram[28] += m;
                }else if (theta<=300) {
                    histogram[29] += m;
                } else if (theta<=310) {
                    histogram[30] += m;
                } else if (theta<=320) {
                    histogram[31] += m;
                } else if (theta<=330) {
                    histogram[32] += m;
                } else if ( theta<=340) {
                    histogram[33] += m;
                } else if (theta<=350) {
                    histogram[34] += m;
                } else if (theta<=360) {
                    histogram[35] += m;
                }

            }
        }
        ArrayList<Double> list = new ArrayList<Double>();

//            获取最大值，即主方向
        double[] map = MaxNum(histogram);

//            获得主方向
        double theta = map[1] * 10;
        list.add(theta);
//            判断是否有多个方向
        for (int k = 0; k < histogram.length; k++) {
            if (histogram[k] > map[0] * 0.8) {
                list.add(k * 10 * 1.0);
            }
        }

        return list;
    }
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
            if (pre != null && next != null && (y - 1) > 0 && (x - 1 > 0) && (y + 1 < height) && (x + 1 < width)) {
//               分别对三个分量求导的所构成的三行一列矩阵的参数矩阵
                double[] df_x = new double[3];
//                3*3的矩阵
                double[][] df_xx = new double[3][3];

                df_x[0] = (img[x + 1][y] - img[x - 1][y]) / 2;
                df_x[1] = (img[x][y + 1] - img[x][y - 1]) / 2;
                df_x[2] = (next[x][y] - pre[x][y]) / 2;

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
//                ？越界判断
                if ((y - 1) > 0 || (x - 1 > 0) || (y + 1 < height) || (x + 1 < width)) {
                    return null;
                }
            }
//            超出迭代次数，舍弃该点
            if (i >= MAX_STEP) {
                return null;
            }
//            kpoint.setPreX((int)(x+offX)*(1<<octave));
//            kpoint.setPreY((int)(y+offY)*(1<<octave));
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
     * @ Date       ：Created in 2:25 2019/5/30
     * @ Description：生成128个描述子
     * @ Return     ：
     */
    public static HashMap<Integer,double[]> GetGescriptor(HashMap<Integer, double[][]> gaussPyramid,MyPoint kpoints,int nLayer,int d){

//        装4*4*8的的描述子
        HashMap<Integer,double[]>hashMap=new HashMap<>();
        int index=kpoints.getOctave()*nLayer+kpoints.getS();
        double[][]gauss=gaussPyramid.get(index);

        double theta=kpoints.getTheta();
        int x=kpoints.getX();
        int y=kpoints.getY();
//        旋转关键点坐标
        x=(int)(Math.cos(theta)*x-Math.sin(theta)*y);
        y=(int)(Math.sin(theta)*x+Math.cos(theta)*y);

//        在论文中d取4
        double R=(3*theta*Math.sqrt(2)*(d+1)+1)/2;
        int x1=(int)(x-Math.sqrt(2)/2*R);
        int y1=(int)(y+Math.sqrt(2)/2*R);
        int x2=(int)(x+Math.sqrt(2)/2*R);
        int y2=(int)(y-Math.sqrt(2)/2*R);
        int key=0;
        double num=Math.sqrt(2)/4*R;
        while(key<16) {
            double a=x1+num;
            double b=y1+num;
            double[]drection=new double[8];
            for (int i = x1; i < a; i++) {
                for (int j = y1; j < b; j++) {
                        if(((i+1)<a&&(i-1)>=0&&(j+1)<b&&(j-1)>=0)){
                            double b2 = gauss[j + 1][i];
                            double b1 = gauss[j- 1][i];
                            double a2 = gauss[j][i + 1];
                            double a1 = gauss[j][i - 1];
//                    计算莫
                            double m = Math.sqrt((a2 - a1) * (a2 - a1) + (b2 - b1) * (b2 - b1));
//                    计算梯度方向,转换成360度
                            double tha = Math.atan2((b2 - b1), (a2 - a1)) * (180 / Math.PI) + 180;
                            if (tha<=45){
                                drection[0]+=m;
                            }else if (tha<=90){
                                drection[1]+=m;
                            }else if (tha<=135){
                                drection[2]+=m;
                            }else if (tha<=180){
                                drection[3]+=m;
                            }else if (tha<=225){
                                drection[4]+=m;
                            }else if (tha<=270){
                                drection[5]+=m;
                            }else if (tha<=315){
                                drection[6]+=m;
                            }else if (tha<=360){
                                drection[7]+=m;
                            }
                        }
                }
            }
            hashMap.put(key,drection);
            x1=(int)a;
            y1=(int)b;
            if (x1>=x2||y1>=y2){
                break;
            }
            key++;
        }
//        归一化处理
       return hashMap;
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
}
