package com.example.emailandpicture;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

public class MainActivity extends AppCompatActivity {

    private Button btnFetchData;
    private TextView tvData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnFetchData = findViewById(R.id.btn_fetch_data);
        tvData = findViewById(R.id.tv_data);

        btnFetchData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 启动异步任务来获取数据
                new FetchDataTask().execute();
            }
        });
    }

    private class FetchDataTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String result = "Error";

            try {
                Connection conn = DbOpenHelper.getConnection();  // 获取数据库连接
                if (conn != null) {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT value FROM Login LIMIT 1");

                    if (rs.next()) {
                        result = rs.getString("value");  // 获取结果
                    }

                    // 关闭资源
                    rs.close();
                    stmt.close();
                    DbOpenHelper.closeResources();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                result = "Database Error: " + e.getMessage();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            // 更新UI
            tvData.setText(result);
        }
    }
}
