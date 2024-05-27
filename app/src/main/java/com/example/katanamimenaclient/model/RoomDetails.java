package com.example.katanamimenaclient.model;

import java.util.Set;

public class RoomDetails {
    private String locationName;
    private String countryName;
    private double price;
    private float rating;
    private int ratingCount;
    private int imageResourceId;
    private String formatedDate;

    private Set<String> requestdDates;

    private String name;


    public RoomDetails(String locationName, String countryName, double price, float rating, int ratingCount, int imageResourceId, Set<String> requestedDates, String name) {
        this.locationName = locationName;
        this.countryName = countryName;
        this.price = price;
        this.rating = rating;
        this.ratingCount = ratingCount;
        this.imageResourceId = imageResourceId;
        this.requestdDates = requestedDates;
        this.name = name;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getCountryName() {
        return countryName;
    }

    public double getPrice() {
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

    public Set<String> getRequestdDates() {
        return requestdDates;
    }

    public void setRequestdDates(Set<String> requestdDates) {
        this.requestdDates = requestdDates;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}