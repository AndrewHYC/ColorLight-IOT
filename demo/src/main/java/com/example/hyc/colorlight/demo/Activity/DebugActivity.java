package com.example.hyc.colorlight.demo.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.hyc.colorlight.demo.MQTT.MQTTService;
import com.example.hyc.colorlight.demo.R;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class DebugActivity extends AppCompatActivity{

    MQTTService myService = new MQTTService();
    Intent serviceIntent = null;

    private EditText mEmailView;
    private EditText mPasswordView;
    private TextView mCallbackView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);
        mCallbackView = (TextView)findViewById(R.id.callback_message_view);

        //注册广播接收器接收Mqtt回调信息
        MqttReceiver2 receiver=new MqttReceiver2();
        IntentFilter filter=new IntentFilter();
        filter.addAction("com.example.hyc.colorlight.demo.MQTT.MQTTService2");
        registerReceiver(receiver,filter);


        serviceIntent = new Intent(this, MQTTService.class);
        startService(serviceIntent);

        mPasswordView = (EditText) findViewById(R.id.password);


        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            myService.setTopic(email);
            myService.publish(password);
        }
    }

    @Override
    protected void onDestroy() {
        stopService(serviceIntent);
        super.onDestroy();
    }


    public class MqttReceiver2 extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle=intent.getExtras();
            String mqttMessage=bundle.getString("Message");

            SimpleDateFormat formatter    =   new    SimpleDateFormat    (" yyyy-MM-dd   HH:mm:ss     \n");
            Date curDate    =   new    Date(System.currentTimeMillis());//获取当前时间
            String    date    =    formatter.format(curDate);

            String old = mCallbackView.getText().toString();

            String newMessage = date + mqttMessage +old +"\n\n";
            mCallbackView.setText(newMessage);
        }
    }
}

