package com.example.myapplication;

import android.content.Intent;
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
import java.net.URLEncoder;

public class RegisterActivity extends AppCompatActivity {

    private EditText registerUsername;
    private EditText registerPassword;
    private Button registerButton;
    private Button returnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 初始化视图
        registerUsername = findViewById(R.id.registerUsername);
        registerPassword = findViewById(R.id.registerPassword);
        registerButton = findViewById(R.id.registerButton);
        returnButton = findViewById(R.id.returnButton);

        // 注册按钮点击事件
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = registerUsername.getText().toString().trim();
                String password = registerPassword.getText().toString().trim();

                if (account.isEmpty() || password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "账号或密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    registerAccount(account, password);
                }
            }
        });

        // 返回按钮点击事件，返回到登录页面
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, TestActivity.class);
                startActivity(intent);
                finish(); // 结束注册页面
            }
        });
    }

    // 注册账号的方法
    private void registerAccount(String account, String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 设置 API 的 URL
                    URL url = new URL("http://www.maxtral.fun/APIphp/MyEmailUserCreate.php");

                    // 创建 HttpURLConnection 对象
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);

                    // 创建 POST 请求的参数
                    String urlParameters = "account=" + URLEncoder.encode(account, "UTF-8") +
                            "&password=" + URLEncoder.encode(password, "UTF-8");

                    // 将参数写入请求体
                    OutputStream os = conn.getOutputStream();
                    os.write(urlParameters.getBytes());
                    os.flush();
                    os.close();

                    // 检查响应代码
                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String inputLine;
                        StringBuilder response = new StringBuilder();

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();

                        // 显示注册成功
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(RegisterActivity.this, "注册过程中发生错误", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }
}
