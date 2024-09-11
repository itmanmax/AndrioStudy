package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ButtonsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buttons);

        Button chatButton = findViewById(R.id.chatButton);
        Button button2 = findViewById(R.id.button2);
        Button button3 = findViewById(R.id.button3);
        Button button4 = findViewById(R.id.button4);
        Button healthTipsButton = findViewById(R.id.healthTipsButton); // 更新此处

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ButtonsActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ButtonsActivity.this, MailPushActivity.class);
                startActivity(intent);
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创建一个AlertDialog.Builder
                AlertDialog.Builder builder = new AlertDialog.Builder(ButtonsActivity.this);
                builder.setTitle("请输入城市编码");

                // 设置输入框
                final EditText input = new EditText(ButtonsActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setHint("默认: 340209");  // 提示用户默认编码
                builder.setView(input);

                // 设置确认按钮
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String cityCode = input.getText().toString().trim();
                        if (cityCode.isEmpty()) {
                            cityCode = "340209";  // 默认编码
                        }

                        // 启动WeatherActivity并传递城市编码
                        Intent intent = new Intent(ButtonsActivity.this, WeatherActivity.class);
                        intent.putExtra("CITY_CODE", cityCode);
                        startActivity(intent);
                    }
                });

                // 设置取消按钮
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                // 显示对话框
                builder.show();
            }
        });

        healthTipsButton.setOnClickListener(v -> {
            Intent intent = new Intent(ButtonsActivity.this, HealthMainActivity.class); // 修正此处的类名
            startActivity(intent);
        });

        // TODO: 设置 button4 的点击事件
        Button backButton = findViewById(R.id.button5);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ButtonsActivity.this, TestActivity.class);
            startActivity(intent);
            finish(); // 结束当前活动，防止返回按钮后退到这里
        });
        Button scheduleManagementButton = findViewById(R.id.scheduleManagementButton);
        scheduleManagementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ButtonsActivity.this, ScheduleActivity.class);
                startActivity(intent);
            }
        });


    }
}
