package com.dv.nhat.nsmart;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.dv.nhat.nsmart.utils.Constants;

public class SettingActivity extends AppCompatActivity {

    private EditText edtUrl, edtCode;
    private Button btSave;
    private ImageButton btBack;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String urlServer, codeEspWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        sharedPreferences = getSharedPreferences(Constants.fileSetting,MODE_PRIVATE);
        editor = sharedPreferences.edit();
        urlServer = sharedPreferences.getString(Constants.keyUrl,"");
        codeEspWeather = sharedPreferences.getString(Constants.codeEsp,"");


        edtUrl = findViewById(R.id.edt_url_server);
        edtCode = findViewById(R.id.edt_code_weather);
        btBack = findViewById(R.id.bt_back);
        btSave = findViewById(R.id.bt_save);

        edtUrl.setText(urlServer);
        edtCode.setText(codeEspWeather);

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString(Constants.keyUrl,edtUrl.getText().toString());
                editor.putString(Constants.codeEsp,edtCode.getText().toString());
                editor.apply();
                setResult(Activity.RESULT_OK);
                finish();
            }
        });

        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
    }
}
