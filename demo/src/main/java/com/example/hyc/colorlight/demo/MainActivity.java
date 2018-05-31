/*
 * Copyright (C) 2017 Jared Rummler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.hyc.colorlight.demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Timer;
import java.util.TimerTask;

//implements ColorPickerDialogListener
public class MainActivity extends AppCompatActivity implements DemoFragment.FragmentInteraction,
        ColorFragment.FragmentInteraction2{

  private static final String TAG = "MianFragment";

  // Give your color picker dialog unique IDs if you have multiple dialogs.
  private static final int DIALOG_ID = 0;

  public static final String LIGHT_NAME = "light_name";
  public static final String LIGHT_IMAGE_ID = "light_image_id";
  public static final String LIGHT_ID = "light_id";

  final MQTTService myService = new MQTTService();
  private String lightId;
  private String newColor;
  Intent serviceIntent;

  private TextView textView = null;    //显示Mqtt回调数据
  private MqttReceiver receiver = null;


  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Intent intent = getIntent();
    String lightName = intent.getStringExtra(LIGHT_NAME);
    int lightImageId = intent.getIntExtra(LIGHT_IMAGE_ID, 0);
    lightId = intent.getStringExtra(LIGHT_ID);
    Log.d(TAG, "onCreate: lightId="+lightId);

    //监测网络是否连接
    ConnectivityManager connectivityManager = (ConnectivityManager) this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo info = connectivityManager.getActiveNetworkInfo();
    if (info != null && info.isAvailable()) {
      String name = info.getTypeName();
      Toast.makeText(this,"已连接网络",Toast.LENGTH_SHORT).show();
      Log.i(TAG, "MQTT当前网络名称：" + name);
    } else {
      Toast.makeText(this,"无可用网络",Toast.LENGTH_SHORT).show();
      Log.i(TAG, "MQTT 没有可用网络");
    }

    myService.setTopic(lightId);
    serviceIntent = new Intent(this, MQTTService.class);
    startService(serviceIntent);

    //注册广播接收器
    receiver=new MqttReceiver();
    IntentFilter filter=new IntentFilter();
    filter.addAction("com.example.hyc.colorlight.demo.MQTTService");
    MainActivity.this.registerReceiver(receiver,filter);
    textView = (TextView)findViewById(R.id.MqttCallbacktext);

//    Bundle bundle = new Bundle();       //Activity向Fragment传值
//    bundle.putString("LIGHT_ID", lightId);

    //标题栏
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
    ImageView homeImageView = (ImageView)findViewById(R.id.light_image_view);
    setSupportActionBar(toolbar);

    //设置返回键返回上一页
    ActionBar actionBar = getSupportActionBar();
    if(actionBar != null){
      actionBar.setDisplayHomeAsUpEnabled(true);
    }
    //标题栏图片
    collapsingToolbarLayout.setTitle(lightName);
    Glide.with(this).load(lightImageId).into(homeImageView);


    if(savedInstanceState == null) {

      DemoFragment demoFragment = new DemoFragment();
//      demoFragment.setArguments(bundle);

      ColorFragment colorFragment = new ColorFragment();
//      colorFragment.setArguments(bundle);

      getFragmentManager().beginTransaction()
              .add(R.id.light_card_view1, demoFragment)
              .commit();

      getFragmentManager().beginTransaction()
              .add(R.id.light_card_view2, colorFragment)
              .commit();
    }

    ToggleButton breathButton = (ToggleButton)findViewById(R.id.breath_tb);
    breathButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(b){
          String json = "{\"ledmode\":1,\"cc\":1}";
//          Toast.makeText(MainActivity.this,json,Toast.LENGTH_SHORT).show();
//          myService.publish(json);
          myService.publish("1023");
        }else{
          String json = "{\"ledmode\":1,\"cc\":0}";
//          Toast.makeText(MainActivity.this,json,Toast.LENGTH_SHORT).show();
//          myService.publish(json);
          myService.publish("0");
        }
      }
    });

//    //==================Debug==============//
//    final EditText editText = (EditText)findViewById(R.id.Edit_Text);
//    findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View view) {
//        String str = editText.getText().toString();
//        myService.publish(str);
//      }
//    });

  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        new Thread(new Runnable() {
          @Override
          public void run() {
            stopService(serviceIntent);
          }
        }).start();
        finish();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }


  @Override
  public void process(String str) {
    if(str!=null){
      newColor = str;
      Log.d(TAG, "process: newColor="+newColor);
    }
    StringBuffer sb = new StringBuffer(newColor);
    int index;
    for(index=2; index < sb.length(); index+=3){
      sb.insert(index,',');
    }
    String[] array = sb.toString().split(",");
    String json = "{\"ledmode\":2"
            +",\"cr\":"+array[1]+",\"cg\":"+array[2]+",\"cb\":"+array[3]+"}";
//    Toast.makeText(this,json,Toast.LENGTH_SHORT).show();
    myService.publish(json);
  }

  @Override
  public void process2(String str) {
    if(str!=null){
      newColor = str;
      Log.d(TAG, "process2: newColor="+newColor);
    }
    StringBuffer sb = new StringBuffer(newColor);
    int index;
    for(index=2; index < sb.length(); index+=3){
      sb.insert(index,',');
    }
    String[] array = sb.toString().split(",");
    String json = "{\"ledmode\":2"
            +",\"cr\":"+array[1]+",\"cg\":"+array[2]+",\"cb\":"+array[3]+"}";
//    Toast.makeText(this,json,Toast.LENGTH_SHORT).show();
    myService.publish(json);
  }

  @Override
  public boolean dispatchKeyEvent(KeyEvent event) {

    new Thread(new Runnable() {
      @Override
      public void run() {
        stopService(serviceIntent);
      }
    }).start();

    finish();

    return super.dispatchKeyEvent(event);
  }

  @Override
  protected void onDestroy() {

    unregisterReceiver(receiver);//释放广播接收者
    super.onDestroy();
  }


  public class MqttReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      Bundle bundle=intent.getExtras();
      String mqttMessage=bundle.getString("Message");

      Toast toast = null;

      if(mqttMessage.equals("ok")){
        toast = Toast.makeText(getApplicationContext(),
                "成功", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 10);
        LinearLayout toastView = (LinearLayout) toast.getView();
        ImageView imageCodeProject = new ImageView(getApplicationContext());
        imageCodeProject.setImageResource(R.drawable.suc);
        toastView.addView(imageCodeProject, 0);
        toast.show();
      }else if(mqttMessage.equals("")){
        toast = Toast.makeText(getApplicationContext(),
                "失败", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 10);
        LinearLayout toastView = (LinearLayout) toast.getView();
        ImageView imageCodeProject = new ImageView(getApplicationContext());
        imageCodeProject.setImageResource(R.drawable.unsuc);
        toastView.addView(imageCodeProject, 0);
        toast.show();
      }

    }
  }
}
 