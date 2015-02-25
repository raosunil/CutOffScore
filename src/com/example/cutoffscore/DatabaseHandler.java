package com.example.cutoffscore;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {

	// All Static variables
	// Database Version 
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "coordinateDB";

	// Coordinates table name
	private static final String TABLE_COORDINATES = "cordinates";

	// Coordinates Table Columns names
	private static final String KEY_ID = "id";
	private static final String KEY_XVALUE = "xval";
	private static final String KEY_YVALUE = "yval";

	/*IRCBoat
	 * private int id;

    private String boatname;
	private String loa;
    private String lwl;
    private String beam;
    private String disp;
    private String sailrArea;


    //output variables
    private String hullspeed;   
	private String d_over_l;
    private String sa_over_d;
    private String capI;
    private String comfI;

	 */

	private final ArrayList<Coordinate> coordinate_list = new ArrayList<Coordinate>();

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d("DatabaseHandler","Inside onCreate ");
		String CREATE_COORDINATES_TABLE = "CREATE TABLE " + TABLE_COORDINATES + "("
		+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_XVALUE + " NUMERIC,"
		+ KEY_YVALUE + " NUMERIC"
		+ ")";
		db.execSQL(CREATE_COORDINATES_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_COORDINATES);

		// Create tables again
		onCreate(db);
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations
	 */

	// Adding new coordinate
	public void Add_Coordinate(Coordinate xy) {
		Log.d("DatabaseHandler","Inside Add_IRCBoat");

		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_XVALUE, xy.getxValue()); 
		values.put(KEY_YVALUE, xy.getyValue());


		// Inserting Row
		db.insert(TABLE_COORDINATES, null, values);
		db.close(); // Closing database connection
	}

	// Getting single coordinate
	Coordinate Get_Coordinates(int id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_COORDINATES, new String[] { KEY_ID,
				KEY_XVALUE, 	KEY_YVALUE }, KEY_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();

		Coordinate xy = new Coordinate(Integer.parseInt(cursor.getString(0)),
				cursor.getDouble(1),cursor.getDouble(2));
		// return coordinate
		cursor.close();
		db.close();

		return xy;
	}

	// Getting All coordinates
	public ArrayList<Coordinate> Get_Coordinates() {
		try {
			coordinate_list.clear();

			// Select All Query
			String selectQuery = "SELECT  * FROM " + TABLE_COORDINATES;

			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					Coordinate xy = new Coordinate();
					xy.setId(Integer.parseInt(cursor.getString(0)));
					xy.setxValue(cursor.getDouble(1));
					xy.setyValue(cursor.getDouble(2));

					// Adding coordinate to list
					coordinate_list.add(xy);
				} while (cursor.moveToNext());
			}

			// return coordinates list
			cursor.close();
			db.close();	    
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("all_ircboats", "" + e);
		}

		return coordinate_list;
	}

	// Updating single coordinate
	public int Update_Coordinate(Coordinate xy) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_XVALUE, xy.getxValue()); 
		values.put(KEY_YVALUE, xy.getyValue()); 


		// updating row
		return db.update(TABLE_COORDINATES, values, KEY_ID + " = ?",
				new String[] { String.valueOf(xy.getId()) });
	}

	// Deleting single coordinate
	public void Delete_Coordinate(int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_COORDINATES, KEY_ID + " = ?",
				new String[] { String.valueOf(id) });
		db.close();
	}

	// Getting coordinates Count
	public int Get_Total_Coordinates() {
		int retValue = -1;   
		String countQuery = "SELECT  * FROM " + TABLE_COORDINATES;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		retValue = cursor.getCount();
		cursor.close();	
		db.close();
		// return count
		return retValue;
	}

	// Getting coordinates Count group by x value
	public double Get_Coordinates_GroupedByXValue(double xValue) {
		double retValue = -1;   
		String countQuery = "SELECT  * FROM " + TABLE_COORDINATES + " where " + KEY_XVALUE + "="+ xValue;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		retValue = cursor.getCount(); 
		cursor.close();	
		db.close();
		// return count
		Log.d("DatabaseHandler","Inside Get_Coordinates_GroupedByXValue - val of retValue:"+ retValue);
		return retValue;
	}
	
	public ArrayList<Double> Get_Unique_XValues(){

		ArrayList<Double> list = new ArrayList<Double>();

		String countQuery = "SELECT  distinct " + KEY_XVALUE+" FROM " + TABLE_COORDINATES;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		if (cursor.moveToFirst()) {
			do {

				list.add(cursor.getDouble(0));				
			} while (cursor.moveToNext());
		}

		// return list
		cursor.close();
		db.close();
		
		return list;

	}

}
