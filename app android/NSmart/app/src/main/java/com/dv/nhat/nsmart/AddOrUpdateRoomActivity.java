package com.dv.nhat.nsmart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dv.nhat.nsmart.adpaters.IconsAdapter;
import com.dv.nhat.nsmart.helper.GridSpacingItemDecoration;
import com.dv.nhat.nsmart.helper.Icons;
import com.dv.nhat.nsmart.models.Room;
import com.dv.nhat.nsmart.utils.PostUtil;

import java.util.ArrayList;
import java.util.Arrays;

public class AddOrUpdateRoomActivity extends AppCompatActivity {

    private static final String URL_ADD_ROOM = "https://mrdam1102.000webhostapp.com/server/addroom.php";
    private static final String URL_UPDATE_ROOM = "https://mrdam1102.000webhostapp.com/server/updateroom.php";



    private ImageButton btBack;
    private TextView title;
    private EditText edtNameRoom, edtCodeEsp;
    private Button btAddOrUpdate;
    private ImageView imgIcon;

    private boolean addOrUpdate;
    private Room room;
    private String idUser;

    private ProgressDialog progressDialog;
    private Dialog dialogIcons;
    private int indexIcon = 0;

    private Handler handlerAddRoom = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progressDialog.dismiss();
            isWait =false;
            String result = msg.obj.toString();
            if(result.equals("OK")){
                setResult(Activity.RESULT_OK);
            }
            else{
                setResult(Activity.RESULT_CANCELED);
            }
            Toast.makeText(AddOrUpdateRoomActivity.this,result,Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    private Handler handlerUpdate = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progressDialog.dismiss();
            isWait = false;
            String result = msg.obj.toString();
            if(result.equals("OK")){
                room.setName(edtNameRoom.getText().toString());
                room.setCodeEsp8266(edtCodeEsp.getText().toString());
                room.setIndexIcon(indexIcon);
                Intent data = new Intent();
                data.putExtra("roomUpdate",room);
                setResult(Activity.RESULT_OK,data);
            }else{
                setResult(Activity.RESULT_CANCELED);
            }
            Toast.makeText(AddOrUpdateRoomActivity.this,result,Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    private Handler handler = new Handler();
    private boolean isWait =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_add_or_update_room);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        dialogIcons = new Dialog(this);
        dialogIcons.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogIcons.setContentView(R.layout.dialog_icons);


        RecyclerView recyIcons = dialogIcons.findViewById(R.id.recy_icons);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,2);
        recyIcons.setLayoutManager(layoutManager);
        ArrayList<Integer> arrIcons = new ArrayList<>(Arrays.asList(Icons.getIconsRoom()));
        Log.i("TAG",arrIcons.get(0)+"");
        IconsAdapter adapter = new IconsAdapter(this, arrIcons);
        recyIcons.setAdapter(adapter);
        recyIcons.addItemDecoration(new GridSpacingItemDecoration(2,20,true));


        btAddOrUpdate = findViewById(R.id.bt_add_or_update);
        btBack = findViewById(R.id.bt_back);
        title = findViewById(R.id.title_add_or_update);
        edtNameRoom = findViewById(R.id.edt_name_room);
        edtCodeEsp = findViewById(R.id.edt_code_esp);
        imgIcon = findViewById(R.id.icon_room);

        imgIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogIcons.show();
            }
        });

        addOrUpdate = getIntent().getBooleanExtra("AddOrUpdate",true);
        idUser = getIntent().getStringExtra("iduser");
        if(addOrUpdate){
            title.setText("Add Room");
            btAddOrUpdate.setText("Add Room");
            progressDialog.setTitle("Adding");
            progressDialog.setMessage("Wait...");
        }
        else{
            title.setText("Update Room");
            btAddOrUpdate.setText("Update Room");
            room = getIntent().getParcelableExtra("room");
            progressDialog.setTitle("Updating");
            progressDialog.setMessage("Wait...");
            edtNameRoom.setText(room.getName());
            edtCodeEsp.setText(room.getCodeEsp8266());
            indexIcon = room.getIndexIcon();
            imgIcon.setImageResource(Icons.getIndexIcon(room.getIndexIcon()));
        }

        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btAddOrUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(edtCodeEsp.getText().toString())&&!TextUtils.isEmpty(edtNameRoom.getText().toString())){
                    setTimeOut(15000);
                    if(addOrUpdate){
                        String params = "iduser="+idUser + "&name="+edtNameRoom.getText().toString()+"&codeesp="+edtCodeEsp.getText().toString()+"&indexicon="+indexIcon;
                        PostUtil.post(URL_ADD_ROOM,params,handlerAddRoom);
                    }
                    else{
                        String params = "id="+room.getId()+"&iduser="+room.getIduser() + "&name="+edtNameRoom.getText().toString()+"&codeesp="+edtCodeEsp.getText().toString()+"&indexicon="+indexIcon;
                        PostUtil.post(URL_UPDATE_ROOM,params,handlerUpdate);
                    }
                }
            }
        });
    }

    private void setTimeOut(int timeOut){
        isWait =true;
        progressDialog.show();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isWait){
                    isWait = false;
                    progressDialog.dismiss();
                    Toast.makeText(AddOrUpdateRoomActivity.this, "No response", Toast.LENGTH_LONG).show();
                }
            }
        },timeOut);
    }

    public void onSelectIconRoom(int indexIcon){
        dialogIcons.dismiss();
        this.indexIcon = indexIcon;
        imgIcon.setImageResource(Icons.getIndexIcon(indexIcon));
    }
}
