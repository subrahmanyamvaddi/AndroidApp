package com.example.subrahmanyamvaddi.detect;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;
import android.view.MotionEvent;
import android.view.GestureDetector;
import android.support.v4.view.GestureDetectorCompat;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnDbOpListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(findViewById(R.id.fragement_container) != null){

            if(savedInstanceState != null)
                return;

            HomeFragment fragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.fragement_container,fragment).commit();


        }

    }

    @Override
    public void dpOpPerformed(int method) {
        switch (method)
        {
            case 0:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragement_container,new LoginFragment()).addToBackStack(null).commit();
                break;
            case 1:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragement_container,new MainFragment()).addToBackStack(null).commit();
                break;
        }
    }
}
