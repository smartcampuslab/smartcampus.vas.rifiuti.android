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

	private Context mCtx;

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
		mCtx = ctx;
	};

	private static String getNotesSQL() {
		return "select * from " + DBHelper.TABLE_NOTE + " where " + DBHelper.NOTE_PROFILE + "=" + "\""
				+ PreferenceUtils.getProfile(mHelper.mCtx, PreferenceUtils.getCurrentProfilePosition(mHelper.mCtx)).getName()
				+ "\"" + " order by " + DBHelper.NOTE_DATE;
	}

	private static String getAllNotesSQL() {
		return "select * from " + DBHelper.TABLE_NOTE;
	}

	public static List<Note> getAllNotes() {
		SQLiteDatabase db = RifiutiHelper.getDBHelper().getReadableDatabase();
		return getAllNotes(db);
	}

	public static List<Note> getAllNotes(SQLiteDatabase db) {
		List<Note> notes = new ArrayList<Note>();
		Cursor c = null;
		try {
			c = db.rawQuery(getAllNotesSQL(), null);
			while (c.moveToNext()) {
				Profile p = new Profile();
				p.setName(c.getString(c.getColumnIndex(DBHelper.NOTE_PROFILE)));
				Note n = new Note(c.getInt(c.getColumnIndex(DBHelper.NOTE_ID)),
						c.getString(c.getColumnIndex(DBHelper.NOTE_TXT)), p, new Date(c.getLong(c
								.getColumnIndex(DBHelper.NOTE_DATE))));
				notes.add(n);
			}
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return notes;
	}

	public static List<Note> getNotes() {
		SQLiteDatabase db = RifiutiHelper.getDBHelper().getReadableDatabase();
		return getNotes(db);
	}

	public static List<Note> getNotes(SQLiteDatabase db) {
		List<Note> notes = new ArrayList<Note>();
		Cursor c = null;
		try {
			c = db.rawQuery(getNotesSQL(), null);
			while (c.moveToNext()) {
				Profile p = new Profile();
				p.setName(c.getString(c.getColumnIndex(DBHelper.NOTE_PROFILE)));
				Note n = new Note(c.getInt(c.getColumnIndex(DBHelper.NOTE_ID)),
						c.getString(c.getColumnIndex(DBHelper.NOTE_TXT)), RifiutiHelper.getProfile(), new Date(c.getLong(c
								.getColumnIndex(DBHelper.NOTE_DATE))));
				notes.add(n);
			}
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return notes;
	}

	public static void addNote(String s) {
		SQLiteDatabase db = RifiutiHelper.getDBHelper().getWritableDatabase();
		db.insert(DBHelper.TABLE_NOTE, null, Note.toContentValues(s, RifiutiHelper.getProfile()));
		db.close();
	}

	public static void editNote(Note n) {
		SQLiteDatabase db = RifiutiHelper.getDBHelper().getWritableDatabase();
		String where = DBHelper.NOTE_ID + "=" + n.getID();
		db.update(DBHelper.TABLE_NOTE, n.toContentValues(), where, null);
		db.close();
	}

	public static void deleteNotes(Note... notes) {
		SQLiteDatabase db = RifiutiHelper.getDBHelper().getWritableDatabase();
		String whereClause = "";
		String[] whereArgs = new String[notes.length];
		for (int i = 0; i < notes.length; i++) {
			if (i > 0)
				whereClause += "OR ";
			whereClause += DBHelper.NOTE_ID + " = ? ";
			whereArgs[i] = "" + notes[i].getID();
		}
		int rows = db.delete(DBHelper.TABLE_NOTE, whereClause, whereArgs);
		Log.i("deleted", rows + "");
		db.close();
	}

	public static void deleteNotes(String profileName) {
		SQLiteDatabase db = RifiutiHelper.getDBHelper().getWritableDatabase();
		String whereClause = DBHelper.NOTE_PROFILE + " = ? ";
		String[] whereArgs = new String[] { profileName };
		int rows = db.delete(DBHelper.TABLE_NOTE, whereClause, whereArgs);
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
