package com.example.cakeImage.arithmetic;

import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * @ Author     ：CrazyCake
 * @ Date       ：Created in 17:29 2019/5/27
 * @ Description：特征点类
 * @ Modified By：
 * @Version: 1.0$
 */
public class MyPoint {
    private int preX;
    private int preY;

    private int x;
    private int y;
    private int s;//高斯金字塔组内第s张
    private int octave;///处于第几组（第几层金字塔）
    private double theta;///关键点的方向

    private double[] grads;//特征向量——————梯度分布统计

    private boolean isMatched;///是否匹配过了

    /**
     * 构造函数
     * @param x
     * @param y
     * @param s
     * @param octave
     * @param theta
     * @param grads
     */
    public MyPoint(int x,int y, int s,int octave,double theta,double[] grads,boolean isMatch) {
        this.x=x;
        this.y=y;
        this.s=s;
        this.octave=octave;
        this.grads=grads;
        this.theta=theta;
        this.isMatched=isMatch;
        // TODO Auto-generated constructor stub
    }

    ///默认构造函数
    public MyPoint() {
        // TODO Auto-generated constructor stub
    }




    public double[] getGrads() {
        return grads;
    }
    public void setGrads(double[] grads) {
        this.grads = grads;
    }

    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }
    public int getOctave() {
        return octave;
    }
    public void setOctave(int octave) {
        this.octave = octave;
    }
    public int getS() {
        return s;
    }
    public void setS(int s) {
        this.s = s;
    }




    public double getTheta() {
        return theta;
    }




    public void setTheta(double theta) {
        this.theta = theta;
    }

    public boolean isMatched() {
        return isMatched;
    }

    public void setMatched(boolean isMatched) {
        this.isMatched = isMatched;
    }

    public int getPreX() {
        return preX;
    }

    public void setPreX(int preX) {
        this.preX = preX;
    }

    public int getPreY() {
        return preY;
    }

    public void setPreY(int preY) {
        this.preY = preY;
    }
}
