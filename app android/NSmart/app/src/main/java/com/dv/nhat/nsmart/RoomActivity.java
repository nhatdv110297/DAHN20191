package com.dv.nhat.nsmart;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.dv.nhat.nsmart.adpaters.ButtonAdapter;
import com.dv.nhat.nsmart.helper.GridSpacingItemDecoration;
import com.dv.nhat.nsmart.helper.Icons;
import com.dv.nhat.nsmart.models.Buttonn;
import com.dv.nhat.nsmart.models.Room;
import com.dv.nhat.nsmart.service.ServerRealTimeService;
import com.dv.nhat.nsmart.utils.PostUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RoomActivity extends AppCompatActivity {

    private static final String URL_LOAD_BUTTON ="https://mrdam1102.000webhostapp.com/server/loadbutton.php";
    private static final String URL_DELETE_BUTTON ="https://mrdam1102.000webhostapp.com/server/deletebutton.php";

    public static final String LOCAL_BROAD_CAST_LOAD_DATA = "broadcast_load_data";
    public static final String LOCAL_BROAD_CAST_CLICK_BUTTON = "broadcast_click_button";

    private static final int REQUEST_CODE_UPDATE_BUTTON = 200;
    private static final int REQUEST_CODE_ADD_BUTTON = 201;

    private RecyclerView recyButton;
    private ArrayList<Buttonn> listButton;
    private ButtonAdapter adapter;
    private ImageView iconRom;
    private TextView txtNameRom;

    private int possitionUpdate = -1;
    private int possitionClick = -1;
    private Room room;

//    private Socket socket;

    private PopupMenu popupMenu;
    private ImageButton btMore, btBack;

    private boolean isWait = false;
    private ProgressDialog progressDialog;

    private int[] stateButton = new int[8];

    private BroadcastReceiver broadcastReceiver;

    private Handler handlerLoadButton = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String params = msg.obj.toString();
            Log.i("TAG",params);
            isWait = false;
            progressDialog.dismiss();
            parserJson(listButton,params);
            adapter.notifyDataSetChanged();
        }
    };

    private Handler handlerDelete = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String params = msg.obj.toString();
            if(params.toLowerCase().equals("ok")){
                PostUtil.post(URL_LOAD_BUTTON,"idroom="+room.getId(),handlerLoadButton);
            }
            else{
                progressDialog.dismiss();
            }
            Toast.makeText(RoomActivity.this,params,Toast.LENGTH_LONG).show();
        }
    };

    private Handler handler = new Handler();

    private Runnable timeOutRunnable = new Runnable() {
        @Override
        public void run() {
            if(isWait){
                isWait = false;
                progressDialog.dismiss();
                Toast.makeText(RoomActivity.this,"No response!",Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
//
        room = getIntent().getParcelableExtra("room");

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Wait...");

        btBack = findViewById(R.id.bt_back);
        btBack .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btMore = findViewById(R.id.bt_more);

        popupMenu = new PopupMenu(this, btMore);
        popupMenu.getMenuInflater().inflate(R.menu.room_menu,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id){
                    case R.id.add_button:{
                        Intent i = new Intent(RoomActivity.this, AddOrUpdateButtonActivity.class);
                        i.putExtra("idroom",room.getId());
                        i.putExtra("AddOrUpdate",true);
                        startActivityForResult(i,REQUEST_CODE_ADD_BUTTON);
                    }break;
                    case R.id.refresh:{
                        loadData();
                        setTimeout(15000);
                    }
//                    case R.id.setup_esp:{
//                        Intent i = new Intent(HomeActivity.this, SetupESPActivity.class);
//                        startActivity(i);
//                    }break;
//                    case R.id.setting:{
//                        Intent i = new Intent(HomeActivity.this,SettingActivity.class);
//                        startActivity(i);
//                    }break;
                    case R.id.logout:{
                        Intent i = new Intent(RoomActivity.this, MainActivity.class);
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
        recyButton = findViewById(R.id.recy_button);
        listButton = new ArrayList<>();
        adapter = new ButtonAdapter(this, listButton);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,2);
        recyButton.setLayoutManager(layoutManager);
        recyButton.addItemDecoration(new GridSpacingItemDecoration(2,50,false));
        recyButton.setAdapter(adapter);

        iconRom = findViewById(R.id.icon_room);
        iconRom.setImageResource(Icons.getIndexIcon(room.getIndexIcon()));
        txtNameRom =findViewById(R.id.txt_name_room);
        txtNameRom.setText(room.getName());

        IntentFilter intentFilter = new IntentFilter(ServerRealTimeService.LOCAL_BROADCAST_DATA);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()){
                    case ServerRealTimeService.LOCAL_BROADCAST_DATA:{
                        String data = intent.getStringExtra("data");
                        Log.i("TAG","data receive"+data);
                        try {
                            JSONObject obj = new JSONObject(data);
                            int act = obj.getInt("act");
                            if(act==0){
                                // load data
                                JSONArray arrdata = obj.getJSONArray("data");
                                for(int i = 0;i<stateButton.length;i++){
                                    stateButton[i] = arrdata.getInt(i);
                                }
                                Log.i("TAG",arrdata.toString());
                                PostUtil.post(URL_LOAD_BUTTON,"idroom="+room.getId(),handlerLoadButton);
                            }
                            else{
                                //change status
                                int status = obj.getInt("sta");
                                int pin = obj.getInt("pin");
                                stateButton[pin-2] = status;
                                if(possitionClick!=-1){
                                    listButton.get(possitionClick).setState(status);
                                    possitionClick=-1;
                                }
                                else{
                                    for(Buttonn bt:listButton){
                                        if(bt.getPin()==pin){
                                            bt.setState(status);
                                        }
                                    }
                                }
                                adapter.notifyDataSetChanged();
                                isWait = false;
                                progressDialog.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }break;
                    default:break;
                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);

//        PostUtil.post(URL_LOAD_BUTTON,"idroom="+room.getId(),handlerLoadButton);

        loadData();
        setTimeout(15000);

//        try {
//            socket = IO.socket("http://192.168.0.103:8000");
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
//            @Override
//            public void call(Object... args) {
//                loadData();
//            }
//        });
//        socket.on("server-sendto-client", new Emitter.Listener() {
//            @Override
//            public void call(final Object... args) {
//                final JSONObject obj = (JSONObject)args[0];
//                try {
//                    int act = obj.getInt("act");
//                    if(act==0){
//                        // load data
//                        JSONArray arrdata = obj.getJSONArray("data");
//                        for(int i = 0;i<stateButton.length;i++){
//                            stateButton[i] = arrdata.getInt(i);
//                        }
//                        Log.i("TAG",arrdata.toString());
//                        PostUtil.post(URL_LOAD_BUTTON,"idroom="+room.getId(),handler);
//                    }
//                    else{
//                        //change status
//                        int status = obj.getInt("sta");
//                        int pin = obj.getInt("pin");
//                        stateButton[pin-2] = status;
////                        isWait = false;
////                        progressDialog.dismiss();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });
//        socket.connect();
    }

    private void setTimeout(int timeotut){
        isWait= true;
        progressDialog.show();
        handler.postDelayed(timeOutRunnable,timeotut);
    }

    private void parserJson(ArrayList<Buttonn> listButton, String data){
        if(listButton==null) listButton = new ArrayList<>();
        else listButton.clear();
        try {
            JSONArray root = new JSONArray(data);
            for(int i = 0; i< root.length(); i++){
                int pin =root.getJSONObject(i).getInt("Pin");
                listButton.add(new Buttonn(root.getJSONObject(i).getString("Id"),root.getJSONObject(i).getString("Name"),stateButton[pin-2],pin,root.getJSONObject(i).getInt("Indexicon")));
            }
        } catch (JSONException e) {
            Toast.makeText(RoomActivity.this,"Dữ liệu bị lỗi!", Toast.LENGTH_SHORT);
        }
    }

    private void loadData(){
//        JSONObject obj = new JSONObject();
//        try {
//            obj.put("cod", room.getCodeEsp8266());
//            obj.put("act",0);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        Intent i = new Intent(LOCAL_BROAD_CAST_LOAD_DATA);
        Log.i("TAG","load button");
        i.putExtra("cod",room.getCodeEsp8266());
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }

    public void onClickItem(int index){
        possitionClick =index;
        Intent i = new Intent(LOCAL_BROAD_CAST_CLICK_BUTTON);
        int state;
        Buttonn buttonn = listButton.get(index);
        if(buttonn.getState()==1) state =0;
        else state = 1;
        String data = "{\"cod\":\""+room.getCodeEsp8266()+"\",\"act\":1,\"sta\":"+state+",\"pin\":"+buttonn.getPin()+"}";
        i.putExtra("data_click",data);
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
        setTimeout(15000);
    }

    public void showInforItem(Buttonn buttonn){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.infor_button);
        ImageView icon = dialog.findViewById(R.id.icon_button);
        TextView id = dialog.findViewById(R.id.id_button);
        TextView name = dialog.findViewById(R.id.name_button);
        TextView codeesp = dialog.findViewById(R.id.pin);
        Button bt = dialog.findViewById(R.id.bt_close);

        icon.setImageResource(Icons.getIndexIconLight(buttonn.getIndexIcon()));
        id.setText("ID: "+buttonn.getId());
        name.setText("Name: "+buttonn.getName());
        codeesp.setText("Pin: "+buttonn.getPin());
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void update(Buttonn buttonn, int posssition){
        possitionUpdate = posssition;
        Intent i = new Intent(RoomActivity.this, AddOrUpdateButtonActivity.class);
        i.putExtra("button",buttonn);
        i.putExtra("AddOrUpdate",false);
        startActivityForResult(i, REQUEST_CODE_UPDATE_BUTTON);

    }

    public void delete(String param){
        setTimeout(15000);
        PostUtil.post(URL_DELETE_BUTTON,param,handlerDelete);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_ADD_BUTTON){
            if(resultCode == Activity.RESULT_OK){
                setTimeout(15000);
                PostUtil.post(URL_LOAD_BUTTON,"idroom="+room.getId(),handlerLoadButton);
            }
        }
        else if(requestCode == REQUEST_CODE_UPDATE_BUTTON){
            if(resultCode == Activity.RESULT_OK){
                Buttonn bt = data.getParcelableExtra("buttonUpdate");
                if(possitionUpdate!=-1){
                    listButton.set(possitionUpdate,bt);
                    possitionUpdate = -1;
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("TAG", "destroy");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }
}
