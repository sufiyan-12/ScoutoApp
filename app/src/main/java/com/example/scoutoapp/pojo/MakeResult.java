package com.example.scoutoapp.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MakeResult {
    @SerializedName("Count")
    public int Count;
    @SerializedName("Message")
    public  String Message;
    @SerializedName("Results")
    public List<Make> Results;
    @SerializedName("SearchCriteria")
    public String SearchCriteria;
}
