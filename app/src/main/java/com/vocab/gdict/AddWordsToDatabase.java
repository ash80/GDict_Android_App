package com.vocab.gdict;

//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.InputStream;
//import java.util.Locale;
//import java.util.Scanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
//import android.widget.Toast;

public class AddWordsToDatabase {
    private SQLiteDatabase mDB = null;
    private DatabaseOpenHelper mDbHelper;
    Context context;
    int activeTable;
    AddWordsToDatabase(Context context, int activeTable) {
        mDbHelper = new DatabaseOpenHelper(context);
        mDB = mDbHelper.getWritableDatabase();
        this.context = context;
        this.activeTable = activeTable;
    }

//    public void deleteDuplicates(){
//        mDbHelper.getWritableDatabase().execSQL("delete from post where _id not in (SELECT MIN(_id ) FROM post GROUP BY post_id)");
//    }

    void insertWord(String mWord, String mPOS, String mDef, String mSentence, String mSynonym,
                    String mAntonym, String mPronunciation)
    {
        ContentValues values = new ContentValues();
        values.put(DatabaseOpenHelper.WORD, mWord);
        values.put(DatabaseOpenHelper.PART_OF_SPEECH, mPOS);
        values.put(DatabaseOpenHelper.MEANING, mDef);
        values.put(DatabaseOpenHelper.SENTENCE, mSentence);
        values.put(DatabaseOpenHelper.SYNONYM, mSynonym);
        values.put(DatabaseOpenHelper.ANTONYM, mAntonym);
        values.put(DatabaseOpenHelper.STATUS, mPronunciation);

//        mDB.insert(DatabaseOpenHelper.TABLE_NAME+activeTable, null, values);
        Cursor cur = readLocalWordsWithWordAndMean(mWord, mDef, activeTable);
        if(cur.isAfterLast()) {
            mDB.insert(DatabaseOpenHelper.TABLE_NAME + activeTable, null, values);
        }
//        deleteDuplicates();
        values.clear();
        mDB.close();
    }

    private Cursor readLocalWordsWithWordAndMean(String constraint_word, String constraint_mean, int activeTable) {

        return mDB.query(DatabaseOpenHelper.TABLE_NAME+activeTable,
                null, "word = ? AND meaning = ?",
                new String[] {constraint_word, constraint_mean}, null, null, null);
    }

}
