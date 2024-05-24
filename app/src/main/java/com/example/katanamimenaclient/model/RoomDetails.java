package com.example.katanamimenaclient.model;

public class RoomDetails {
    private String locationName;
    private String countryName;
    private int price;
    private float rating;
    private int ratingCount;
    private int imageResourceId;

    public RoomDetails(String locationName, String countryName, int price, float rating, int ratingCount, int imageResourceId) {
        this.locationName = locationName;
        this.countryName = countryName;
        this.price = price;
        this.rating = rating;
        this.ratingCount = ratingCount;
        this.imageResourceId = imageResourceId;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getCountryName() {
        return countryName;
    }

    public int getPrice() {
        return price;
    }

    public float getRating() {
        return rating;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }
}