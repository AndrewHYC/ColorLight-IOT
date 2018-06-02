package com.example.hyc.colorlight.demo.HomeFragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.hyc.colorlight.demo.Light;
import com.example.hyc.colorlight.demo.MyDatabaseHelper;
import com.example.hyc.colorlight.demo.R;
import com.example.hyc.colorlight.demo.SelfDialog;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by hyc on 18-6-1.
 */

public class DeviceFragment extends Fragment {
    private View view;
    private MyDatabaseHelper databaseHelper;

    private Light[] lights;// = {new Light("Light Name",R.drawable.light2,"ID Number")};
    private SelfDialog selfDialog;
    private RecyclerView recyclerView;

    private List<Light> lightList = new ArrayList<>();

    private LightAdapter adapter;

    private AddReceiver addReceiver=null;

    private int position;  //左滑删除时获取位置

    private static String TAG = "DeviceFragment";
    ItemTouchHelper.SimpleCallback callback;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //注册广播接收器接收Mqtt回调信息
        addReceiver=new AddReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction("com.example.hyc.colorlight.demo.MQTT.Add");
        getContext().registerReceiver(addReceiver,filter);

        //=======================database=================//
        boolean isTableExist=true;
        SQLiteDatabase db0= getContext().openOrCreateDatabase("LightStore.db", 0, null);
        Cursor c=db0.rawQuery("SELECT count(*) FROM sqlite_master WHERE type='table' AND name='Light'", null);
        Log.d(TAG, "onCreate: cursor.Count="+c.getColumnCount());
        if (c.getCount()==0) {
            isTableExist=false;
        }
        c.close();
        db0.close();
        if(isTableExist == false){
            databaseHelper = new MyDatabaseHelper(getContext(),"LightStore.db",null,1);
        }else{
            databaseHelper = new MyDatabaseHelper(getContext());
        }

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        //查询Light表中所有数据

        Cursor cursor = db.query("Light",null,null,null,null,null,null);
        int length = cursor.getCount();
        Log.d(TAG, "onCreate: length = "+length);
        if(length == 0){
            showSnackBar("没有设备?\n请通过上方扫码或手动添加");
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


        callback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT,
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
                Log.d(TAG, "onSwiped: "+direction);
                if(direction == 32){
                    final AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
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
                }else{

                    final AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
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
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_device, container,false);

        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view1);
        GridLayoutManager layoutManager = new GridLayoutManager(view.getContext(),1);
        recyclerView.setLayoutManager(layoutManager);

        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        initLights();

        return view;
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
        for(int i = 0; i < lights.length; i++) {
            Log.d(TAG, "initLights: light[" + i + "] = " + lights[i]);
            lightList.add(lights[i]);
        }

        adapter = new LightAdapter(lightList);
        Log.d(TAG, "initLights: adapter="+adapter);
        recyclerView.setAdapter(adapter);
    }

    private void deleteValue() {

        String deleteId = lights[position].getId();

        lightList.remove(position);
        adapter.notifyItemRemoved(position);
        if(lightList.size()==0){
            showSnackBar("没有设备?\n请通过上方扫码或手动添加");
        }
        //=======================database=====================//
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        long l = db.delete("Light","id = ?",new String[]{deleteId});
        Toast.makeText(view.getContext(),"删除成功",Toast.LENGTH_SHORT).show();
        //====================================================//
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
            Toast.makeText(view.getContext(),"添加成功",Toast.LENGTH_SHORT).show();
            //=====================================================//

            adapter = new LightAdapter(lightList);
            recyclerView.setAdapter(adapter);

        }
    }

    private void callSelfDialog(String id){
        selfDialog = new SelfDialog(view.getContext(),id,"确定",new SelfDialog.PriorityListener() {
            @Override
            public void refreshPriorityUI(String new_light_name, String new_light_id) {
                WindowManager.LayoutParams lp = ((AppCompatActivity) getActivity()).getWindow().getAttributes();
                lp.alpha = 1.0f;
                ((AppCompatActivity) getActivity()).getWindow().setAttributes(lp);
                ((AppCompatActivity) getActivity()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                updateHomeView(new_light_name, new_light_id);
            }
        });

        selfDialog.setNoOnclickListener("取消", new SelfDialog.onNoOnclickListener() {
            @Override
            public void onNoClick() {
                selfDialog.dismiss();
                WindowManager.LayoutParams lp = ((AppCompatActivity) getActivity()).getWindow().getAttributes();
                lp.alpha = 1.0f;
                ((AppCompatActivity) getActivity()).getWindow().setAttributes(lp);
                ((AppCompatActivity) getActivity()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            }
        });

        selfDialog.setCanceledOnTouchOutside(true);

        WindowManager.LayoutParams lp = ((AppCompatActivity) getActivity()).getWindow().getAttributes();
        lp.alpha = 0.3f;
        ((AppCompatActivity) getActivity()).getWindow().setAttributes(lp);
        ((AppCompatActivity) getActivity()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        selfDialog.show();
    }



//
//    /**
//     * 通过反射，设置menu显示icon
//     *
//     * @param view
//     * @param menu
//     * @return
//     */
//    @SuppressLint("RestrictedApi")
//    @Override
//    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
//        if (menu != null) {
//            if (menu.getClass() == MenuBuilder.class) {
//                try {
//                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
//                    m.setAccessible(true);
//                    m.invoke(menu, true);
//                } catch (Exception e) {
//                }
//            }
//        }
//        return super.onPrepareOptionsPanel(view, menu);
//    }


    /**
     * 展示一个SnackBar
     */
    public void showSnackBar(String message) {
        //去掉虚拟按键
        ((AppCompatActivity) getActivity()).getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION //隐藏虚拟按键栏
                | View.SYSTEM_UI_FLAG_IMMERSIVE //防止点击屏幕时,隐藏虚拟按键栏又弹了出来
        );
        final Snackbar snackbar = Snackbar.make(((AppCompatActivity) getActivity()).getWindow().getDecorView(), message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("知道了", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
                //隐藏SnackBar时记得恢复隐藏虚拟按键栏,不然屏幕底部会多出一块空白布局出来,和难看
                ((AppCompatActivity) getActivity()).getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
        }).show();
    }

    public class AddReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra("type");
            String id = intent.getStringExtra("id");
            callSelfDialog(id);
        }
    }
}
