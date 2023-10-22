package com.example.rephone.Thread;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DBThread extends Thread {
    int count = 1;
    StringBuffer buffer = new StringBuffer();
    String job;
    String[] parameters;
    URL url = null;

    public DBThread(String... job) {
        this.job = job[0];
        parameters = job;
    }

    @Override
    public void run() {
        try {
            if (job.equals("select_new_phone")) {
                url = new URL("http://144.24.92.222/");
            } else if (job.equals("select_old_phone")) {
                url = new URL("http://144.24.92.222/");
            } else if (job.equals("select_used_before")) {
                url = new URL("http://144.24.92.222/");
            } else if (job.equals("create_qr_code")) {
                url = new URL("http://144.24.92.222/rephone/createToken.jsp");
            } else if (job.equals("check_new_phone")) {
                url = new URL(String.format("http://144.24.92.222/rephone/checkNewPhone.jsp?token=%s", parameters[1]));
            } else if (job.equals("check_old_phone")) {
                url = new URL(String.format("http://144.24.92.222/rephone/checkOldPhone.jsp?token=%s", parameters[1]));
            } else if (job.equals("check_token_validate")) {
                url = new URL(String.format("http://144.24.92.222/rephone/checkTokenValidate.jsp?token=%s", parameters[1]));
            }
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/x-www-form-unlencoded");
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8);
                BufferedReader reader = new BufferedReader(tmp);
                String str;

                while ((str = reader.readLine()) != null) {
                    buffer.append(str);
                }
            } else
                buffer = null;
        } catch (Exception e) {
            e.printStackTrace();
            buffer = null;
        }
    }

    public String getToken() {
        if (buffer != null)
            return buffer.toString();
        else
            return null;
    }

    public String checkNewPhone() {
        if (buffer == null || buffer.toString().isEmpty())
            return "fail";
        else if (buffer != null && buffer.toString().equals("true"))
            return "true";
        else
            return "false";
    }

    public boolean checkOldPhone() {
        return buffer.toString().equals("true");
    }

    public boolean checkTokenValidate() {
        return buffer.toString().equals("true");
    }
}
