package com.example.myapplication;

import com.sun.mail.imap.IMAPMessage;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.search.FlagTerm;
import android.os.AsyncTask;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class EmailChecker {
    private String imapServer;
    private String emailAccount;
    private String emailPassword;
    private String qmsgKey;
    private String qqNumber;

    public EmailChecker(String imapServer, String emailAccount, String emailPassword, String qmsgKey, String qqNumber) {
        this.imapServer = imapServer;
        this.emailAccount = emailAccount;
        this.emailPassword = emailPassword;
        this.qmsgKey = qmsgKey;
        this.qqNumber = qqNumber;
    }

    public void checkEmail() {
        new CheckEmailTask().execute();
    }

    private class CheckEmailTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            Properties properties = new Properties();
            properties.put("mail.imap.ssl.enable", "true");
            properties.put("mail.imap.auth", "true");

            try {
                Session session = Session.getInstance(properties);
                Store store = session.getStore("imap");
                store.connect(imapServer, emailAccount, emailPassword);

                Folder inbox = store.getFolder("INBOX");
                inbox.open(Folder.READ_WRITE);  // 需要设置为READ_WRITE以便修改邮件状态

                Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));

                for (Message message : messages) {
                    if (message instanceof IMAPMessage) {
                        IMAPMessage imapMessage = (IMAPMessage) message;

                        String subject = imapMessage.getSubject();
                        String from = ((InternetAddress) imapMessage.getFrom()[0]).getPersonal();
                        if (from == null) {
                            from = ((InternetAddress) imapMessage.getFrom()[0]).getAddress();
                        }
                        String content = getTextFromMessage(imapMessage);  // 获取纯文本内容
                        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(imapMessage.getReceivedDate());

                        String msg = "你有新邮件：发件人[" + from + "], 邮件主题是[" + subject + "], 邮件文本内容是[" + content + "], 邮件发送时间是[" + date + "]";

                        Log.d("EmailChecker", "Subject: " + subject);
                        Log.d("EmailChecker", "From: " + from);
                        Log.d("EmailChecker", "Content: " + content);
                        Log.d("EmailChecker", "Date: " + date);

                        // 发送Qmsg通知
                        if (sendQmsgNotification(msg)) {
                            // 标记邮件为已读
                            imapMessage.setFlag(Flags.Flag.SEEN, true);
                        }

                        // 延时5秒发送下一个通知，避免推送过快
                        Thread.sleep(5000);
                    }
                }

                inbox.close(true);  // 确保保存对邮件状态的更改
                store.close();
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            } catch (MessagingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        private String getTextFromMessage(Message message) throws Exception {
            if (message.isMimeType("text/plain")) {
                return message.getContent().toString();
            } else if (message.isMimeType("multipart/*")) {
                MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
                return getTextFromMimeMultipart(mimeMultipart);
            }
            return "";
        }

        private String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws Exception {
            StringBuilder result = new StringBuilder();
            int count = mimeMultipart.getCount();
            for (int i = 0; i < count; i++) {
                BodyPart bodyPart = mimeMultipart.getBodyPart(i);
                if (bodyPart.isMimeType("text/plain")) {
                    result.append(bodyPart.getContent());
                    break;  // 如果找到纯文本部分就结束遍历
                } else if (bodyPart.isMimeType("text/html")) {
                    String html = (String) bodyPart.getContent();
                    result.append(org.jsoup.Jsoup.parse(html).text());
                } else if (bodyPart.getContent() instanceof MimeMultipart) {
                    result.append(getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent()));
                }
            }
            return result.toString();
        }

        private boolean sendQmsgNotification(String msg) {
            OkHttpClient client = new OkHttpClient();
            FormBody body = new FormBody.Builder()
                    .add("msg", msg)
                    .add("qq", qqNumber)
                    .build();

            Request request = new Request.Builder()
                    .url("https://qmsg.zendee.cn/send/" + qmsgKey)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    Log.d("EmailChecker", "通知已发送到QQ。");
                    return true;  // 发送成功，返回true
                } else {
                    Log.d("EmailChecker", "通知发送失败，错误码：" + response.code());
                    return false;  // 发送失败，返回false
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;  // 发生异常时返回false
            }
        }
    }
}
