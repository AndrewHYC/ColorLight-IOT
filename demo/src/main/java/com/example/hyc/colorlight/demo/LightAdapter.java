package com.example.hyc.colorlight.demo;

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
import com.example.hyc.colorlight.demo.Activity.MainActivity;

import java.util.List;
import java.util.Random;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * Created by hyc on 18-5-24.
 */

public class LightAdapter extends RecyclerView.Adapter<LightAdapter.myViewHolder>{

//    onCallbackListener listener;

    class myViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        CardView cardView;
        ImageView lightImage;
        TextView lightName;
        TextView lightId;
//        Button deleteButton;

        public myViewHolder(View view){
            super(view);
            cardView = (CardView)view;
            lightImage = (ImageView)view.findViewById(R.id.light_image);
            lightName = (TextView)view.findViewById(R.id.light_name);
            lightId = (TextView)view.findViewById(R.id.light_id);
//            deleteButton = (Button)view.findViewById(R.id.delete);
//            deleteButton.setOnClickListener(this);
//            cardView.setOnClickListener(this);
            lightImage.setOnClickListener(this);
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

            final Light light;
            light = mLightList.get(position);

            switch (view.getId()){
//                case R.id.delete:
//                    final AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
//                    dialog.setTitle("警告");
//                    dialog.setMessage("确定要删除吗?");
//                    dialog.setCancelable(true);
//                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//
//                        }
//                    });
//                    dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                        }
//                    });
//                    dialog.show();
//                    break;
                case R.id.light_image:
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.putExtra(MainActivity.LIGHT_NAME, light.getName());
                    intent.putExtra(MainActivity.LIGHT_IMAGE_ID, light.getImageId());
                    intent.putExtra(MainActivity.LIGHT_ID, light.getId());
                    mContext.startActivity(intent);
                    break;
                default:
                    break;

            }

        }
    }

    private Context mContext;
    private List<Light> mLightList;

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
    }

    @Override
    public int getItemCount() {
        return mLightList.size();
    }

}
