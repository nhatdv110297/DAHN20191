package com.dv.nhat.nsmart.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.dv.nhat.nsmart.RoomActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ServerRealTimeService extends Service {

    public static final String LOCAL_BROADCAST_NOTIFI_CONNECT = "broadcast-notifi-connect";
    public static final String LOCAL_BROADCAST_DATA = "broadcast-data";
    public static final String LOCAL_BROADCAST_WEATHER = "broadcast-weather";
    private Socket socket;

    public ServerRealTimeService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter(RoomActivity.LOCAL_BROAD_CAST_LOAD_DATA);
        intentFilter.addAction(RoomActivity.LOCAL_BROAD_CAST_CLICK_BUTTON);
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()){
                    case RoomActivity.LOCAL_BROAD_CAST_LOAD_DATA:{
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("cod", intent.getStringExtra("cod"));
                            obj.put("act",0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        socket.emit("client-sendto-server",obj);
                    }break;
                    case RoomActivity.LOCAL_BROAD_CAST_CLICK_BUTTON:{
                        try {
                            String data = intent.getStringExtra("data_click");
                            Log.i("TAG", data);
                            JSONObject obj = new JSONObject(data);
                            socket.emit("client-sendto-server",obj);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }break;
                    default:break;
                }
            }
        },intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String url = intent.getStringExtra("urlserver");
        try {
            socket = IO.socket("http://"+url);
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    notifiConnect();
                }
            });
            socket.on("server-sendto-client", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    sendDataBroadcast(((JSONObject)args[0]).toString());
                }
            });
            socket.on("server-send-weather", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    sendWeatherBroadcast(((JSONObject)args[0]).toString());
                }
            });
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        socket.disconnect();
        socket.close();
    }

    private void notifiConnect(){
        Intent i = new Intent(LOCAL_BROADCAST_NOTIFI_CONNECT);
        i.putExtra("notificonect","ok");
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
        Log.i("TAG",socket.id());
    }

    private void sendDataBroadcast(String data){
        Intent i = new Intent(LOCAL_BROADCAST_DATA);
        i.putExtra("data", data);
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }

    private void sendWeatherBroadcast(String data){
        Intent i = new Intent(LOCAL_BROADCAST_WEATHER);
        i.putExtra("weather",data);
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }
}
