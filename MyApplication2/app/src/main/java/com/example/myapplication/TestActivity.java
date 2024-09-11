package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

    public class TestActivity extends AppCompatActivity {


            private Button buttonToRegisterActivity;
            private Button buttonToTestActivityActivity;

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_test);


                buttonToRegisterActivity = findViewById(R.id.buttonToRegisterActivity);
                buttonToTestActivityActivity= findViewById(R.id.buttonTest);


        buttonToRegisterActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
                buttonToTestActivityActivity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(TestActivity.this, TestLoginActivity.class);
                        startActivity(intent);
                    }
                });
    }
}
