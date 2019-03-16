package com2027.killaz.kalorie.gitfit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Chris on 03/03/2019.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper instance;
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "GitFitDB.db";

    private static final String USER_RECORDS = "user_record";
    private static final String USER_NAME = "user_name";
    private static final String USER_RECORD_DATE = "date";
    private static final String USER_RECORD_STEPS = "steps";

    /**
     * Private constructor.
     *
     * @param context The application context.
     */
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * A method to get the instance of the DatabaseHelper.
     *
     * @param context The application context.
     * @return The helper instance.
     */
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context);
        }
        return instance;
    }

    /**
     * A method when the database is created.
     *
     * @param sqLiteDatabase The database.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createDatabase(sqLiteDatabase);
    }

    /**
     * A method called when the database is upgraded.
     *
     * @param db The database.
     * @param oldVersion The old database version number.
     * @param newVersion The new database version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_RECORDS);
        createDatabase(db);
    }

    /**
     * A method called to set up the database table(s).
     *
     * @param sqLiteDatabase
     */
    public void createDatabase(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_TABLE = "CREATE TABLE " + USER_RECORDS + "(" +
                USER_NAME + "VARCHAR(30) PRIMARY KEY, " +
                USER_RECORD_DATE + " VARCHAR(10) UNIQUE NOT NULL, " +
                USER_RECORD_STEPS + " INTEGER)";

        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);
    }

    /**
     * A method to insert a new daily record into the db.
     *
     * @param date The record date.
     * @param steps The number of steps completed.
     */
    public void newRecord(String user, Date date, int steps) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Convert date to string for insertion
        String dateString = getDateString(date);

        ContentValues values = new ContentValues();
        values.put(USER_NAME, user);
        values.put(USER_RECORD_DATE, dateString);
        values.put(USER_RECORD_STEPS, steps);

        long newID = db.insert(USER_RECORDS, null, values);
    }

    /**
     * A method to delete a daily record from the db.
     *
     * @param date The record date.
     */
    public void deleteRecord(String user, Date date) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Convert date to string for insertion
        String dateString = getDateString(date);

        String where = USER_NAME + " = ? AND " + " USER_RECORD_DATE = ?";
        String[] values = new String[]{user, dateString};

        db.delete(USER_RECORDS, where, values);
    }

    /**
     * A method to get the steps taken on a particular date.
     *
     * @param date The date to get the steps taken from.
     * @return The steps taken.
     */
    public int getSteps(String user, Date date) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + USER_RECORDS +
                " WHERE " + USER_NAME + " = ? AND " +
                USER_RECORD_DATE + " = ?";

        // Convert date to string for insertion
        String dateString = getDateString(date);

        int result;
        Cursor cursor = db.rawQuery(query, new String[]{user, dateString});

        if (!(cursor.moveToFirst()) || cursor.getCount() ==0){
            // cursor is empty
            result = 0;
        } else {
            // get steps
            result = cursor.getInt(cursor.getColumnIndex(USER_RECORD_STEPS));
        }

        cursor.close();
        return result;
    }

    public String getDateString(Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-mm-DD", Locale.getDefault());
        return df.format(date);
    }
}
