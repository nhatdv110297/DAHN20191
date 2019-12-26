package com.dv.nhat.nsmart.utils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PostUtil {


    public static void post(final String link, final String params, final Handler handler){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(link);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    // Send post request
                    con.setDoOutput(true);
                    DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                    wr.writeBytes(params);
                    wr.flush();
                    wr.close();

                    int responseCode = con.getResponseCode();
                    Log.v("TAG", "Sending 'POST' request to URL : " + url.toString());
                    Log.v("TAG", "Response Code : " + responseCode);

                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String line;
                    StringBuilder data = new StringBuilder();
                    while ((line = in.readLine()) != null) {
                        data.append(line);
                    }
                    in.close();
                    Message msg = new Message();
                    msg.obj = data;
                    handler.sendMessage(msg);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
