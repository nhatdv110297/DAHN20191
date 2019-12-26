package com.dv.nhat.nsmart;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.dv.nhat.nsmart.adpaters.UserAdapter;
import com.dv.nhat.nsmart.models.User;
import com.dv.nhat.nsmart.utils.PostUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class ManageUserActivity extends AppCompatActivity {

    private static final String URL_LOAD_USER = "https://mrdam1102.000webhostapp.com/server/loaduser.php";
    private static final String URL_DELETE = "https://mrdam1102.000webhostapp.com/server/deleteuser.php";

    private static final int RESQUEST_CODE_UPDATE_USER = 2000;
    private static final int RESQUEST_CODE_ADD_USER = 2001;

    private RecyclerView recyUser;
    private UserAdapter adapter;
    private ArrayList<User> listUser;

    private ImageButton btBack, btMore;
    private PopupMenu popupMenu;

    private ProgressDialog progressDialog;
    private boolean isWait = false;
    private int possitionUpdate=-1;

    private Handler handlerLoadUser = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            isWait = false;
            progressDialog.dismiss();
            String data = msg.obj.toString();
            Log.i("TAG",data);
            parserJson(listUser, data);
            adapter.notifyDataSetChanged();
        }
    };

    private Handler handlerDelete = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            String data = msg.obj.toString();
            if(data.toLowerCase().equals("ok")){
                PostUtil.post(URL_LOAD_USER,"iduser=1",handlerLoadUser);
            }
            else{
                isWait =false;
                progressDialog.dismiss();
            }
            Toast.makeText(ManageUserActivity.this, data, Toast.LENGTH_LONG).show();
        }
    };

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Wait...");

        btBack  = findViewById(R.id.bt_back);
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btMore = findViewById(R.id.bt_more);
        popupMenu = new PopupMenu(this, btMore);
        popupMenu.getMenuInflater().inflate(R.menu.manage_user_menu,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id){
                    case R.id.add_user:{
                        Intent i = new Intent(ManageUserActivity.this, AddOrUpdateUserActivity.class);
                        i.putExtra("addorupdate",true);
                        startActivityForResult(i, RESQUEST_CODE_ADD_USER);
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

        recyUser = findViewById(R.id.recy_user);
        listUser = new ArrayList<>();
        adapter = new UserAdapter(this, listUser);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyUser.setLayoutManager(layoutManager);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        recyUser.addItemDecoration(itemDecoration);
        recyUser.setAdapter(adapter);

        setTimeOut(15000);
        PostUtil.post(URL_LOAD_USER,"iduser=1",handlerLoadUser);
    }

    private void parserJson(ArrayList<User> listUser, String data) {
        if(listUser==null) listUser = new ArrayList<>();
        else listUser.clear();
        try {
            JSONArray root = new JSONArray(data);
            for(int i = 0; i< root.length(); i++){
                listUser.add(new User(root.getJSONObject(i).getString("Id"),root.getJSONObject(i).getString("Username"),root.getJSONObject(i).getString("Password")));
            }
        } catch (JSONException e) {
            Toast.makeText(ManageUserActivity.this,"Dữ liệu bị lỗi!", Toast.LENGTH_SHORT);
        }
    }

    public void update(User user, int possition){
        possitionUpdate = possition;
        Intent i = new Intent(ManageUserActivity.this, AddOrUpdateUserActivity.class);
        i.putExtra("addorupdate",false);
        i.putExtra("user",user);
        startActivityForResult(i, RESQUEST_CODE_UPDATE_USER);

    }

    public void delete(String params){
        setTimeOut(15000);
        PostUtil.post(URL_DELETE, params, handlerDelete);
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
                    Toast.makeText(ManageUserActivity.this, "No response!", Toast.LENGTH_LONG).show();
                }
            }
        },timeOut);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESQUEST_CODE_UPDATE_USER){
            if(resultCode == Activity.RESULT_OK){
                User user = data.getParcelableExtra("userUpdate");
                listUser.set(possitionUpdate,user);
                adapter.notifyDataSetChanged();
            }
        }
        else if(requestCode == RESQUEST_CODE_ADD_USER){
            if(resultCode == Activity.RESULT_OK){
                setTimeOut(15000);
                PostUtil.post(URL_LOAD_USER,"iduser=1",handlerLoadUser);
            }
        }
    }
}
