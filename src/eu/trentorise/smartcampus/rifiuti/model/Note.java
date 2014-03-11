package eu.trentorise.smartcampus.rifiuti.model;

import java.util.Date;

import android.content.ContentValues;
import eu.trentorise.smartcampus.rifiuti.data.DBHelper;

public class Note {
	private int mID;
	private String mText;
	private Profile mProfile;
	private Date mDate;

	public Note(int id, String text, Profile p, Date date) {
		super();
		this.mID = id;
		this.mText = text;
		this.mProfile = p;
		this.mDate = date;
	}

	public Note(String text, Profile p, Date date) {
		super();
		this.mText = text;
		this.mProfile = p;
		this.mDate = date;
	}

	public int getID() {
		return mID;
	}

	public void setID(int id) {
		this.mID = id;
	}

	public String getText() {
		return mText;
	}

	public void setText(String text) {
		this.mText = text;
	}

	public Date getDate() {
		return mDate;
	}

	public void setmDate(Date date) {
		this.mDate = date;
	}

	public static ContentValues toContentValues(String s, Profile p) {
		ContentValues cv = new ContentValues();
		cv.put(DBHelper.NOTE_TXT, s);
		cv.put(DBHelper.NOTE_DATE, new Date().getTime());
		cv.put(DBHelper.NOTE_PROFILE, p.getName());
		return cv;
	}

	public ContentValues toContentValues() {
		ContentValues cv = new ContentValues();
		cv.put(DBHelper.NOTE_TXT, this.mText);
		cv.put(DBHelper.NOTE_DATE, this.mDate.getTime());
		cv.put(DBHelper.NOTE_PROFILE, this.mProfile.getName());
		return cv;
	}

	@Override
	public String toString() {
		return mText;
	}

	public Profile getProfile() {
		return mProfile;
	}

	public void setProfile(Profile profile) {
		this.mProfile = profile;
	}

}
