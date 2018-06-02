package com.example.hyc.colorlight.demo;

/**
 * Created by hyc on 18-6-1.
 */

public class Menu {
    private String menu_name;
    private int imageId;
    private int image2;

    public Menu(String name,int imageId,int image2){
        this.menu_name=name;
        this.imageId=imageId;
        this.image2=image2;
    }

    public String getMenu_name() {
        return menu_name;
    }

    public void setMenu_name(String menu_name) {
        this.menu_name = menu_name;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getImage2() {
        return image2;
    }

    public void setImage2(int image2) {
        this.image2 = image2;
    }
}
