package com.dex.ocv;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Scalar;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    static boolean isRecording = false;


    TextView textView, textView1, textView2, textView3, textView4, textView5, textView6;

    SeekBar hMin,hMax,sMin,sMax,vMin,vMax;

    CameraViewHolder cameraViewHolder;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    Button recButton;

    CSVFileWriter csvWriter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        OpenCVLoader.initDebug();

        csvWriter = new CSVFileWriter();

        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        setContentView(R.layout.activity_main);

        recButton = findViewById(R.id.button);
        recButton.setOnClickListener(this);

        textView = findViewById(R.id.textView);

        hMin = findViewById(R.id.seek0);
        hMax = findViewById(R.id.seek1);

        sMin = findViewById(R.id.seek2);
        sMax = findViewById(R.id.seek3);

        vMin = findViewById(R.id.seek4);
        vMax = findViewById(R.id.seek5);

        hMin.setOnSeekBarChangeListener(this);
        hMax.setOnSeekBarChangeListener(this);

        sMin.setOnSeekBarChangeListener(this);
        sMax.setOnSeekBarChangeListener(this);

        vMin.setOnSeekBarChangeListener(this);
        vMax.setOnSeekBarChangeListener(this);

        textView1 = findViewById(R.id.textView2);
        textView2 = findViewById(R.id.textView3);
        textView3 = findViewById(R.id.textView4);
        textView4 = findViewById(R.id.textView5);
        textView5 = findViewById(R.id.textView6);
        textView6 = findViewById(R.id.textView7);

        cameraViewHolder = new CameraViewHolder((JavaCameraView) findViewById(R.id.cameraView), this, csvWriter);

        loadSettings();
        setLabelValues();
    }


    @Override
    protected void onPause() {
        super.onPause();
        saveSettings();
        cameraViewHolder.disableView();
        csvWriter.stop();
        recButton.setText("Start Rec.");
        isRecording = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSettings();
        cameraViewHolder.enableView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraViewHolder.disableView();
    }

    protected void setText(final String value){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(value);
            }
        });
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        int hMinValue = hMin.getProgress();
        int hMaxValue = hMax.getProgress();
        int sMinValue = sMin.getProgress();
        int sMaxValue = sMax.getProgress();
        int vMinValue = vMin.getProgress();
        int vMaxValue = vMax.getProgress();

        Scalar sLow = new Scalar(hMinValue, sMinValue, vMinValue);
        Scalar sHigh = new Scalar(hMaxValue, sMaxValue, vMaxValue);

        cameraViewHolder.setRange(sLow,sHigh);
        setLabelValues();

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void setLabelValues(){
        textView1.setText("hMin: " + hMin.getProgress());
        textView2.setText("hMax: " + hMax.getProgress());
        textView3.setText("sMin: " + sMin.getProgress());
        textView4.setText("sMin: " + sMax.getProgress());
        textView5.setText("vMin: " + vMin.getProgress());
        textView6.setText("vMin: " + vMax.getProgress());
    }

    private void loadSettings(){
        hMin.setProgress(sharedPref.getInt("hMin",0));
        hMax.setProgress(sharedPref.getInt("hMax",30));

        sMin.setProgress(sharedPref.getInt("sMin",0));
        sMax.setProgress(sharedPref.getInt("sMax",30));

        vMin.setProgress(sharedPref.getInt("vMin",0));
        vMax.setProgress(sharedPref.getInt("vMax",30));
    }

    private void saveSettings(){
        editor.putInt("hMin",hMin.getProgress());
        editor.putInt("hMax",hMax.getProgress());

        editor.putInt("sMin",sMin.getProgress());
        editor.putInt("sMax",sMax.getProgress());

        editor.putInt("vMin",vMin.getProgress());
        editor.putInt("vMax",vMax.getProgress());

        editor.commit();

    }

    @Override
    public void onClick(View view) {
        isRecording = !isRecording;
        if(isRecording){
            recButton.setText("Stop Rec.");
            Toast.makeText(this, "Started Record", Toast.LENGTH_SHORT).show();
            csvWriter.start();
        } else {
            recButton.setText("Start Rec.");
            Toast.makeText(this, "Stopped Record", Toast.LENGTH_SHORT).show();
            csvWriter.stop();
        }
    }
}
