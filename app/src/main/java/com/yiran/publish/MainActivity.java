package com.yiran.publish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_all, tv_mine;
    private ImageView iv_menu;
    private FloatingActionButton floatButton;
    private PublishAdapter adapter;
    private ListView list_publish;
    private List<DataBean> all_list = new ArrayList<>();
    private List<DataBean> my_list = new ArrayList<>();
    private SharedPreferences sp;
    private String name;
    private boolean flag = false;
    private DBHelper dbHelper;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    all_list = (List<DataBean>) msg.obj;
                    initMyData();
                    break;
                case 1:
                    my_list = (List<DataBean>) msg.obj;
                    if (flag){
                        adapter = new PublishAdapter(MainActivity.this, my_list);
                        list_publish.setAdapter(adapter);
                    }else {
                        adapter = new PublishAdapter(MainActivity.this, all_list);
                        list_publish.setAdapter(adapter);
                    }
                    break;
                case 2:
                    initAllData();
                    Toast.makeText(MainActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new DBHelper(this);
        initView();
    }

    private void initAllData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (dbHelper.queryAll() != null){
                    Message message = handler.obtainMessage();
                    message.obj = dbHelper.queryAll();
                    message.what = 0;
                    handler.sendMessage(message);
                }
            }
        }).start();
    }

    private void initMyData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (dbHelper.queryPublish(name) != null){
                    Message message = handler.obtainMessage();
                    message.obj = dbHelper.queryPublish(name);
                    message.what = 1;
                    handler.sendMessage(message);
                }
            }
        }).start();
    }

    private void initView() {
        sp = SharePreferenceUtil.getSp(this);
        tv_all = findViewById(R.id.tv_all);
        tv_mine = findViewById(R.id.tv_mine);
        list_publish = findViewById(R.id.listview_publish);
        iv_menu = findViewById(R.id.iv_menu);
        floatButton = findViewById(R.id.float_add);

        name = sp.getString("name", null);

        tv_all.setOnClickListener(this);
        tv_mine.setOnClickListener(this);
        iv_menu.setOnClickListener(this);
        floatButton.setOnClickListener(this);

        list_publish.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (flag) {
                    showPublishMenu(view, position);
                }
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("flag---->"+flag);
        initAllData();
    }

    private void showMenu(View view){
        PopupMenu menu = new PopupMenu(this, view);
        getMenuInflater().inflate(R.menu.menu_main, menu.getMenu());
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_info:
                        startActivity(new Intent(MainActivity.this, ModifyActivity.class));
                        break;
                    case R.id.menu_exit:
                        sp.edit().putBoolean("isLogin", false).commit();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        menu.show();
    }


    private void showPublishMenu(View view, int pos){
        PopupMenu menu = new PopupMenu(this, view);
        getMenuInflater().inflate(R.menu.menu_publish, menu.getMenu());
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_modify:
                        Intent intent = new Intent(MainActivity.this, ModifyPubActivity.class);
                        intent.putExtra("title", my_list.get(pos).getTitle());
                        intent.putExtra("content", my_list.get(pos).getContent());
                        intent.putExtra("id", my_list.get(pos).getId());
                        startActivity(intent);
                        break;
                    case R.id.menu_delete:
                        deletePublish(my_list.get(pos).getId());
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        menu.show();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_all:
                flag = false;
                tv_all.setTextSize(20);
                tv_all.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
                tv_mine.setTextSize(16);
                tv_mine.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);

                adapter = new PublishAdapter(this, all_list);
                list_publish.setAdapter(adapter);

                break;
            case R.id.tv_mine:
                flag = true;
                tv_mine.setTextSize(20);
                tv_mine.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
                tv_all.setTextSize(16);
                tv_all.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);

                adapter = new PublishAdapter(this, my_list);
                list_publish.setAdapter(adapter);
                break;
            case R.id.iv_menu:
                showMenu(v);
                break;
            case R.id.float_add:
                startActivity(new Intent(this, PublishActivity.class));
                break;
            default:
                break;
        }
    }


    private void deletePublish(int id){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (dbHelper.deletePublish(id)){
                    handler.sendEmptyMessage(2);
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}