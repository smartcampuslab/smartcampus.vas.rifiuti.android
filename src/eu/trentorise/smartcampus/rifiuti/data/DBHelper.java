/*******************************************************************************
 * Copyright 2012-2013 Trento RISE
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either   express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package eu.trentorise.smartcampus.rifiuti.data;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author raman
 *
 */
public class DBHelper extends SQLiteOpenHelper {
	
	private static final String DB_NAME = "rifiuti";

    private SQLiteDatabase myDataBase; 
 
    private final Context mContext;
    
	/**
	 * @param context
	 * @param name
	 * @param factory
	 * @param version
	 */
	public DBHelper(Context context, int version) {
		super(context, DB_NAME, null, version);
		this.mContext = context;
	}

    public void openDataBase() throws SQLException {
    	//Open the database
        String myPath = dbPath(mContext);
    	myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }
    
    private String dbPath(Context ctx) {
    	return ctx.getDatabasePath(DB_NAME).getAbsolutePath();
    }
    @Override
	public synchronized void close() {
   	    if(myDataBase != null) {
   	    	myDataBase.close();
   	    }
    	super.close();
	}    
    /**
    * Creates a empty database on the system and rewrites it with your own database.
    * */
   public void createDataBase() throws IOException{
	   boolean dbExist = checkDataBase();
	   if(!dbExist){
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
    
	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
	/**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){
    	SQLiteDatabase checkDB = null;
    	try{
    		String myPath = dbPath(mContext);
    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    	}catch(SQLiteException e){
    		//database does't exist yet.
    	}
    	if(checkDB != null){
    		checkDB.close();
    	}
 
    	return checkDB != null ? true : false;
    }
    
    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{
    	//Open your local db as the input stream
    	InputStream myInput = mContext.getAssets().open(DB_NAME);
    	// Path to the just created empty db
    	String outFileName = dbPath(mContext);
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
    }
}
