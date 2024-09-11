package com.example.myapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HealthQueryActivity extends AppCompatActivity {

    private EditText queryInput;
    private Button consultButton;
    private TextView resultTextView;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_query);

        // 初始化组件
        queryInput = findViewById(R.id.queryInput);
        consultButton = findViewById(R.id.consultButton);
        resultTextView = findViewById(R.id.resultTextView);
        backButton = findViewById(R.id.backButton);

        // 咨询按钮点击事件
        consultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String queryWord = queryInput.getText().toString();
                if (!queryWord.isEmpty()) {
                    // 执行API查询
                    new FetchHealthAdviceTask().execute(queryWord);
                } else {
                    resultTextView.setText("请输入有效的健康症状");
                }
            }
        });

        // 返回按钮点击事件
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 返回到上一个活动
            }
        });
    }

    // 异步任务用于获取健康建议
    private class FetchHealthAdviceTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String word = params[0];
            String apiURL = "http://www.maxtral.fun/APIphp/Healthget.php?word=";
            String requestURL = apiURL + word;

            try {
                // 创建URL对象
                URL url = new URL(requestURL);
                // 打开连接
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // 设置请求方法为GET
                connection.setRequestMethod("GET");

                // 获取响应码
                int responseCode = connection.getResponseCode();

                // 当响应码为200时，表示成功
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String inputLine;

                    // 读取每一行的输入
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    // 返回响应结果
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
                Pattern pattern = Pattern.compile("\\[title\\] => (.*?)\\s*\\[content\\] => (.*?)\\s*\\)");
                Matcher matcher = pattern.matcher(result);

                int index = 1;
                while (matcher.find()) {
                    String content = matcher.group(2);
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
