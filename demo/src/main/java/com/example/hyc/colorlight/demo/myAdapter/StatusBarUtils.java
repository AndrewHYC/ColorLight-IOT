package com.example.hyc.colorlight.demo.myAdapter;

import android.app.Activity;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by hyc on 18-6-3.
 */

public class StatusBarUtils {
    public static void setWindowStatusBarColor(Activity activity, int colorResId) {
        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                Window window = activity.getWindow();

                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

                window.setStatusBarColor(activity.getResources().getColor(colorResId));



                //底部导航栏

                //window.setNavigationBarColor(activity.getResources().getColor(colorResId));

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

    }
}
