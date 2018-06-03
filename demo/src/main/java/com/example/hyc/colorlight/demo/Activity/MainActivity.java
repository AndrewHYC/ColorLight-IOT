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

package com.example.hyc.colorlight.demo.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.example.hyc.colorlight.demo.LightFragment.ColorFragment;
import com.example.hyc.colorlight.demo.LightFragment.DemoFragment;
import com.example.hyc.colorlight.demo.MQTT.MQTTService;
import com.example.hyc.colorlight.demo.R;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;

//implements ColorPickerDialogListener
public class MainActivity extends AppCompatActivity implements DemoFragment.FragmentInteraction,
        ColorFragment.FragmentInteraction2 {

  private static final String TAG = "MianFragment";

  // Give your color picker dialog unique IDs if you have multiple dialogs.
  private static final int DIALOG_ID = 0;

  public static final String LIGHT_NAME = "light_name";
  public static final String LIGHT_IMAGE_ID = "light_image_id";
  public static final String LIGHT_ID = "light_id";
  private SeekBar mseekBarvolume;     //滑块

  MQTTService myService = new MQTTService();
  private String lightId;
  private String lightName;
  private String newColor;
  Intent serviceIntent;

  private MqttReceiver receiver = null;
  ConnectivityManager connectivityManager=null;
  NetworkInfo info = null;

  private IntentFilter intentFilter = null;
  private NetworkChangeReceiver networkChangeReceiver=null;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Intent intent = getIntent();
    lightName = intent.getStringExtra(LIGHT_NAME);
    int lightImageId = intent.getIntExtra(LIGHT_IMAGE_ID, 0);
    lightId = intent.getStringExtra(LIGHT_ID);
    boolean isConfig = intent.getBooleanExtra("isConfig",false);
    if(!isConfig){
      showSnackBar("请先进行配置");
    }
    Log.d(TAG, "onCreate: lightId="+lightId);

    //监测网络是否连接
    connectivityManager = (ConnectivityManager) this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    info = connectivityManager.getActiveNetworkInfo();


    myService.setTopic(lightId);
    serviceIntent = new Intent(this, MQTTService.class);
    startService(serviceIntent);

    mseekBarvolume = (SeekBar)findViewById(R.id.color_seekbar);
    mseekBarvolume.setMax(1023);

    mseekBarvolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        int tmpInt = seekBar.getProgress();
        if(tmpInt < 0){
          tmpInt =0;
        }

        String brightness = "{\"ledmode\":1,\"cl\":"+String.valueOf(tmpInt)+"}";

        myService.publish(brightness);

      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {

      }
    });

    //注册广播接收器接收Mqtt回调信息
    receiver=new MqttReceiver();
    IntentFilter filter=new IntentFilter();
    filter.addAction("com.example.hyc.colorlight.demo.MQTT.MQTTService");
    MainActivity.this.registerReceiver(receiver,filter);

    //监听网络变化
    intentFilter = new IntentFilter();
    intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
    networkChangeReceiver = new NetworkChangeReceiver();
    registerReceiver(networkChangeReceiver,intentFilter);

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

    final ToggleButton breathButton = (ToggleButton)findViewById(R.id.breath_tb);
    breathButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(b){
          String json = "{\"ledmode\":3,\"cc\":1}";
//          Toast.makeText(MainActivity.this,json,Toast.LENGTH_SHORT).show();

          if (info != null && info.isAvailable()) {
              myService.publish(json);
//              myService.publish("1023");
          }else{

//            Toast toast = Toast.makeText(getApplicationContext(),
//                    "失败", Toast.LENGTH_SHORT);
//            toast.setGravity(Gravity.BOTTOM, 0, 100);
//            LinearLayout toastView = (LinearLayout) toast.getView();
//            ImageView imageCodeProject = new ImageView(getApplicationContext());
//            imageCodeProject.setImageResource(R.drawable.unsuc);
//            toastView.addView(imageCodeProject, 0);
//            toast.show();
            showSnackBar("请检查网络是否连接");
            breathButton.setChecked(false);
          }
        }else{
          String json = "{\"ledmode\":3,\"cc\":0}";
//          Toast.makeText(MainActivity.this,json,Toast.LENGTH_SHORT).show();
          if (info != null && info.isAvailable()) {
              myService.publish(json);
//            myService.publish("0");
          }else{

//            Toast toast = Toast.makeText(getApplicationContext(),
//                    "失败", Toast.LENGTH_SHORT);
//            toast.setGravity(Gravity.BOTTOM, 0, 100);
//            LinearLayout toastView = (LinearLayout) toast.getView();
//            ImageView imageCodeProject = new ImageView(getApplicationContext());
//            imageCodeProject.setImageResource(R.drawable.unsuc);
//            toastView.addView(imageCodeProject, 0);
//            toast.show();
            showSnackBar("请检查网络是否连接");
            breathButton.setChecked(true);
          }
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
            stopService(serviceIntent);
        finish();
        return true;
      case R.id.config:
        Intent intent = new Intent(MainActivity.this, WifiConnectActivity.class);
        intent.putExtra(WifiConnectActivity.ID, lightId);
        intent.putExtra(WifiConnectActivity.NAME, lightName);
        intent.putExtra(WifiConnectActivity.TYPE, "爱心灯");
        startActivity(intent);
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
    if (info != null && info.isAvailable()) {
              myService.publish(json);
    }else{
//      Toast toast = Toast.makeText(getApplicationContext(),
//              "失败", Toast.LENGTH_SHORT);
//      toast.setGravity(Gravity.BOTTOM, 0, 100);
//      LinearLayout toastView = (LinearLayout) toast.getView();
//      ImageView imageCodeProject = new ImageView(getApplicationContext());
//      imageCodeProject.setImageResource(R.drawable.unsuc);
//      toastView.addView(imageCodeProject, 0);
//      toast.show();
      showSnackBar("请检查网络是否连接");
    }
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
    if (info != null && info.isAvailable()) {
              myService.publish(json);
    }else{
//      Toast toast = Toast.makeText(getApplicationContext(),
//              "失败", Toast.LENGTH_SHORT);
//      toast.setGravity(Gravity.BOTTOM, 0, 100);
//      LinearLayout toastView = (LinearLayout) toast.getView();
//      ImageView imageCodeProject = new ImageView(getApplicationContext());
//      imageCodeProject.setImageResource(R.drawable.unsuc);
//      toastView.addView(imageCodeProject, 0);
//      toast.show();
      showSnackBar("请检查网络是否连接");
    }
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.config,menu);
    return true;
  }


  @Override
  public boolean dispatchKeyEvent(KeyEvent event) {

        stopService(serviceIntent);

    finish();

    return super.dispatchKeyEvent(event);
  }

  @Override
  protected void onDestroy() {
    stopService(serviceIntent);
    unregisterReceiver(networkChangeReceiver);//释放广播接收者
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
        toast.setGravity(Gravity.BOTTOM, 0, 100);
        LinearLayout toastView = (LinearLayout) toast.getView();
        ImageView imageCodeProject = new ImageView(getApplicationContext());
        imageCodeProject.setImageResource(R.drawable.suc);
        toastView.addView(imageCodeProject, 0);
        toast.show();
      }

    }
  }

  public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

      info = connectivityManager.getActiveNetworkInfo();
      if(info != null && info.isAvailable()){
        Toast.makeText(context,"网络已连接",Toast.LENGTH_SHORT).show();
        startService(serviceIntent);
      }else{
        Toast.makeText(context,"网络未连接",Toast.LENGTH_SHORT).show();
        stopService(serviceIntent);
      }
    }
  }

  /**
   * 展示一个SnackBar
   */
  public void showSnackBar(String message) {
    //去掉虚拟按键
    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION //隐藏虚拟按键栏
            | View.SYSTEM_UI_FLAG_IMMERSIVE //防止点击屏幕时,隐藏虚拟按键栏又弹了出来
    );
    final Snackbar snackbar = Snackbar.make(getWindow().getDecorView(), message, Snackbar.LENGTH_INDEFINITE);
    snackbar.setAction("知道了", new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        snackbar.dismiss();
        //隐藏SnackBar时记得恢复隐藏虚拟按键栏,不然屏幕底部会多出一块空白布局出来,和难看
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
      }
    }).show();
  }

}
 