package com.example.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageButton;
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
    public static String fileName1 = "recorded.3gp";
    String fileName = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + fileName1;
    private boolean isRecording=false;
    private static ImageButton btnStop;
    private static ImageButton btnRecord;
    private static ImageButton btnPlay;
    File file;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator);
//        if (!dir.exists())
//            dir.mkdirs();
        btnStop =  findViewById(R.id.btnStop);
        btnRecord =  findViewById(R.id.btnRecord);
        btnPlay =  findViewById(R.id.btnPlay);

        btnPlay.setEnabled(false);
        btnStop.setEnabled(false);


        if (checkedPermissionsFromDevice()){
            textView = findViewById(R.id.textView);

            file = new File(fileName);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            mediaRecorder = new MediaRecorder();


        }else{
            requestPermission();
        }


    }


    private void prepareMediaRecorder(MediaRecorder mediaRecorder, File file){
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

        if(Build.VERSION.SDK_INT < 26) {
            mediaRecorder.setOutputFile(file.getAbsolutePath());
        }else {
            mediaRecorder.setOutputFile(file);
        }

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        btnRecord.setEnabled(false);
        btnStop.setEnabled(true);
        btnPlay.setEnabled(false);

        if(!isRecording) {
//            try {
//
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            prepareMediaRecorder(mediaRecorder,file);
            mediaRecorder.start();

            textView.setText("Audio recording .....");
            isRecording=true;
        }
    }

    private void stopAudio() {
        btnRecord.setEnabled(true);
        btnStop.setEnabled(false);
        btnPlay.setEnabled(true);

        if(isRecording){
            textView.setText("Recording Stopped ...");
            mediaRecorder.stop();
//            mediaRecorder.release();
            isRecording =false;
        }
    }

    private void play() {
//        btnRecord.setEnabled(true);
//        btnStop.setEnabled(false);
//        btnPlay.setEnabled(true);

        textView.setText("Playing recorded audio ...");
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(fileName);
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



///////////////////////////////////////////PERMISSIONS/////////////////////////////////////////////////////////////////////////////////
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





}