package com.example.clima;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button homeLoginButton;
    TextView regButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        homeLoginButton = (Button) findViewById(R.id.homeLoginBtn);
        homeLoginButton.setOnClickListener(this);
        regButton = (TextView) findViewById(R.id.homeRegBtn);
        regButton.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
            switch (v.getId()){
                case R.id.homeLoginBtn:startActivity(new Intent(this, LoginActivity.class));
                break;
                case R.id.homeRegBtn:startActivity(new Intent(this, RegisterActivity.class));
                    break;
            }
    }
}