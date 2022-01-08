package com.yiran.publish;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;


public class DBHelper extends SQLiteOpenHelper {
    private Context mContext;

    private static final String LOG_TAG = "DBHelper";

    public static final String DATABASE_NAME = "saved_publish.db";
    private static final int DATABASE_VERSION = 1;

    public static abstract class DBHelperItem implements BaseColumns {
        public static final String TABLE_NAME_USER = "t_user";
        public static final String TABLE_NAME_PUBLISH = "t_publish";

        //title, content, time, account
        public static final String COLUMN_NAME_ACCOUNT = "account";
        public static final String COLUMN_NAME_PASSWORD = "password";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_CONTENT = "content";
        public static final String COLUMN_NAME_TIME = "time";
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_PUBLISH =
            "CREATE TABLE " + DBHelperItem.TABLE_NAME_PUBLISH + " (" +
                    DBHelperItem._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
                    DBHelperItem.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    DBHelperItem.COLUMN_NAME_CONTENT + TEXT_TYPE + COMMA_SEP +
                    DBHelperItem.COLUMN_NAME_TIME + TEXT_TYPE + COMMA_SEP +
                    DBHelperItem.COLUMN_NAME_ACCOUNT + TEXT_TYPE + ")";

    private static final String SQL_CREATE_USER =
            "CREATE TABLE " + DBHelperItem.TABLE_NAME_USER + " (" +
                    DBHelperItem.COLUMN_NAME_ACCOUNT + TEXT_TYPE + COMMA_SEP +
                    DBHelperItem.COLUMN_NAME_PASSWORD + TEXT_TYPE+ ")";

    @SuppressWarnings("unused")
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DBHelperItem.TABLE_NAME_USER;

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_USER);
        db.execSQL(SQL_CREATE_PUBLISH);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    /**
     * 插入user
     */
    public boolean insertUser(String account, String pw){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHelperItem.COLUMN_NAME_ACCOUNT, account);
        cv.put(DBHelperItem.COLUMN_NAME_PASSWORD, pw);
        long rowId = db.insert(DBHelperItem.TABLE_NAME_USER, null, cv);
        if (rowId != -1){
            return true;
        }
        return false;
    }

    /**
     * 插入发布
     */
    public boolean insertPublish(String account, String title, String content, String time){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHelperItem.COLUMN_NAME_TITLE, title);
        cv.put(DBHelperItem.COLUMN_NAME_CONTENT, content);
        cv.put(DBHelperItem.COLUMN_NAME_TIME, time);
        cv.put(DBHelperItem.COLUMN_NAME_ACCOUNT, account);
        long rowId = db.insert(DBHelperItem.TABLE_NAME_PUBLISH, null, cv);
        if (rowId != -1){
            return true;
        }
        return false;
    }

    /**
     * 查询所有发布
     * @return
     */
    @SuppressLint("Range")
    public List<DataBean> queryAll(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<DataBean> list= new ArrayList<DataBean>();
        String sql = "select * from t_publish";
        Cursor c = db.rawQuery(sql, null);
        if (c.moveToFirst()){
            do{
                DataBean item = new DataBean();
                item.setId(c.getInt(c.getColumnIndex(DBHelperItem._ID)));
                item.setAccount(c.getString(c.getColumnIndex(DBHelperItem.COLUMN_NAME_ACCOUNT)));
                item.setTitle(c.getString(c.getColumnIndex(DBHelperItem.COLUMN_NAME_TITLE)));
                item.setContent(c.getString(c.getColumnIndex(DBHelperItem.COLUMN_NAME_CONTENT)));
                item.setTime(c.getString(c.getColumnIndex(DBHelperItem.COLUMN_NAME_TIME)));
                list.add(item);
            }
            while(c.moveToNext());
        }
        c.close();
        db.close();
        return list;
    }

    /**
     * 根据用户查询所有发布
     * @return
     */
    @SuppressLint("Range")
    public List<DataBean> queryPublish(String account){
        List<DataBean> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select * from t_publish where account="+"'"+account+"'";
        Cursor c = db.rawQuery(sql, null);
        if (c.moveToFirst()){
            do{
                DataBean item = new DataBean();
                item.setId(c.getInt(c.getColumnIndex(DBHelperItem._ID)));
                item.setAccount(c.getString(c.getColumnIndex(DBHelperItem.COLUMN_NAME_ACCOUNT)));
                item.setTitle(c.getString(c.getColumnIndex(DBHelperItem.COLUMN_NAME_TITLE)));
                item.setContent(c.getString(c.getColumnIndex(DBHelperItem.COLUMN_NAME_CONTENT)));
                item.setTime(c.getString(c.getColumnIndex(DBHelperItem.COLUMN_NAME_TIME)));
                list.add(item);

            }
            while(c.moveToNext());
        }
        c.close();
        db.close();
        return list;
    }

    /**
     * 登陆验证
     *
     * @return
     */
    @SuppressLint("Range")
    public boolean isLogin(String account, String pw) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select * from t_user where account="+"'"+account+"'";
        Cursor c = db.rawQuery(sql, null);
        if (c.moveToFirst()){
            do{
                String password = c.getString(c.getColumnIndex(DBHelperItem.COLUMN_NAME_PASSWORD));
                if (pw.equals(password)){
                    return true;
                }
            }
            while(c.moveToNext());
        }
        c.close();
        db.close();
        return false;
    }

    //编辑用户信息
    public boolean updateUser(String account, String pw){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHelperItem.COLUMN_NAME_ACCOUNT, account);
        cv.put(DBHelperItem.COLUMN_NAME_PASSWORD, pw);
        String[] whereArgs = {account};
        db.update(DBHelperItem.TABLE_NAME_USER, cv,"account=?", whereArgs);

        return true;
    }

    /**
     * 删除发布
     * @param id
     * @return
     */
    public boolean deletePublish(int id){
        SQLiteDatabase db = getWritableDatabase();
        int result = db.delete(DBHelperItem.TABLE_NAME_PUBLISH, "_ID=?",
                new String[] { String.valueOf(id) });
        if (result > 0){
            return true;
        }
        return false;
    }

    //编辑发布
    public boolean updatePublish(int id, String title, String content){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHelperItem.COLUMN_NAME_TITLE, title);
        cv.put(DBHelperItem.COLUMN_NAME_CONTENT, content);
        String[] whereArgs = {String.valueOf(id),};
        db.update(DBHelperItem.TABLE_NAME_PUBLISH, cv,"_ID=?", whereArgs);
        return true;
    }
}

