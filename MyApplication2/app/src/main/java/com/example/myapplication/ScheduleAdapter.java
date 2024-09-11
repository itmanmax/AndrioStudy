package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ScheduleAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ScheduleItem> scheduleList;
    private DatabaseHelper myDb;

    public ScheduleAdapter(Context context, ArrayList<ScheduleItem> scheduleList) {
        this.context = context;
        this.scheduleList = scheduleList;
        this.myDb = new DatabaseHelper(context);
    }

    @Override
    public int getCount() {
        return scheduleList.size();
    }

    @Override
    public Object getItem(int position) {
        return scheduleList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.schedule_item, parent, false);
        }

        // 获取当前的日程项
        ScheduleItem currentItem = (ScheduleItem) getItem(position);

        // 设置时间、地点和描述
        TextView txtTime = convertView.findViewById(R.id.txtTime);
        TextView txtLocation = convertView.findViewById(R.id.txtLocation);
        TextView txtDescription = convertView.findViewById(R.id.txtDescription);
        txtTime.setText("时间：" + currentItem.getTime());
        txtLocation.setText("地点：" + currentItem.getLocation());
        txtDescription.setText("事情：" + currentItem.getDescription());

        // 设置背景颜色，未完成是橙色，已完成是绿色
        LinearLayout itemLayout = convertView.findViewById(R.id.scheduleItemLayout);
        if (currentItem.getStatus() == 0) {
            itemLayout.setBackgroundColor(context.getResources().getColor(android.R.color.holo_orange_light));
        } else {
            itemLayout.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_light));
        }

        // 改变日程状态按钮
        Button btnChangeStatus = convertView.findViewById(R.id.btnChangeStatus);
        btnChangeStatus.setOnClickListener(v -> {
            // 切换状态
            int newStatus = (currentItem.getStatus() == 0) ? 1 : 0;
            currentItem.setStatus(newStatus);

            // 更新数据库
            myDb.updateStatus(currentItem.getId(), newStatus);

            // 刷新列表
            notifyDataSetChanged();
        });

        // 删除日程按钮
        Button btnDeleteItem = convertView.findViewById(R.id.btnDeleteItem);
        btnDeleteItem.setOnClickListener(v -> {
            myDb.deleteItem(currentItem.getId());
            scheduleList.remove(position);
            notifyDataSetChanged();
        });
        return convertView;
    }
}
