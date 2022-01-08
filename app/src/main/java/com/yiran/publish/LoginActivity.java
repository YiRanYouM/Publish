package com.yiran.publish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button bt_login;
    private TextView tv_register;
    private EditText edit_name, edit_pw;
    private SharedPreferences sp;
    private String name, pw;
    private DBHelper dbHelper;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    sp.edit().putString("name", name).commit();
                    sp.edit().putString("pw", pw).commit();
                    sp.edit().putBoolean("isLogin", true).commit();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                    break;
                case 1:
                    Toast.makeText(LoginActivity.this,"账号或密码错误", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sp = SharePreferenceUtil.getSp(this);

        dbHelper = new DBHelper(this);

        bt_login = findViewById(R.id.bt_login);
        tv_register = findViewById(R.id.tv_register);
        edit_name = findViewById(R.id.edit_name);
        edit_pw = findViewById(R.id.edit_pw);

        bt_login.setOnClickListener(this);
        tv_register.setOnClickListener(this);

        autoLogin();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_login:
                login();
                break;
            case R.id.tv_register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            default:
                break;
        }
    }

    private void login(){
        name = edit_name.getText().toString();
        pw = edit_pw.getText().toString();
        if (name.isEmpty()){
            edit_name.setError("账号不能为空");
        }else if (pw.isEmpty()){
            edit_pw.setError("密码不能为空");
        }else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (dbHelper.isLogin(name, pw)){
                        handler.sendEmptyMessage(0);
                    }else {
                        handler.sendEmptyMessage(1);
                    }
                }
            }).start();
        }
    }



    private void autoLogin(){
        boolean isLogin = sp.getBoolean("isLogin", false);
        if (isLogin){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
}