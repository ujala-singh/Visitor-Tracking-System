package com.example.jolly.visitormanagement.models;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

public class PlaceInfo {
    private String name;
    private String address;
    private String phoneno;
    private String id;
    private Uri websiteuri;
    private LatLng latLng;
    private float rating;

    public PlaceInfo() {

    }

    public PlaceInfo(String name, String address, String phoneno, String id, Uri websiteuri, LatLng latLng, float rating) {
        this.name = name;
        this.address = address;
        this.phoneno = phoneno;
        this.id = id;
        this.websiteuri = websiteuri;
        this.latLng = latLng;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Uri getWebsiteuri() {
        return websiteuri;
    }

    public void setWebsiteuri(Uri websiteuri) {
        this.websiteuri = websiteuri;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}

