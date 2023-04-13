package com.example.scoutoapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.example.scoutoapp.CarItem;

import java.util.ArrayList;
import java.util.Arrays;

public class CarsDB extends SQLiteOpenHelper {

    private static final String DB_NAME = "cars_db";
    private static final int DB_VERSION = 4;

    public static CarsDB INSTANCE;
    private static final String USER_TABLE = "users";
    private static final String CARS_TABLE = "cars";

    private static final String ID = "id";
    private static final String MAKE_ID = "make_id";
    private static final String CAR_MAKE = "car_make";
    private static final String CAR_MODEL = "car_model";
    private static final String IMAGE_URL = "image_url";
    private static final String USER_EMAIL = "user_name";
    private static final String USER_PASS = "user_pass";
    private static final String BLOB_IMG = "blob_img";

    public CarsDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // singleton object
    public static synchronized CarsDB getInstance(Context context){
        if(INSTANCE == null){
            return new CarsDB(context.getApplicationContext());
        }
        return INSTANCE;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createCarsTable = "CREATE TABLE IF NOT EXISTS " + CARS_TABLE + " ( " + MAKE_ID + " INTEGER PRIMARY KEY, " + ID + " INTEGER," +
                CAR_MAKE + " TEXT, " + CAR_MODEL + " TEXT, " + IMAGE_URL + " TEXT, "+ BLOB_IMG +" BLOB);";
        String createUserTable = "CREATE TABLE IF NOT EXISTS " + USER_TABLE + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                USER_EMAIL + " TEXT, " + USER_PASS + " TEXT);";
        db.execSQL(createCarsTable);
        db.execSQL(createUserTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String DROP_TABLE1 = "DROP TABLE IF EXISTS " + CARS_TABLE;
        String DROP_TABLE2 = "DROP TABLE IF EXISTS " + USER_TABLE;
        db.execSQL(DROP_TABLE1);
        db.execSQL(DROP_TABLE2);
        onCreate(db);
    }

    public ArrayList<CarItem> getAllCarsItems(int _id) {
        ArrayList<CarItem> carsList = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + CARS_TABLE + " WHERE "+ ID + " = " +_id, null);

        while (cursor.moveToNext()) {
            int make_id = cursor.getInt(0);
            String car_make = cursor.getString(2);
            String car_model = cursor.getString(3);
            String image_url = cursor.getString(4);
            byte[] imageBytes = cursor.getBlob(5);
            CarItem item = new CarItem(make_id,car_make, car_model,image_url, imageBytes);
            carsList.add(item);
        }

        cursor.close();
        db.close();
        return carsList;
    }

    public int isUserAvailable(String email, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + USER_TABLE, null);

        while (cursor.moveToNext()) {
            int getID = Integer.parseInt(cursor.getString(0));
            String getEmail = cursor.getString(1);
            String getPassword = cursor.getString(2);

            if (email.equals(getEmail) && password.equals(getPassword)) {
                cursor.close();
                db.close();
                return getID;
            }
        }

        cursor.close();
        db.close();
        return -1;
    }

    public void addCar(CarItem carItem, int user_id) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        byte[] arr = new byte[0];
        values.put(MAKE_ID, carItem.getMake_id());
        values.put(ID, user_id);
        values.put(CAR_MAKE, carItem.getCar_make());
        values.put(CAR_MODEL, carItem.getCar_model());
        values.put(IMAGE_URL, "");
        values.put(BLOB_IMG, arr);
        db.insert(CARS_TABLE, null, values);
        db.close();
    }

    public void updateCar(CarItem item, int user_id, String img, byte[] imageString){
        SQLiteDatabase db = getWritableDatabase();
        String updateQuery;
        if(img == null || img.isEmpty()){
            updateQuery = "UPDATE " + CARS_TABLE +" SET "+BLOB_IMG+" = "+ Arrays.toString(imageString) + " WHERE "+ID+" = "+user_id+" AND "+MAKE_ID+" = "+item.getMake_id()+";";
        }else{
            updateQuery = "UPDATE "+CARS_TABLE+" SET "+IMAGE_URL+" = '"+ img + "' WHERE "+ID+" = "+user_id+" AND "+MAKE_ID+" = "+item.getMake_id()+";";
        }
        db.execSQL(updateQuery);
        db.close();
    }

    public void deleteCar(CarItem item, int user_id){
        SQLiteDatabase db = getWritableDatabase();
        String deleteQuery = "DELETE FROM "+CARS_TABLE +" WHERE "+ID +" = "+user_id+" AND "+MAKE_ID+" = "+item.getMake_id()+" ;";
        db.execSQL(deleteQuery);
        db.close();
    }
    public void addUser(String email, String password) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_EMAIL, email);
        values.put(USER_PASS, password);
        db.insert(USER_TABLE, null, values);
        db.close();
    }
}

