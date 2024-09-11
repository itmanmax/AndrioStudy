package com.example.testlogin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;



import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class LoginTestActivity extends AppCompatActivity {

    private EditText accountEditText, passwordEditText;
    private Button loginButton, registerButton;
    private static final String TAG = "LoginTestActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        accountEditText = findViewById(R.id.accountEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);

        loginButton.setOnClickListener(v -> {
            String account = accountEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            if (!account.isEmpty() && !password.isEmpty()) {
                // 执行登录操作
                new Thread(() -> {
                    boolean isAuthenticated = authenticate(account, password);
                    runOnUiThread(() -> {
                        if (isAuthenticated) {
                            // 登录成功，跳转到 ButtonsActivity
                            Log.d(TAG, "Login successful");
                            Intent intent = new Intent(LoginTestActivity.this, ButtonsActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // 登录失败，提示错误
                            Log.d(TAG, "Login failed");
                            Toast.makeText(LoginTestActivity.this, "账号或密码错误", Toast.LENGTH_SHORT).show();
                        }
                    });
                }).start();
            } else {
                Toast.makeText(LoginTestActivity.this, "请输入账号和密码", Toast.LENGTH_SHORT).show();
            }
        });

        registerButton.setOnClickListener(v -> {
            // 跳转到注册界面
            Intent intent = new Intent(LoginTestActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private boolean authenticate(String account, String password) {
        try {
            String encodedAccount = URLEncoder.encode(account, "UTF-8");
            String encodedPassword = URLEncoder.encode(password, "UTF-8");
            String apiUrl = "http://www.maxtral.fun/APIphp/MyEmailUserCheck.php?account=" + encodedAccount + "&password=" + encodedPassword;

            Log.d(TAG, "API URL: " + apiUrl);

            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");

            int responseCode = connection.getResponseCode();
            Log.d(TAG, "Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                Log.d(TAG, "Response: " + response.toString());

                // 解析 JSON 响应
                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONObject dataObject = jsonResponse.getJSONObject("data");

                // 检查 err_code 是否为 0
                if (dataObject.getInt("err_code") == 0) {
                    JSONArray sqlArray = dataObject.getJSONArray("sql");
                    // 检查 sql 数组中是否有 id，并且 id 不为 0
                    if (sqlArray.length() > 0) {
                        JSONObject sqlObject = sqlArray.getJSONObject(0);
                        if (sqlObject.has("id") && sqlObject.getInt("id") != 0) {
                            return true; // 登录成功
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in authenticate method", e);
        }
        return false; // 登录失败
    }
}
