package com.vocab.gdict;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
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
	// private static int numberOfTables;

	// final private static String[] CREATE_CMD = new String[10];
	/* =

	""_id"	INTEGER PRIMARY KEY AUTOINCREMENT,
	"word"	TEXT NOT NULL,
	"part_of_speech"	TEXT,
	"meaning"	TEXT NOT NULL,
	"sentence"	TEXT,
	"synonym"	TEXT,
	"antonym"	TEXT,
	"status"	TEXT */

	final private static String NAME = "saved_words";
	private String DB_PATH;
	final private static Integer VERSION = 1;
	final private Context mContext;

	public DatabaseOpenHelper(Context context) {
		super(context, NAME, null, VERSION);
		this.mContext = context;
		/*
		numberOfTables = j;
		for(int i = 0; i<numberOfTables; i++) {
			CREATE_CMD[i] =
			"CREATE TABLE " + TABLE_NAME + Integer.toString(i) + " (" + _ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ WORD + " TEXT NOT NULL, "
					+ MEANING + " TEXT NOT NULL, "
					+ SENTENCE + " TEXT NOT NULL, "
					+ EWORD + " TEXT NOT NULL, "
					+ STATUS + " TEXT NOT NULL, "
					+ PART_OF_SPEECH + " TEXT NOT NULL);";
		}
		*/
		this.DB_PATH = Environment.getDataDirectory()+"/data/"+mContext.getPackageName()+"/databases/";
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		//for(int i = 0; i<numberOfTables; i++) {
		//	db.execSQL(CREATE_CMD[i]);
		//}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// N/A
	}

	public void createDataBase() throws IOException{
		 
    	boolean dbExist = checkDataBase();
 
    	if(dbExist){
    		//do nothing - database already exist
    	}else{
 
    		//By calling this method and empty database will be created into the default system path
               //of your application so we are gonna be able to overwrite that database with our database.
        	this.getReadableDatabase();
 
        	try {
 
    			copyDataBase();
 
    		} catch (IOException e) {
 
        		throw new Error("Error copying database");
 
        	}
    	}
 
    }
	
	private void copyDataBase() throws IOException{
		 
    	//Open your local db as the input stream
    	InputStream myInput = mContext.getAssets().open("saved_words.db");
 
    	// Path to the just created empty db
    	String outFileName = DB_PATH + NAME;
 
    	//Open the empty db as the output stream
    	OutputStream myOutput = new FileOutputStream(outFileName);
 
    	//transfer bytes from the inputfile to the outputfile
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = myInput.read(buffer))>0){
    		myOutput.write(buffer, 0, length);
    	}
 
    	//Close the streams
    	myOutput.flush();
    	myOutput.close();
    	myInput.close();
    	boolean dbExist = checkDataBase();
    	if(dbExist)
    	{
    		
    	}
 
    }
	
	private boolean checkDataBase(){
		 
    	SQLiteDatabase checkDB = null;
 
    	try{
    		String myPath = DB_PATH + NAME;
    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
 
    	} catch(SQLiteException e) {
 
    		//database does't exist yet.
 
    	}
 
    	if(checkDB != null){
 
    		checkDB.close();
 
    	}
 
    	return checkDB != null ? true : false;
    }
	
}
