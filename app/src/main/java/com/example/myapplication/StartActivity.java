package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);


        Thread updateSeekbar = new Thread(){
            @Override
            public void run() {

                try {
                        sleep(500);
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));


                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }



            }
        };

        updateSeekbar.start();
    }
}