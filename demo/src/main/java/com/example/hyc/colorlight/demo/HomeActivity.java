package com.example.hyc.colorlight.demo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements LightAdapter.onCallbackListener {

    private static String TAG = "HomeActivity";
    private MyDatabaseHelper databaseHelper;

    private long exitTime = 0;

    private Light[] lights;// = {new Light("Light Name",R.drawable.light2,"ID Number")};
    private SelfDialog selfDialog;
    private RecyclerView recyclerView;

    private List<Light> lightList = new ArrayList<>();

    private LightAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //=======================database=================//
        boolean isTableExist=true;
        SQLiteDatabase db0=openOrCreateDatabase("LightStore.db", 0, null);
        Cursor c=db0.rawQuery("SELECT count(*) FROM sqlite_master WHERE type='table' AND name='Light'", null);
        Log.d(TAG, "onCreate: cursor.Count="+c.getColumnCount());
        if (c.getCount()==0) {
            isTableExist=false;
        }
        c.close();
        db0.close();
        if(isTableExist == false){
            databaseHelper = new MyDatabaseHelper(this,"LightStore.db",null,1);
        }else{
            databaseHelper = new MyDatabaseHelper(this);
        }

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        //查询Light表中所有数据

        Cursor cursor = db.query("Light",null,null,null,null,null,null);
        int length = cursor.getCount();
        Log.d(TAG, "onCreate: length = "+length);
        if(length == 0){
            Snackbar.make(getWindow().getDecorView(),"请点击上方悬浮球添加爱心灯",Snackbar.LENGTH_LONG).show();
        }
            Light[] tempLights = new Light[length];
            lights = tempLights;

            int i = 0;
            if(cursor.moveToFirst()){
                do {
                    //遍历Cursor对象,并取出数据
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    String id = cursor.getString(cursor.getColumnIndex("id"));
                    switch (i%5){
                        case 0:
                            lights[i] = new Light(name,R.drawable.light1,id);
                            break;
                        case 1:
                            lights[i] = new Light(name,R.drawable.light2,id);
                            break;
                        case 2:
                            lights[i] = new Light(name,R.drawable.light3,id);
                            break;
                        case 3:
                            lights[i] = new Light(name,R.drawable.light4,id);
                            break;
                        case 4:
                            lights[i] = new Light(name,R.drawable.light5,id);
                            break;
                            default:
                                break;
                    }
                    i++;
                }while(cursor.moveToNext());
            }
//        }

        //==================================================//

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);

        initLights();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final SelfDialog selfDialog = new SelfDialog(HomeActivity.this,"确定",new SelfDialog.PriorityListener() {
                    @Override
                    public void refreshPriorityUI(String new_light_name, String new_light_id) {
                        WindowManager.LayoutParams lp = getWindow().getAttributes();
                        lp.alpha = 1.0f;
                        getWindow().setAttributes(lp);
                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                        if(new_light_name!=null&&new_light_id!=null)
                        {
                            Light[] temlights = new Light[lights.length+1];
                            System.arraycopy(lights,0,temlights,0,lights.length);

                            switch (lights.length%5){
                                case 0:
                                    temlights[lights.length] = new Light(new_light_name,R.drawable.light1,new_light_id);
                                    break;
                                case 1:
                                    temlights[lights.length] = new Light(new_light_name,R.drawable.light2,new_light_id);
                                    break;
                                case 2:
                                    temlights[lights.length] = new Light(new_light_name,R.drawable.light3,new_light_id);
                                    break;
                                case 3:
                                    temlights[lights.length] = new Light(new_light_name,R.drawable.light4,new_light_id);
                                    break;
                                case 4:
                                    temlights[lights.length] = new Light(new_light_name,R.drawable.light5,new_light_id);
                                    break;
                                    default:
                                        break;
                            }

                            lights = temlights;
                            initLights();

                            //======================database=====================//
                            SQLiteDatabase db = databaseHelper.getWritableDatabase();
                            ContentValues values = new ContentValues();
                            values.put("name",new_light_name);
                            values.put("id",new_light_id);
                            long i = db.insert("Light",null,values);
                            Toast.makeText(HomeActivity.this,"添加成功",Toast.LENGTH_SHORT).show();
                            //=====================================================//

                            adapter = new LightAdapter(lightList);
                            recyclerView.setAdapter(adapter);

                        }

                    }
                });

                selfDialog.setNoOnclickListener("取消", new SelfDialog.onNoOnclickListener() {
                    @Override
                    public void onNoClick() {
                        selfDialog.dismiss();
                        WindowManager.LayoutParams lp = getWindow().getAttributes();
                        lp.alpha = 1.0f;
                        getWindow().setAttributes(lp);
                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                    }
                });

                selfDialog.setCanceledOnTouchOutside(true);

                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 0.3f;
                getWindow().setAttributes(lp);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

                selfDialog.show();
            }
        });

    }

    private void initLights(){
        lightList.clear();
        for(int i = 0; i < lights.length; i++)
        {
            Log.d(TAG, "initLights: light["+i+"] = "+lights[i]);
            lightList.add(lights[i]);
        }
        adapter = new LightAdapter(lightList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void SendMessageValue(String StrValue) {
        String deleteId = StrValue;

        for (int i = 0; i < lights.length; i++)
        {
            if (deleteId.equals(lights[i].getId())){

                Light[] temlights = new Light[lights.length-1];

                System.arraycopy(lights,0,temlights,0,i);

                for(int j = i; j+1 < lights.length; j++ )
                {
                    temlights[j] = lights[j+1];
                }
                lights = temlights;
                initLights();

                //=======================database=====================//
                SQLiteDatabase db = databaseHelper.getWritableDatabase();
                long l = db.delete("Light","id = ?",new String[]{deleteId});
                Toast.makeText(HomeActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                //====================================================//


                adapter = new LightAdapter(lightList);
                recyclerView.setAdapter(adapter);
            }
        }
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {       //返回键退出程序
//        if (keyCode == KeyEvent.KEYCODE_BACK
//                && event.getAction() == KeyEvent.ACTION_DOWN) {
//            if ((System.currentTimeMillis() - exitTime) > 2000) {
//                Toast.makeText(getApplicationContext(), "再按一次退出程序",
//                        Toast.LENGTH_SHORT).show();
//                exitTime = System.currentTimeMillis();
//            } else {
//                finish();
//                System.exit(0);
//            }
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

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
                System.exit(0);
            }
            return true;
        }


        return super.dispatchKeyEvent(event);
    }
}
