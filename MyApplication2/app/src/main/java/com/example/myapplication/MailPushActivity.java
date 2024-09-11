package com.example.myapplication;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MailPushActivity extends AppCompatActivity {
    private Button imapButton, credentialsButton, apiQQButton, hintButton, runPythonButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_push);

        imapButton = findViewById(R.id.imapButton);
        credentialsButton = findViewById(R.id.credentialsButton);
        apiQQButton = findViewById(R.id.apiQQButton);
        hintButton = findViewById(R.id.hintButton);
        runPythonButton = findViewById(R.id.runPythonButton);

        imapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MailPushActivity.this, ImapAddressActivity.class);
                startActivity(intent);
            }
        });

        credentialsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MailPushActivity.this, EmailCredentialsActivity.class);
                startActivity(intent);
            }
        });

        apiQQButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MailPushActivity.this, QmsgKeyAndQQActivity.class);
                startActivity(intent);
            }
        });

        hintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.maxtral.fun/index.php/archives/28/"));
                startActivity(browserIntent);
            }
        });

        runPythonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        fetchAndRunPythonScript();
                    }
                }).start();
            }
        });
    }

    private String readImapAddressFromFile() {
        String fileName = "imap_address.txt";
        StringBuilder stringBuilder = new StringBuilder();

        try {
            FileInputStream fis = openFileInput(fileName);
            int character;
            while ((character = fis.read()) != -1) {
                stringBuilder.append((char) character);
            }
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MailPushActivity.this, "IMAP 地址读取失败", Toast.LENGTH_SHORT).show();
                }
            });
            return null;
        }

        return stringBuilder.toString().trim();
    }

    private void fetchAndRunPythonScript() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String account = sharedPreferences.getString("account", null);

        if (account == null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MailPushActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }

        // 从本地文件读取 IMAP 地址
        String imapServer = readImapAddressFromFile();
        if (imapServer == null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MailPushActivity.this, "读取 IMAP 地址失败", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }

        // 调用 API 并解析响应
        String urlString = "http://www.maxtral.fun/APIphp/myAndroiduseremailinfo.php?account=" + account;
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONObject data = jsonResponse.getJSONObject("data");
            JSONArray sqlArray = data.getJSONArray("sql");

            if (sqlArray.length() > 0) {
                JSONObject userInfo = sqlArray.getJSONObject(0);

                String emailAccount = userInfo.getString("emailaccount");
                String emailPassword = userInfo.getString("emailpassword");
                String qmsgKey = userInfo.getString("qmsgkey");
                String qqNumber = userInfo.getString("qqnumber");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showEmailInfoDialog(imapServer, emailAccount, emailPassword, qmsgKey, qqNumber);
                    }
                });
            } else {
                // 如果没有找到用户信息，调用API传递account
                sendUserAccountInfo(account);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MailPushActivity.this, "获取用户信息失败", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // 如果没有找到用户信息，调用API传递account
    private void sendUserAccountInfo(String account) {
        // 在子线程中调用 CreateUser.createUser() 方法
        new Thread(new Runnable() {
            @Override
            public void run() {
                CreateUser.createUser(account);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MailPushActivity.this, "账户信息已发送", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }



    private void showEmailInfoDialog(String imapServer, String emailAccount, String emailPassword, String qmsgKey, String qqNumber) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_email_info);

        TextView textViewImapAddress = dialog.findViewById(R.id.textViewImapAddress);
        TextView textViewEmailAccount = dialog.findViewById(R.id.textViewEmailAccount);
        TextView textViewEmailPassword = dialog.findViewById(R.id.textViewEmailPassword);
        TextView textViewQmsgKey = dialog.findViewById(R.id.textViewQmsgKey);
        TextView textViewQqNumber = dialog.findViewById(R.id.textViewQqNumber);
        Button buttonCancel = dialog.findViewById(R.id.buttonCancel);
        Button buttonConfirm = dialog.findViewById(R.id.buttonConfirm);

        textViewImapAddress.setText("IMAP地址: " + imapServer);
        textViewEmailAccount.setText("Email账户: " + emailAccount);
        textViewEmailPassword.setText("Email密码: " + emailPassword);
        textViewQmsgKey.setText("Qmsg Key: " + qmsgKey);
        textViewQqNumber.setText("QQ号码: " + qqNumber);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                // 创建并运行EmailChecker实例
                EmailChecker emailChecker = new EmailChecker(
                        imapServer,
                        emailAccount,
                        emailPassword,
                        qmsgKey,
                        qqNumber
                );

                // 调用检查邮件的方法
                emailChecker.checkEmail();
            }
        });

        // 设置弹窗显示位置和大小
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.8); // 80% 屏幕宽度
            layoutParams.gravity = Gravity.CENTER; // 弹窗居中显示
            window.setAttributes(layoutParams);
        }

        dialog.show();
    }
}
