package eu.trentorise.smartcampus.rifiuti.data;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import eu.trentorise.smartcampus.rifiuti.model.Note;
import eu.trentorise.smartcampus.rifiuti.model.Profile;

public class NotesHelper {

	private static final int DB_VERSION = 1;

	private static NotesHelper mHelper = null;

	private DBHelper dbHelper = null;

	private Context mContext = null;

	/**
	 * Initialize data access layer support
	 * 
	 * @param ctx
	 * @throws IOException
	 */
	public static void init(Context ctx) throws IOException {
		mHelper = new NotesHelper(ctx);
	}

	/**
	 * @param ctx
	 * @throws IOException
	 */
	private NotesHelper(Context ctx) throws IOException {
		super();
		this.mContext = ctx;
		dbHelper = new DBHelper(ctx, DB_VERSION);
		dbHelper.createDataBase();
		dbHelper.openDataBase();
	};

	private static final String SQL_GET_NOTES = "select * from "
			+ DBHelper.TABLE_NOTE + " order by " + DBHelper.NOTE_DATE;

	public static List<Note> getNotes() {
		SQLiteDatabase db = mHelper.dbHelper.getWritableDatabase();
		List<Note> notes = new ArrayList<Note>();
		Cursor c = db.rawQuery(SQL_GET_NOTES, null);
		while (c.moveToNext()) {
			Note n = new Note(c.getInt(c.getColumnIndex(DBHelper.NOTE_ID)),
					c.getString(c.getColumnIndex(DBHelper.NOTE_TXT)), new Date(
							c.getLong(c.getColumnIndex(DBHelper.NOTE_DATE))));
			notes.add(n);
		}
		return notes;
	}

	public static void addNote(String s) {
		SQLiteDatabase db = mHelper.dbHelper.getWritableDatabase();
		db.insert(DBHelper.TABLE_NOTE, null, Note.toContentValues(s));
	}

}
