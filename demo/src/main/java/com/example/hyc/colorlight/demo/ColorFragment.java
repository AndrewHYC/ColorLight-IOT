package com.example.hyc.colorlight.demo;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerPreference;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by hyc on 18-5-25.
 */

public class ColorFragment extends PreferenceFragment {

    private static final String TAG = "ColorFragment";
    private String LightId;

    private static final String KEY_DEFAULT_COLOR = "color_picker_preference2";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.color);

        Bundle bundle = getArguments();
        if(bundle != null){
            LightId = bundle.getString("LIGHT_ID");
            Log.d(TAG, "Fragment onCreate: lightId="+LightId);
        }

        ColorPickerPreference colorPickerPreference = (ColorPickerPreference)findPreference(KEY_DEFAULT_COLOR);
        colorPickerPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {   //颜色选中时
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                if (KEY_DEFAULT_COLOR.equals(preference.getKey())) {
                    String newDefaultColor = Integer.toHexString((int) o);
                    Log.d(TAG, "2.New default color is: #" + newDefaultColor);
                    Toast.makeText(getActivity(),"选中灯光颜色: #"+newDefaultColor,Toast.LENGTH_SHORT).show();
                    initBanner(newDefaultColor, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String responseText = response.body().string();
                        }
                    });
                }
                    return true;
            }
        });

        colorPickerPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {    //颜色点击时
            @Override
            public boolean onPreferenceClick(Preference preference) {
                return true;
            }
        });
    }

    private void initBanner(String newColor, okhttp3.Callback callback) {
        OkHttpClient mOkHttpClient = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("Light_Id",LightId)
                .add("New_Color",newColor).build();

        Request request = new Request.Builder()
                .url("http://10.26.255.255/get_data.json")
                .post(formBody)
                .build();

        mOkHttpClient.newCall(request).enqueue(callback);
    }
}
