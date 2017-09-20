package org.opencv.samples.facedetect;

import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDbHelper extends SQLiteOpenHelper {
	
	private static final String DEBUGGING             	= "DBdebugging";
	private static final String dbName 					= "projectDB";
	private static final int    dbVersion 				= 12;
	private static final String repTableName 			= "repTable";
	private static final String exerTableName 			= "exerTable";
	// Table Columns
	private static final String tblValID 				= "tblID";
	private static final String tblValDate 				= "Date";
	private static final String tblValUser 				= "User";
	private static final String tblValExercise 			= "Exercise";
	private static final String tblValWeight 			= "Weight";
	private static final String tblValSetNumber			= "SetNumber";
	private static final String tblValReps 				= "Reps";
	private static final String exertblValID			= "exerID";
	private static final String exertblValList 			= "exerciseList";
	
	private static MyDbHelper sInstance;

	public MyDbHelper(Context context) {
		super(context, dbName, null, dbVersion);
	}

	public static synchronized MyDbHelper getInstance(Context context) {
        if (sInstance == null) 
            sInstance = new MyDbHelper(context.getApplicationContext());
        return sInstance;
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
		String createRepTable = "CREATE TABLE IF NOT EXISTS " + repTableName + 
				"(" +
					tblValID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
					tblValDate + " TEXT," + 
					tblValUser + " TEXT," +
					tblValExercise + " TEXT," +
					tblValWeight + " INTEGER," +
					tblValSetNumber + " INTEGER," +
					tblValReps + " INTEGER" +
				");";	
		String createExerciseTable = "CREATE TABLE IF NOT EXISTS " + exerTableName + 
				"(" +
					exertblValID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
					exertblValList + " TEXT" + 
				");";
		String enterExercises = "INSERT INTO " + exerTableName + "(" + exertblValList + ") VALUES ('Squat'),('Bicep Curls');";
		db.execSQL(createRepTable);
		db.execSQL(createExerciseTable);
		db.execSQL(enterExercises);
		Log.i(DEBUGGING, "TABLE CREATED SUCCESSFULLY");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if(oldVersion != newVersion){
			db.execSQL("DROP TABLE IF EXISTS " + repTableName + ";");
			db.execSQL("DROP TABLE IF EXISTS " + exerTableName + ";");
			onCreate(db);
			Log.i(DEBUGGING, "TABLE UPGRADED SUCCESSFULLY");
		}
	}
	
	public void dropTable() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.beginTransaction();
		try {
			db.execSQL("DROP TABLE IF EXISTS " + repTableName + ";");
			db.execSQL("DROP TABLE IF EXISTS " + exerTableName + ";");
			db.setTransactionSuccessful();
			Log.i(DEBUGGING, "TABLES DROPPED SUCCESSFULLY");
		} catch (Exception e) {
			Log.i(DEBUGGING, "ERROR DROPPING TABLE " + e.getMessage());
		} finally {
			db.endTransaction();
		}
	}
	
	public void setInfoToDB(String passedDate, String passedUser, String passedExer, int passedWeight, int passedSetNum, int passedRepNum) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues ipValues = new ContentValues();
		ipValues.put(tblValDate, passedDate);
		ipValues.put(tblValUser, passedUser);
		ipValues.put(tblValExercise, passedExer);
		ipValues.put(tblValWeight, passedWeight);
		ipValues.put(tblValSetNumber, passedSetNum);
		ipValues.put(tblValReps, passedRepNum);
		db.insert(repTableName, null, ipValues);
		db.close();
		Log.i(DEBUGGING, "JUST PUT VALUES INTO DB " + passedDate + passedUser + passedExer + passedWeight + passedSetNum + passedRepNum);
	}
	
	public ArrayList<String> returnStoredExercises() {
		ArrayList<String> returnExercises = new ArrayList<String>();
		String exerQuery = "SELECT " + exertblValList + " FROM " + exerTableName + ";";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor myCursor = db.rawQuery(exerQuery, null);
		myCursor.moveToPosition(0);
        returnExercises.add(myCursor.getString(myCursor.getColumnIndex(exertblValList)));
		myCursor.moveToPosition(1);
        returnExercises.add(myCursor.getString(myCursor.getColumnIndex(exertblValList)));
		return returnExercises;
	}
	
	public ArrayList<String> returnCurrentDayDbData(String currentUserName, String currentDate) {
		ArrayList<String> currentDayInfo = new ArrayList<String>();
		String queryAll = "SELECT User,Exercise,Weight,SetNumber,Reps FROM " + repTableName + 
				" WHERE " + tblValUser + "='" + currentUserName + "' AND " + tblValDate + "='" + currentDate + "';";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor myCursor = db.rawQuery(queryAll, null);
		if(myCursor.moveToFirst()){
			do{
				StringBuilder sb = new StringBuilder();
        		sb.append(myCursor.getString(1)	+ " | ");
        		sb.append(myCursor.getString(2) + "kg |	");
        		sb.append("Set(#" + myCursor.getString(3) + ") | ");
        		sb.append("Reps: " + myCursor.getString(4) + "");
        		currentDayInfo.add(sb.toString());
			} while (myCursor.moveToNext());
		}
		return currentDayInfo;	
	}
	
	public ArrayList<String> returnHistoricalDbData(String currentUserName) {
		ArrayList<String> historicalInfo = new ArrayList<String>();
		String queryAll = "SELECT Date,Exercise,Weight,SetNumber,Reps FROM " + repTableName + 
				" WHERE " + tblValUser + "='" + currentUserName+ "';";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor myCursor = db.rawQuery(queryAll, null);
		if(myCursor.moveToFirst()){
			do{
				StringBuilder sb = new StringBuilder();
				sb.append(myCursor.getString(0) + " - ");
        		sb.append(myCursor.getString(1)	+ " | ");
        		sb.append(myCursor.getString(2) + "kg |	");
        		sb.append("Set(#" + myCursor.getString(3) + ") | ");
        		sb.append("Reps: " + myCursor.getString(4) + "");
        		historicalInfo.add(sb.toString());
			} while (myCursor.moveToNext());
		}
		return historicalInfo;	
	}
}