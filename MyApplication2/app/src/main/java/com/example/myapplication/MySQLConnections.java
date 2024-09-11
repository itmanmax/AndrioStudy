package com.example.myapplication;

import java.sql.Connection;
import java.sql.DriverManager;

public class MySQLConnections {
    private String driver = "com.mysql.jdbc.Driver";
    private String dbURL = "jdbc:mysql://10.0.2.2:3307/test1?useSSL=false&serverTimezone=UTC";
    private String user = "test1";
    private String password = "041129";
    private static MySQLConnections instance = null;
    private MySQLConnections() throws Exception {
        // 注册驱动
        Class.forName(driver);
        System.out.println("Driver Registered!");
    }

    public static Connection getConnection() {
        Connection conn = null;
        if (instance == null) {
            try {
                instance = new MySQLConnections();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        try {
            // 创建连接
            conn = DriverManager.getConnection(instance.dbURL,
                    instance.user, instance.password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }
}
