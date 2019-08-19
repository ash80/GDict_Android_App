package com.vocab.gdict;

//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

public class DatabaseOpenHelper extends SQLiteOpenHelper {
	
	final static String TABLE_NAME = "vocab";
	final static String _ID = "_id";
	final static String WORD = "word";
	final static String MEANING = "meaning";
	final static String SENTENCE = "sentence";
	final static String SYNONYM = "synonym";
	final static String ANTONYM = "antonym";
	final static String PART_OF_SPEECH = "part_of_speech";
	final static String[] columns = { _ID, WORD, PART_OF_SPEECH, MEANING, SENTENCE, SYNONYM, ANTONYM };
	private int numberOfTables;

    private String[] CREATE_CMD;

	final private static String NAME = "saved_words";
	private String DB_PATH;
	final private static Integer VERSION = 1;
//	final private Context mContext;

	DatabaseOpenHelper(Context context) {
		super(context, NAME, null, VERSION);
//		this.mContext = context;
		this.numberOfTables = 4;
        this.CREATE_CMD = new String[this.numberOfTables];
		for(int i = 0; i<this.numberOfTables; i++) {
			CREATE_CMD[i] =
            "CREATE TABLE " + TABLE_NAME + i + " (" + _ID
                + "	INTEGER PRIMARY KEY AUTOINCREMENT, "
                + WORD + " 	TEXT NOT NULL, "
                + PART_OF_SPEECH + "  TEXT, "
                + MEANING + " TEXT NOT NULL, "
                + SENTENCE + " TEXT, "
                + SYNONYM + " TEXT, "
                + ANTONYM + " TEXT, "
                + "status TEXT);";
		}
		this.DB_PATH = Environment.getDataDirectory()+"/data/"+context.getPackageName()
                +"/databases/" + NAME;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		for(int i = 0; i<this.numberOfTables; i++) {
			db.execSQL(CREATE_CMD[i]);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// N/A
	}

	boolean createDataBase() {
        this.getReadableDatabase();
        SQLiteDatabase getDB = SQLiteDatabase.openOrCreateDatabase(this.DB_PATH, null);
        return getDB != null;
    }

}
