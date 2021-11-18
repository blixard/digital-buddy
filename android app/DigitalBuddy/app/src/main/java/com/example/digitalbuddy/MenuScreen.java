package com.example.digitalbuddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MenuScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_screen);

        setButtons();
    }

    private void setButtons() {
        ConstraintLayout clCreateroom = findViewById(R.id.cl_createroom);
        clCreateroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext() , CreateRoom.class);
                startActivity(intent);
            }
        });

        ConstraintLayout clJoinroom = findViewById(R.id.cl_joinroom);
        clJoinroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext() , JoinRoom.class);
                startActivity(intent);
            }
        });

        ConstraintLayout clSetting = findViewById(R.id.cl_setting);
        clSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext() , SettingPage.class);
                startActivity(intent);
            }
        });
    }
}