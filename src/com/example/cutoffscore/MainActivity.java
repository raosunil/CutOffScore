package com.example.cutoffscore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.TreeSet;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private static final String INPUT_DATA_FILENAME = "inputdata.txt";
	DatabaseHandler dbHandler = null;
	
	TreeSet<Double> setValues = null;
	Integer binSize = null;
	DescriptiveStatistics ds = null;
	
	Double cutoffPercentage = null;
	
   
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);   
        dbHandler = new DatabaseHandler(this);	
		
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    /**For handling click event of Quit button
     * @param v
     */
    public void moveBackground(View v){   	
    	System.runFinalization();
    	System.exit(0);
    	
    }
    
    /**Handler for play button
     * @param v
     */
    public void playHandler(View v){
    	if (getBinSize()==null)
    		Toast.makeText(this, "Data not loaded; please click Reload", Toast.LENGTH_SHORT).show();
    	else if (getCutoffPercentage()==null)
    		Toast.makeText(this, "Please enter percentile value between 0 and 1 and click Reload", Toast.LENGTH_SHORT).show();
    	else{
    		if (getBinSize()!=null && getCutoffPercentage()!=null){
    			HistogramGraph hist = new HistogramGraph();
    			Intent intent = hist.getIntent(this);
    			startActivity(intent);  

    		}
    	}
    }
    
    /**Handler for reloading data
     * @param v
     */
    public void reloadHandler(View v){

    	EditText percentage = (EditText)findViewById(R.id.percentileText);
    	try {
    		setCutoffPercentage(Double.valueOf(percentage.getText().toString()));
    		if (cutoffPercentage!=null && (cutoffPercentage< 0 || cutoffPercentage > 1.0))
    			setCutoffPercentage(null);
    	} catch (NumberFormatException e1) {
    		// TODO Auto-generated catch block
    		Log.d("MainActivity","Inside reloadHandler: Exception"+e1);
    	}

    	//Flush all the existing values in the DB
    	if (getBinSize() == null){
    		SQLiteDatabase db = dbHandler.getWritableDatabase();
    		dbHandler.onUpgrade(db, 1, 2);
    		db.close();


    		//Read the input file
    		Resources resources = this.getResources();
    		AssetManager assetManager = resources.getAssets();        
    		setValues = new TreeSet<Double>();
    		ds = new DescriptiveStatistics();
    		try {
    			InputStream is = assetManager.open(INPUT_DATA_FILENAME);
    			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    			String tmpData = null;
    			while((tmpData = reader.readLine())!=null){
    				Log.d("MainActivity","Found data : "+tmpData);
    				double val = Double.valueOf(tmpData);
    				setValues.add(val);
    				ds.addValue(val);
    			}
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			Log.d("MainActivity", "Error opening inputdata.txt" , e);
    		}

    		//bin size
    		//Struges formula
    		//binSize = (int) (((int)Math.log(setValues.size())/Math.log(2)) + 1);

    		//Freedman-Diaconis rule
    		double iqr = (ds.getPercentile(75) - ds.getPercentile(25));
    		Log.d("MainActivity", "Value of iqr is " + iqr );
    		double h  = (2 * iqr) / Math.pow(setValues.size(), 3);
    		binSize = (int) h ;
    		if (binSize == 0)
    			binSize = 1;
    		Log.d("MainActivity", "Value of binsize is " + binSize );
    		for (double yVal: setValues){
    			int num = ((int)(yVal/binSize)) * binSize;
    			if (yVal < 0){				
    				num = num - binSize;				
    			}
    			//to make the xValue point to middle of the bin - as the above step points to bin edges
    			num = num + ((int)binSize/2);
    			Coordinate xy = new Coordinate();
    			xy.setxValue(num);
    			Log.d("MainActivity","Value set is : "+xy.getxValue());
    			xy.setyValue(yVal);
    			dbHandler.Add_Coordinate(xy);

    		}
    	}
    	Toast.makeText(this, "Loaded Input Data successfully", Toast.LENGTH_SHORT).show();


    }
    
    
    public DatabaseHandler getDbHandler() {
		return dbHandler;
	}
    
    public Integer getBinSize() {
		return binSize;
	}
    
    @Override
    public void onDestroy() {
		if(dbHandler!=null){
			dbHandler.close();
		}
		super.onDestroy();
	}
    
    public DescriptiveStatistics getDs() {
		return ds;
	}


	public Double getCutoffPercentage() {
		return cutoffPercentage;
	}


	public void setCutoffPercentage(Double cutoffPercentage) {
		this.cutoffPercentage = cutoffPercentage;
	}



    
    /* Checks if external storage is available for read and write 
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

     Checks if external storage is available to at least read 
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
            Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
    
    void readExternalStoragePrivateFile() {
        // Create a path where we will place our private file on external
        // storage.
        File file = new File(getExternalFilesDir(null), "Cutoffscore_input.txt");
        InputStream is = null;
        
       try {
            // Very simple code to copy a picture from the application's
            // resource into the external file.  Note that this code does
            // no error checking, and assumes the picture is small (does not
            // try to copy it in chunks).  Note that if external storage is
            // not currently mounted this will silently fail.
            is = new FileInputStream(file);            
            byte[] data = new byte[is.available()];
            is.read(data);
            //TODO
            Log.d("MainActivity" , "Inside readExternalStoragePrivateFile - data val is " + data);
    	   file.createNewFile();
        } catch (IOException e) {
            // Unable to create file, likely because external storage is
            // not currently mounted.
            Log.d("ExternalStorage", "Error writing " + file, e);
        }finally{
        	if(is!=null){
        		try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.d("ExternalStorage", "Error closing " + file, e);
				}
        	}
        }
    }

    void deleteExternalStoragePrivateFile() {
        // Get path for the file on external storage.  If external
        // storage is not currently mounted this will fail.
        File file = new File(getExternalFilesDir(null), "Cutoffscore_input.txt");
        if (file != null) {
            file.delete();
        }
    }

    boolean hasExternalStoragePrivateFile() {
        // Get path for the file on external storage.  If external
        // storage is not currently mounted this will fail.
        File file = new File(getExternalFilesDir(null), "Cutoffscore_input.txt");
        if (file != null) {
            return file.exists();
        }
        return false;
    }*/
    
}
