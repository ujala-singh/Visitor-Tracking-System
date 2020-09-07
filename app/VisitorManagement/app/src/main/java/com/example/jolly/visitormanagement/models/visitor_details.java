package com.example.jolly.visitormanagement.models;

import android.net.Uri;

/**
 * Created by Jolly on 23-Mar-18.
 */

public class visitor_details {
    public String name;
    public String oname;
    public String no;
    public String uid;
    public String visitor_email;
    public String purpose;
    public double latitude;
    public double longitude;
    public String type;
    public String comment;
    public float rating;
    public String gender;
    public int age;
    public String state;
    public String location;
    public String reason;
    public String TimeStamp;
    public String PhotoUrl;

    public visitor_details() {
    }

    public visitor_details(String name, String oname, String no, String type, String uid, String visitor_email, String purpose,
                           double latitude, double longitude, String comment, float rating, String gender
                            , int age, String state, String loc, String request, String date, String photoUrl) {

        this.name = name;
        this.oname = oname;
        this.no = no;
        this.type = type;
        this.gender = gender;
        this.uid = uid;
        this.visitor_email = visitor_email;
        this.purpose = purpose;
        this.latitude = latitude;
        this.longitude = longitude;
        this.comment = comment;
        this.rating = rating;
        this.age = age;
        this.state = state;
        this.location = loc;

        this.reason = request;
        this.TimeStamp = date;
        this.PhotoUrl = photoUrl;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOname() {
        return oname;
    }

    public void setOname(String oname) {
        this.oname = oname;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getVisitor_email() {
        return visitor_email;
    }

    public void setVisitor_email(String visitor_email) {
        this.visitor_email = visitor_email;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
