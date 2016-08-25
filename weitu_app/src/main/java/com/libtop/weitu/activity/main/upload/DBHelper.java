package com.libtop.weitu.activity.main.upload;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * <p>
 * Title: DBHelper.java
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * CreateTime：16/5/18
 * </p>
 *
 * @author 作者名
 * @version common v1.0
 */
public class DBHelper extends SQLiteOpenHelper {
    public static String sqlite = "test13";
    public static int factory = 1;
    public String tablename;
    private Context mcontext;
    public static final String TABLENAME = "yuntu";

    public DBHelper(Context context) {
        super(context, sqlite, null, factory);
        this.mcontext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table yuntu(id string, score1 string)");

    }

    /**
     * 删除视频ID
     */
    public void Droptablename(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL("delete from yuntu WHERE id = " + "'" + id + "'");
        } catch (Exception e) {
            Log.e("删除表中名 ", e.getMessage());
        }
    }

    /**
     * 添加视频ID
     */
    public void insertTalbe(String id, String url) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", id);
        cv.put("score1", url);
        db.insert("yuntu", null, cv);
    }


    public Cursor query(String sql, String[] args) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, args);
        return cursor;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
