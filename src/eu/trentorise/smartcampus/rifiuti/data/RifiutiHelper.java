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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import eu.trentorise.smartcampus.rifiuti.model.Profile;

/**
 * @author raman
 *
 */
public class RifiutiHelper {

	private static final int DB_VERSION = 1;

	private static RifiutiHelper mHelper = null;
	
	protected Context mContext = null;
	private DBHelper dbHelper = null;
	private Profile mProfile = null;
	private List<String> mAreas = null;

	/**
	 * Initialize data access layer support
	 * @param ctx
	 * @throws IOException
	 */
	public static void init(Context ctx) throws IOException {
		mHelper = new RifiutiHelper(ctx);
		// TODO replace test data
		setProfile(new Profile("test", "utenza domestica", "Bleggio Superiore", "via Dante", "1", "Bleggio Superiore"));
	}		
			
	public static void setProfile(Profile profile) {
		mHelper.mProfile = profile;
		mHelper.mAreas = mHelper.readUserAreas();
	}
	
	/**
	 * @param ctx
	 * @throws IOException 
	 */
	private RifiutiHelper(Context ctx) throws IOException {
		super();
		this.mContext = ctx;
		dbHelper = new DBHelper(ctx, DB_VERSION);
		dbHelper.createDataBase();
	};
	
	/**
	 * Read 'tipi di rifiuti' for the specified user
	 * @return
	 */
	public static List<String> readTipologiaRifiuti() {
		SQLiteDatabase db = mHelper.dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = db.rawQuery("SELECT DISTINCT tipologiaRifiuto FROM raccolta "
					+ "WHERE area IN " + getAreeForQuery(mHelper.mAreas)+ " AND tipologiaUtenza = \"" + mHelper.mProfile.getUtenza() + "\"", null);
			List<String> result = new ArrayList<String>();
			if (cursor != null) {
				cursor.moveToFirst();
				for (int i = 0; i < cursor.getCount(); i++) {
					result.add(cursor.getString(0));
					cursor.moveToNext();
				}
			}
			return result;
		} finally {
			cursor.close();
		}

	}

	/**
	 * Read 'tipi di raccolta' for the specified user
	 * @return
	 */
	public static List<String> readTipologiaRaccolta() {
		SQLiteDatabase db = mHelper.dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = db.rawQuery("SELECT DISTINCT tipologiaRaccolta FROM raccolta "
					+ "WHERE area IN " + getAreeForQuery(mHelper.mAreas)+ " AND tipologiaUtenza = \"" + mHelper.mProfile.getUtenza() + "\"", null);
			List<String> result = new ArrayList<String>();
			if (cursor != null) {
				cursor.moveToFirst();
				for (int i = 0; i < cursor.getCount(); i++) {
					result.add(cursor.getString(0));
					cursor.moveToNext();
				}
			}
			return result;
		} finally {
			cursor.close();
		}

	}

	/**
	 * recover all user areas recursively
	 */
	private List<String> readUserAreas() {
		String parent = mProfile.getArea();
		SQLiteDatabase db = mHelper.dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try {
			List<String> result = new ArrayList<String>();
			while (parent != null && parent.trim().length() > 0) {
				result.add(parent);
				String query = "SELECT parent FROM aree WHERE nome = \"" + parent + "\"";
				cursor = db.rawQuery(query, null);
				if (cursor != null) {
					cursor.moveToFirst();
					parent = cursor.getString(0);
				}
			}
			return result;
		} finally {
			cursor.close();
		}
		
	}

	private static String getAreeForQuery(List<String> areas) {
		String aree = "(";
		for (String area : areas) {
			aree += "\"" + area + "\",";
		}
		aree = aree.substring(0, aree.length() - 1) + ")";
		return aree;
	}

}
