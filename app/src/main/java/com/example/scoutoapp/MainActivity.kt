package com.example.scoutoapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class MainActivity : AppCompatActivity() {
    private lateinit var pref: SharedPreferences;
    private var USER_ID: Int = -1;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pref = getSharedPreferences("user_account", Context.MODE_PRIVATE)
        if(pref != null){
            USER_ID = pref.getInt("user_id", -1);
        }
        if(USER_ID == -1){
            Handler(Looper.myLooper()!!).postDelayed({
                startActivity(Intent(this, RegisterOrLoginActivity::class.java))
                finish()
            },4100)
        }else{
            Handler(Looper.myLooper()!!).postDelayed({
                startActivity(Intent(this, DashboardActivity::class.java))
                finish()
            },4100)
        }
    }
}