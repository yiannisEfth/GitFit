package com2027.killaz.kalorie.gitfit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Developed by Christopher Dueck and David Devlin.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper instance;
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "GitFitDB.db";

    private static final String USER_RECORDS = "user_records";
    private static final String USER_NAME = "user_name";
    private static final String USER_RECORD_DATE = "date";
    private static final String USER_RECORD_STEPS = "steps";

    private static final String USER_BMI = "user_bmi";
    private static final String USER_WEIGHT = "user_weight";
    private static final String USER_HEIGHT = "user_height";
    private static final String USER_GENDER = "user_gender";

    private static final String GET_USER_ROWS = "SELECT count(*) FROM " + USER_RECORDS +
            " WHERE " + USER_NAME + " = ? ORDER BY " + USER_RECORD_DATE;

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
        String SQL_CREATE_TABLE1 = "CREATE TABLE " + USER_RECORDS + "(" +
                USER_NAME + " VARCHAR(30) NOT NULL, " +
                USER_RECORD_DATE + " VARCHAR(10) NOT NULL, " +
                USER_RECORD_STEPS + " INTEGER, " +
                "PRIMARY KEY (" + USER_NAME + ", " + USER_RECORD_DATE + "))";

        String SQL_CREATE_TABLE_BMI = "CREATE TABLE " + USER_BMI + "(" +
                USER_NAME + " VARCHAR(30) NOT NULL, " +
                USER_WEIGHT + " REAL, " +
                USER_HEIGHT + " REAL, " +
                USER_GENDER + " INTEGER, " +
                "PRIMARY KEY (" + USER_NAME + "))";

        sqLiteDatabase.execSQL(SQL_CREATE_TABLE1);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_BMI);
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
        db.execSQL("DROP TABLE IF EXISTS " + USER_BMI);
        onCreate(db);
    }

    /**
     * A method to insert a new daily record into the db.
     *
     * @param user The user to be accessed.
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

        // Check if there are more than 31 days stored.
        Cursor cursor = db.rawQuery(GET_USER_ROWS, new String[]{user});

        // If there are more than 31 days stored, delete the oldest record.
        if (cursor.getCount() > 31) {
            cursor.moveToFirst();
            String oldDateString = cursor.getString(cursor.getColumnIndex(USER_RECORD_DATE));
            Date oldDate = null;
            try {
                oldDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(oldDateString);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            deleteRecord(user, oldDate);
        }
    }

    /**
     * A method to update the steps in a record.
     *
     * @param user The user to be accessed.
     * @param date The date to be accessed.
     * @param steps The steps to be updated.
     */
    public void updateRecordSteps(String user, Date date, int steps) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Convert date to string for insertion
        String dateString = getDateString(date);

        ContentValues values = new ContentValues();
        values.put(USER_RECORD_STEPS, steps);

        String where = USER_NAME + " = ? AND " + USER_RECORD_DATE + " = ?";
        String[] whereArgs = new String[]{user, dateString};

        int rowsAffected = -1;
        try {
            rowsAffected = db.update(USER_RECORDS, values, where, whereArgs);
            Log.i("ROWS_UPDATED", String.valueOf(rowsAffected));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // I should change this later so it checks first
        if (rowsAffected == 0) {
            newRecord(user, date, steps);
        }
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

        String where = USER_NAME + " = ? AND " + USER_RECORD_DATE + " = ?";
        String[] values = new String[]{user, dateString};

        db.delete(USER_RECORDS, where, values);
    }

    /**
     * A method to get the steps taken on a particular date.
     *
     * @param user The user to access.
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

        int result = 0;
        try {
            Cursor cursor = db.rawQuery(query, new String[]{user, dateString});

            // Check cursor is not empty
            if (cursor.moveToFirst() && cursor.getCount() > 0) {
                result = cursor.getInt(cursor.getColumnIndex(USER_RECORD_STEPS));
            }

            cursor.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }


        return result;
    }

    public String getDateString(Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return df.format(date);
    }

    /** BMI TABLE METHODS **/

    /**
     *
     * This method saves a users weight and height to the database.
     * This method edits an existing entry if the user is already in the table,
     * so a new entry is only created if it has to be
     * @param user The user to access.
     * @param weight The input weight of the user
     * @param height The input height of the user
     */
    public boolean saveRecordsBMI(String user, float weight, float height, int gender){
        boolean success = false;
        SQLiteDatabase dbRead = this.getReadableDatabase();
        SQLiteDatabase dbWrite = this.getWritableDatabase();
        String query = "SELECT * FROM " + USER_BMI + " WHERE " + USER_NAME + " = ?";

        ContentValues values = new ContentValues();
        values.put(USER_WEIGHT, weight);
        values.put(USER_HEIGHT, height);
        values.put(USER_NAME, user);
        if(gender == 1 || gender == 0) {
            values.put(USER_GENDER, gender);
        }

        // Edit existing row if user is already in table
        Cursor cursor = dbRead.rawQuery(query, new String[]{user});

        if (cursor.getCount() > 0){
            int rowsAffected = -1;
            try {
                String where = USER_NAME + " = ?";
                String[] args = new String[]{user};
                rowsAffected = dbWrite.update(USER_BMI, values, where, args);
                Log.i("ROWS_UPDATED", String.valueOf(rowsAffected));
                if (rowsAffected >= 0) {
                    success = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                long newID = dbWrite.insert(USER_BMI, null, values);
                if (newID != -1) {
                    success = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        cursor.close();
        return success;
    }

    /**
     * Used to get the users weight from the local database
     * @param user The current user
     * @return the weight of the user stored in the db
     */
    public double getUserWeight(String user) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + USER_BMI +
                " WHERE " + USER_NAME + " = ?";

        double result = 0;
        try {
            Cursor cursor = db.rawQuery(query, new String[]{user});

            // Check cursor is not empty
            if (cursor.moveToFirst() && cursor.getCount() > 0) {
                result = cursor.getDouble(cursor.getColumnIndex(USER_WEIGHT));
            }

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Used to get the users height from the local database
     * @param user The current user
     * @return the height of the user stored in the db
     */
    public double getUserHeight(String user) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + USER_BMI +
                " WHERE " + USER_NAME + " = ?";

        double result = 0;
        try {
            Cursor cursor = db.rawQuery(query, new String[]{user});

            // Check cursor is not empty
            if (cursor.moveToFirst() && cursor.getCount() > 0) {
                result = cursor.getDouble(cursor.getColumnIndex(USER_HEIGHT));
            }

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Used to get the users BMI from the local database
     * @param user The current user
     * @return the BMI of the user stored in the db
     */
    public double getUserBMI(String user) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + USER_BMI +
                " WHERE " + USER_NAME + " = ?";

        double result = 0;
        try {
            Cursor cursor = db.rawQuery(query, new String[]{user});

            // Check cursor is not empty
            if (cursor.moveToFirst() && cursor.getCount() > 0) {
                result = cursor.getDouble(cursor.getColumnIndex(USER_BMI));
            }

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * Used to get the users gender from the local database
     * @param user The current user
     * @return the gender of the user stored in the db
     */
    public int getUserGender(String user) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + USER_BMI +
                " WHERE " + USER_NAME + " = ?";

        int result = 0;
        try {
            Cursor cursor = db.rawQuery(query, new String[]{user});

            // Check cursor is not empty
            if (cursor.moveToFirst() && cursor.getCount() > 0) {
                result = cursor.getInt(cursor.getColumnIndex(USER_GENDER));
            }

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean deleteUser(String user) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + USER_RECORDS + " WHERE " + USER_NAME + " = ?";

        int result = 0;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, new String[]{user});

            // Delete all user records
            while(cursor.moveToNext()) {
                String dateString = cursor.getString(cursor.getColumnIndex(USER_RECORD_DATE));
                Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.UK).parse(dateString);
                Log.d("Deleting: ", date.toString());
                deleteRecord(user, date);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (cursor != null) {
            cursor.close();
        }
        return true;
    }


}
