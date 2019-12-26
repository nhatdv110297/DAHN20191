package com.dv.nhat.nsmart;

import androidx.annotation.NonNull;
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
import com.dv.nhat.nsmart.models.Buttonn;
import com.dv.nhat.nsmart.utils.PostUtil;

import java.util.ArrayList;
import java.util.Arrays;

public class AddOrUpdateButtonActivity extends AppCompatActivity {

    private static final String URL_ADD_BUTTON ="https://mrdam1102.000webhostapp.com/server/addbutton.php";
    private static final String URL_UPDATE_BUTTON = "https://mrdam1102.000webhostapp.com/server/updatebutton.php";

    private ImageButton btBack;
    private TextView title;
    private EditText edtNameButton, edtPin;
    private Button btAddOrUpdate;
    private ImageView imgIcon;

    private boolean addOrUpdate;
    private Buttonn buttonn;
    private String idroom;

    private ProgressDialog progressDialog;
    private Dialog dialogIcons;
    private int indexIcon = 0;

    private Handler handlerAddButton= new Handler(){
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
            Toast.makeText(AddOrUpdateButtonActivity.this,result,Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    private Handler handlerUpdateButton = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            progressDialog.dismiss();
            isWait = false;
            String result = msg.obj.toString();
            if(result.equals("OK")){
                buttonn.setName(edtNameButton.getText().toString());
                buttonn.setPin(Integer.parseInt(edtPin.getText().toString()));
                buttonn.setIndexIcon(indexIcon);
                Intent data = new Intent();
                data.putExtra("buttonUpdate",buttonn);
                setResult(Activity.RESULT_OK,data);
            }else{
                setResult(Activity.RESULT_CANCELED);
            }
            Toast.makeText(AddOrUpdateButtonActivity.this,result,Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    private Handler handler =new Handler();
    private boolean isWait = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_update_button);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        dialogIcons = new Dialog(this);
        dialogIcons.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogIcons.setContentView(R.layout.dialog_icons);

        RecyclerView recyIcons = dialogIcons.findViewById(R.id.recy_icons);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,2);
        recyIcons.setLayoutManager(layoutManager);
        ArrayList<Integer> arrIcons = new ArrayList<>(Arrays.asList(Icons.getIconsLight()));
        Log.i("TAG",arrIcons.get(0)+"");
        IconsAdapter adapter = new IconsAdapter(this, arrIcons);
        recyIcons.setAdapter(adapter);
        recyIcons.addItemDecoration(new GridSpacingItemDecoration(2,20,true));

        btAddOrUpdate = findViewById(R.id.bt_add_or_update);
        btBack = findViewById(R.id.bt_back);
        title = findViewById(R.id.title_add_or_update);
        edtNameButton = findViewById(R.id.edt_name_button);
        edtPin = findViewById(R.id.edt_pin);
        imgIcon = findViewById(R.id.icon_button);

        imgIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogIcons.show();
            }
        });
        addOrUpdate = getIntent().getBooleanExtra("AddOrUpdate",true);

        if(addOrUpdate){
            title.setText("Add Button");
            btAddOrUpdate.setText("Add Button");
            progressDialog.setTitle("Adding");
            progressDialog.setMessage("Wait...");
            idroom = getIntent().getStringExtra("idroom");
        }
        else{
            title.setText("Update Button");
            btAddOrUpdate.setText("Update Button");
            buttonn = getIntent().getParcelableExtra("button");
            progressDialog.setTitle("Updating");
            progressDialog.setMessage("Wait...");
            edtNameButton.setText(buttonn.getName());
            edtPin.setText(buttonn.getPin()+"");
            indexIcon = buttonn.getIndexIcon();
            imgIcon.setImageResource(Icons.getIndexIconLight(buttonn.getIndexIcon()));
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
                if(!TextUtils.isEmpty(edtNameButton.getText().toString())&&!TextUtils.isEmpty(edtPin.getText().toString())){
                    int tmp = 0;
                    try{
                        tmp = Integer.parseInt(edtPin.getText().toString());
                    }catch (NumberFormatException e){
                        Toast.makeText(AddOrUpdateButtonActivity.this,"Pin is a number from 2 to 9",Toast.LENGTH_LONG).show();
                        return;
                    }
                    if((tmp>=2)&&(tmp<=9)) {
                        setTimeOut(30000);
                        if (addOrUpdate) {
                            String params = "idroom=" + idroom + "&namebutton=" + edtNameButton.getText().toString() + "&pin=" + edtPin.getText().toString() + "&indexicon=" + indexIcon;
                            PostUtil.post(URL_ADD_BUTTON, params, handlerAddButton);
                        } else {
                            String params = "id=" + buttonn.getId() + "&namebutton=" + edtNameButton.getText().toString() + "&pin=" + edtPin.getText().toString() + "&indexicon=" + indexIcon;
                            PostUtil.post(URL_UPDATE_BUTTON, params, handlerUpdateButton);
                        }
                    }
                    else{
                        Toast.makeText(AddOrUpdateButtonActivity.this,"Pin is a number from 2 to 9",Toast.LENGTH_LONG).show();
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
                    Toast.makeText(AddOrUpdateButtonActivity.this, "No response", Toast.LENGTH_LONG).show();
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
