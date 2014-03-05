package eu.trentorise.smartcampus.rifiuti.model;

import java.util.Date;

import android.content.ContentValues;
import eu.trentorise.smartcampus.rifiuti.data.DBHelper;

public class Note {
	private int mID;
	private String mText;
	private Date mDate;

	public Note(int mID, String mText, Date mDate) {
		super();
		this.mID = mID;
		this.mText = mText;
		this.mDate = mDate;
	}

	public Note(String mText, Date mDate) {
		super();
		this.mText = mText;
		this.mDate = mDate;
	}

	public int getID() {
		return mID;
	}

	public void setID(int mID) {
		this.mID = mID;
	}

	public String getText() {
		return mText;
	}

	public void setText(String mText) {
		this.mText = mText;
	}

	public Date getDate() {
		return mDate;
	}

	public void setmDate(Date mDate) {
		this.mDate = mDate;
	}

	public static ContentValues toContentValues(String s) {
		ContentValues cv = new ContentValues();
		cv.put(DBHelper.NOTE_TXT, s);
		cv.put(DBHelper.NOTE_DATE, new Date().getTime());
		return cv;
	}

	public ContentValues toContentValues() {
		ContentValues cv = new ContentValues();
		cv.put(DBHelper.NOTE_TXT, this.mText);
		cv.put(DBHelper.NOTE_DATE, this.mDate.getTime());
		return cv;
	}

	@Override
	public String toString() {
		return mText;
	}
	
	

}
