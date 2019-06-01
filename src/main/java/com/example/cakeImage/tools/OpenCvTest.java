package com.example.cakeImage.tools;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import org.opencv.imgproc.Imgproc;

/**
 * @ Author     ：CrazyCake
 * @ Date       ：Created in 22:20 2019/5/31
 * @ Description：1.0
 * @ Modified By：
 * @Version: $
 */
public class OpenCvTest {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        System.out.println("Welcome to OpenCV " + Core.VERSION);
 	System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	Mat m = Mat.eye(3, 3, CvType.CV_8UC1);
 	System.out.println("m = " + m.dump());
    }

    }

