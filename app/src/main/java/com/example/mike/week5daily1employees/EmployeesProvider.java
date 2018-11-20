package com.example.mike.week5daily1employees;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;

public class EmployeesProvider extends ContentProvider {
    static final String PROVIDER_NAME = "com.example.mike.week5daily1employees.EmployeesProvider";
    static final String URL = "content://" + PROVIDER_NAME + "/employees";
    static final Uri CONTENT_URI = Uri.parse(URL);

    public static final String _ID = "_id";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String SALARY = "salary";
    public static final String DAYS_ON_JOB = "days_employed";

    private static HashMap<String, String> EMPLOYEES_PROJECTION_MAP;

    static final int EMPLOYEES = 1;
    static final int EMPLOYEE_ID = 2;

    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "employees", EMPLOYEES);
        uriMatcher.addURI(PROVIDER_NAME, "employees/#", EMPLOYEE_ID);
    }

    private SQLiteDatabase db;
    static final String DATABASE_NAME = "Workplace";
    static final String EMPLOYEES_TABLE_NAME = "employees";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_DB_TABLE =
            " CREATE TABLE " + EMPLOYEES_TABLE_NAME +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    FIRST_NAME+" TEXT NOT NULL, " +
                    LAST_NAME+" TEXT NOT NULL, " +
                    SALARY+ " TEXT NOT NULL, " +
                    DAYS_ON_JOB+"  TEXT NOT NULL);";

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + EMPLOYEES_TABLE_NAME);
            onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        db = dbHelper.getWritableDatabase();
        return db != null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowID = db.insert(EMPLOYEES_TABLE_NAME, "", values);

        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }

        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public Cursor query(Uri uri, String[] projection,
                        String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(EMPLOYEES_TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case EMPLOYEES:
                qb.setProjectionMap(EMPLOYEES_PROJECTION_MAP);
                break;

            case EMPLOYEE_ID:
                qb.appendWhere( _ID + "=" + uri.getPathSegments().get(1));
                break;

            default:
        }

        if (sortOrder == null || sortOrder == ""){
            sortOrder = LAST_NAME;
        }

        Cursor c = qb.query(db,	projection,	selection,
                selectionArgs,null, null, sortOrder);

        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)){
            case EMPLOYEES:
                count = db.delete(EMPLOYEES_TABLE_NAME, selection, selectionArgs);
                break;

            case EMPLOYEE_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete(EMPLOYEES_TABLE_NAME, _ID +  " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values,
                      String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case EMPLOYEES:
                count = db.update(EMPLOYEES_TABLE_NAME, values, selection, selectionArgs);
                break;

            case EMPLOYEE_ID:
                count = db.update(EMPLOYEES_TABLE_NAME, values,
                        _ID + " = " + uri.getPathSegments().get(1) +
                                (!TextUtils.isEmpty(selection) ? " AND (" +selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            case EMPLOYEES:
                return "vnd.android.cursor.dir/vnd.example.employees";

            case EMPLOYEE_ID:
                return "vnd.android.cursor.item/vnd.example.employees";

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }
}


