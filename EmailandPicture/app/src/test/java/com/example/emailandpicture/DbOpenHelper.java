package com.example.emailandpicture;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class DbOpenHelper {
    private static String CLS = "com.mysql.jdbc.Driver";// MySql驱动
    private static String URL = "jdbc:mysql://8.130.97.54:22/test1";//外网地址
    private static String USER = "test1";//账号
    private static String PWD = "041129";//密码

    public static Connection conn;      // 连接对象
    public static Statement stmt;       // 命令集
    public static PreparedStatement pStmt; // 预编译命令集
    public static ResultSet rs;         // 结果表

    // 取得连接的方法
    public static void getConnection(){
        try{
            Class.forName(CLS);
            conn = DriverManager.getConnection(URL, USER, PWD);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // 关闭数据库操作对象
    public static void closeAll()
    {
        try {
            if(rs!=null){
                rs.close();
                rs=null;
            }
            if(stmt!=null){
                stmt.close();
                stmt=null;
            }
            if(pStmt!=null){
                pStmt.close();
                pStmt=null;
            }
            if(conn!=null){
                conn.close();
                conn=null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}