package com.example.emailandpicture;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DbOpenHelper {
    private static final String CLS = "com.mysql.jdbc.Driver"; // MySQL驱动
    private static final String URL = "jdbc:mysql://8.130.97.54:3306/test1?useSSL=false";
    private static final String USER = "test1";
    private static final String PWD = "041129";

    public static Connection conn;      // 连接对象
    public static Statement stmt;       // 命令集
    public static PreparedStatement pStmt; // 预编译命令集
    public static ResultSet rs;         // 结果表

    static {
        try {
            // 加载 MySQL 驱动
            Class.forName(CLS);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("MySQL driver not found", e);
        }
    }

    public static Connection getConnection() {
        if (conn == null) {
            try {
                conn = DriverManager.getConnection(URL, USER, PWD);
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to connect to database", e);
            }
        }
        return conn;
    }

    public static void closeResources() {
        try {
            if (rs != null) rs.close();
            if (pStmt != null) pStmt.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
