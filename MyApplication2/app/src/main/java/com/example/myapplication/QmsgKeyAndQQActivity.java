package com.example.myapplication;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class QmsgKeyAndQQActivity extends AppCompatActivity {

    private EditText qmsgKeyEditText;
    private EditText qqNumberEditText;
    private Button saveButton;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qmsg_key_and_qq);

        qmsgKeyEditText = findViewById(R.id.qmsgKeyEditText);
        qqNumberEditText = findViewById(R.id.qqNumberEditText);
        saveButton = findViewById(R.id.saveButton);
        backButton = findViewById(R.id.backButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String qmsgKey = qmsgKeyEditText.getText().toString().trim();
                String qqNumber = qqNumberEditText.getText().toString().trim();

                if (qmsgKey.isEmpty() || qqNumber.isEmpty()) {
                    Toast.makeText(QmsgKeyAndQQActivity.this, "QmsgKey或QQ号码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            updateQmsgKeyAndQQNumber(qmsgKey, qqNumber);
                        }
                    }).start();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 返回到上一个界面
            }
        });
    }

    private void updateQmsgKeyAndQQNumber(String qmsgKey, String qqNumber) {
        String account = getAccountFromPreferences();
        if (account == null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(QmsgKeyAndQQActivity.this, "账户信息丢失，请重新登录", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }

        try {
            // API URL
            URL url = new URL("http://www.maxtral.fun/APIphp/MyEmailPushQmsgandqqWrite.php");

            // 创建请求参数字符串
            String postData = "account=" + account + "&qmsgkey=" + qmsgKey + "&qqnumber=" + qqNumber;

            // 创建连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", String.valueOf(postData.length()));
            connection.setDoOutput(true);

            // 发送请求参数
            try (OutputStream os = connection.getOutputStream()) {
                os.write(postData.getBytes(StandardCharsets.UTF_8));
                os.flush();
            }

            // 获取响应状态码
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 读取响应
                StringBuilder response = new StringBuilder();
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(QmsgKeyAndQQActivity.this, "信息更新成功", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(QmsgKeyAndQQActivity.this, "信息更新失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(QmsgKeyAndQQActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private String getAccountFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        return sharedPreferences.getString("account", null);
    }
}
