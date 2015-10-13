package org.medsolis.vitalreading;

import java.io.File;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

public class DatabaseHandler extends SQLiteOpenHelper {
     
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "DB_iHealthDevices";
 
    // Device table name
    private static final String TABLE_DEVICE_LIST = "tbl_deviceList";
 
    // Device List Table Columns names
    private static final String KEY_DEVICE_TYPE = "device_type";
    private static final String KEY_DEVICE_MAC = "device_mac";
 
    public DatabaseHandler(Context context) {
        super(context, Environment.getExternalStorageDirectory()
                + File.separator+ DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_DEVICE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_DEVICE_LIST + "("
                + KEY_DEVICE_TYPE + " TEXT,"
                + KEY_DEVICE_MAC + " TEXT PRIMARY KEY" + ")";
        db.execSQL(CREATE_DEVICE_TABLE);
    }
 
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEVICE_LIST);
        // Create tables again
        onCreate(db);
    }
    
    // Adding new Device
    public void addDevice(DeviceList device) {
        SQLiteDatabase db = this.getWritableDatabase();
     
        ContentValues values = new ContentValues();
        values.put(KEY_DEVICE_TYPE, device.getDeviceType()); // Device Type
        values.put(KEY_DEVICE_MAC, device.getDeviceMAC()); // Device MAC
     
        // Inserting Row
        db.insert(TABLE_DEVICE_LIST, null, values);
        db.close(); // Closing database connection
    }
    
    // Getting single Device
    public Cursor getDevice(String macID) {
        SQLiteDatabase db = this.getReadableDatabase();
     
        Cursor cursor = db.query(TABLE_DEVICE_LIST, new String[] {
                KEY_DEVICE_TYPE, KEY_DEVICE_MAC }, KEY_DEVICE_MAC + "=?",
                new String[] { macID }, null, null, null, null);
    
        // return contact
        return cursor;
    }
    
    // Getting All Devices
    public Cursor getAllDevices() {
      
       // Select All Query
       String selectQuery = "SELECT  * FROM " + TABLE_DEVICE_LIST;
       SQLiteDatabase db = this.getWritableDatabase();
       Cursor cursor = db.rawQuery(selectQuery, null);
       // return cursor
       return cursor;
   }
    
    // Getting Device Count
    public int getDeviceCount() {
        String countQuery = "SELECT  * FROM " + TABLE_DEVICE_LIST;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
 
        // return count
        return cursor.getCount();
    }
}
