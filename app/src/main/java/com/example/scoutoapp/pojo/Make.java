package com.example.scoutoapp.pojo;

import com.google.gson.annotations.SerializedName;

public class Make {
    @SerializedName("Make_ID")
    public int MAKE_ID;
    @SerializedName("Make_Name")
    public String MAKE_NAME;

    public Make(int MAKE_ID, String MAKE_NAME) {
        this.MAKE_ID = MAKE_ID;
        this.MAKE_NAME = MAKE_NAME;
    }

    public int getMAKE_ID() {
        return MAKE_ID;
    }

    public void setMAKE_ID(int MAKE_ID) {
        this.MAKE_ID = MAKE_ID;
    }

    public String getMAKE_NAME() {
        return MAKE_NAME;
    }

    public void setMAKE_NAME(String MAKE_NAME) {
        this.MAKE_NAME = MAKE_NAME;
    }
}
