package com.yiran.publish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class PublishActivity extends AppCompatActivity {
    private ImageView iv_back;
    private Button bt_publish;
    private EditText edit_title, edit_content;
    private SharedPreferences sp;
    private String name;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);

        dbHelper = new DBHelper(this);

        sp = SharePreferenceUtil.getSp(this);
        iv_back = findViewById(R.id.iv_back);
        bt_publish = findViewById(R.id.bt_publish);
        edit_title = findViewById(R.id.edit_title);
        edit_content = findViewById(R.id.edit_content);
        name = sp.getString("name", null);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bt_publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = edit_title.getText().toString();
                String content = edit_content.getText().toString();
                if (title.isEmpty()){
                    edit_title.setError(Html.fromHtml("<font color=#E10979><br/>请输入标题</font>"));
                }

                if (content.isEmpty()){
                    edit_content.setError(Html.fromHtml("<font color=#E10979><br/>请输入内容</font>"));
                }

                if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(content)){
                    String time = TimeUtil.getTime();
                    publish(title, content, time);
                }
            }
        });
    }

    private void publish(String title, String content, String time){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (dbHelper.insertPublish(name,title, content, time)){
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
                    Toast.makeText(PublishActivity.this,"发布成功", Toast.LENGTH_SHORT).show();
                    finish();
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