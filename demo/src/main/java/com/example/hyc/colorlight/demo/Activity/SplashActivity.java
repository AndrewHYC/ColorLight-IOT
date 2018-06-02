package com.example.hyc.colorlight.demo.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.hyc.colorlight.demo.R;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final Intent it = new Intent(this, HomeActivity.class); //你要转向的Activity
//        final Intent it = new Intent(this, Home2Activity.class); //你要转向的Activity
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                startActivity(it); //执行
                finish();
            }
        };
        timer.schedule(task, 1000 * 1); //1秒后

    }
}
