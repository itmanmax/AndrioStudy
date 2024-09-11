package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class NewScheduleActivity extends AppCompatActivity {

    private EditText editTextTime;
    private EditText editTextLocation;
    private EditText editTextDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_schedule);

        editTextTime = findViewById(R.id.editTextTime);
        editTextLocation = findViewById(R.id.editTextLocation);
        editTextDescription = findViewById(R.id.editTextDescription);

        Button btnConfirm = findViewById(R.id.btnConfirm);
        Button btnCancel = findViewById(R.id.btnCancel);

        // 确定按钮点击事件
        btnConfirm.setOnClickListener(v -> {
            String time = editTextTime.getText().toString();
            String location = editTextLocation.getText().toString();
            String description = editTextDescription.getText().toString();

            // TODO: 处理新建日程的逻辑，例如保存数据到数据库

            // 返回到 ScheduleActivity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("TIME", time);
            resultIntent.putExtra("LOCATION", location);
            resultIntent.putExtra("DESCRIPTION", description);
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        // 取消按钮点击事件
        btnCancel.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }
}
