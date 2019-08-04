package com.dex.ocv;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoWriter;

import java.util.ArrayList;
import java.util.List;

public class CameraViewHolder implements CameraBridgeViewBase.CvCameraViewListener2 {


    JavaCameraView javaCamera2View;

    Mat matOld, matNew, matNew2;
    List<MatOfPoint> contours = new ArrayList<>();


    Scalar scalarLow, scalarHigh;
    MainActivity activity;
    double hMin,hMax,sMin,sMax,vMin,vMax = 0;



    int width;
    int height;

    Scalar color = new Scalar(0,255,0);

    CSVFileWriter csvFileWriter;

    public CameraViewHolder(JavaCameraView javaCamera2View, MainActivity activity, CSVFileWriter csvFileWriter) {

        this.csvFileWriter = csvFileWriter;
        this.activity = activity;
        this.javaCamera2View = javaCamera2View;
        this.javaCamera2View.setCameraIndex(0);
        this.javaCamera2View.setCvCameraViewListener(this);


        enableView();


        scalarLow = new Scalar(45, 20, 50);
        scalarHigh = new Scalar(130, 200, 160);
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        matOld = new Mat(width, height, CvType.CV_16UC4);
        matNew = new Mat(width, height, CvType.CV_16UC4);
        this.width = width;
        this.height = height;
    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Imgproc.cvtColor(inputFrame.rgba(), matOld, Imgproc.COLOR_RGB2HSV);
        //matNew2 = new Mat(width, height, CvType.CV_16UC4);
        Mat matOrg = inputFrame.rgba();
        Core.inRange(matOld, scalarLow, scalarHigh, matNew);
        int value = Core.countNonZero(matNew);
        int size = matNew.rows() * matNew.cols();
        contours.clear();

        Imgproc.findContours(matNew, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);


        Double sumOfArea = 0.;
        for(MatOfPoint mop : contours){
            double area = Imgproc.contourArea(mop);
            if (area > 20){
                double xxx  = 0.001 * Imgproc.arcLength(new MatOfPoint2f(mop.toArray()), true);
                MatOfPoint2f approx = new MatOfPoint2f();
                Imgproc.approxPolyDP(new MatOfPoint2f(mop.toArray()), approx ,xxx, true);
                MatOfPoint m4 = new MatOfPoint();
                approx.convertTo(m4,CvType.CV_32S);
                List<MatOfPoint> list = new ArrayList<>();
                list.add(m4);
                sumOfArea += area;
                Imgproc.drawContours(matOrg, list , 0, color, 3);
            }
        }



        float ratio = (float) value / size;
        activity.setText(size + "\n"
                + value + "\n"
                + matNew.rows() + " x " + matNew.cols()+ "\n"
                +String.format("%.4g%n", ratio) + "\n"
                + sumOfArea );
        csvFileWriter.dump(size, value, ratio, sumOfArea);
        return matOrg;
    }

    public void enableView() {
        javaCamera2View.enableView();
    }

    public void disableView() {
        javaCamera2View.disableView();
    }

    public void setRange(Scalar scalarLow, Scalar scalarHigh){
       this.scalarLow = scalarLow;
       this.scalarHigh = scalarHigh;
    }
}
