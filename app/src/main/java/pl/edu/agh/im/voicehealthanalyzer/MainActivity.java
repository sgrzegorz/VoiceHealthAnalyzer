package pl.edu.agh.im.voicehealthanalyzer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private WavAudioRecorder recorder;

    private static final String fileNameBase = "recording.wav";
    private final String audioFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + fileNameBase;

    private boolean isRecording = false;

    private ImageButton btnStop;
    private ImageButton btnRecord;
    private ImageButton btnPlay;

    File file;

    private static final String[] permissions = new String[]{
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @RequiresApi(30)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStop = findViewById(R.id.btnStop);
        btnRecord = findViewById(R.id.btnRecord);
        btnPlay = findViewById(R.id.btnPlay);

        btnPlay.setEnabled(false);
        btnStop.setEnabled(false);

        if (hasNecessaryPermissions()) {
            file = new File(audioFileName);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            recorder = WavAudioRecorder.getInstance();
        } else {
            requestPermission();
        }
    }

    public void onClick(View v) {
        if (v.getId() == R.id.btnRecord) {
            record();
        } else if (v.getId() == R.id.btnStop) {
            stopAudio();
        } else if (v.getId() == R.id.btnPlay) {
            play();
        }
    }

    private void record() {
        btnRecord.setEnabled(false);
        btnStop.setEnabled(true);
        btnPlay.setEnabled(false);

        if (!isRecording) {
            recorder.setOutputFile(audioFileName);
            recorder.prepare();
            recorder.start();

            Toast.makeText(this, "Audio recording...", Toast.LENGTH_SHORT).show();
            isRecording = true;
        }
    }

    private void stopAudio() {
        btnRecord.setEnabled(true);
        btnStop.setEnabled(false);
        btnPlay.setEnabled(true);

        if (isRecording) {
            recorder.stop();
            recorder.reset();
            Toast.makeText(this, "Recording stopped...", Toast.LENGTH_SHORT).show();
            isRecording = false;
        }
    }

    private void play() {
        Toast.makeText(this, "Playing recorded audio...", Toast.LENGTH_SHORT).show();
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(audioFileName);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //////////////////////////////////////////PERMISSIONS//////////////////////////////////////////
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, permissions, 1000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissions Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permissions Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean hasNecessaryPermissions() {
        return Arrays.stream(permissions).allMatch(perm ->
                ContextCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (recorder != null) {
            recorder.release();
        }
    }
}