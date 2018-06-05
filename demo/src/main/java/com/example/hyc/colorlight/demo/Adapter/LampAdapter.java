package com.example.hyc.colorlight.demo.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hyc.colorlight.demo.Activity.DebugActivity;
import com.example.hyc.colorlight.demo.Activity.MainActivity;
import com.example.hyc.colorlight.demo.Light;
import com.example.hyc.colorlight.demo.Activity.OtherActivity;
import com.example.hyc.colorlight.demo.R;

import java.util.List;
import java.util.Random;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * Created by hyc on 18-5-24.
 */

public class LightAdapter extends RecyclerView.Adapter<LightAdapter.myViewHolder>{

//    onCallbackListener listener;

    private Context mContext;
    private List<Light> mLightList;


    class myViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        CardView cardView;
        ImageView lightImage;
        TextView lightName;
        TextView lightId;
        TextView lightType;

//        Button deleteButton;

        public myViewHolder(View view){
            super(view);
            cardView = (CardView)view;
            lightImage = (ImageView)view.findViewById(R.id.light_image);
            lightName = (TextView)view.findViewById(R.id.light_name);
            lightId = (TextView)view.findViewById(R.id.light_id);
            lightType = (TextView)view.findViewById(R.id.light_type);
//            deleteButton = (Button)view.findViewById(R.id.delete);
//            deleteButton.setOnClickListener(this);
//            cardView.setOnClickListener(this);
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position;
            if((position = getAdapterPosition()) == -1){
                Log.d(TAG, "onClick: index = "+position);
                // position = 0;

                //there is an bug
                Random random = new Random();
                position = random.nextInt(mLightList.size());
            }

            Light light;
            light = mLightList.get(position);
            Log.d(TAG, "onClick: light.getIsConfig="+light.getIsConfig());
            Intent intent=null;
            switch (light.getType()){
                case "爱心灯":
                    if(light.getIsConfig()==0){
//                        intent = new Intent(mContext, WifiConnectActivity.class);
//                        intent.putExtra(WifiConnectActivity.TYPE, light.getType());
//                        intent.putExtra(WifiConnectActivity.ID, light.getId());
//                        intent.putExtra(WifiConnectActivity.NAME, light.getName());
//                        mContext.startActivity(intent);

                        intent= new Intent(mContext, MainActivity.class);
                        intent.putExtra("isConfig",false);
                        intent.putExtra(MainActivity.LIGHT_NAME, light.getName());
                        intent.putExtra(MainActivity.LIGHT_IMAGE_ID, light.getImageId());
                        intent.putExtra(MainActivity.LIGHT_ID, light.getId());
                        mContext.startActivity(intent);

                    }else if(light.getIsConfig()==1){
                        intent= new Intent(mContext, MainActivity.class);
                        intent.putExtra("isConfig",true);
                        intent.putExtra(MainActivity.LIGHT_NAME, light.getName());
                        intent.putExtra(MainActivity.LIGHT_IMAGE_ID, light.getImageId());
                        intent.putExtra(MainActivity.LIGHT_ID, light.getId());
                        mContext.startActivity(intent);
                    }
                    break;
                case "其他":

                    if(light.getName().equals("调试")||light.getName().equals("DEBUG")){
                        intent= new Intent(mContext, DebugActivity.class );
                        mContext.startActivity(intent);
                        }else{
                        intent = new Intent(mContext, OtherActivity.class);
                        intent.putExtra(OtherActivity.DEVICE_NAME, light.getName());
                        intent.putExtra(OtherActivity.DEVICE_ID,light.getId());
                        mContext.startActivity(intent);
                    }
                    break;
                default:
                    break;

            }

        }
    }

    public LightAdapter(List<Light> lightList){
        mLightList = lightList;
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(mContext == null){
            mContext = parent.getContext();
        }
        Log.d(TAG, "onCreateViewHolder: mContext = "+mContext);

//        listener = (onCallbackListener) mContext;

        View view = LayoutInflater.from(mContext).inflate(R.layout.light_item, parent,false);

        Log.d(TAG, "onCreateViewHolder: parent = "+parent);
        Log.d(TAG, "onCreateViewHolder: view = "+view);

        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(myViewHolder holder, int position) {
        Light light = mLightList.get(position);
        holder.lightName.setText(light.getName());
        Glide.with(mContext).load(light.getImageId()).into(holder.lightImage);
        holder.lightId.setText(light.getId());
        holder.lightType.setText(light.getType());
    }

    @Override
    public int getItemCount() {
        return mLightList.size();
    }



}
