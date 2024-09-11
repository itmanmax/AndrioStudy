package com.example.myapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HealthTipsActivity extends AppCompatActivity {
    private TextView resultTextView;
    private Button fetchButton;
    private Button backButton; // 返回按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_tips);

        resultTextView = findViewById(R.id.resultTextView);
        fetchButton = findViewById(R.id.fetchButton);
        backButton = findViewById(R.id.button5); // 获取返回按钮的引用

        fetchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FetchHealthTipsTask().execute();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() { // 设置返回按钮的点击事件
            @Override
            public void onClick(View v) {
                finish(); // 结束当前Activity，返回到上一个Activity
            }
        });
    }
    private class FetchHealthTipsTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String apiURL = "http://www.maxtral.fun/APIphp/HealthTips.php";

            try {
                URL url = new URL(apiURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    return response.toString();
                } else {
                    return "GET请求未成功，响应码: " + responseCode;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "发生错误: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // 解析和提取内容
            StringBuilder formattedResult = new StringBuilder();
            try {
                // 正则表达式提取content
                Pattern pattern = Pattern.compile("\\[content\\] => (.*?)\\s*\\)");
                Matcher matcher = pattern.matcher(result);

                int index = 1;
                while (matcher.find()) {
                    String content = matcher.group(1); // 注意这里的索引调整
                    formattedResult.append(index++).append(". ").append(content).append("\n\n");
                }

                // 将格式化的结果显示在TextView中
                resultTextView.setText(formattedResult.toString());

            } catch (Exception e) {
                e.printStackTrace();
                resultTextView.setText("解析数据时发生错误");
            }
        }

    }
}
