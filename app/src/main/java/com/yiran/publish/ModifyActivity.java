package com.yiran.publish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class ModifyActivity extends AppCompatActivity {
    private ImageView iv_back;
    private EditText edit_old, edit_new, edit_again;
    private Button bt_ok;
    private SharedPreferences sp;
    private String name, pw;
    private DBHelper dbHelper;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    Toast.makeText(ModifyActivity.this,"修改成功", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);
        dbHelper = new DBHelper(this);
        initView();
    }

    private void initView() {
        edit_old = findViewById(R.id.edit_old);
        edit_new = findViewById(R.id.edit_new);
        edit_again = findViewById(R.id.edit_new_again);
        bt_ok = findViewById(R.id.bt_save);
        iv_back = findViewById(R.id.iv_modify_back);
        sp = SharePreferenceUtil.getSp(this);
        name = sp.getString("name", null);
        pw = sp.getString("pw", null);

        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyPw();
            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void updateData(final String new_pw){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (dbHelper.updateUser(name, new_pw)){
                    handler.sendEmptyMessage(0);
                }
            }
        }).start();
    }

    private void modifyPw(){
        String old = edit_old.getText().toString();
        String new_pw = edit_new.getText().toString();
        String pw_again = edit_again.getText().toString();

        if (old.isEmpty()){
            edit_old.setError("旧密码不能为空");
        }else if (new_pw.isEmpty()){
            edit_new.setError("新密码不能为空");
        }else if (pw_again.isEmpty()){
            edit_again.setError("请确认密码");
        }else if (!old.equals(pw)){
            edit_old.setError("输入的原密码不对");
        }else if (!new_pw.equals(pw_again)){
            edit_again.setError("两次输入的密码不一致");
        } else {
            updateData(new_pw);
        }
    }
}