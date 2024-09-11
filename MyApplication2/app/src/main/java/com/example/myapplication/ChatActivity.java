package com.example.myapplication;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ChatActivity extends AppCompatActivity {

    private TextView chatTextView;
    private Button sendButton;
    private EditText messageEditText;
    private String user = "user";
    private boolean sendFlag = false;
    private Button changeUserButton;
    private EditText newUserEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatTextView = findViewById(R.id.chatTextView);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        newUserEditText = findViewById(R.id.newUserEditText);
        changeUserButton = findViewById(R.id.changeUserButton);

        changeUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = newUserEditText.getText().toString();
                Toast.makeText(ChatActivity.this, "用户名修改成功", Toast.LENGTH_SHORT).show();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFlag = true;
            }
        });

        MessageSender messageSender = new MessageSender();
        messageSender.start();

        MessageReceiver messageReceiver = new MessageReceiver();
        messageReceiver.start();
    }

    class MessageSender extends Thread {
        @Override
        public void run() {
            while (true) {
                while (sendFlag) {
                    try {
                        String msg = messageEditText.getText().toString().trim();

                        if (msg.isEmpty()) {
                            Looper.prepare();
                            Toast.makeText(ChatActivity.this, "消息不能为空", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                            sendFlag = false;
                            break;
                        }

                        if (msg.length() > 199) {
                            Looper.prepare();
                            Toast.makeText(ChatActivity.this, "消息长度超过限制", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                            sendFlag = false;
                            break;
                        }

                        String urlString = "http://yesapi.maxtral.fun/?s=App.SuperTable.Create&return_data=0&model_name=myAndroidChat&database=super&data={\"name\":\"" + user + "\",\"msg\":\"" + msg + "\"}&app_key=623DD798A9320007CADE94639092BC30&sign=FB975E4CA50650EAB683CC3E4386F37D";
                        URL url = new URL(urlString);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");

                        int responseCode = connection.getResponseCode();
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ChatActivity.this, "消息发送成功", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ChatActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        connection.disconnect();
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ChatActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    sendFlag = false;
                }
            }
        }
    }

    class MessageReceiver extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    // 构建 API URL
                    String urlString = "http://www.maxtral.fun/APIphp/myAndroidChatGet.php";
                    URL url = new URL(urlString);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = in.readLine()) != null) {
                            response.append(line);
                        }
                        in.close();
                        connection.disconnect();

                        // 解析 JSON 数据
                        JSONObject jsonResponse = new JSONObject(response.toString());
                        JSONObject data = jsonResponse.getJSONObject("data");
                        JSONArray items = data.getJSONArray("sql");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                chatTextView.setText(null); // 清空之前的聊天记录
                            }
                        });

                        for (int i = 0; i < items.length(); i++) {
                            JSONObject item = items.getJSONObject(i);
                            String name = item.getString("name");
                            String msg = item.getString("msg");
                            String sendTime = item.getString("sendtime");

                            final String message = name + " (" + sendTime + "): " + msg + "\n\n";
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    chatTextView.append(message); // 更新聊天记录
                                }
                            });
                        }
                    } else {
                        System.out.println("获取消息失败");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    sleep(5000); // 每5秒更新一次
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
