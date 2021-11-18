package com.example.digitalbuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class TestActivity extends AppCompatActivity {

    private MediaPlayer player;
    private String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("Audio/" + "audio-2021-10-24-15-13-22.3gp");
        try {
            File localFile = File.createTempFile("audio", "3gp");
            Log.d("local file", "onCreate: " + localFile.getAbsolutePath().toString());
            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getApplicationContext(), "created localFile" ,Toast.LENGTH_SHORT).show();
                    Log.d("local file", "onCreate: " + localFile.getAbsolutePath().toString());
                    fileName = localFile.getAbsolutePath().toString();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        findViewById(R.id.play_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPlay(true);
            }
        });
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(fileName);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e("LOG_TAG", "prepare() failed");
        }
    }

    private void stopPlaying() {
        player.release();
        player = null;
    }

}