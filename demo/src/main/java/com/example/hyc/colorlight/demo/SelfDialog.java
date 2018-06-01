package com.example.hyc.colorlight.demo;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.ContentValues.TAG;

/**
 * Created by hyc on 18-5-25.
 */

public class SelfDialog extends Dialog {


    private Button yes;//确定按钮
    private Button no;//取消按钮
    private TextView titleTv;//消息标题文本
    private EditText dialog_light_name_text = null; //编辑灯名
    private EditText dialog_light_id_text = null;   //编辑灯ID
    private String titleStr;//从外界设置的title文本
//    private String messageStr;//从外界设置的消息文本
    private String new_light_name = null;
    private String new_light_id = null;
    private TextView warrningText = null;

    //确定文本和取消文本的显示内容
    private String yesStr, noStr;

    private onNoOnclickListener noOnclickListener;//取消按钮被点击了的监听器

    private PriorityListener yesListener;

    /**
 * 设置取消按钮的显示内容和监听
 *     * @param str
 * @param onNoOnclickListener
 */
    public void setNoOnclickListener(String str, onNoOnclickListener onNoOnclickListener) {
     if (str != null) {
     noStr = str;
     }
     this.noOnclickListener = onNoOnclickListener;
     }

    public interface PriorityListener {
        /**
         * 回调函数，用于在Dialog的监听事件触发后刷新Activity的UI显示
         */
        public void refreshPriorityUI(String name, String id);
    }



    public SelfDialog(Context context,String Id, String str, PriorityListener listener) {
        super(context, R.style.MyDialog);
        if(str != null){
            yesStr = str;
        }
        if(Id != null){
            new_light_id = Id;
        }
        this.yesListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_layout);
        //按空白处不能取消动画
        setCanceledOnTouchOutside(false);

        //初始化界面控件
        initView();

        //初始化界面数据
        initData();
        //初始化界面控件的事件
        initEvent();

    }

    /**
     * 初始化界面的确定和取消监听器
     */
    private void initEvent() {
        //设置确定按钮被点击后，向外界提供监听
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    if(dialog_light_id_text.getText().toString().equals("")){

                        warrningText.setText("");
                        warrningText.setText("不能没有产品ID哦");

                    }else{
                        if(dialog_light_name_text.getText().toString().equals("")){
                            new_light_name = "爱心灯";
                        }else{
                            Log.d(TAG, "onClick: "+dialog_light_name_text.getText().toString());
                            new_light_name = dialog_light_name_text.getText().toString();
                        }
                        Log.d(TAG, "onClick: "+dialog_light_id_text.getText().toString());
                        new_light_id = dialog_light_id_text.getText().toString();

                        dismiss();
                        yesListener.refreshPriorityUI(new_light_name, new_light_id);
                    }



            }
        });
        //设置取消按钮被点击后，向外界提供监听
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noOnclickListener != null) {
                    noOnclickListener.onNoClick();
                }
            }
        });

    }

    /**
     * 初始化界面控件的显示数据
     */
    private void initData() {
        //如果用户自定了title和message
        if (titleStr != null) {
            titleTv.setText(titleStr);
        }
        //如果设置按钮的文字
        if (yesStr != null) {
            yes.setText(yesStr);
        }
        if (noStr != null) {
            no.setText(noStr);
        }
    }

    /**
     * 初始化界面控件
     */
    private void initView() {
        yes = (Button) findViewById(R.id.yes);
        no = (Button) findViewById(R.id.no);
        titleTv = (TextView) findViewById(R.id.title);
        dialog_light_name_text = (EditText)findViewById(R.id.dialog_light_name_text);
        dialog_light_id_text = (EditText)findViewById(R.id.dialog_light_id_text);
        if(new_light_id != null){
            dialog_light_id_text.setText(new_light_id);
        }
        warrningText = (TextView)findViewById(R.id.warning);
    }

    /**
     * 从外界Activity为Dialog设置标题
     *
     * @param title
     */
    public void setTitle(String title) {
        titleStr = title;
    }

    /**
     * 设置确定按钮和取消被点击的接口
     */
//    public interface onYesOnclickListener {
//        public void onYesClick();YesOnclickListener
//    }
//
//    public class YesClick implements onYesOnclickListener{
//
//        public void onYesClick(){
//            dismiss();
//            String new_light_name = dialog_light_name_text.getText().toString();
//            String new_light_id = dialog_light_id_text.getText().toString();
//            listener.refreshPriorityUI(new_light_name, new_light_id);
//        }
//    }

    public interface onNoOnclickListener {
        public void onNoClick();
    }
}
