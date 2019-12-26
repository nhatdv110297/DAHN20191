package com.dv.nhat.nsmart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dv.nhat.nsmart.models.User;
import com.dv.nhat.nsmart.utils.PostUtil;

public class AddOrUpdateUserActivity extends AppCompatActivity {
    private static final String URL_UPDATE_USER = "https://mrdam1102.000webhostapp.com/server/updateuser.php";
    private static final String URL_ADD_USER = "https://mrdam1102.000webhostapp.com/server/adduser.php";

    private EditText edtUsernam, edtPass;
    private Button btAddOrUpdate;
    private TextView title;
    private ImageButton btBack;

    private User user;
    private boolean addOrUpdate;
    private ProgressDialog progressDialog;

    private Handler handlerAddUser = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
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
            Toast.makeText(AddOrUpdateUserActivity.this,result,Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    private Handler handlerUpdateUser = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            progressDialog.dismiss();
            isWait =false;
            String result = msg.obj.toString();
            if(result.equals("OK")){
                user.setPass(edtPass.getText().toString());
                Intent data = new Intent();
                data.putExtra("userUpdate",user);
                setResult(Activity.RESULT_OK,data);
            }else{
                setResult(Activity.RESULT_CANCELED);
            }
            Toast.makeText(AddOrUpdateUserActivity.this,result,Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    private boolean isWait = false;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_update_user);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);


        edtUsernam = findViewById(R.id.edt_username);
        edtPass = findViewById(R.id.edt_password);
        title = findViewById(R.id.title_add_or_update);

        btAddOrUpdate = findViewById(R.id.bt_add_or_update);
        btBack = findViewById(R.id.bt_back);
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        addOrUpdate = getIntent().getBooleanExtra("addorupdate",true);


        if(addOrUpdate){
            title.setText("Add user");
            edtUsernam.setEnabled(true);
            btAddOrUpdate.setText("Add");
            progressDialog.setTitle("Adding");
            progressDialog.setMessage("Wait...");
        }
        else {
            user = getIntent().getParcelableExtra("user");
            title.setText("Update User");
            edtUsernam.setEnabled(false);
            btAddOrUpdate.setText("Update");
            progressDialog.setTitle("Updating");
            progressDialog.setMessage("Wait...");
            edtUsernam.setText(user.getUsername());
        }

        btAddOrUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(edtUsernam.getText().toString())&&!TextUtils.isEmpty(edtPass.getText().toString())){
                    setTimeOut(15000);
                    if(addOrUpdate){
                        String params = "username="+edtUsernam.getText().toString()+"&password="+edtPass.getText().toString();
                        PostUtil.post(URL_ADD_USER,params,handlerAddUser);
                    }
                    else{
                        String params = "id="+user.getId()+"&username="+edtUsernam.getText().toString()+"&password="+edtPass.getText().toString();
                        PostUtil.post(URL_UPDATE_USER,params,handlerUpdateUser);
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
                    Toast.makeText(AddOrUpdateUserActivity.this, "No response", Toast.LENGTH_LONG).show();
                }
            }
        },timeOut);
    }
}
