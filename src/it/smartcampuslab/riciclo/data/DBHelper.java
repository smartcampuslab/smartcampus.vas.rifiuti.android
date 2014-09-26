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

package it.smartcampuslab.riciclo.data;

import it.smartcampuslab.riciclo.model.Note;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

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

	private static List<Note> mNotes;

	private SQLiteDatabase myDataBase;

	/**
	 * @param context
	 * @param name
	 * @param factory
	 * @param version
	 */
	protected DBHelper(Context context, int version) {
		super(context, DB_NAME, null, version);
	}

	private void openDataBase() throws SQLException {
		// Open the database
		myDataBase = getWritableDatabase();
	}

	private static String dbPath(Context ctx) {
		return ctx.getDatabasePath(DB_NAME).getAbsolutePath();
	}

	@Override
	public synchronized void close() {
		if (myDataBase != null) {
			myDataBase.close();
		}
		super.close();
	}

	public static final String TABLE_NOTE = "note";
	public static final String NOTE_ID = "_id";
	public static final String NOTE_TXT = "txt";
	public static final String NOTE_PROFILE = "profile";
	public static final String NOTE_DATE = "ndate";

	private static final String CREATE_NOTE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NOTE + "(" + NOTE_ID
			+ " INTEGER PRIMARY KEY," + NOTE_TXT + " TEXT," + NOTE_PROFILE + " TEXT," + NOTE_DATE + " INTEGER)";

	/**
	 * Creates a empty database on the system and rewrites it with your own
	 * database.
	 * 
	 * @param dbVersion
	 * @param ctx
	 * */
	public static DBHelper createDataBase(Context ctx, int dbVersion) throws IOException {
		int version = checkDataBase(ctx);
		if (version < 0) {
			// By calling this method and empty database will be created into
			// the default system path
			// of your application so we are gonna be able to overwrite that
			// database with our database.
			// this.getReadableDatabase();
			try {
				copyDataBase(ctx);
			} catch (IOException e) {
				throw new Error("Error copying database");
			}
		} else if (version < dbVersion) {
			SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath(ctx), null, SQLiteDatabase.OPEN_READONLY);
			mNotes = NotesHelper.getAllNotes(db);
			db.close();
			try {
				copyDataBase(ctx);
			} catch (IOException e) {
				throw new Error("Error copying database");
			}
		}
		DBHelper helper = new DBHelper(ctx, dbVersion);
		helper.openDataBase();
		return helper;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_NOTE_TABLE);
		db.beginTransaction();
		try {
			if (mNotes != null) {
				for (Note n : mNotes) {
					NotesHelper.saveNote(db, n);
				}
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onCreate(db);
	}

	/**
	 * Check if the database already exist to avoid re-copying the file each
	 * time you open the application.
	 * 
	 * @return version of a db or -1
	 */
	private static int checkDataBase(Context ctx) {
		SQLiteDatabase checkDB = null;
		int version = -1;
		try {
			String myPath = dbPath(ctx);
			checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
			version = checkDB.getVersion();
		} catch (SQLiteException e) {
			// database does't exist yet.
		}
		if (checkDB != null) {
			checkDB.close();
		}

		return version;
	}

	/**
	 * Copies your database from your local assets-folder to the just created
	 * empty database in the system folder, from where it can be accessed and
	 * handled. This is done by transfering bytestream.
	 * */
	private static void copyDataBase(Context ctx) throws IOException {
		// Open your local db as the input stream
		InputStream myInput = ctx.getAssets().open(DB_NAME);
		// Path to the just created empty db
		String outFileName = dbPath(ctx);
		// Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);
		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}
		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}
}
