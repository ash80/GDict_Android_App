package com.vocab.gdict;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Scanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

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

    void insertWord(String mWord, String mPOS, String mDef, String mSentence, String mSynonym, String mAntonym)
    {
        ContentValues values = new ContentValues();
        values.put(DatabaseOpenHelper.WORD, mWord);
        values.put(DatabaseOpenHelper.PART_OF_SPEECH, mPOS);
        values.put(DatabaseOpenHelper.MEANING, mDef);
        values.put(DatabaseOpenHelper.SENTENCE, mSentence);
        values.put(DatabaseOpenHelper.SYNONYM, mSynonym);
        values.put(DatabaseOpenHelper.ANTONYM, mAntonym);

        mDB.insert(DatabaseOpenHelper.TABLE_NAME+activeTable, null, values);
        values.clear();
        mDB.close();
    }

    
    public void insertWordsFromAssetFile(InputStream ipStr, int choice) throws FileNotFoundException {

        Scanner scanner =  new Scanner(ipStr);
        ContentValues values = new ContentValues();
        String thisLine, nextLine, mSynonym, mAntonym;
        while (scanner.hasNextLine()){
            thisLine = scanner.nextLine();
            int ij =0;
            if(thisLine.equals("")) {
                ij = 1+ij;
            }
            else {
                String [] wordMeaning = processLine(thisLine).split(" ",2);
                values.put(DatabaseOpenHelper.WORD, wordMeaning[0]);
                values.put(DatabaseOpenHelper.PART_OF_SPEECH, wordMeaning[1]);
                values.put(DatabaseOpenHelper.MEANING, wordMeaning[2]);
                values.put(DatabaseOpenHelper.SENTENCE, wordMeaning[3]);
                values.put(DatabaseOpenHelper.SYNONYM, wordMeaning[4]);
                values.put(DatabaseOpenHelper.ANTONYM, wordMeaning[5]);
                mDB.insert(DatabaseOpenHelper.TABLE_NAME+choice, null, values);
                values.clear();
            }
        }
        scanner.close();
        mDB.close();
    }

    public void insertWordsFromFile(String path, int choice) throws FileNotFoundException {

        File fFile1 = new File(path);
        Scanner scanner =  new Scanner(fFile1);
        ContentValues values = new ContentValues();
        String thisLine, nextLine,eWord;
        while (scanner.hasNextLine()){
            thisLine = scanner.nextLine();
            int ij =0;
            if(thisLine.equals("")) {
                ij = 1+ij;
            }
            else {
                String [] wordMeaning = processLine(thisLine).split(" ",2);
                values.put(DatabaseOpenHelper.WORD, wordMeaning[0]);
                values.put(DatabaseOpenHelper.PART_OF_SPEECH, wordMeaning[1]);
                values.put(DatabaseOpenHelper.MEANING, wordMeaning[2]);
                values.put(DatabaseOpenHelper.SENTENCE, wordMeaning[3]);
                values.put(DatabaseOpenHelper.SYNONYM, wordMeaning[4]);
                values.put(DatabaseOpenHelper.ANTONYM, wordMeaning[5]);
                mDB.insert(DatabaseOpenHelper.TABLE_NAME+choice, null, values);
                values.clear();
            }
        }
        scanner.close();
        mDB.close();
    }

    private String processLine(String aLine) {
        //use a second Scanner to parse the content of each line
        Scanner scanner = new Scanner(aLine);
        scanner.useDelimiter("\\.");
        if (scanner.hasNext()){
            //assumes the line has a certain structure
            scanner.next();
            String tmp = scanner.next();
            scanner.close();
            return tmp.trim();
        }
        else {
            scanner.close();
            return("Empty or invalid line. Unable to process.");
        }
    }

    void clearAll(int choice) {

        mDB.delete(DatabaseOpenHelper.TABLE_NAME+choice, null, null);

    }

    void deleteOneRow(int table_number, int id) {

        mDB.delete(DatabaseOpenHelper.TABLE_NAME+table_number, "WHERE _id = ?",
                        new String [] {Integer.toString(id)});

    }

    Cursor getCursor(long primeKey, int activeTable) {
        Cursor c = mDB.query(DatabaseOpenHelper.TABLE_NAME+Integer.toString(activeTable),
                DatabaseOpenHelper.columns, null, new String[] {}, null, null,
                null);
        c.moveToFirst();
        while (c.isAfterLast() == false) {
            if (c.getLong(0) == primeKey) {
                break;
            }
            c.moveToNext();
        }
        return c;

		/*return mDB.query(true, DatabaseOpenHelper.TABLE_NAME+Integer.toString(activeTable),
				DatabaseOpenHelper.columns, DatabaseOpenHelper._ID + "=" + primeKey, null,
	              null, null, null, null);*/
    }

    private Cursor readLocalWords(String value, int activeTable) {

        Cursor c = mDB.query(DatabaseOpenHelper.TABLE_NAME+activeTable,
                DatabaseOpenHelper.columns, null, new String[] {}, null, null,
                null);
        c.moveToFirst();
        while (c.isAfterLast() == false) {
            if (c.getString(1).equalsIgnoreCase(value)) {
                break;
            }
            c.moveToNext();
        }
        return c;
    }

    private Cursor readLocalWordsWithPOS(String value, String Pos, int activeTable) {

        Cursor c = mDB.query(DatabaseOpenHelper.TABLE_NAME+activeTable,
                DatabaseOpenHelper.columns, null, new String[] {}, null, null,
                null);
        c.moveToFirst();
        while (c.isAfterLast() == false) {
            if (c.getString(1).equalsIgnoreCase(value) && c.getString(2).equalsIgnoreCase(Pos)) {
                break;
            }
            c.moveToNext();
        }
        return c;
    }

    public static String extractTheWord(String sentence, String word) {
        int a;//b;
        String word1;
        String [] word2;
        if(sentence.equalsIgnoreCase("") || word.equalsIgnoreCase("") || sentence == null || word == null)
        {
            return "";
        }
        else {
            if(sentence.toLowerCase(Locale.getDefault()).contains(word.toLowerCase())) {
                a = sentence.toLowerCase(Locale.getDefault()).indexOf(word.toLowerCase());
                //System.out.println("i1: "+a);
                word2 = sentence.substring(a).trim().split(" ",2);
                //System.out.println("RW: "+word2[0].trim());
                if(word2[0].charAt(word2[0].length()-1)=='.' || word2[0].charAt(word2[0].length()-1)==',' || word2[0].charAt(word2[0].length()-1)==':'
                        || word2[0].charAt(word2[0].length()-1)==';' || word2[0].charAt(word2[0].length()-1)=='!' || word2[0].charAt(word2[0].length()-1)==')'
                        || word2[0].charAt(word2[0].length()-1)=='"' || word2[0].charAt(word2[0].length()-1)=='\'' || word2[0].charAt(word2[0].length()-1)=='?')
                    word2[0] = word2[0].substring(0,word2[0].length()-1);
                //b = a + word2[0].trim().length();
                //System.out.println("RW: "+word2[0].trim()+" i1: "+a+" i2: "+b);
                return word2[0].trim();
            }
            else {
                word1 = word.substring(0, word.length()-1);
                //System.out.println(word1);
                if(sentence.toLowerCase(Locale.getDefault()).contains(word1.toLowerCase())) {
                    a = sentence.toLowerCase(Locale.getDefault()).indexOf(word1.toLowerCase());
                    //System.out.println("i1: "+a);
                    word2 = sentence.substring(a).trim().split(" ",2);
                    //System.out.println("RW: "+word2[0].trim());
					/*System.out.println("substring: "+word2[0].substring(word2[0].length()-1)
							+"\t'.': "+word2[0].charAt(word2[0].length()-1)=='.'
							+"\t',': "+word2[0].substring(word2[0].length()-1).matches("."));*/
                    if(word2[0].charAt(word2[0].length()-1)=='.' || word2[0].charAt(word2[0].length()-1)==',' || word2[0].charAt(word2[0].length()-1)==':'
                            || word2[0].charAt(word2[0].length()-1)==';' || word2[0].charAt(word2[0].length()-1)=='!' || word2[0].charAt(word2[0].length()-1)==')'
                            || word2[0].charAt(word2[0].length()-1)=='"' || word2[0].charAt(word2[0].length()-1)=='\'' || word2[0].charAt(word2[0].length()-1)=='?')
                        word2[0] = word2[0].substring(0,word2[0].length()-1);
                    //b = a + word2[0].trim().length();
                    //System.out.println("RW: "+word2[0].trim()+" i1: "+a+" i2: "+b);
                    return word2[0].trim();
					/*b = a;
					while(sentence.charAt(b)!='\b' || sentence.charAt(b)!='.' || sentence.charAt(b)!=',')
					{ b++; System.out.print("  "+b); }
					System.out.println(sentence.substring(a, b)+" i1: "+a+" i2: "+b);
					return sentence.substring(a, b);*/
                }
                else {
                    word1 = word.substring(0, word.length()-2);
                    //System.out.println(word1);
                    if(sentence.toLowerCase(Locale.getDefault()).contains(word1.toLowerCase())) {
                        a = sentence.toLowerCase(Locale.getDefault()).indexOf(word1.toLowerCase());
                        //System.out.println("i1: "+a);
                        word2 = sentence.substring(a).trim().split(" ",2);
                        //System.out.println("RW: "+word2[0].trim());
                        if(word2[0].charAt(word2[0].length()-1)=='.' || word2[0].charAt(word2[0].length()-1)==',' || word2[0].charAt(word2[0].length()-1)==':'
                                || word2[0].charAt(word2[0].length()-1)==';' || word2[0].charAt(word2[0].length()-1)=='!' || word2[0].charAt(word2[0].length()-1)==')'
                                || word2[0].charAt(word2[0].length()-1)=='"' || word2[0].charAt(word2[0].length()-1)=='\'' || word2[0].charAt(word2[0].length()-1)=='?')
                            word2[0] = word2[0].substring(0,word2[0].length()-1);
                        //b = a + word2[0].trim().length();
                        //System.out.println("RW: "+word2[0].trim()+" i1: "+a+" i2: "+b);
                        return word2[0].trim();
						/*b = a;
						while(sentence.charAt(b)!=' ' || sentence.charAt(b)!='.' || sentence.charAt(b)!=',')
						{ b++; System.out.print("  "+b); }
						System.out.println(sentence.substring(a, b)+" i1: "+a+" i2: "+b);
						return sentence.substring(a, b);*/
                    }
                    else if(word.charAt(1)=='a'){
                        word1 = word.replaceFirst("a", "u");
                        //System.out.println(word1);
                        if(sentence.toLowerCase(Locale.getDefault()).contains(word1.toLowerCase())) {
                            a = sentence.toLowerCase(Locale.getDefault()).indexOf(word1.toLowerCase());
                            //System.out.println("i1: "+a);
                            word2 = sentence.substring(a).trim().split(" ",2);
                            //System.out.println("RW: "+word2[0].trim());
                            if(word2[0].charAt(word2[0].length()-1)=='.' || word2[0].charAt(word2[0].length()-1)==',' || word2[0].charAt(word2[0].length()-1)==':'
                                    || word2[0].charAt(word2[0].length()-1)==';' || word2[0].charAt(word2[0].length()-1)=='!' || word2[0].charAt(word2[0].length()-1)==')'
                                    || word2[0].charAt(word2[0].length()-1)=='"' || word2[0].charAt(word2[0].length()-1)=='\'' || word2[0].charAt(word2[0].length()-1)=='?')
                                word2[0] = word2[0].substring(0,word2[0].length()-1);
                            //b = a + word2[0].trim().length();
                            //System.out.println("RW: "+word2[0].trim()+" i1: "+a+" i2: "+b);
                            return word2[0].trim();
                        }
                        else
                            return "";

                    }
                    else if(word.charAt(1)=='i' || word.charAt(2)=='i') {
                        word1 = word.replaceFirst("i", "u");
                        //System.out.println(word1);
                        if(sentence.toLowerCase(Locale.getDefault()).contains(word1.toLowerCase())) {
                            a = sentence.toLowerCase(Locale.getDefault()).indexOf(word1.toLowerCase());
                            //System.out.println("i1: "+a);
                            word2 = sentence.substring(a).trim().split(" ",2);
                            //System.out.println("RW: "+word2[0].trim());
                            if(word2[0].charAt(word2[0].length()-1)=='.' || word2[0].charAt(word2[0].length()-1)==',' || word2[0].charAt(word2[0].length()-1)==':'
                                    || word2[0].charAt(word2[0].length()-1)==';' || word2[0].charAt(word2[0].length()-1)=='!' || word2[0].charAt(word2[0].length()-1)==')'
                                    || word2[0].charAt(word2[0].length()-1)=='"' || word2[0].charAt(word2[0].length()-1)=='\'' || word2[0].charAt(word2[0].length()-1)=='?')
                                word2[0] = word2[0].substring(0,word2[0].length()-1);
                            //b = a + word2[0].trim().length();
                            //System.out.println("RW: "+word2[0].trim()+" i1: "+a+" i2: "+b);
                            return word2[0].trim();
                        }
                        word1 = word.replaceFirst("i", "a");
                        if(sentence.toLowerCase(Locale.getDefault()).contains(word1.toLowerCase())) {
                            a = sentence.toLowerCase(Locale.getDefault()).indexOf(word1.toLowerCase());
                            //System.out.println("i1: "+a);
                            word2 = sentence.substring(a).trim().split(" ",2);
                            //System.out.println("RW: "+word2[0].trim());
                            if(word2[0].charAt(word2[0].length()-1)=='.' || word2[0].charAt(word2[0].length()-1)==',' || word2[0].charAt(word2[0].length()-1)==':'
                                    || word2[0].charAt(word2[0].length()-1)==';' || word2[0].charAt(word2[0].length()-1)=='!' || word2[0].charAt(word2[0].length()-1)==')'
                                    || word2[0].charAt(word2[0].length()-1)=='"' || word2[0].charAt(word2[0].length()-1)=='\'' || word2[0].charAt(word2[0].length()-1)=='?')
                                word2[0] = word2[0].substring(0,word2[0].length()-1);
                            //b = a + word2[0].trim().length();
                            //System.out.println("RW: "+word2[0].trim()+" i1: "+a+" i2: "+b);
                            return word2[0].trim();
                        }
                        else
                            return "";
                    }
                    else
                        return "";
                }
            }

        }
    }

    public static String getStringStatus(int n) {
        switch (n) {
            case 0:
                return "New(0)";
            case 1:
                return "Beginner(1)";
            case 2:
                return "Rookie(2)";
            case 3:
                return "Acquainted(3)";
            case 4:
                return "Mediocre(4)";
            case 5:
                return "Graduate(5)";
            case 6:
                return "Seasoned(6)";
            case 7:
                return "Adept(7)";
            case 8:
                return "Expert(8)";
            case 9:
                return "Master(9)";
            default:
                return "Disabled";
        }
    }

    public static int getIntStatus(String str) {
        if(str.contains("0"))
            return 0;
        else if(str.contains("1"))
            return 1;
        else if(str.contains("2"))
            return 2;
        else if(str.contains("3"))
            return 3;
        else if(str.contains("4"))
            return 4;
        else if(str.contains("5"))
            return 5;
        else if(str.contains("6"))
            return 6;
        else if(str.contains("7"))
            return 7;
        else if(str.contains("8"))
            return 8;
        else if(str.contains("9"))
            return 9;
        else
            return -1;
    }

}
