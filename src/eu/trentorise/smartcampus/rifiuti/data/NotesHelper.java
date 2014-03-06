package eu.trentorise.smartcampus.rifiuti.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import eu.trentorise.smartcampus.rifiuti.model.Note;

public class NotesHelper {

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
		dbHelper = new DBHelper(ctx, RifiutiHelper.DB_VERSION);
//		dbHelper.openDataBase();
	};

	private static final String SQL_GET_NOTES = "select * from "
			+ DBHelper.TABLE_NOTE + " order by " + DBHelper.NOTE_DATE;

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

	/**
	 * @param n
	 */
	public static void saveNote(SQLiteDatabase db, Note n) {
		db.insert(DBHelper.TABLE_NOTE, null, n.toContentValues());
	}

}
