package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class HealthMainActivity extends AppCompatActivity {

    private Button healthTipsButton;
    private Button consultButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_main);

        healthTipsButton = findViewById(R.id.healthTipsButton);
        consultButton = findViewById(R.id.consultButton);

        healthTipsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 启动健康小贴士活动
                Intent intent = new Intent(HealthMainActivity.this, HealthTipsActivity.class);
                startActivity(intent);
            }
        });

        consultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 启动症状咨询活动
                Intent intent = new Intent(HealthMainActivity.this, HealthQueryActivity.class);
                startActivity(intent);
            }
        });
        Button backButton = findViewById(R.id.button5);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(HealthMainActivity.this, ButtonsActivity.class);
            startActivity(intent);
            finish(); // 结束当前活动，防止返回按钮后退到这里
        });

    }
}
