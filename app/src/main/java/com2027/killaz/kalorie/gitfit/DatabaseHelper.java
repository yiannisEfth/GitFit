package com2027.killaz.kalorie.gitfit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Chris on 03/03/2019.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper instance;
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "GitFitDB.db";

    private static final String TABLE_NAME = "user_record";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_STEPS = "steps";

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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        createDatabase(db);
    }

    /**
     * A method called to set up the database table(s).
     *
     * @param sqLiteDatabase
     */
    public void createDatabase(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                COLUMN_DATE + " VARCHAR(10) PRIMARY KEY, " +
                COLUMN_STEPS + " INTEGER)";

        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);
    }

    /**
     * A method to insert a new daily record into the db.
     *
     * @param date The record date.
     * @param steps The number of steps completed.
     */
    public void newRecord(String date, int steps) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_STEPS, steps);

        long newID = db.insert(TABLE_NAME, null, values);
    }

    /**
     * A method to delete a daily record from the db.
     *
     * @param date The record date.
     */
    public void deleteRecord(String date) {
        SQLiteDatabase db = this.getWritableDatabase();

        String where = COLUMN_DATE + " = ?";
        String[] dateArr = new String[]{date};

        db.delete(TABLE_NAME, where, dateArr);
    }

    /**
     * A method to get the steps taken on a particular date.
     *
     * @param date The date to get the steps taken from.
     * @return The steps taken.
     */
    public int getSteps(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME +
                "WHERE " + COLUMN_DATE + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{date});

        return cursor.getInt(cursor.getColumnIndex(COLUMN_STEPS));
    }
}
