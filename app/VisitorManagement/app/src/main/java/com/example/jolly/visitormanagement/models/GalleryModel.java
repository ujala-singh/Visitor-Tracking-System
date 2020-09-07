package com.example.jolly.visitormanagement.models;

public class GalleryModel {
    String content;
    //    String office;
    int img;

    public GalleryModel(String content, int img) {
        this.content = content;
        this.img = img;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }
}

