package com.example.myapplication;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

public class WeatherService {
    private static final String API_KEY = "f10904bd40be33b413242e42e868a371"; // 替换为你的高德API密钥
    private static final String BASE_URL = "https://restapi.amap.com/v3/weather/weatherInfo";

    public static JSONObject getWeather(String cityCode) {
        OkHttpClient client = new OkHttpClient();
        String url = BASE_URL + "?key=" + API_KEY + "&city=" + cityCode + "&extensions=base&output=json";

        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                return new JSONObject(responseBody);
            } else {
                throw new Exception("Failed to fetch weather data");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
