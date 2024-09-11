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

public class EmailCredentialsActivity extends AppCompatActivity {

    private EditText emailAccountEditText;
    private EditText emailPasswordEditText;
    private Button saveButton;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_credentials);

        emailAccountEditText = findViewById(R.id.emailAccountEditText);
        emailPasswordEditText = findViewById(R.id.emailPasswordEditText);
        saveButton = findViewById(R.id.saveButton);
        backButton = findViewById(R.id.backButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailAccount = emailAccountEditText.getText().toString().trim();
                String emailPassword = emailPasswordEditText.getText().toString().trim();

                if (emailAccount.isEmpty() || emailPassword.isEmpty()) {
                    Toast.makeText(EmailCredentialsActivity.this, "邮箱账号或密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            updateEmailInfo(emailAccount, emailPassword);
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

    private void updateEmailInfo(String emailAccount, String emailPassword) {
        String account = getAccountFromPreferences();
        if (account == null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(EmailCredentialsActivity.this, "账户信息丢失，请重新登录", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }

        try {
            // 构建 URL
            URL url = new URL("http://www.maxtral.fun/APIphp/MyEmailPushUserWrite.php");

            // 创建请求参数字符串
            String postData = "account=" + account + "&emailaccount=" + emailAccount + "&emailpassword=" + emailPassword;

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
                        Toast.makeText(EmailCredentialsActivity.this, "信息更新成功", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(EmailCredentialsActivity.this, "信息更新失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(EmailCredentialsActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private String getAccountFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        return sharedPreferences.getString("account", null);
    }
}
