package com.personal.bewise.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class LoginTable extends TransactionsDatabase {

    // LOGIN Table Columns names
    private static final String LOGIN_ID = "id";
    // TODO: Make password encrypted
    private static final String LOGIN_PASSWORD = "password";
    // Create login table
    private static final String CREATE_LOGIN_TABLE = "CREATE TABLE IF NOT EXISTS " + LOGIN_TABLE + " (" + LOGIN_ID + " INTEGER PRIMARY KEY," + LOGIN_PASSWORD
            + TEXT_TYPE + " )";

    public LoginTable(Context context) {
        super(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_LOGIN_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy
        // is to simply to discard the data and start over
        db.execSQL(CREATE_LOGIN_TABLE);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public boolean verifiyPassword(String password) {
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();
        String[] projection = {LOGIN_PASSWORD};
        String[] selectionArgs = {"1"};
        Cursor cursor = db.query(LOGIN_TABLE, projection, LOGIN_ID, selectionArgs, null, null, null);
        cursor.moveToFirst();
        String pass = cursor.getString(cursor.getColumnIndexOrThrow(LOGIN_PASSWORD));
        db.close();
        cursor.close();
        return pass.equals(password);
    }

    /**
     * @param password
     */
    public void insertInLoginTable(String password) {
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(LOGIN_ID, 1);
        values.put(LOGIN_PASSWORD, password);

        // Insert the new row, returning the primary key value of the new row
        db.insert(LOGIN_TABLE, null, values);
        db.close();
    }

    /**
     * Delete password.
     */
    public void deletePassword() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(LOGIN_TABLE, LOGIN_ID + " = ?", new String[]{String.valueOf(1)});
        db.close();
    }

}
