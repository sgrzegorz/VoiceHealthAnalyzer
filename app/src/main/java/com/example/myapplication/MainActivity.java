package com.example.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    MediaRecorder mediaRecorder;
    public static String fileName = "recorded.3gp";
    String file = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + fileName;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator);
//        if (!dir.exists())
//            dir.mkdirs();
        if (checkedPermissionsFromDevice()){
            textView = findViewById(R.id.textView);
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
            File f = new File(file);
            if (!f.exists()) {
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            mediaRecorder.setOutputFile(file);
        }else{
            requestPermission();
        }


    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        },1000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case 1000:{
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private boolean checkedPermissionsFromDevice() {
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result =ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        int read_external_storage=ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return write_external_storage_result== PackageManager.PERMISSION_GRANTED && read_external_storage==PackageManager.PERMISSION_GRANTED && record_audio_result==PackageManager.PERMISSION_GRANTED;
    }

    public void onClick(View v) {
        if (v.getId() == R.id.btnRecord) {
            //Record
            record();
        } else if (v.getId() == R.id.btnStop) {
            //Stop
            stopAudio();
        } else if (v.getId() == R.id.btnPlay) {
            //play
            play();
        }
    }

    private void record() {

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        textView.setText("Audio recording .....");

    }

    private void stopAudio() {
        textView.setText("Recording Stopped ...");
        mediaRecorder.stop();
        mediaRecorder.release();

    }

    private void play() {
        textView.setText("Playing recorded audio ...");
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(file);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }




}