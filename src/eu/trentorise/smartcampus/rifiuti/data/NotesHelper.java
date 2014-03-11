package eu.trentorise.smartcampus.rifiuti.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.view.ActionMode;
import android.util.Log;
import eu.trentorise.smartcampus.rifiuti.model.Note;
import eu.trentorise.smartcampus.rifiuti.model.Profile;
import eu.trentorise.smartcampus.rifiuti.utils.PreferenceUtils;

public class NotesHelper {

	private static NotesHelper mHelper = null;

	public static ActionMode notesActionMode;

	private DBHelper dbHelper = null;
	private Profile mProfile = null;
	private Context mCtx;

	/**
	 * Initialize data access layer support
	 * 
	 * @param ctx
	 * @throws IOException
	 */
	public static void init(Context ctx) throws IOException {
		mHelper = new NotesHelper(ctx, PreferenceUtils.getProfile(ctx,
				PreferenceUtils.getCurrentProfilePosition(ctx)));
	}

	public static void init(Context ctx, Profile p) throws IOException {
		mHelper = new NotesHelper(ctx, p);
	}

	public static void init(Context ctx, int profileIndex) throws IOException {
		mHelper = new NotesHelper(ctx, profileIndex);
	}

	/**
	 * @param ctx
	 * @throws IOException
	 */
	private NotesHelper(Context ctx, Profile p) throws IOException {
		super();
		dbHelper = new DBHelper(ctx, RifiutiHelper.DB_VERSION);
		mCtx = ctx;
		mProfile = p;
	};

	/**
	 * @param ctx
	 * @throws IOException
	 */
	private NotesHelper(Context ctx, int profileIndex) throws IOException {
		super();
		dbHelper = new DBHelper(ctx, RifiutiHelper.DB_VERSION);
		mCtx = ctx;
		mProfile = PreferenceUtils.getProfile(mHelper.mCtx, profileIndex);
	};

	private static String getNotesSQL() {
		return "select * from "
				+ DBHelper.TABLE_NOTE
				+ " where "+ DBHelper.NOTE_PROFILE+ "="
				+ "\""
				+ PreferenceUtils
						.getProfile(mHelper.mCtx,
								PreferenceUtils.getCurrentProfilePosition(mHelper.mCtx)).getName() 
				+ "\"" 
				+ " order by " + DBHelper.NOTE_DATE;
	}

	public static List<Note> getNotes() {
		SQLiteDatabase db = mHelper.dbHelper.getReadableDatabase();
		return getNotes(db);
	}

	/**
	 * @param db
	 * @return
	 */
	public static List<Note> getNotes(SQLiteDatabase db) {
		List<Note> notes = new ArrayList<Note>();
		Cursor c = db.rawQuery(getNotesSQL(), null);
		while (c.moveToNext()) {
			PreferenceUtils.getCurrentProfilePosition(mHelper.mCtx);
			Note n = new Note(c.getInt(c.getColumnIndex(DBHelper.NOTE_ID)),
					c.getString(c.getColumnIndex(DBHelper.NOTE_TXT)),
					mHelper.mProfile, new Date(c.getLong(c
							.getColumnIndex(DBHelper.NOTE_DATE))));
			notes.add(n);
		}
		return notes;
	}

	public static void addNote(String s) {
		SQLiteDatabase db = mHelper.dbHelper.getWritableDatabase();
		db.insert(DBHelper.TABLE_NOTE, null,
				Note.toContentValues(s, mHelper.mProfile));
		db.close();
	}

	public static void deleteNotes(Note... notes) {
		SQLiteDatabase db = mHelper.dbHelper.getWritableDatabase();
		String whereClause = "";
		String[] whereArgs = new String[notes.length];
		for (int i = 0; i < notes.length; i++) {
			if (i > 0)
				whereClause += "OR ";
			whereClause += DBHelper.NOTE_ID + " = ? ";
			whereArgs[i] = "" + notes[i].getID();
		}
		int rows = db.delete(DBHelper.TABLE_NOTE, whereClause, whereArgs);
		Log.i("sql", whereClause + "" + whereArgs[0]);
		Log.i("deleted", rows + "");
		db.close();
	}

	/**
	 * @param n
	 */
	public static void saveNote(SQLiteDatabase db, Note n) {
		db.insert(DBHelper.TABLE_NOTE, null, n.toContentValues());
	}

}
