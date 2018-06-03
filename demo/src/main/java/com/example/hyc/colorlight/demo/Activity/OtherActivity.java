package com.example.hyc.colorlight.demo.Activity;

import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.ImageView;

import com.example.hyc.colorlight.demo.R;

public class OtherActivity extends AppCompatActivity {

    String deviceId = null;
    public static final String DEVICE_NAME = "device_name";
    public static final String DEVICE_ID = "device_id";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);

        Intent intent = getIntent();
        String deviceName = intent.getStringExtra(DEVICE_NAME);
        deviceId = intent.getStringExtra(DEVICE_ID);

        //标题栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle(deviceName);
        setSupportActionBar(toolbar);
        //标题栏图片

        //设置返回键返回上一页
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }


    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        finish();
        return super.dispatchKeyEvent(event);
    }
}
