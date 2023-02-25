package com.example.translatortext.translator_history;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DataHelper  extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "TranslationHistory.db"; // название бд
    private static int SCHEMA = 1; // версия базы данных
    static final String TABLE = "history"; // название таблицы в бд

    // названия столбцов
    public static final String COLUMN_ID = "id";
    public static String COLUMN_INPUT_VALUE = "input_value";
    public static String COLUMN_MODE = "mode";
    public static final String COLUMN_OUTPUT_VALUE = "output_value";

    public DataHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE "+TABLE+" (" + COLUMN_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_INPUT_VALUE
                + " TEXT," + COLUMN_MODE + " TEXT," + COLUMN_OUTPUT_VALUE + " TEXT) ;");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addNote(SQLiteDatabase db, String inputValue, String mode, String outputValue){
        inputValue = inputValue.replace("\n","");
        mode = mode.replace("\n","");
        outputValue = outputValue.replace("\n","");
        db.execSQL("INSERT INTO " + TABLE + " (" + COLUMN_INPUT_VALUE
                + ", " + COLUMN_MODE + ", " + COLUMN_OUTPUT_VALUE + ") VALUES ('"+inputValue+"','"+mode+"','"+outputValue+"');");
    }

    public List<History> getAllNotes(SQLiteDatabase db){
        List<History> data = new ArrayList<History>();

        Cursor dataCursor = db.rawQuery("select * from "+ DataHelper.TABLE, null);
        dataCursor.moveToFirst();

        for(int i=0; i<dataCursor.getCount() ; i++){
            History currentHistory = new History();

            currentHistory.setId(dataCursor.getInt(0));
            currentHistory.setInputValue(dataCursor.getString(1));
            currentHistory.setMode(dataCursor.getString(2));
            currentHistory.setOutputValue(dataCursor.getString(3));
            data.add(currentHistory);

            dataCursor.moveToNext();
        }

        return data;
    }

    public void removeNote(SQLiteDatabase db, History history){
        db.execSQL("delete from "+ DataHelper.TABLE +" where id="+history.getId()+";");
    }

    public void removeAllNotes(SQLiteDatabase db){
        db.execSQL("delete from "+ DataHelper.TABLE +";");
    }
}