package com.example.myapplication;
public class ScheduleItem {
    private int id;
    private String time;
    private String location;
    private String description;
    private int status; // 0 表示未完成，1 表示已完成

    public ScheduleItem(int id, String time, String location, String description, int status) {
        this.id = id;
        this.time = time;
        this.location = location;
        this.description = description;
        this.status = status;
    }

    // 获取 ID
    public int getId() {
        return id;
    }

    // 获取时间
    public String getTime() {
        return time;
    }

    // 获取地点
    public String getLocation() {
        return location;
    }

    // 获取描述
    public String getDescription() {
        return description;
    }

    // 获取状态
    public int getStatus() {
        return status;
    }

    // 设置状态
    public void setStatus(int status) {
        this.status = status;
    }
}
