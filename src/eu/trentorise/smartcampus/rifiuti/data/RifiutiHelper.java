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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import eu.trentorise.smartcampus.rifiuti.R;
import eu.trentorise.smartcampus.rifiuti.model.Area;
import eu.trentorise.smartcampus.rifiuti.model.Calendario;
import eu.trentorise.smartcampus.rifiuti.model.CalendarioItem;
import eu.trentorise.smartcampus.rifiuti.model.DatiTipologiaRaccolta;
import eu.trentorise.smartcampus.rifiuti.model.Gestore;
import eu.trentorise.smartcampus.rifiuti.model.Istituzione;
import eu.trentorise.smartcampus.rifiuti.model.Profile;
import eu.trentorise.smartcampus.rifiuti.model.PuntoRaccolta;

/**
 * @author raman
 * 
 */
public class RifiutiHelper {

	public static final int DB_VERSION = 2;

	private static RifiutiHelper mHelper = null;

	protected Context mContext = null;
	private DBHelper dbHelper = null;
	private Profile mProfile = null;
	private List<String> mAreas = null;

	private Map<String, Integer> colorMap = null;
	private Map<String, Drawable> tipiRifiutoDrawablesMap = null;

	/**
	 * Initialize data access layer support
	 * 
	 * @param ctx
	 * @throws IOException
	 */
	public static void init(Context ctx) throws IOException {
		mHelper = new RifiutiHelper(ctx);
		// TODO replace test data
		// setProfile(new Profile("test", "utenza domestica",
		// "Bleggio Superiore", "via Dante", "1", "Bleggio Superiore"));
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
		dbHelper = DBHelper.createDataBase(ctx, DB_VERSION);
	};

	public static RifiutiHelper getInstance() {
		return mHelper;
	}

