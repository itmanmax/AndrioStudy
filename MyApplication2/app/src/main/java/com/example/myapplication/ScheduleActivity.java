package com.example.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ScheduleActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_NEW_SCHEDULE = 1; // 添加请求码
    DatabaseHelper myDb;
    ListView listView;
    ArrayList<ScheduleItem> scheduleList;
    ScheduleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        myDb = new DatabaseHelper(this);
        listView = findViewById(R.id.scheduleList);

        // 加载日程数据
        loadScheduleData();

        Button btnNew = findViewById(R.id.btnNew);
        Button btnDelete = findViewById(R.id.btnDelete);
        Button btnBack = findViewById(R.id.btnBack);

        // 新建日程按钮点击事件
        btnNew.setOnClickListener(v -> {
            Intent intent = new Intent(ScheduleActivity.this, NewScheduleActivity.class);
            startActivityForResult(intent, REQUEST_CODE_NEW_SCHEDULE);
        });
        // ScheduleActivity.java
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ScheduleActivity.this, ButtonsActivity.class);
            startActivity(intent);
            finish(); // 结束当前的 ScheduleActivity
        });


        // 删除日程按钮点击事件
        btnDelete.setOnClickListener(v -> {
            // TODO: 实现删除功能
        });

        // 返回按钮点击事件
        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_NEW_SCHEDULE && resultCode == RESULT_OK) {
            String time = data.getStringExtra("TIME");
            String location = data.getStringExtra("LOCATION");
            String description = data.getStringExtra("DESCRIPTION");

            // 默认状态为未完成（0）
            int status = 0;

            // 将新建的日程信息插入数据库
            myDb.insertData(time, location, description, status);

            // 重新加载日程数据
            loadScheduleData();
        }
    }

    // 加载日程数据
    private void loadScheduleData() {
        scheduleList = new ArrayList<>();
        Cursor res = myDb.getAllData();
        while (res.moveToNext()) {
            int id = res.getInt(0);
            String time = res.getString(1);
            String location = res.getString(2);
            String description = res.getString(3);
            int status = res.getInt(4);
            scheduleList.add(new ScheduleItem(id, time, location, description, status));
        }
        adapter = new ScheduleAdapter(this, scheduleList);
        listView.setAdapter(adapter);
    }

}
