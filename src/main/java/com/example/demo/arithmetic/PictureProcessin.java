package com.example.demo.arithmetic;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.*;

public class PictureProcessin {
    public static final String path="C:/Users/CrazyCake/Pictures/testPic";

    public static BufferedImage thumb(BufferedImage source,int width,int height,boolean b){
//        targetW,targerH分别表示目标长和宽
        int type=source.getType();
        BufferedImage target=null;
        double sx=(double)width/source.getWidth();
        double sy=(double)height/source.getHeight();
        if(b){
            if(sx>sy){
                sx=sy;
                width=(int)(sx*source.getWidth());
            }else{
                sy=sx;
                height=(int)(sy*source.getHeight());
            }
        }
//     BufferedImage.TYPE_CUSTOM没有识别出图形类型，因此它必定是一个自定义图形
        if(type==BufferedImage.TYPE_CUSTOM){
//            colorModel存储图形颜色数据
            ColorModel cm=source.getColorModel();
            WritableRaster raster=cm.createCompatibleWritableRaster(width,height);
            boolean alphaPremultiplied=cm.isAlphaPremultiplied();
            target=new BufferedImage(cm,raster,alphaPremultiplied,null);
        }else
            target=new BufferedImage(width,height,type);
            Graphics2D g=target.createGraphics();
//            smoother than exlax;
            g.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
            g.drawRenderedImage(source, AffineTransform.getScaleInstance(sx,sy));
            g.dispose();
            return target;
    }
//    灰度值计算
    public static int rgbToGray(int pixels){
//        int _alpha=(pixels>>24)&0xFF;
        int _red=(pixels>>16)&0xFF;
        int _green=(pixels>>8)&0xFF;
        int _blue=(pixels)&0xFF;
        return (int)(0.3*_red+0.59*_green+0.11*_blue);
    }
    /**
     * 读取JPEG图片
     * @param filename 文件名
     * @return BufferedImage 图片对象
     */
    public static BufferedImage readPNGImage(String filename)
    {
        try {
            File inputFile = new File(filename);
            BufferedImage sourceImage = ImageIO.read(inputFile);
            return sourceImage;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
//    计算数组的平均值
    public static int average(int[]piexls){
        float m=0;
        for (int i=0;i<piexls.length;++i){
            m+=piexls[i];
        }
        m=m/piexls.length;
        return (int)m;
    }

}
