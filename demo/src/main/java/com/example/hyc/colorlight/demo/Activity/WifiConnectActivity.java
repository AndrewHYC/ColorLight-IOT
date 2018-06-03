package com.example.hyc.colorlight.demo.Activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hyc.colorlight.demo.myAdapter.WifiListAdapter;
import com.example.hyc.colorlight.demo.R;
//import com.example.hyc.colorlight.demo.wifi.ControlLight;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.functions.Action1;

import static android.os.Build.VERSION_CODES.M;

/**
 * 描述：Wifi连接页面
 */
public class WifiConnectActivity extends BaseActivity {
    private String TAG = "WifiConnectActivity";

    public static String TYPE = "type";
    public static String ID = "id";
    public static String NAME = "name";
    private String name;
    private String type;
    private String id;

//    @BindView(R.id.main_setting) ImageView main_setting;
    @BindView(R.id.wifi_connect_icon) ImageView wifiConnectIcon;
//    @BindView(R.id.wifi_connect_state) ImageView wifiState;
//    @BindView(R.id.wifi_state_ll) LinearLayout wifiStateLL;
    @BindView(R.id.wifi_state_tv_1) TextView wifistate;
//    @BindView(R.id.button_enter) Button enter;
    @BindView(R.id.button_smartconfig) Button smartconfig;
    @BindView(R.id.wifi_list) ListView listView;

    private WifiListAdapter adapter;
    private List<ScanResult> list = new ArrayList<>();
    private Animation operatingAnim;
//    private boolean isConnecting;
    WifiManager wifiManager;

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                List<ScanResult> result = wifiManager.getScanResults();
                if(result == null || result.size()==0){
                    if (Build.VERSION.SDK_INT >= M) {
                        Toast.makeText(WifiConnectActivity.this, "未搜索到wifi信号！请确定是否打开GPS", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(WifiConnectActivity.this, "未搜索到wifi信号！", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Map<String,ScanResult> wifimap = new LinkedHashMap<>();
                    for(ScanResult wifi:result){
                        if(wifimap.containsKey(wifi.SSID))continue;
                        wifimap.put(wifi.SSID,wifi);
                    }
                    List<ScanResult> list = new ArrayList<>();
                    list.addAll(wifimap.values());

                    WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    adapter.setCurrentWifiSSID(wifiInfo.getSSID());
                    adapter.refreshList(list);
                }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_state);
        ButterKnife.bind(this);

        //标题栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle("一键配置");
        setSupportActionBar(toolbar);
        //标题栏图片

        //设置返回键返回上一页
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        name = intent.getStringExtra(NAME);
        id = intent.getStringExtra(ID);
        type = intent.getStringExtra(TYPE);

        Log.d(TAG, "onCreate: name = "+name);
        Log.d(TAG, "onCreate: id = "+id);
        Log.d(TAG, "onCreate: type = "+type);


        initView();

        if (Build.VERSION.SDK_INT >= M) {
            RxPermissions.getInstance(WifiConnectActivity.this)
                    .request(Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                    .subscribe(new Action1<Boolean>() {
                        @Override
                        public void call(Boolean aBoolean) {
                            if (aBoolean) {
                                initWifi();
                            }
                        }
                    });
        } else {
            initWifi();
        }

//        initWifi();

    }

    private void initWifi() {
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(getApplicationContext(), "wifi未打开！正在打开...", Toast.LENGTH_SHORT).show();
            wifiManager.setWifiEnabled(true);
        }

        adapter = new WifiListAdapter(this,list);
        listView.setAdapter(adapter);
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(receiver, filter);
    }

    private void initView() {

        operatingAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
        operatingAnim.setInterpolator(new LinearInterpolator());

        smartconfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WifiConnectActivity.this, SmartConfigActivity.class);
                intent.putExtra(WifiConnectActivity.TYPE, type);
                intent.putExtra(WifiConnectActivity.ID, id);
                intent.putExtra(WifiConnectActivity.NAME, name);

                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (wifiManager != null) {
            wifiManager.startScan();
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


    public boolean dispatchKeyEvent(KeyEvent event) {
        finish();
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(receiver != null){
            unregisterReceiver(receiver);
        }
    }
}
