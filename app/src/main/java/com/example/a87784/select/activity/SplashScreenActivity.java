package com.example.a87784.select.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.a87784.select.R;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        SharedPreferences sharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
        if(!(sharedPreferences.getString("name","").isEmpty())){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(SplashScreenActivity.this,MainActivity.class);
                    startActivity(i);
                    SplashScreenActivity.this.finish();
                }
            },3000);    //启动页面持续3秒结束。进入主界面
        }else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(SplashScreenActivity.this,LoginActivity.class);
                    startActivity(i);
                    SplashScreenActivity.this.finish();
                }
            },3000);    //启动页面持续3秒结束。进入主界面
        }


    }
}
