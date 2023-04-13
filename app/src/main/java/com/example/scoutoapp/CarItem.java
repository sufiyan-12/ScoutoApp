package com.example.scoutoapp;

public class CarItem {
    private int make_id;
    private String car_make;
    private String car_model;
    private String imageUrl;
    private byte[] imageBytes;


    public CarItem(){
        this.make_id = 0;
        this.car_make = "";
        this.car_model = "";
        this.imageUrl = "";
        this.imageBytes = new byte[0];
    }

    public CarItem(int make_id, String car_make, String car_model, String imageUrl, byte[] imageBytes){
        this.make_id = make_id;
        this.car_make = car_make;
        this.car_model = car_model;
        this.imageUrl = imageUrl;
        this.imageBytes = imageBytes;
    }

    public byte[] getImageBytes() {
        return imageBytes;
    }

    public void setImageBytes(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }

    public int getMake_id() {
        return make_id;
    }

    public void setMake_id(int make_id) {
        this.make_id = make_id;
    }

    public String getCar_make() {
        return car_make;
    }

    public void setCar_make(String car_make) {
        this.car_make = car_make;
    }

    public String getCar_model() {
        return car_model;
    }

    public void setCar_model(String car_model) {
        this.car_model = car_model;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
