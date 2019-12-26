package com.dv.nhat.nsmart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dv.nhat.nsmart.utils.PostUtil;

public class MainActivity extends AppCompatActivity implements View.OnKeyListener {

    private static final String URL_LOGIN = "https://mrdam1102.000webhostapp.com/server/checklogin.php";
    private static final String FILE_PREFERENCES = "user";

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private EditText edtUser, edtPass;
    private Button btLogin;
    private TextView txtSetupEsp;

    private Dialog dialog;
    private Handler handlerLogin = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            dialog.dismiss();
            isWait = false;
            String data = msg.obj.toString();
            try{
                Integer.parseInt(data);
                Intent i = new Intent(MainActivity.this,HomeActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("iduser",data);
                startActivity(i);
                Toast.makeText(MainActivity.this,"OK",Toast.LENGTH_SHORT).show();
            }catch (NumberFormatException e){
                Toast.makeText(MainActivity.this,data,Toast.LENGTH_SHORT).show();
            }

        }
    };

    private Handler handler = new Handler();
    private boolean isWait = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences(FILE_PREFERENCES, MODE_PRIVATE);
        editor = preferences.edit();

        dialog = new Dialog(this);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.layout_login);

        edtUser = findViewById(R.id.edt_user);
        edtPass = findViewById(R.id.edt_pass);
        btLogin = findViewById(R.id.bt_login);
        txtSetupEsp = findViewById(R.id.txt_setup_esp);

        edtUser.setOnKeyListener(this);
        edtPass.setOnKeyListener(this);

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = edtUser.getText().toString();
                String pass = edtPass.getText().toString();
                if(!TextUtils.isEmpty(user)&&!TextUtils.isEmpty(pass)){
                    dialog.show();
                    isWait = true;
                    String params = "username=" + user + "&password=" + pass;
                    PostUtil.post(URL_LOGIN,params, handlerLogin);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(isWait){
                                isWait = false;
                                dialog.dismiss();
                                Toast.makeText(MainActivity.this,"No response",Toast.LENGTH_LONG).show();
                            }
                        }
                    }, 15000);
                }
            }
        });

        txtSetupEsp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, SetupESPActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        int id = view.getId();
        switch (id){
            case R.id.edt_user:{
                if((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)){
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(edtPass, InputMethodManager.SHOW_IMPLICIT);
                }
            }break;
            case R.id.edt_pass:{
                if((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)){
                    String user = edtUser.getText().toString();
                    String pass = edtPass.getText().toString();
                    if(!TextUtils.isEmpty(user)&&!TextUtils.isEmpty(pass)){
                        dialog.show();
                        String params = "username=" + user + "&password=" + pass;
                        PostUtil.post(URL_LOGIN,params, handlerLogin);
                    }
                }
            }break;
            default:break;
        }
        return false;
    }
}
