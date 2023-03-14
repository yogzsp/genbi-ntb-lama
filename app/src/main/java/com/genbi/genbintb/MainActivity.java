package com.genbi.genbintb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Animation topAnim = AnimationUtils.loadAnimation(this, R.anim.top_anim);
//        Animation RightAnim = AnimationUtils.loadAnimation(this, R.anim.right_anim);
//        Animation LeftAnim = AnimationUtils.loadAnimation(this, R.anim.left_anim);
//        ImageView logo = findViewById(R.id.LogoGenbi);
//        TextView text1 = findViewById(R.id.TitleLogo);
//        TextView text2 = findViewById(R.id.PlaceTitle);
//
//        logo.setAnimation(topAnim);
//        text1.setAnimation(LeftAnim);
//        text2.setAnimation(RightAnim);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        }, 1000L); //3000 L = 3 detik
    }

}