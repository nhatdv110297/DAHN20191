package com.dv.nhat.nsmart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.dv.nhat.nsmart.utils.PostUtil;

public class SetupESPActivity extends AppCompatActivity {

    private static final String URL ="http://192.168.4.1/";

    private EditText edtId, edtPass, edtUrl;
    private Button btSetup;
    private ImageButton btBack;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_esp);


        edtId = findViewById(R.id.id_wifi);
        edtPass = findViewById(R.id.pass_wifi);
        edtUrl = findViewById(R.id.url_server);
        btSetup = findViewById(R.id.bt_setup);
        btBack = findViewById(R.id.bt_back);

        btSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = edtId.getText().toString();
                String pass = edtPass.getText().toString();
                String url = edtUrl.getText().toString();
                if(!TextUtils.isEmpty(id)&&!TextUtils.isEmpty(pass)&&!TextUtils.isEmpty(url)){
                    String params = "ssid=" + id + "&pass=" + pass + "&url=" + url;
                    Toast.makeText(SetupESPActivity.this,"Wait for your Esp device init!",Toast.LENGTH_LONG).show();
                    PostUtil.post(URL,params,handler);
                }
            }
        });

        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
