package com.example.hyc.colorlight.demo;

/**
 * Created by hyc on 18-5-24.
 */

public class Light {
    private String name;
    private String id;
    private String type;
    private int imageId;
    private int isConfig;

    public int getIsConfig() {
        return isConfig;
    }

    public void setIsConfig(int isConfig) {
        this.isConfig = isConfig;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


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

    public Light(String name,String type, int imageId, String id,int isConfig){
        this.name = name;
        this.type = type;
        this.imageId = imageId;
        this.id = id;
        this.isConfig = isConfig;
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
