package com.example.hyc.colorlight.demo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeActivity extends AppCompatActivity{

    private static String TAG = "HomeActivity";
    private MyDatabaseHelper databaseHelper;

    private long exitTime = 0;

    private Light[] lights;// = {new Light("Light Name",R.drawable.light2,"ID Number")};
    private SelfDialog selfDialog;
    private RecyclerView recyclerView;

    private List<Light> lightList = new ArrayList<>();

    private LightAdapter adapter;

    private int position;  //左滑删除时获取位置

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
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);

        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT,
                ItemTouchHelper.START | ItemTouchHelper.END) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                if (fromPosition < toPosition) {
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(lightList, i, i + 1);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(lightList, i, i - 1);
                    }
                }

                updateSQLiteData();

                adapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }


            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                position = viewHolder.getAdapterPosition();
                final AlertDialog.Builder dialog = new AlertDialog.Builder(HomeActivity.this);
                dialog.setTitle("警告");
                dialog.setMessage("确定要删除吗?");
                dialog.setCancelable(true);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteValue();
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        recyclerView.setAdapter(adapter);
                    }
                });
                dialog.show();
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    final float alpha = 1 - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
                    viewHolder.itemView.setAlpha(alpha);
                    viewHolder.itemView.setTranslationX(dX);
                }
            }
        };

        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        initLights();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callSelfDialog(null);
            }
        });

    }

    private void updateSQLiteData() {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.execSQL("DELETE FROM Light");
        ContentValues values = new ContentValues();
        for(int i = 0;i < lightList.size(); i++)
        {
            values.put("name",lightList.get(i).getName());
            values.put("id",lightList.get(i).getId());
            db.insert("Light",null,values);
            values.clear();
        }
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

    private void deleteValue() {

        String deleteId = lights[position].getId();

        lightList.remove(position);
        adapter.notifyItemRemoved(position);

        //=======================database=====================//
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        long l = db.delete("Light","id = ?",new String[]{deleteId});
        Toast.makeText(HomeActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
        //====================================================//
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
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
                Intent intent = new Intent(HomeActivity.this, CaptureActivity.class);
                intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
                startActivityForResult(intent, REQUEST_CODE_SCAN);
                return true;
            case R.id.ques:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://www.baidu.com")));
                } catch (ActivityNotFoundException ignored) {
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
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
                System.exit(0);
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {

                String new_light_id = data.getStringExtra(Constant.CODED_CONTENT);
                Log.d(TAG, "onActivityResult: Result:"+new_light_id);
                callSelfDialog(new_light_id);
            }
        }
    }

    private void updateHomeView(String new_light_name, String new_light_id)
    {
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

            lightList.add(temlights[lights.length-1]);

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

    private void callSelfDialog(String id){
        selfDialog = new SelfDialog(HomeActivity.this,id,"确定",new SelfDialog.PriorityListener() {
            @Override
            public void refreshPriorityUI(String new_light_name, String new_light_id) {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                updateHomeView(new_light_name, new_light_id);
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


    /**
     * 通过反射，设置menu显示icon
     *
     * @param view
     * @param menu
     * @return
     */
    @SuppressLint("RestrictedApi")
    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        if (menu != null) {
            if (menu.getClass() == MenuBuilder.class) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                }
            }
        }
        return super.onPrepareOptionsPanel(view, menu);
    }
}