	/**
	 * Read 'tipi di rifiuti' for the specified user
	 * 
	 * @return
	 */
	public static List<String> readTipologiaRifiuti() {
		SQLiteDatabase db = mHelper.dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = db
					.rawQuery("SELECT DISTINCT tipologiaRifiuto FROM raccolta " + "WHERE area IN "
							+ getAreeForQuery(mHelper.mAreas) + " AND tipologiaUtenza = \"" + mHelper.mProfile.getUtenza()
							+ "\"", null);
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
			if (cursor != null)
				cursor.close();
		}

	}

	/**
	 * Read 'tipi di raccolta' for the specified user
	 * 
	 * @return
	 */
	public static List<DatiTipologiaRaccolta> readTipologiaRaccolta() {
		SQLiteDatabase db = mHelper.dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = db.rawQuery(
					"SELECT DISTINCT tipologiaRaccolta, tipologiaPuntoRaccolta, colore FROM raccolta " + "WHERE area IN "
							+ getAreeForQuery(mHelper.mAreas) + " AND tipologiaUtenza = \"" + mHelper.mProfile.getUtenza()
							+ "\"" + " AND tipologiaRaccolta IS NOT NULL AND tipologiaRaccolta != ''"
							+ " ORDER BY colore DESC, tipologiaRaccolta", null);
			List<DatiTipologiaRaccolta> result = new ArrayList<DatiTipologiaRaccolta>();
			if (cursor != null) {
				cursor.moveToFirst();
				for (int i = 0; i < cursor.getCount(); i++) {
					DatiTipologiaRaccolta dtr = new DatiTipologiaRaccolta();
					dtr.setColore(cursor.getString(cursor.getColumnIndex("colore")));
					dtr.setTipologiaPuntoRaccolta(cursor.getString(cursor.getColumnIndex("tipologiaPuntoRaccolta")));
					dtr.setTipologiaRaccolta(cursor.getString(cursor.getColumnIndex("tipologiaRaccolta")));
					result.add(dtr);
					cursor.moveToNext();
				}
			}
			return result;
		} finally {
			if (cursor != null)
				cursor.close();
		}
	}

	public static List<DatiTipologiaRaccolta> readTipologiaRaccoltaPerTipologiaPuntoRaccolta(String tipologiaPuntoRaccolta) {
		SQLiteDatabase db = mHelper.dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = db.rawQuery("SELECT DISTINCT tipologiaRaccolta, colore FROM raccolta " + "WHERE area IN "
					+ getAreeForQuery(mHelper.mAreas) + " AND tipologiaUtenza = \"" + mHelper.mProfile.getUtenza()
					+ "\" AND tipologiaPuntoRaccolta = \"" + tipologiaPuntoRaccolta + "\"", null);
			List<DatiTipologiaRaccolta> result = new ArrayList<DatiTipologiaRaccolta>();
			if (cursor != null) {
				cursor.moveToFirst();
				for (int i = 0; i < cursor.getCount(); i++) {
					DatiTipologiaRaccolta dtr = new DatiTipologiaRaccolta();
					dtr.setColore(cursor.getString(cursor.getColumnIndex("colore")));
					dtr.setTipologiaRaccolta(cursor.getString(cursor.getColumnIndex("tipologiaRaccolta")));
					result.add(dtr);
					cursor.moveToNext();
				}
			}
			return result;
		} finally {
			if (cursor != null)
				cursor.close();
		}
	}

	/**
	 * Read all 'rifiuti' that correspond to the specified 'tipo rifiuti'
	 * 
	 * @param tipoRifiuto
	 * @return
	 * @throws Exception
	 */
	public static List<String> getRifiutoPerTipoRifiuti(String tipoRifiuto) throws Exception {
		SQLiteDatabase db = mHelper.dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try {
			String aree = getAreeForQuery(mHelper.mAreas);
			String query = "SELECT DISTINCT nome FROM riciclabolario WHERE tipologiaRifiuto = \"" + tipoRifiuto
					+ "\" AND area in " + aree + " AND tipologiaUtenza = \"" + mHelper.mProfile.getUtenza() + "\"";
			cursor = db.rawQuery(query, null);
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
			if (cursor != null)
				cursor.close();
		}
	}

	/**
	 * Read all 'rifiuti' that correspond to the specified 'tipo raccolta'
	 * 
	 * @param tipoRifiuto
	 * @return
	 * @throws Exception
	 */
	public static List<String> getRifiutoPerTipoRaccolta(String tipoRaccolta) throws Exception {
		SQLiteDatabase db = mHelper.dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try {
			String aree = getAreeForQuery(mHelper.mAreas);
			String query = "SELECT DISTINCT nome FROM riciclabolario ri INNER JOIN raccolta ra ON ri.tipologiaRifiuto = ra.tipologiaRifiuto AND ri.tipologiaUtenza = ra.tipologiaUtenza WHERE ra.tipologiaRaccolta = \""
					+ tipoRaccolta
					+ "\" AND ra.area in "
					+ aree
					+ " AND ri.area in "
					+ aree
					+ " AND ra.tipologiaUtenza = \""
					+ mHelper.mProfile.getUtenza() + "\"";
			cursor = db.rawQuery(query, null);
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
			if (cursor != null)
				cursor.close();
		}
	}

	/**
	 * Read all 'punti raccolta' that correspond to the specified 'tipo
	 * raccolta'
	 * 
	 * @param tipoRifiuto
	 * @return
	 * @throws Exception
	 */
	public static List<PuntoRaccolta> getPuntiRaccoltaPerTipoRaccolta(String tipoRaccolta) throws Exception {
		return getPuntiRaccoltaPerTipo(tipoRaccolta, null);
	}

	/**
	 * Read all 'punti raccolta' that correspond to the specified 'tipo rifiuto'
	 * 
	 * @param tipoRifiuto
	 * @return
	 * @throws Exception
	 */
	public static List<PuntoRaccolta> getPuntiRaccoltaPerTipoRifiuto(String tipoRifiuto) throws Exception {
		return getPuntiRaccoltaPerTipo(null, tipoRifiuto);
	}

	private static List<PuntoRaccolta> getPuntiRaccoltaPerTipo(String tipoRaccolta, String tipoRifiuto) throws Exception {
		SQLiteDatabase db = mHelper.dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try {
			String aree = getAreeForQuery(mHelper.mAreas);
			String query = "SELECT DISTINCT "
					+ "puntiRaccolta.area, "
					+ "puntiRaccolta.tipologiaPuntiRaccolta,"
					+ "puntiRaccolta.tipologiaUtenza,"
					+ "puntiRaccolta.localizzazione,"
					+ "puntiRaccolta.indirizzo FROM puntiRaccolta "
					+ "	INNER JOIN raccolta ON puntiRaccolta.tipologiaPuntiRaccolta = raccolta.tipologiaPuntoRaccolta AND raccolta.tipologiaUtenza = puntiRaccolta.tipologiaUtenza "
					+ " WHERE puntiRaccolta.area IN " + aree + " AND raccolta.area IN " + aree
					+ " AND puntiRaccolta.tipologiaUtenza = \"" + mHelper.mProfile.getUtenza() + "\"";
			String selector = "AND "
					+ (tipoRaccolta != null ? ("raccolta.tipologiaRaccolta = \"" + tipoRaccolta + "\"")
							: ("raccolta.tipologiaRifiuto = \"" + tipoRifiuto + "\""));
			query += selector;

			cursor = db.rawQuery(query, null);
			List<PuntoRaccolta> result = extractListFromCursor(cursor);
			return result;
		} finally {
			if (cursor != null)
				cursor.close();
		}
	}

	/**
	 * Read all 'punti raccolta' for the user profile
	 * 
	 * @return
	 * @throws Exception
	 */
	public static List<PuntoRaccolta> getPuntiRaccolta() throws Exception {
		SQLiteDatabase db = mHelper.dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try {
			String aree = getAreeForQuery(mHelper.mAreas);
			String query = "SELECT DISTINCT " + "area, " + "tipologiaPuntiRaccolta," + "tipologiaUtenza," + "localizzazione,"
					+ "indirizzo FROM puntiRaccolta " + " WHERE puntiRaccolta.area IN " + aree + " AND area IN " + aree
					+ " AND tipologiaUtenza = \"" + mHelper.mProfile.getUtenza() + "\"";

			cursor = db.rawQuery(query, null);
			List<PuntoRaccolta> result = extractListFromCursor(cursor);
			return result;
		} finally {
			if (cursor != null)
				cursor.close();
		}
	}

	/**
	 * @param cursor
	 * @return
	 */
	private static List<PuntoRaccolta> extractListFromCursor(Cursor cursor) {
		List<PuntoRaccolta> result = new ArrayList<PuntoRaccolta>();
		if (cursor != null) {
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++) {
				PuntoRaccolta pr = new PuntoRaccolta();
				pr.setArea(cursor.getString(cursor.getColumnIndex("area")));
				pr.setTipologiaPuntiRaccolta(cursor.getString(cursor.getColumnIndex("tipologiaPuntiRaccolta")));
				pr.setTipologiaUtenza(cursor.getString(cursor.getColumnIndex("tipologiaUtenza")));
				pr.setLocalizzazione(cursor.getString(cursor.getColumnIndex("localizzazione")));
				pr.setIndirizzo(cursor.getString(cursor.getColumnIndex("indirizzo")));
				result.add(pr);
				cursor.moveToNext();
			}
		}
		return result;
	}

	/**
	 * return 'tipo rifiuti' for the specified 'rifiuto'
	 * 
	 * @param rifiuto
	 * @return
	 * @throws Exception
	 */
	public static String getTipoRifiuto(String rifiuto) throws Exception {
		SQLiteDatabase db = mHelper.dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try {
			String aree = getAreeForQuery(mHelper.mAreas);
			String query = "SELECT DISTINCT tipologiaRifiuto FROM riciclabolario WHERE nome = \"" + rifiuto + "\" AND area in "
					+ aree + " AND tipologiaUtenza = \"" + mHelper.mProfile.getUtenza() + "\"";
			cursor = db.rawQuery(query, null);
			if (cursor != null) {
				cursor.moveToFirst();
				return cursor.getString(0);
			}
			return null;
		} finally {
			if (cursor != null)
				cursor.close();
		}
	}

	public static List<DatiTipologiaRaccolta> getDatiTipologiaRaccolta(String rifiuto) {
		SQLiteDatabase db = mHelper.dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try {
			String aree = getAreeForQuery(mHelper.mAreas);
			String query = "SELECT DISTINCT ra.tipologiaRaccolta, ra.tipologiaPuntoRaccolta, ra.colore, ra.infoRaccolta "
					+ "FROM riciclabolario r, raccolta ra WHERE " + "r.nome = \"" + rifiuto + "\" AND r.area in " + aree
					+ " AND r.tipologiaUtenza = \"" + mHelper.mProfile.getUtenza() + "\" AND " + "ra.area in " + aree
					+ " AND ra.tipologiaUtenza = '" + mHelper.mProfile.getUtenza() + "' AND "
					+ "ra.tipologiaRifiuto = r.tipologiaRifiuto";
			cursor = db.rawQuery(query, null);
			List<DatiTipologiaRaccolta> result = new ArrayList<DatiTipologiaRaccolta>();
			if (cursor != null) {
				if (cursor != null) {
					cursor.moveToFirst();
					for (int i = 0; i < cursor.getCount(); i++) {
						DatiTipologiaRaccolta dtr = new DatiTipologiaRaccolta();
						dtr.setColore(cursor.getString(cursor.getColumnIndex("colore")));
						dtr.setInfo(cursor.getString(cursor.getColumnIndex("infoRaccolta")));
						dtr.setTipologiaPuntoRaccolta(cursor.getString(cursor.getColumnIndex("tipologiaPuntoRaccolta")));
						dtr.setTipologiaRaccolta(cursor.getString(cursor.getColumnIndex("tipologiaRaccolta")));
						result.add(dtr);
					}
				}
			}
			return result;
		} finally {
			if (cursor != null)
				cursor.close();
		}

	}

	/**
	 * Read calendars of the specified 'punto raccolta'
	 * 
	 * @param pr
	 * @return
	 */
	public static List<Calendario> getCalendars(PuntoRaccolta pr) {
		SQLiteDatabase db = mHelper.dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try {
			String query = "SELECT DISTINCT dataDa, dataA, il, dalle, alle FROM puntiRaccolta WHERE indirizzo = \""
					+ pr.getIndirizzo() + "\" AND tipologiaUtenza = \"" + pr.getTipologiaUtenza() + "\" AND area = \""
					+ pr.getArea() + "\"" + " AND (puntiRaccolta.dataDa IS NOT NULL AND puntiRaccolta.dataDa != '')"
					+ " AND (puntiRaccolta.dataA IS NOT NULL AND puntiRaccolta.dataA != '')"
					+ " AND (puntiRaccolta.il IS NOT NULL AND puntiRaccolta.il != '')";
			cursor = db.rawQuery(query, null);
			List<Calendario> result = new ArrayList<Calendario>();
			if (cursor != null) {
				cursor.moveToFirst();
				for (int i = 0; i < cursor.getCount(); i++) {
					Calendario c = new Calendario();
					c.setDataDa(cursor.getString(cursor.getColumnIndex("dataDa")));
					c.setDataA(cursor.getString(cursor.getColumnIndex("dataA")));
					c.setIl(cursor.getString(cursor.getColumnIndex("il")));
					c.setDalle(cursor.getString(cursor.getColumnIndex("dalle")));
					c.setAlle(cursor.getString(cursor.getColumnIndex("alle")));
					result.add(c);
					cursor.moveToNext();
				}
			}
			return result;
		} finally {
			if (cursor != null)
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
			if (cursor != null)
				cursor.close();
		}

	}

	/**
	 * Get calendar records for the month defined by the specified date
	 * 
	 * @param ref
	 * @return
	 */
	public static List<List<CalendarioItem>> getCalendarsForMonth(Calendar ref) {
		int max = ref.getActualMaximum(Calendar.DAY_OF_MONTH);
		List<List<CalendarioItem>> res = new ArrayList<List<CalendarioItem>>(max);
		SparseArray<List<Integer>> dayMap = new SparseArray<List<Integer>>();
		Calendar c = (Calendar) ref.clone();
		for (int i = 1; i <= max; i++) {
			c.set(Calendar.DAY_OF_MONTH, i);
			int dow = c.get(Calendar.DAY_OF_WEEK);
			List<Integer> list = dayMap.get(dow);
			if (list == null) {
				list = new ArrayList<Integer>();
				dayMap.put(dow, list);
			}
			list.add(i);
			res.add(new ArrayList<CalendarioItem>());
		}

		SQLiteDatabase db = mHelper.dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try {
			String aree = getAreeForQuery(mHelper.mAreas);
			String query = "SELECT DISTINCT "
					+ "puntiRaccolta.*, raccolta.colore FROM puntiRaccolta "
					+ "	INNER JOIN raccolta ON puntiRaccolta.tipologiaPuntiRaccolta = raccolta.tipologiaPuntoRaccolta AND raccolta.tipologiaUtenza = puntiRaccolta.tipologiaUtenza "
					+ " WHERE (puntiRaccolta.dataDa IS NOT NULL AND puntiRaccolta.dataDa != '')"
					+ " AND (puntiRaccolta.dataA IS NOT NULL AND puntiRaccolta.dataA != '')"
					+ " AND (puntiRaccolta.il IS NOT NULL AND puntiRaccolta.il != '')" + " AND puntiRaccolta.area IN " + aree
					+ " AND raccolta.area IN " + aree + " AND puntiRaccolta.tipologiaUtenza = \""
					+ mHelper.mProfile.getUtenza() + "\"";
			cursor = db.rawQuery(query, null);
			if (cursor != null) {
				cursor.moveToFirst();
				for (int i = 0; i < cursor.getCount(); i++) {
					Calendario calendario = new Calendario();
					calendario.setDataDa(cursor.getString(cursor.getColumnIndex("dataDa")));
					calendario.setDataA(cursor.getString(cursor.getColumnIndex("dataA")));
					calendario.setAlle(cursor.getString(cursor.getColumnIndex("alle")));
					calendario.setDalle(cursor.getString(cursor.getColumnIndex("dalle")));
					calendario.setIl(cursor.getString(cursor.getColumnIndex("il")));
					Integer itemDow = calendario.asCalendarDayOfWeek();
					if (itemDow == null)
						continue;

					PuntoRaccolta pr = new PuntoRaccolta();
					pr.setArea(cursor.getString(cursor.getColumnIndex("area")));
					pr.setIndirizzo(cursor.getString(cursor.getColumnIndex("indirizzo")));
					pr.setLocalizzazione(cursor.getString(cursor.getColumnIndex("localizzazione")));
					pr.setTipologiaPuntiRaccolta(cursor.getString(cursor.getColumnIndex("tipologiaPuntiRaccolta")));
					pr.setTipologiaUtenza(cursor.getString(cursor.getColumnIndex("tipologiaUtenza")));

					CalendarioItem item = new CalendarioItem();
					item.setCalendar(calendario);
					item.setColor(cursor.getString(cursor.getColumnIndex("colore")));
					item.setPoint(pr);
					List<Integer> days = dayMap.get(itemDow);
					for (Integer day : days) {
						c.set(Calendar.DAY_OF_MONTH, day);
						if (calendario.contains(c)) {
							res.get(day - 1).add(item);
						}
					}
					cursor.moveToNext();
				}
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return res;
	}

	/**
	 * recover all user areas recursively
	 */
	public static List<Area> readAreas(String comune) {
		SQLiteDatabase db = mHelper.dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try {
			List<Area> result = new ArrayList<Area>();
			String query = "SELECT nome,parent,comune FROM aree WHERE nome LIKE \"%" + comune + "%\"";
			cursor = db.rawQuery(query, null);
			while (cursor.moveToNext()) {
				Area tmp = new Area();
				tmp.setNome(cursor.getString(cursor.getColumnIndex("nome")));
				tmp.setComune(cursor.getString(cursor.getColumnIndex("comune")));
				tmp.setParent(cursor.getString(cursor.getColumnIndex("parent")));
				// tmp.setVia(cursor.getString(2));
				// tmp.setNumero(cursor.getString(3));
				result.add(tmp);
			}
			return result;
		} finally {
			if (cursor != null)
				cursor.close();
		}

	}

	// /**
	// * recover the parent of all user areas recursively
	// */
	// private List<String> readUserAreas() {
	// String parent = mProfile.getArea();
	// SQLiteDatabase db = mHelper.dbHelper.getReadableDatabase();
	// Cursor cursor = null;
	// try {
	// List<String> result = new ArrayList<String>();
	// while (parent != null && parent.trim().length() > 0) {
	// result.add(parent);
	// String query = "SELECT parent FROM aree WHERE nome = \""
	// + parent + "\"";
	// cursor = db.rawQuery(query, null);
	// if (cursor != null) {
	// cursor.moveToFirst();
	// parent = cursor.getString(0);
	// }
	// }
	// return result;
	// } finally {
	// if (cursor != null)
	// cursor.close();
	// }
	//
	// }

	/**
	 * Get 'tipi di raccolta' for the specified 'tipo di punto di raccolta'
	 * 
	 * @return
	 */
	public static List<DatiTipologiaRaccolta> getTipologiaRaccolta(String tipologiaPuntoRaccolta) {
		SQLiteDatabase db = mHelper.dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = db
					.rawQuery("SELECT DISTINCT tipologiaRaccolta, colore FROM raccolta " + "WHERE tipologiaPuntoRaccolta = \""
							+ tipologiaPuntoRaccolta + "\"" + " AND area IN " + getAreeForQuery(mHelper.mAreas)
							+ " AND tipologiaUtenza = \"" + mHelper.mProfile.getUtenza() + "\"", null);
			List<DatiTipologiaRaccolta> result = new ArrayList<DatiTipologiaRaccolta>();
			if (cursor != null) {
				cursor.moveToFirst();
				for (int i = 0; i < cursor.getCount(); i++) {
					DatiTipologiaRaccolta dtr = new DatiTipologiaRaccolta();
					dtr.setColore(cursor.getString(cursor.getColumnIndex("colore")));
					dtr.setTipologiaPuntoRaccolta(tipologiaPuntoRaccolta);
					dtr.setTipologiaRaccolta(cursor.getString(cursor.getColumnIndex("tipologiaRaccolta")));
					result.add(dtr);
					cursor.moveToNext();
				}
			}
			return result;
		} finally {
			if (cursor != null)
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

	private Map<String, Integer> getColorMap(Context ctx) {
		if (colorMap == null) {
			colorMap = new HashMap<String, Integer>();
			String[] array = ctx.getResources().getStringArray(R.array.color_names);
			TypedArray valueArray = ctx.getResources().obtainTypedArray(R.array.color_values);
			for (int i = 0; i < array.length; i++) {
				colorMap.put(array[i], valueArray.getColor(i, 0));
			}
			valueArray.recycle();
		}
		return colorMap;
	}

	public static int getColorResource(Context ctx, String color) {
		return mHelper.getColorMap(ctx).get(color);
	}

	private Map<String, Drawable> getTipiRifiutoDrawablesMap(Context ctx) {
		if (tipiRifiutoDrawablesMap == null) {
			tipiRifiutoDrawablesMap = new HashMap<String, Drawable>();
			String[] array = ctx.getResources().getStringArray(R.array.tipirifiuto_names);
			TypedArray valueArray = ctx.getResources().obtainTypedArray(R.array.tipirifiuto_drawables);
			for (int i = 0; i < array.length; i++) {
				tipiRifiutoDrawablesMap.put(array[i].toLowerCase(Locale.getDefault()), valueArray.getDrawable(i));
			}
			valueArray.recycle();
		}
		return tipiRifiutoDrawablesMap;
	}

	public static Drawable getTipiRifiutoDrawable(Context ctx, String tipoRifiuto) {
		return mHelper.getTipiRifiutoDrawablesMap(ctx).get(tipoRifiuto.toLowerCase(Locale.getDefault()));
	}

	/**
	 * @param trim
	 * @return list of rifiuti objects with the specified prefix
	 */
	public static List<String> getRifiuti(String pre) {
		SQLiteDatabase db = mHelper.dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try {
			String aree = getAreeForQuery(mHelper.mAreas);
			String query = "SELECT DISTINCT nome FROM riciclabolario WHERE nome LIKE '%" + pre + "%' AND area in " + aree
					+ " AND tipologiaUtenza = \"" + mHelper.mProfile.getUtenza() + "\"";
			cursor = db.rawQuery(query, null);
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
			if (cursor != null)
				cursor.close();
		}
	}

	/**
	 * Find area by comune
	 * 
	 * @param string
	 * @return
	 */
	public static Area findArea(String string) {
		SQLiteDatabase db = mHelper.dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try {
			String query = "SELECT nome,parent,comune FROM aree WHERE nome = '" + string + "'";
			cursor = db.rawQuery(query, null);
			cursor.moveToFirst();
			Area tmp = new Area();
			tmp.setNome(cursor.getString(cursor.getColumnIndex("nome")));
			tmp.setComune(cursor.getString(cursor.getColumnIndex("comune")));
			tmp.setParent(cursor.getString(cursor.getColumnIndex("parent")));
			// tmp.setVia(cursor.getString(2));
			// tmp.setNumero(cursor.getString(3));
			return tmp;
		} finally {
			if (cursor != null)
				cursor.close();
		}
	}

	/**
	 * @return all areas 'real' areas
	 */
	public static List<Area> readAreas() {
		SQLiteDatabase db = mHelper.dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try {
			String query = "SELECT nome,parent,comune FROM aree WHERE " + "comune IS NOT NULL AND comune != ''";
			cursor = db.rawQuery(query, null);
			List<Area> result = new ArrayList<Area>();
			while (cursor.moveToNext()) {
				Area tmp = new Area();
				tmp.setNome(cursor.getString(cursor.getColumnIndex("nome")));
				tmp.setComune(cursor.getString(cursor.getColumnIndex("comune")));
				tmp.setParent(cursor.getString(cursor.getColumnIndex("parent")));
				result.add(tmp);
			}
			return result;
		} finally {
			if (cursor != null)
				cursor.close();
		}
	}

	/**
	 * @return 'gestore' of the user area
	 */
	public static Gestore getGestore() {
		SQLiteDatabase db = mHelper.dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try {
			String query = "SELECT * FROM gestori WHERE ragioneSociale IN (SELECT gestore FROM aree WHERE nome = '"
					+ mHelper.mProfile.getArea() + "')";
			cursor = db.rawQuery(query, null);
			cursor.moveToFirst();
			Gestore g = new Gestore();
			g.setRagioneSociale(cursor.getString(cursor.getColumnIndex("ragioneSociale")));
			g.setDescrizione(cursor.getString(cursor.getColumnIndex("descrizione")));
			g.setEmail(cursor.getString(cursor.getColumnIndex("email")));
			g.setTelefono(cursor.getString(cursor.getColumnIndex("telefono")));
			g.setFax(cursor.getString(cursor.getColumnIndex("fax")));
			g.setSitoWeb(cursor.getString(cursor.getColumnIndex("sitoWeb")));

			return g;
		} finally {
			if (cursor != null)
				cursor.close();
		}
	}

	/**
	 * @return 'gestore' of the user area
	 */
	public static Istituzione getIstituzione() {
		SQLiteDatabase db = mHelper.dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try {
			String query = "SELECT * FROM istituzioni WHERE nome IN (SELECT istituzione FROM aree WHERE nome = '"
					+ mHelper.mProfile.getArea() + "')";
			cursor = db.rawQuery(query, null);
			cursor.moveToFirst();
			Istituzione i = new Istituzione();
			i.setTipologia(cursor.getString(cursor.getColumnIndex("tipologia")));
			i.setNome(cursor.getString(cursor.getColumnIndex("nome")));
			i.setDescrizione(cursor.getString(cursor.getColumnIndex("descrizione")));
			i.setEmail(cursor.getString(cursor.getColumnIndex("email")));
			i.setTelefono(cursor.getString(cursor.getColumnIndex("telefono")));
			i.setPec(cursor.getString(cursor.getColumnIndex("pec")));
			i.setSitoIstituzionale(cursor.getString(cursor.getColumnIndex("sitoIstituzionale")));
			i.setFax(cursor.getString(cursor.getColumnIndex("fax")));
			return i;
		} finally {
			if (cursor != null)
				cursor.close();
		}

	}
}
