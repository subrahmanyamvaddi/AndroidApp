package com.example.subrahmanyamvaddi.detect;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LoginDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "user_info";
    public static final int DATABASE_VERSION = 1;

    public static final String CREATE_TABLE = "create table " + LoginContract.LoginEntry.TABLE_NAME +
            "(" + LoginContract.LoginEntry.CONTACT_ID+ " text,"+ LoginContract.LoginEntry.NAME+ " text,"+
            LoginContract.LoginEntry.EMAIL+ " text," + LoginContract.LoginEntry.PASSWORD+ " text);";

    public static final String DROP_TABLE = "drop table if exists " + LoginContract.LoginEntry.TABLE_NAME;

    public LoginDBHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        Log.d("Database Operations", "Database Created..!");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        Log.d("Database Operations","Table created..!");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    public void addUser(String id, String name, String email,String password,SQLiteDatabase db){
        ContentValues val = new ContentValues();
        val.put(LoginContract.LoginEntry.CONTACT_ID,id);
        val.put(LoginContract.LoginEntry.NAME,name);
        val.put(LoginContract.LoginEntry.EMAIL,email);
        val.put(LoginContract.LoginEntry.PASSWORD,password);

        db.insert(LoginContract.LoginEntry.TABLE_NAME,null,val);
        Log.d("Database Operations", "One record inserted..!");
    }

    public Cursor readUser(SQLiteDatabase db){
        String[] projections = {LoginContract.LoginEntry.CONTACT_ID,LoginContract.LoginEntry.NAME,LoginContract.LoginEntry.EMAIL,
                                LoginContract.LoginEntry.PASSWORD};

        Cursor cursor = db.query(LoginContract.LoginEntry.TABLE_NAME,projections,null,null,null,null,null);
        return cursor;
    }


    public int CheckRecordExists(String Id,String password,SQLiteDatabase db) {
        String Query = "Select * from " + LoginContract.LoginEntry.TABLE_NAME + " where " + LoginContract.LoginEntry.CONTACT_ID + " ='" + Id +"'";
        try {
            Cursor cursor = db.rawQuery(Query, null);
            if(cursor.getCount() <= 0){
                cursor.close();
                return 0;
            }
            else
            {
                while(cursor.moveToNext()){
                    String id = cursor.getString(cursor.getColumnIndex(LoginContract.LoginEntry.CONTACT_ID));
                    String pwd = cursor.getString(cursor.getColumnIndex(LoginContract.LoginEntry.PASSWORD));
                    if(id.equals(Id) && pwd.equals(password))
                        return 1;
                    else
                        return 2;
                }
                return 3;
            }
        }
        catch (SQLiteException e){
            throw new SQLiteException("DB Error: " + e.getMessage());
        }

    }

}
