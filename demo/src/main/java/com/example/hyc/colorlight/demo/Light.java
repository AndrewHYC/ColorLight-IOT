package com.example.hyc.colorlight.demo;

/**
 * Created by hyc on 18-5-24.
 */

public class Light {
    private String name;
    private String id;
    private int imageId;

    public String getNewColor() {
        return newColor;
    }

    private String newColor;

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public void setNewColor(String newColor) {
        this.newColor = newColor;
    }



    public Light(String name, int imageId, String id){
        this.name = name;
        this.imageId = imageId;
        this.id = id;
    }

    public int getImageId() {
        return imageId;
    }


    public String getName() {
        return name;
    }


    public String getId() {
        return id;
    }

}
