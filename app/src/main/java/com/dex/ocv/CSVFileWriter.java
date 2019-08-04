package com.dex.ocv;

import com.opencsv.CSVWriter;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.videoio.VideoWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CSVFileWriter {

    File file = null;
    CSVWriter writer = null;
    boolean isRecording = false;

    String folder = "HDM_VALUES";
    String[] head = {"time", "max", "current", "per", "area"};
    String[] firstLine = {"sep=,"};

    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.GERMANY);
    DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss", Locale.GERMANY);

    String[] csvLine = new String[5];

    VideoWriter vw = null;

    public CSVFileWriter() {
    }

    public void start(){
        checkAndCreateDir();
        String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        String currentTime = df2.format(new Date());
        String filePath = baseDir + File.separator + folder + File.separator +currentTime + ".csv";
        this.file =  new File(filePath);
        try {
            this.writer = new CSVWriter(new FileWriter(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        writer.writeNext(firstLine);
        writer.writeNext(head);
        isRecording = true;
    }

    public void dump(int size, int value, float ratio, double area){
        if(isRecording){
            csvLine[0] =  df.format(new Date());
            csvLine[1] = String.valueOf(size);
            csvLine[2] = String.valueOf(value);
            csvLine[3] = String.format("%.4g", ratio);
            csvLine[4] = String.format("%.4g", area);
            this.writer.writeNext(csvLine);
        }
    }

    public void stop(){
        isRecording = false;
        try {
            writer.close();
            vw.release();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkAndCreateDir(){
        String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        String filePath = baseDir + File.separator + folder;
        File dir = new File(filePath);
        if(!dir.exists())
            dir.mkdir();
    }
}
