package com.example.hyc.colorlight.demo.Activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.esp.smartconfig.EspWifiAdminSimple;
import com.esp.smartconfig.EsptouchTask;
import com.esp.smartconfig.IEsptouchResult;
import com.esp.smartconfig.IEsptouchTask;
import com.esp.smartconfig.SimpleDialogTask;
import com.esp.smartconfig.SweetDialogActivity;
import com.esp.smartconfig.sweet.OnDismissCallbackListener;
import com.example.hyc.colorlight.demo.MyDatabaseHelper;
import com.example.hyc.colorlight.demo.PreferenceUtil;
import com.example.hyc.colorlight.demo.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.hyc.colorlight.demo.Activity.WifiConnectActivity.ID;
import static com.example.hyc.colorlight.demo.Activity.WifiConnectActivity.NAME;
import static com.example.hyc.colorlight.demo.Activity.WifiConnectActivity.TYPE;

/**
 * 描述：配置smartconfig
 */
public class SmartConfigActivity extends SweetDialogActivity{

    private String name;
    private String type;
    private String id;

//    @BindView(R.id.setting_back) ImageView back;
    @BindView(R.id.et_psw) EditText et_psw;
    @BindView(R.id.start) Button start;
    @BindView(R.id.tv_ssid)TextView tv_ssid;

    SimpleDialogTask task;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_config);
        ButterKnife.bind(this);
        //标题栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle("进行配置");
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

        PreferenceUtil.init(this);
        initView();
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
    protected void onResume() {
        super.onResume();
        // display the connected ap's ssid
        String apSsid = EspWifiAdminSimple.getWifiConnectedSsid(this);
        if (apSsid != null) {
            tv_ssid.setText(apSsid);
        } else {
            tv_ssid.setText("");
        }
        et_psw.setText(PreferenceUtil.getInstance().getPSW());
        // check whether the wifi is connected
        boolean isApSsidEmpty = TextUtils.isEmpty(apSsid);
        start.setEnabled(!isApSsidEmpty);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (task != null && task.getStatus() == AsyncTask.Status.RUNNING) {
            task.cancel(true);
        }
    }

    private void initView() {

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //判断是否存在空值
                String ssid = tv_ssid.getText().toString();
                String password = et_psw.getText().toString();

                if(TextUtils.isEmpty(ssid)){
                    Toast.makeText(SmartConfigActivity.this, "确定是否连上wifi", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    Toast.makeText(SmartConfigActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                String apBssid = EspWifiAdminSimple.getWifiConnectedBssid(SmartConfigActivity.this);
                smartconfig(ssid,apBssid,password);
            }
        });
    }

    /**
     * @开始一键配置
     **/
    private void smartconfig(final String apSsid, final String apBssid, final String apPassword){
        Log.d("smartconfig","mEdtApSsid = " + apSsid
                + ", " + " mEdtApPassword = " + apPassword);
        if (task != null && task.getStatus() == AsyncTask.Status.RUNNING) {
            return;
        }
        task = new SimpleDialogTask(this) {

            private IEsptouchTask mEsptouchTask;

            @Override
            public Object onAsync() {
                mEsptouchTask = new EsptouchTask(apSsid, apBssid, apPassword, SmartConfigActivity.this);
                List<IEsptouchResult> resultList = mEsptouchTask.executeForResults(1);
                return resultList;
            }

            @Override
            public void onResult(Object obj) {
                List<IEsptouchResult> result = (List<IEsptouchResult>) obj;
                if(result != null && result.size()>0){
                    IEsptouchResult firstResult = result.get(0);
                    // check whether the task is cancelled and no results received
                    if (!firstResult.isCancelled()) {
                        int count = 0;
                        // max results to be displayed, if it is more than maxDisplayCount,
                        // just show the count of redundant ones
                        final int maxDisplayCount = 5;
                        // the task received some results including cancelled while
                        // executing before receiving enough results
                        if (firstResult.isSuc()) {
                            StringBuilder sb = new StringBuilder();
                            for (IEsptouchResult resultInList : result) {
                                sb.append("配置成功\n"
                                        + "IP地址 = "
                                        + resultInList.getInetAddress()
                                        .getHostAddress() + "\n");
                                count++;
                                PreferenceUtil.getInstance().writePreferences(PreferenceUtil.IP,resultInList.getInetAddress()
                                        .getHostAddress());
                                if (count >= maxDisplayCount) {
                                    break;
                                }
                            }
                            if (count < result.size()) {
                                sb.append("\nthere's " + (result.size() - count)
                                        + " more result(s) without showing\n");
                            }
                            onToast(new OnDismissCallbackListener(sb.toString()){
                                @Override
                                public void onCallback() {
                                    PreferenceUtil.getInstance().writePreferences(PreferenceUtil.PSW,apPassword);
                                    finish();
                                }
                            });

                            //=====更新数据库======//
                            MyDatabaseHelper databaseHelper = new MyDatabaseHelper(SmartConfigActivity.this);
                            SQLiteDatabase db = databaseHelper.getWritableDatabase();
                            ContentValues values = new ContentValues();
                            values.put("isConfig",1);
                            db.update("Light",values,"id = ? && type = ? ",new String[]{id,name});
                            //===========================//

                            if(type.equals("爱心灯")){
                                Intent intent = new Intent(SmartConfigActivity.this, MainActivity.class);
                                intent.putExtra(WifiConnectActivity.TYPE, type);
                                intent.putExtra(WifiConnectActivity.ID, id);
                                intent.putExtra(WifiConnectActivity.NAME, name);
                                startActivity(intent);
                            }else if(type.equals("")){
                                Intent intent = new Intent(SmartConfigActivity.this, HomeActivity.class);
                                startActivity(intent);
                            }
                        } else {
                            onToastErrorMsg("配置失败\n请确保智能终端打开了配置模式");
                        }
                    }
                }
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
                if (mEsptouchTask != null) {
                    mEsptouchTask.interrupt();
                }
            }
        };

        task.startTask("正在配置中，请稍候....");
    }
}
