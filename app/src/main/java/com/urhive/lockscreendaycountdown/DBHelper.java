package com.urhive.lockscreendaycountdown;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Chirag Bhatia on 05-09-2016.
 */
public class DBHelper extends SQLiteOpenHelper {
    // Table Names of Data Base.
    static final String TABLE_NAME = "QUOTES";
    // Data Base Name.
    private static final String DATABASE_NAME = "QuotesDatabase.db";
    // Data Base Version.
    private static final int DATABASE_VERSION = 1;
    public static Context context;
    static SQLiteDatabase sqliteDataBase;
    // The Android's default system path of your application database.
    private static String DB_PATH;

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     *
     * @param context Parameters of super() are    1. Context
     *                2. Data Base Name.
     *                3. Cursor Factory.
     *                4. Data Base Version.
     */
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        DBHelper.context = context;
        DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
    }

    // static methods
    public static String[] getRandomQuote(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        dbHelper.openDataBase();
        return dbHelper.getRQ();
    }

    public static String[] getRandomQuote(Context context, int length) {
        DBHelper dbHelper = new DBHelper(context);
        dbHelper.openDataBase();
        return dbHelper.getRQ(length);
    }

    public static String[] getQuoteById(Context context, long id) {
        DBHelper dbHelper = new DBHelper(context);
        dbHelper.openDataBase();
        return dbHelper.getQById(id);
    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * By calling this method and empty database will be created into the default system path
     * of your application so we are gonna be able to overwrite that database with our database.
     */
    public void createDataBase() throws IOException {
        //check if the database exists
        boolean databaseExist = checkDataBase();

        if (databaseExist) {
            // Do Nothing.
        } else {
            this.getWritableDatabase();
            copyDataBase();
        }// end if else dbExist
    } // end createDataBase().

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     *
     * @return true if it exists, false if it doesn't
     */
    public boolean checkDataBase() {
        File databaseFile = new File(DB_PATH + DATABASE_NAME);
        return databaseFile.exists();
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transferring byte stream.
     */
    private void copyDataBase() throws IOException {
        //Open your local db as the input stream
        InputStream myInput = context.getAssets().open(DATABASE_NAME);
        // Path to the just created empty db
        String outFileName = DB_PATH + DATABASE_NAME;
        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);
        //transfer bytes from the input file to the output file
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    /**
     * This method opens the data base connection.
     * First it create the path up till data base of the device.
     * Then create connection with data base.
     */
    public void openDataBase() throws SQLException {
        //Open the database
        String myPath = DB_PATH + DATABASE_NAME;
        sqliteDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    /**
     * This Method is used to close the data base connection.
     */
    @Override
    public synchronized void close() {
        if (sqliteDataBase != null)
            sqliteDataBase.close();
        super.close();
    }

    /**
     * Apply your methods and class to fetch data using raw or queries on data base using
     * following demo example code as:
     */
    public String[] getRQ() {
        String query = "select * from " + TABLE_NAME + " where length(quote) < " + context.getSharedPreferences("com.urhive.lockscreendaycountdown", Context.MODE_PRIVATE).getInt("maxQuoteLength", 50) + " order by random() limit 1";
        Cursor cursor = sqliteDataBase.rawQuery(query, null);
        String quote[] = new String[5];
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    quote[0] = cursor.getString(0);
                    quote[1] = cursor.getString(1);
                    quote[2] = cursor.getString(2);
                    quote[3] = cursor.getString(3);
                    quote[4] = cursor.getString(4);
                } while (cursor.moveToNext());
            }
        }
        return quote;
    }

    public String[] getRQ(int length) {
        String query = "select * from " + TABLE_NAME + " where length(quote) < " + length + " order by random() limit 1";
        Cursor cursor = sqliteDataBase.rawQuery(query, null);
        String quote[] = new String[5];
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    quote[0] = cursor.getString(0);
                    quote[1] = cursor.getString(1);
                    quote[2] = cursor.getString(2);
                    quote[3] = cursor.getString(3);
                    quote[4] = cursor.getString(4);
                } while (cursor.moveToNext());
            }
        }
        return quote;
    }

    public String[] getQById(long id) {
        String query = "select * from " + TABLE_NAME + " where id = " + id;
        Cursor cursor = sqliteDataBase.rawQuery(query, null);
        String quote[] = new String[5];
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    quote[0] = cursor.getString(0);
                    quote[1] = cursor.getString(1);
                    quote[2] = cursor.getString(2);
                    quote[3] = cursor.getString(3);
                    quote[4] = cursor.getString(4);
                } while (cursor.moveToNext());
            }
        }
        return quote;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // No need to write the create table query.
        // As we are using Pre built data base.
        // Which is ReadOnly.
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // No need to write the update table query.
        // As we are using Pre built data base.
        // Which is ReadOnly.
        // We should not update it as requirements of application.
    }
}
