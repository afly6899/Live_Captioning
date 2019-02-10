package com.google.cloud.android.speech;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.support.annotation.NonNull;
import android.widget.Toast;
import android.widget.Button;
import android.widget.TextView;
import android.media.MediaRecorder;
import android.media.MediaPlayer;
import java.io.IOException;
import android.support.v4.app.ActivityCompat;

// B/c I'm new to Java, most of the code on here is taken from:
// https://medium.com/@ssaurel/create-an-audio-recorder-for-android-94dc7874f3d
// the only changes are when I was doing app specific things and permission checking
public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {



    private Button play, stop, record;
    private MediaRecorder myAudioRecorder;
    private TextView to_print;
    private String output_file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Grab the actual button objects from the xml and link them to these variables?
        play = findViewById(R.id.play);
        stop = findViewById(R.id.stop);
        record = findViewById(R.id.record);
        to_print = findViewById(R.id.text_to_print);

        // For using this function to hold the resulting audio: https://stackoverflow.com/a/13767611
        output_file = getCacheDir() + "/recording.3gp";

        // disable the stop and play buttons cause there's nothing to stop nor play
        play.setEnabled(false);
        stop.setEnabled(false);



    }

    // Help with doing permission request for microphone: https://stackoverflow.com/a/39846797
    // and this: https://github.com/googlesamples/android-RuntimePermissionsBasic/blob/master/Application/src/main/java/com/example/android/basicpermissions/MainActivity.java
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                     @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                actually_record_audio();
            }else{

            }
        }
    }

    // Help with doing permission request for microphone: https://stackoverflow.com/a/39846797
    public void start_record(View v) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.RECORD_AUDIO },
                    10);
        } else {
            actually_record_audio();
        }
    }

    // On a good method to deal with button presses by using other functions:
    // https://medium.com/@jorgecool/i-just-have-to-say-that-doing-any-of-the-first-3-options-is-not-the-ideal-way-because-you-are-also-e5e039d14038
    private void actually_record_audio() {
        try {
            // Initialize Media things
            myAudioRecorder = new MediaRecorder();

            myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            System.out.print("Successfully connected MIC");
            myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            System.out.print("Successfully set output format");
            myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
            System.out.print("Successfully set encoder");
            myAudioRecorder.setOutputFile(output_file);
            System.out.print("Successfully set output file");
            myAudioRecorder.prepare();
            System.out.print("Successfully prepared");
            myAudioRecorder.start();
            System.out.print("Successfully started");

            record.setEnabled(false);
            stop.setEnabled(true);
            Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();
        } catch (IllegalStateException i_s_except) {
            to_print.setText("Cannot Record: Illegal State Exception - " + i_s_except.toString());
        } catch (IOException io_except) {
            to_print.setText("Cannot Record: I/O Exception - " + io_except.toString());
        }
    }

    public void stop_record(View v) {
        myAudioRecorder.stop();
        myAudioRecorder.release();
        myAudioRecorder = null; // but wouldn't we just want to reset it?

        // now change the buttons
        record.setEnabled(true);
        stop.setEnabled(false);
        play.setEnabled(true);

        Toast.makeText(getApplicationContext(), "Audio Recorded Successfully", Toast.LENGTH_LONG).show();
        //to_print.setText("Stopped Recording");
    }

    public void play_record(View v) {
        MediaPlayer tempMediaPlayer = new MediaPlayer();

        try {
            tempMediaPlayer.setDataSource(output_file);
            tempMediaPlayer.prepare();
            tempMediaPlayer.start();
        } catch (Exception except) {
            to_print.setText("Cannot Play: Exception");
        }


    }
}
