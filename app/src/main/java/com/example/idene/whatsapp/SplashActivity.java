package com.example.idene.whatsapp;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.idene.whatsapp.activity.LoginActivity;

public class SplashActivity extends AppCompatActivity {
    static int splash_timeout=500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);



//        Handler().postDelayed(object :Runnable {
//            override void run()
//            {
//                Intent home=Intent(this@,LoginActivity::class.java)
//                // val home=Intent(this,LoginActivity::class.java)
//                startActivity(home);
//                finish();
//            }
//        },splash_timeout.toLong())


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i=new Intent(SplashActivity.this,
                        LoginActivity.class);
                //Intent is used to switch from one activity to another.

                startActivity(i);
                //invoke the SecondActivity.

                finish();
                //the current activity will get finished.
            }
        }, splash_timeout);
    }

}
