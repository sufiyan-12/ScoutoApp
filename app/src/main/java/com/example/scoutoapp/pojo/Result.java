package com.example.scoutoapp.pojo;

import com.google.gson.annotations.SerializedName;

public class Result {
    @SerializedName("Make_ID")
    public int Make_ID;
    @SerializedName("Make_Name")
    public String Make_Name;
    @SerializedName("Model_ID")
    public int Model_ID;
    @SerializedName("Model_Name")
    public String Model_Name;

    public Result(int make_ID, String make_Name, int model_ID, String model_Name) {
        Make_ID = make_ID;
        Make_Name = make_Name;
        Model_ID = model_ID;
        Model_Name = model_Name;
    }

    public int getMake_ID() {
        return Make_ID;
    }

    public void setMake_ID(int make_ID) {
        Make_ID = make_ID;
    }

    public String getMake_Name() {
        return Make_Name;
    }

    public void setMake_Name(String make_Name) {
        Make_Name = make_Name;
    }

    public int getModel_ID() {
        return Model_ID;
    }

    public void setModel_ID(int model_ID) {
        Model_ID = model_ID;
    }

    public String getModel_Name() {
        return Model_Name;
    }

    public void setModel_Name(String model_Name) {
        Model_Name = model_Name;
    }
}
