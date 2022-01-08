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

public class ModifyPubActivity extends AppCompatActivity {
    private ImageView iv_back;
    private EditText edit_title, edit_content;
    private Button bt_save;
    private int id;
    private String old_title, old_content;
    private DBHelper dbHelper;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    Toast.makeText(ModifyPubActivity.this,"修改成功", Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_modify_pub);

        dbHelper = new DBHelper(this);
        old_title = getIntent().getStringExtra("title");
        old_content = getIntent().getStringExtra("content");
        id = getIntent().getIntExtra("id", 0);

        initView();
    }

    private void initView() {
        edit_title = findViewById(R.id.edit_md_title);
        edit_content = findViewById(R.id.edit_md_content);
        bt_save = findViewById(R.id.bt_md_save);
        iv_back = findViewById(R.id.iv_publish_back);

        edit_title.setText(old_title);
        edit_content.setText(old_content);

        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyPub();
            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void updateData(int id, String title, String content){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (dbHelper.updatePublish(id, title, content)){
                    handler.sendEmptyMessage(0);
                }
            }
        }).start();
    }

    private void modifyPub(){
        String title = edit_title.getText().toString();
        String content = edit_content.getText().toString();


        if (title.isEmpty()){
            edit_title.setError("标题不能为空");
        }else if (content.isEmpty()){
            edit_content.setError("内容不能为空");
        } else {
            updateData(id, title, content);
        }
    }
}