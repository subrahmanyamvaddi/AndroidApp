package com.example.subrahmanyamvaddi.detect;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.awt.font.TextAttribute;

public class MessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Intent intent = getIntent();
        String msg = intent.getStringExtra("Message");

        TextView textView = findViewById(R.id.textView);
        textView.setText(msg);
    }

    public void goFinal(View view) {
        startActivity(new Intent(this,FinalActivity.class));
    }
}
