package com.yiran.publish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private Button bt_register;
    private TextView tv_go_login;
    private EditText edit_account, edit_pw;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DBHelper(this);

        bt_register = findViewById(R.id.bt_register);
        tv_go_login = findViewById(R.id.tv_go_login);
        edit_account = findViewById(R.id.edit_re_account);
        edit_pw = findViewById(R.id.edit_re_pw);

        bt_register.setOnClickListener(this);
        tv_go_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_register:
                register();
                break;
            case R.id.tv_go_login:
                finish();
                break;
            default:
                break;
        }
    }

    private void register(){
        String name = edit_account.getText().toString();
        String pw = edit_pw.getText().toString();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (dbHelper.insertUser(name, pw)){
                    handler.sendEmptyMessage(0);
                }
            }
        }).start();
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    Toast.makeText(RegisterActivity.this,"注册成功", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}