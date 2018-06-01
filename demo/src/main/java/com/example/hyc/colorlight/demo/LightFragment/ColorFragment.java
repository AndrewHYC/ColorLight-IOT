package com.example.hyc.colorlight.demo.LightFragment;

import android.app.Activity;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.Toast;

import com.example.hyc.colorlight.demo.R;
import com.flask.colorpicker.ColorPickerPreference;

/**
 * Created by hyc on 18-5-25.
 */

public class ColorFragment extends PreferenceFragment implements ColorPickerPreference.OnPreferenceChangeListener{

    //用于用来与外部activity交互，获取到宿主activity
    private FragmentInteraction2 listener;

    //定义了所有activity必须实现的接口方法
    public interface FragmentInteraction2 {
        void process2(String str);
    }

    private static final String TAG = "ColorFragment";
    private String LightId;

    private static final String KEY_DEFAULT_COLOR = "color_picker_preference2";

    private String newDefaultColor;

    // 当FRagmen被加载到activity的时候会被回调
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if(activity instanceof DemoFragment.FragmentInteraction) {
            listener = (FragmentInteraction2)activity; // 2.2 获取到宿主activity并赋值
        } else{
            throw new IllegalArgumentException("activity must implements FragmentInteraction");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.color);
//
//        Bundle bundle = getArguments();
//        if(bundle != null){
//            LightId = bundle.getString("LIGHT_ID");
//            Log.d(TAG, "Fragment onCreate: lightId="+LightId);
//        }

        ColorPickerPreference colorPickerPreference = (ColorPickerPreference)findPreference(KEY_DEFAULT_COLOR);
        colorPickerPreference.setOnPreferenceChangeListener(this);

        colorPickerPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {    //颜色点击时
            @Override
            public boolean onPreferenceClick(Preference preference) {
                return true;
            }
        });
    }

//    private void initBanner(String newColor, okhttp3.Callback callback) {
//        OkHttpClient mOkHttpClient = new OkHttpClient();
//        RequestBody formBody = new FormBody.Builder()
//                .add("Light_Id",LightId)
//                .add("New_Color",newColor).build();
//
//        Request request = new Request.Builder()
//                .url("http://10.26.255.255/get_data.json")
//                .post(formBody)
//                .build();
//
//        mOkHttpClient.newCall(request).enqueue(callback);
//    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (KEY_DEFAULT_COLOR.equals(preference.getKey())) {
            newDefaultColor = Integer.toHexString((int) newValue);
            Log.d(TAG, "1.New default color is: #" + newDefaultColor);
            Toast.makeText(getActivity(),"选中灯光颜色: #"+newDefaultColor,Toast.LENGTH_SHORT).show();
            listener.process2(newDefaultColor);
//          initBanner(newDefaultColor, new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//              e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//              String responseText = response.body().string();
//            }
//          });
        }
        return true;
    }

    //把传递进来的activity对象释放掉
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
