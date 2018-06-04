package com.example.hyc.colorlight.demo.Adapter;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hyc.colorlight.demo.Activity.GuideActivity;
import com.example.hyc.colorlight.demo.Activity.WifiConnectActivity;
import com.example.hyc.colorlight.demo.Menu;
import com.example.hyc.colorlight.demo.R;

import java.util.List;

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
                    Intent intent = new Intent(view.getContext(), WifiConnectActivity.class);
                    intent.putExtra(WifiConnectActivity.TYPE, "");
                    intent.putExtra(WifiConnectActivity.ID, "");
                    intent.putExtra(WifiConnectActivity.NAME, "");
                    view.getContext().startActivity(intent);

                    break;
                case 1:
                    try {
                        Intent intent1 = new Intent(view.getContext(), GuideActivity.class);
                        view.getContext().startActivity(intent1);

                    } catch (ActivityNotFoundException ignored) {
                    }
                    break;
                case 2:
                    try {
                        view.getContext().startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://www.qq.com")));
                    } catch (ActivityNotFoundException ignored) {
                    }
                    break;
                case 3:
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
