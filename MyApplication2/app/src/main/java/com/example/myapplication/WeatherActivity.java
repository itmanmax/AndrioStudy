package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.data.WeatherDatabaseHelper;
import org.json.JSONObject;
import android.content.Intent;

public class WeatherActivity extends AppCompatActivity {

    private TextView weatherInfoTextView;
    private ImageView weatherIconImageView;
    private WeatherDatabaseHelper dbHelper;
    private Button backButton; // 返回按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        weatherInfoTextView = findViewById(R.id.weatherInfo);
        weatherIconImageView = findViewById(R.id.weatherIcon);
        backButton = findViewById(R.id.button5); // 初始化返回按钮
        dbHelper = new WeatherDatabaseHelper(this);

        // 从Intent中获取城市编码
        Intent intent = getIntent();
        String cityCode = intent.getStringExtra("CITY_CODE");

        if (cityCode == null || cityCode.isEmpty()) {
            cityCode = "340209"; // 默认城市编码
        }

        // 获取天气数据并更新UI
        fetchWeatherData(cityCode);

        // 设置返回按钮的点击事件
        backButton.setOnClickListener(v -> finish());  // 结束当前活动，返回上一活动
    }

    private void fetchWeatherData(String cityCode) {
        new Thread(() -> {
            JSONObject weatherData = WeatherService.getWeather(cityCode);
            if (weatherData != null) {
                try {
                    JSONObject live = weatherData.getJSONArray("lives").getJSONObject(0);
                    String city = live.getString("city");
                    double temperature = live.getDouble("temperature");
                    String condition = live.getString("weather");
                    String updateTime = live.getString("reporttime");

                    // 将数据保存到数据库
                    dbHelper.insertWeather(city, temperature, condition, updateTime);

                    // 更新 UI
                    runOnUiThread(() -> {
                        weatherInfoTextView.setText(
                                String.format("City: %s\nTemperature: %.2f°C\nCondition: %s\nUpdated: %s",
                                        city, temperature, condition, updateTime));
                        updateWeatherIcon(condition);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                runOnUiThread(() -> weatherInfoTextView.setText("Failed to retrieve weather data."));
            }
        }).start();
    }

    private void updateWeatherIcon(String condition) {
        // 根据天气条件更改图片
        if (condition.contains("晴")) {
            weatherIconImageView.setImageResource(R.drawable.sunny); // 晴天图片
        } else if (condition.contains("多云")) {
            weatherIconImageView.setImageResource(R.drawable.cloudy); // 多云图片
        } else if (condition.contains("阴")) {
            weatherIconImageView.setImageResource(R.drawable.overcast); // 阴天图片
        } else if (condition.contains("雨")) {
            weatherIconImageView.setImageResource(R.drawable.rainy); // 有雨图片
        } else {
            weatherIconImageView.setImageResource(R.drawable.unknown); // 未知天气图片
        }
    }
}
