package com.dv.nhat.nsmart;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dv.nhat.nsmart.adpaters.RoomAdapter;
import com.dv.nhat.nsmart.helper.GridSpacingItemDecoration;
import com.dv.nhat.nsmart.helper.Icons;
import com.dv.nhat.nsmart.models.Room;
import com.dv.nhat.nsmart.service.ServerRealTimeService;
import com.dv.nhat.nsmart.utils.Constants;
import com.dv.nhat.nsmart.utils.PostUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private static final String URL_LOAD_ROOM = "https://mrdam1102.000webhostapp.com/server/loadroom.php";
    private static final String URL_DELETE_ROOM = "https://mrdam1102.000webhostapp.com/server/deleteroom.php";
    private static final int REQUEST_CODE_UPDATE_ROOM = 1000;
    private static final int REQUEST_CODE_ADD_ROOM = 1001;
    private static final int REQUEST_CODE_SETTING = 1002;

    private ArrayList<Room> listRoom;
    private RoomAdapter adapter;
    private RecyclerView recyRoom;
    private ProgressBar progressBar;
    private TextView temperature,humidity;
    private ImageView iconRain;

    private PopupMenu popupMenu;
    private ImageButton btMore;

    private ProgressDialog progressDialog;
    private String iduser;
    private int possitionUpdate = -1;

    private boolean isWait = false;

    private SharedPreferences sharedPreferences;
    private String urlServer,codeEspWeather;

    private Handler handlerLoadRoom = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isWait = false;
            progressDialog.dismiss();
            String data = msg.obj.toString();
            Log.i("TAG",data);
            parserJson(listRoom, data);
            adapter.notifyDataSetChanged();
        }
    };

    private Handler handlerDelete = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String data = msg.obj.toString();
            if(data.toLowerCase().equals("ok")){
                PostUtil.post(URL_LOAD_ROOM, "iduser="+iduser, handlerLoadRoom);
            }
            else{
                progressDialog.dismiss();
            }
            Toast.makeText(HomeActivity.this, data, Toast.LENGTH_LONG).show();
        }
    };

    private Handler handler = new Handler();

    private Intent intentRealTimeService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_home);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Wait...");
        //progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        iduser = getIntent().getExtras().getString("iduser");

        btMore = findViewById(R.id.bt_more);

        popupMenu = new PopupMenu(this, btMore);
        if(iduser.equals("1")){
            popupMenu.getMenuInflater().inflate(R.menu.home_menu_admin,popupMenu.getMenu());
        }
        else{
            popupMenu.getMenuInflater().inflate(R.menu.home_menu,popupMenu.getMenu());
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id){
                    case R.id.add_room:{
                        Intent i = new Intent(HomeActivity.this, AddOrUpdateRoomActivity.class);
                        i.putExtra("iduser",iduser);
                        i.putExtra("AddOrUpdate",true);
                        startActivityForResult(i,REQUEST_CODE_ADD_ROOM);
                    }break;
                    case R.id.refresh:{
                        PostUtil.post(URL_LOAD_ROOM, "iduser="+iduser, handlerLoadRoom);
                    }break;
                    case R.id.manage_user:{
                        Intent i = new Intent(HomeActivity.this, ManageUserActivity.class);
                        startActivity(i);
                    }break;
                    case R.id.setup_esp:{
                        Intent i = new Intent(HomeActivity.this, SetupESPActivity.class);
                        startActivity(i);
                    }break;
                    case R.id.setting:{
                        Intent i = new Intent(HomeActivity.this,SettingActivity.class);
                        startActivityForResult(i,REQUEST_CODE_SETTING);
                    }break;
                    case R.id.logout:{
                        Intent i = new Intent(HomeActivity.this, MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    }break;
                    default: break;
                }
                return false;
            }
        });

        btMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupMenu.show();
            }
        });

        progressBar = findViewById(R.id.pro_cir);
        temperature = findViewById(R.id.txt_nhiet_do);
        humidity = findViewById(R.id.txt_do_am);
        iconRain = findViewById(R.id.icon_rain);

        recyRoom = findViewById(R.id.recy_list_room);
        listRoom = new ArrayList<>();


        adapter = new RoomAdapter(this, listRoom);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,2);
        recyRoom.setLayoutManager(layoutManager);
        recyRoom.addItemDecoration(new GridSpacingItemDecoration(2,50,false));
        recyRoom.setAdapter(adapter);

        sharedPreferences = getSharedPreferences(Constants.fileSetting,MODE_PRIVATE);
        urlServer = sharedPreferences.getString(Constants.keyUrl,"");
        codeEspWeather = sharedPreferences.getString(Constants.codeEsp,"");

        if(!TextUtils.isEmpty(urlServer)&&!TextUtils.isEmpty(codeEspWeather)){
            //start service;
            intentRealTimeService = new Intent(HomeActivity.this, ServerRealTimeService.class);
            intentRealTimeService.putExtra("urlserver", urlServer);
            startService(intentRealTimeService);
            setTimeOut(15000);
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Need setting!");
            builder.setMessage("You need setting for your app");
            builder.setCancelable(false);
            builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //
                }
            });
            builder.setNegativeButton("Go Setting", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_SETTING);
                }
            });
            builder.show();

        }

        IntentFilter intentFilter = new IntentFilter(ServerRealTimeService.LOCAL_BROADCAST_NOTIFI_CONNECT);
        intentFilter.addAction(ServerRealTimeService.LOCAL_BROADCAST_WEATHER);
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()){
                    case ServerRealTimeService.LOCAL_BROADCAST_NOTIFI_CONNECT:{
                        String data = intent.getStringExtra("notificonect");
                        if(data.equals("ok")) {
                            Toast.makeText(HomeActivity.this, "connect successfull", Toast.LENGTH_LONG).show();
                            PostUtil.post(URL_LOAD_ROOM, "iduser="+iduser, handlerLoadRoom);
                        }
                        else
                        {
                            Toast.makeText(HomeActivity.this, "connect Error", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    }break;
                    case ServerRealTimeService.LOCAL_BROADCAST_WEATHER:{
                        progressBar.setVisibility(View.INVISIBLE);
                        try {
                            JSONObject obj = new JSONObject(intent.getStringExtra("weather"));
                            if(codeEspWeather.equals(obj.getString("cod"))){
                                temperature.setText(((float)Math.round(obj.getDouble("tem")*10)/10)+"°C");
                                humidity.setText(((float)Math.round(obj.getDouble("hum")*10)/10)+"%");
                                if(obj.getInt("rain")==1){
                                    iconRain.setImageResource(R.drawable.icon_weather_ngay_k_mua);
                                }
                                else{
                                    iconRain.setImageResource(R.drawable.icon_weather_mua);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }break;
                    default: break;
                }
            }
        }, intentFilter);

    }

    private void setTimeOut(int timeOut){
        progressDialog.show();
        isWait = true;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isWait){
                    isWait = false;
                    progressDialog.dismiss();
                    Toast.makeText(HomeActivity.this, "No response!", Toast.LENGTH_LONG).show();
                    stopService(intentRealTimeService);
                }
            }},timeOut);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    private void parserJson(ArrayList<Room> listRoom, String data){
        if(listRoom==null) listRoom = new ArrayList<>();
        else listRoom.clear();
        try {
            JSONArray root = new JSONArray(data);
            for(int i = 0; i< root.length(); i++){
                listRoom.add(new Room(root.getJSONObject(i).getString("Id"),root.getJSONObject(i).getString("Iduser"),root.getJSONObject(i).getString("Name"),root.getJSONObject(i).getString("Codeesp"),root.getJSONObject(i).getInt("Indexicon"),root.getJSONObject(i).getInt("Numberdevices")));
            }
        } catch (JSONException e) {
            Toast.makeText(HomeActivity.this,"Dữ liệu bị lỗi!", Toast.LENGTH_SHORT);
        }
    }

    public void delete(String params){
        setTimeOut(15000);
        PostUtil.post(URL_DELETE_ROOM,params,handlerDelete);
    }

    public void update(Room room, int possition){
        possitionUpdate = possition;
        Intent i = new Intent(this, AddOrUpdateRoomActivity.class);
        i.putExtra("room",room);
        i.putExtra("AddOrUpdate",false);
        startActivityForResult(i, REQUEST_CODE_UPDATE_ROOM);
    }

    public void showInforItem(Room room){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.infor_room);
        ImageView icon = dialog.findViewById(R.id.icon_room);
        TextView id = dialog.findViewById(R.id.id_room);
        TextView name = dialog.findViewById(R.id.name_room);
        TextView codeesp = dialog.findViewById(R.id.code_esp);
        Button bt = dialog.findViewById(R.id.bt_close);

        icon.setImageResource(Icons.getIndexIcon(room.getIndexIcon()));
        id.setText("ID: "+room.getId());
        name.setText("Name: "+room.getName());
        codeesp.setText("Code ESP: "+room.getCodeEsp8266());
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void onClickItem(Room room){
        Intent i = new Intent(HomeActivity.this, RoomActivity.class);
        i.putExtra("room",room);
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_ADD_ROOM){
            if(resultCode == Activity.RESULT_OK){
                setTimeOut(15000);
                PostUtil.post(URL_LOAD_ROOM, "iduser="+iduser, handlerLoadRoom);
            }
        }
        else if(requestCode == REQUEST_CODE_UPDATE_ROOM){
            if(resultCode == Activity.RESULT_OK){
                Room room = data.getParcelableExtra("roomUpdate");
                listRoom.set(possitionUpdate,room);
                adapter.notifyDataSetChanged();
            }
        }
        else if(requestCode == REQUEST_CODE_SETTING){
            if(resultCode == Activity.RESULT_OK){
                Log.i("TAG", "chay den day roi");
                urlServer = sharedPreferences.getString(Constants.keyUrl,"");
                codeEspWeather = sharedPreferences.getString(Constants.codeEsp,"");
                if(intentRealTimeService !=null){
                    stopService(intentRealTimeService);
                }
                else{
                    intentRealTimeService = new Intent(HomeActivity.this, ServerRealTimeService.class);
                }

                    //start service;
                intentRealTimeService.putExtra("urlserver", urlServer);
                startService(intentRealTimeService);
                setTimeOut(15000);
            }
            else{
                Log.i("TAG", "dang o day");
                urlServer = sharedPreferences.getString(Constants.keyUrl,"");
                codeEspWeather = sharedPreferences.getString(Constants.codeEsp,"");

                if(TextUtils.isEmpty(urlServer)||TextUtils.isEmpty(codeEspWeather)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Need setting!");
                    builder.setMessage("You need setting for your app");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //
                        }
                    });
                    builder.setNegativeButton("Go Setting", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
                            startActivityForResult(intent, REQUEST_CODE_SETTING);
                        }
                    });
                    builder.show();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(intentRealTimeService!=null) stopService(intentRealTimeService);
    }
}
