package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
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

public class TestLoginActivity extends AppCompatActivity {

    private EditText editTextAccount;
    private EditText editTextPassword;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_test);

        editTextAccount = findViewById(R.id.editTextAccount);
        editTextPassword = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.loginButton);
        Button backButton = findViewById(R.id.backButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = editTextAccount.getText().toString();
                String password = editTextPassword.getText().toString();

                new AuthenticateTask().execute(account, password); // 使用 AsyncTask 执行网络请求
            }
        });

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(TestLoginActivity.this, TestActivity.class);
            startActivity(intent);
            finish(); // 结束当前活动，防止返回按钮后退到这里
        });
    }

    private class AuthenticateTask extends AsyncTask<String, Void, Boolean> {
        private String account; // 用于保存账号信息

        @Override
        protected Boolean doInBackground(String... params) {
            account = params[0];
            String password = params[1];
            return authenticate(account, password);
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                // 登录成功，保存账号并跳转到 ButtonsActivity
                saveAccountToPreferences(account);
                Intent intent = new Intent(TestLoginActivity.this, ButtonsActivity.class);
                startActivity(intent);
            } else {
                // 登录失败，显示错误提示
                Toast.makeText(TestLoginActivity.this, "登录失败，请检查账号或密码", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean authenticate(String account, String password) {
        try {
            String encodedAccount = URLEncoder.encode(account, "UTF-8");
            String encodedPassword = URLEncoder.encode(password, "UTF-8");
            String apiUrl = "http://www.maxtral.fun/APIphp/MyEmailUserCheck.php?account=" + encodedAccount + "&password=" + encodedPassword;

            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONObject dataObject = jsonResponse.getJSONObject("data");

                if (dataObject.getInt("err_code") == 0) {
                    JSONArray sqlArray = dataObject.getJSONArray("sql");
                    if (sqlArray.length() > 0) {
                        JSONObject sqlObject = sqlArray.getJSONObject(0);
                        if (sqlObject.has("id") && sqlObject.getInt("id") != 0) {
                            return true; // 登录成功
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false; // 登录失败
    }

    // 保存账号到 SharedPreferences
    private void saveAccountToPreferences(final String account) {
        runOnUiThread(() -> {
            SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("account", account);
            editor.apply();
            Toast.makeText(TestLoginActivity.this, "账号已保存", Toast.LENGTH_SHORT).show();
        });
    }

    // 从 SharedPreferences 中读取账号
    private String getAccountFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        return sharedPreferences.getString("account", null);
    }
}
