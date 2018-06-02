package com.example.hyc.colorlight.demo.HomeFragment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hyc.colorlight.demo.Activity.DebugActivity;
import com.example.hyc.colorlight.demo.Activity.MainActivity;
import com.example.hyc.colorlight.demo.Light;
import com.example.hyc.colorlight.demo.Menu;
import com.example.hyc.colorlight.demo.R;

import java.util.List;
import java.util.Random;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * Created by hyc on 18-6-1.
 */

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {



    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        View view;
        ImageView menuImage;
        TextView menuName;
        ImageView menuDefaultImg;

        public ViewHolder(View view){
            super(view);
            this.view = view;
            menuImage = (ImageView)view.findViewById(R.id.menu_img);
            menuName = (TextView)view.findViewById(R.id.menu_con);
            menuDefaultImg = (ImageView)view.findViewById(R.id.default_img);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position;
            if((position = getAdapterPosition()) == -1) {
                position = 0;
            }

            switch (position) {
                case 0:
                    try {
                        view.getContext().startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://www.baidu.com")));
                    } catch (ActivityNotFoundException ignored) {
                    }
                    break;
                case 1:
                    try {
                        view.getContext().startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://www.qq.com")));
                    } catch (ActivityNotFoundException ignored) {
                    }
                    break;
                case 2:
                    if (false == true) {
                        try {
                            view.getContext().startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("https://www.136.com")));
                        } catch (ActivityNotFoundException ignored) {
                        }
                    } else {
                    }
                    break;
                default:
                    break;
            }

        }
    }

    private List<Menu> mMenuList;

    public MenuAdapter(List<Menu> menuList){
        mMenuList = menuList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Menu menu = mMenuList.get(position);
        holder.menuDefaultImg.setImageResource(menu.getImage2());
        holder.menuImage.setImageResource(menu.getImageId());
        holder.menuName.setText(menu.getMenu_name());

    }

    @Override
    public int getItemCount() {
        return mMenuList.size();
    }

}
