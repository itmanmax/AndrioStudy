package com.example.picture;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 获取布局文件中的ImageView组件
        ImageView imageBox = findViewById(R.id.imageBox);

        // 调用setWebImage方法，传入图片的URL地址和ImageView组件
        setWebImage("https://img.zcool.cn/community/011a5357b64c620000018c1b9e7e67.png@2o.png", imageBox);
    }

    /**
     * 从网络地址加载图片并设置到ImageView中
     *
     * @param webSrc  网络图片的URL地址
     * @param imageBox 要显示图片的ImageView控件
     */
    public void setWebImage(String webSrc, ImageView imageBox) {
        // 创建一个异步任务，用于在后台线程加载网络图片
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... voids) {
                try {
                    URL url = new URL(webSrc);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    connection.connect();
                    InputStream inputStream = connection.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    inputStream.close(); // 关闭输入流
                    connection.disconnect(); // 断开连接
                    return bitmap;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                if (bitmap != null) {
                    imageBox.setImageBitmap(bitmap);
                } else {
                    imageBox.setImageResource(R.drawable.ic_launcher_background); // 确保ic_launcher_background存在
                }
            }
        }.execute();
    }
}
