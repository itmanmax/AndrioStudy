package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImapAddressActivity extends AppCompatActivity {
    private EditText imapAddressEditText;
    private Button saveButton, readButton, backButton;
    private TextView messageTextView;
    private static final String FILE_NAME = "imap_address.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imap_address);

        imapAddressEditText = findViewById(R.id.imapAddressEditText);
        saveButton = findViewById(R.id.saveButton);
        readButton = findViewById(R.id.readButton);
        backButton = findViewById(R.id.backButton);
        messageTextView = findViewById(R.id.messageTextView);

        // 保存按钮点击事件
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imapAddress = imapAddressEditText.getText().toString().trim();
                if (imapAddress.isEmpty()) {
                    Toast.makeText(ImapAddressActivity.this, "IMAP地址不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    FileOutputStream fos = openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
                    fos.write(imapAddress.getBytes());
                    fos.close();
                    Toast.makeText(ImapAddressActivity.this, "IMAP地址已保存", Toast.LENGTH_SHORT).show();
                    messageTextView.setText("保存成功");
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(ImapAddressActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 读取按钮点击事件
        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    FileInputStream fis = openFileInput(FILE_NAME);
                    int character;
                    StringBuilder stringBuilder = new StringBuilder();
                    while ((character = fis.read()) != -1) {
                        stringBuilder.append((char) character);
                    }
                    fis.close();
                    String savedAddress = stringBuilder.toString().trim();
                    if (savedAddress.isEmpty()) {
                        messageTextView.setText("保存内容为空");
                    } else {
                        imapAddressEditText.setText(savedAddress);
                        messageTextView.setText("读取成功");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(ImapAddressActivity.this, "读取失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 返回按钮点击事件
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ImapAddressActivity.this, MailPushActivity.class);
                startActivity(intent);
                finish(); // 结束当前活动
            }
        });
    }
}
