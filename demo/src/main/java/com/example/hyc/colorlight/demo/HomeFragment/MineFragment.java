package com.example.hyc.colorlight.demo.HomeFragment;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.hyc.colorlight.demo.Activity.HomeActivity;
import com.example.hyc.colorlight.demo.Menu;
import com.example.hyc.colorlight.demo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hyc on 18-6-1.
 */

public class MineFragment extends Fragment{

    private static String TAG = "MineFragment";

    private List<Menu> menuList;
    private boolean updatable = false;
    private UpdateReceiver updateReceiver=null;
    MenuAdapter adapter=null;
    ListView listView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //注册广播接收器接收Mqtt回调信息
        updateReceiver=new UpdateReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction("com.example.hyc.colorlight.demo.MQTT.UpdateService");
        getContext().registerReceiver(updateReceiver,filter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View v = inflater.inflate(R.layout.fragment_mine, container, false);

        Log.d(TAG, "onCreateView: updatable=" + updatable);
        menuList = new ArrayList<>();

        initMenu();

        RecyclerView recyclerView = (RecyclerView)v.findViewById(R.id.menu_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        MenuAdapter adapter = new MenuAdapter(menuList);
        recyclerView.setAdapter(adapter);

        return v;
    }

    private void initMenu() {
        Menu howtouse = new Menu("用户教程", R.drawable.questions, R.drawable.next);
        menuList.add(howtouse);
        Menu aboutus = new Menu("关于我们", R.drawable.about, R.drawable.next);
        menuList.add(aboutus);
        if (updatable == false) {
            Menu version = new Menu("当前版本 " + getAppVersionName(getContext()), R.drawable.version, R.drawable.next);
            menuList.add(version);
        } else {
            Menu update = new Menu("有可更新版本", R.drawable.update, R.drawable.next);
            menuList.add(update);
        }
    }


    public String getAppVersionName(Context context) {
        String versionName = "";
        int versionCode = 0;
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            versionCode = pi.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (versionName == null || versionName.length() <= 0) {
            versionName = "";
        }

        return versionName;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(updateReceiver);
    }

    public class UpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
                menuList.remove(2);
                Menu update = new Menu("有可更新版本", R.drawable.update, R.drawable.next);
                menuList.add(update);
                adapter.notifyDataSetChanged();
            }
    }
}