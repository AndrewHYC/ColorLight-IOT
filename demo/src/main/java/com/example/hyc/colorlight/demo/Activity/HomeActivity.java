package com.example.hyc.colorlight.demo.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.KeyEvent;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.hyc.colorlight.demo.HomeFragment.DeviceFragment;
import com.example.hyc.colorlight.demo.HomeFragment.DeviceFragmentPagerAdapter;
import com.example.hyc.colorlight.demo.HomeFragment.MineFragment;

import com.example.hyc.colorlight.demo.MQTT.UpdateService;
import com.example.hyc.colorlight.demo.R;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener{

    private static String TAG = "HomeActivity";

    private long exitTime = 0;

    String themeColor;
    String alphathemeColor;

    private UpdateService Service = new UpdateService();
    private boolean update;

    private ViewPager myviewpager;
    //fragment的集合，对应每个子页面
    private ArrayList<Fragment> fragments;
    //选项卡中的按钮
    private Button btn_first;
    private Button btn_second;

    //作为指示标签的按钮
    private ImageView cursor;
    //标志指示标签的横坐标
    float cursorX = 0;
    //所有按钮的宽度的集合
    private int[] widthArgs;
    //所有按钮的集合
    private Button[] btnArgs;

    Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        serviceIntent = new Intent(this, UpdateService.class);
//        startService(serviceIntent);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);

        initView();
    }


    public void initView(){

        themeColor = "#"+Integer.toHexString(getResources().getColor(R.color.colorPrimary));
        alphathemeColor = "#"+Integer.toHexString(getResources().getColor(R.color.colorPrimary1));

        myviewpager = (ViewPager)findViewById(R.id.myviewpager);

        btn_first = (Button)findViewById(R.id.btn_first);
        btn_second = (Button)findViewById(R.id.btn_second);

        btnArgs = new Button[]{btn_first,btn_second};

        cursor = (ImageView)findViewById(R.id.cursor_btn);

        cursor.setBackgroundColor(Color.parseColor(alphathemeColor));


        myviewpager.setOnPageChangeListener(this);
        btn_first.setOnClickListener(this);
        btn_second.setOnClickListener(this);

        fragments = new ArrayList<Fragment>();
        fragments.add(new DeviceFragment());
        fragments.add(new MineFragment());

        DeviceFragmentPagerAdapter adapter = new DeviceFragmentPagerAdapter(getSupportFragmentManager(),fragments);
        myviewpager.setAdapter(adapter);

        if(widthArgs==null){
            WindowManager wm = (WindowManager) this
                    .getSystemService(Context.WINDOW_SERVICE);
            int width = wm.getDefaultDisplay().getWidth();

            widthArgs = new int[]{width/2,
                    width/2};
        }

        resetButtonColor();
        btn_first.setTextColor(Color.parseColor("#ffffff"));
        cursorAnim(0);
    }

    //重置所有按钮的颜色
    public void resetButtonColor(){

        btn_first.setBackgroundColor(Color.parseColor(themeColor));
        btn_second.setBackgroundColor(Color.parseColor(themeColor));

        btn_first.setTextColor(Color.parseColor("#bebebe"));
        btn_second.setTextColor(Color.parseColor("#bebebe"));
    }

    @Override
    public void onClick(View whichbtn) {
        // TODO Auto-generated method stub

        switch (whichbtn.getId()) {
            case R.id.btn_first:
                myviewpager.setCurrentItem(0);
                cursorAnim(0);
                btn_first.setTextColor(Color.parseColor("#FFFFFF"));
                break;
            case R.id.btn_second:
                myviewpager.setCurrentItem(1);
                cursorAnim(1);
                btn_second.setTextColor(Color.parseColor("#FFFFFF"));
                break;
            default:
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageSelected(int arg0) {
        // TODO Auto-generated method stub

        //每次滑动首先重置所有按钮的颜色
        resetButtonColor();
        //将滑动到的当前按钮颜色设置为红色
        btnArgs[arg0].setTextColor(Color.parseColor("#FFFFFF"));
        cursorAnim(arg0);
    }

    //指示器的跳转，传入当前所处的页面的下标
    public void cursorAnim(int curItem){
        //每次调用，就将指示器的横坐标设置为0，即开始的位置
        cursorX = 0;
        //再根据当前的curItem来设置指示器的宽度
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)cursor.getLayoutParams();
        //减去边距*2，以对齐标题栏文字
        lp.width = widthArgs[curItem]-btnArgs[0].getPaddingLeft()*2;
        cursor.setLayoutParams(lp);
        //循环获取当前页之前的所有页面的宽度
        for(int i=0; i<curItem; i++){
            cursorX = cursorX + btnArgs[i].getWidth();
        }
        //再加上当前页面的左边距，即为指示器当前应处的位置
        cursor.setX(cursorX+btnArgs[curItem].getPaddingLeft());
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN
                && event.getRepeatCount() == 0) {
            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                stopService(serviceIntent);
                System.exit(0);
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(serviceIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    private int REQUEST_CODE_SCAN = 111;

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scan:
                /*ZxingConfig是配置类  可以设置是否显示底部布局，闪光灯，相册，是否播放提示音  震动等动能
                * 也可以不传这个参数
                * 不传的话  默认都为默认不震动  其他都为true
                * */
                ZxingConfig config = new ZxingConfig();
                config.setShowbottomLayout(true);//底部布局（包括闪光灯和相册）
                config.setPlayBeep(true);//是否播放提示音
                config.setShake(true);//是否震动
                config.setShowAlbum(true);//是否显示相册
                config.setShowFlashLight(true);//是否显示闪光灯
                Intent intent = new Intent(this, CaptureActivity.class);
                intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
                startActivityForResult(intent, REQUEST_CODE_SCAN);
                return true;
//            case R.id.ques:
//                try {
//                    startActivity(new Intent(Intent.ACTION_VIEW,
//                            Uri.parse("https://www.baidu.com")));
//                } catch (ActivityNotFoundException ignored) {
//                }
//                return true;
            case R.id.add:
                SendAddBroadCast(null,null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {

                String type = null;
                String result = data.getStringExtra(Constant.CODED_CONTENT);
                String[] arr = result.split("/");
                Log.d(TAG, "onActivityResult: arr[0]="+arr[0]);
                if(arr[0].equals("other")){
                    type = "其他";
                }else if(arr[0].equals("lamp")){
                    type = "爱心灯";
                }
                SendAddBroadCast(type,arr[1]);
            }
        }
    }

    private void SendAddBroadCast(String type, String id){
        //发送广播
        Intent intent=new Intent();
        intent.putExtra("type",type);
        intent.putExtra("id",id);
        intent.setAction("com.example.hyc.colorlight.demo.MQTT.Add");
        sendBroadcast(intent);
    }
}
