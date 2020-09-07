package com.example.jolly.visitormanagement.models;


public class AboutModel {

    String content;
    String office;
    int img;

    public AboutModel(String content, int img, String office) {
        this.content = content;
        this.img = img;
        this.office = office;
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

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }
}