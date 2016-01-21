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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import it.smartcampuslab.riciclo.R;
import it.smartcampuslab.riciclo.model.Area;
import it.smartcampuslab.riciclo.model.Calendario;
import it.smartcampuslab.riciclo.model.CalendarioItem;
import it.smartcampuslab.riciclo.model.DatiTipologiaRaccolta;
import it.smartcampuslab.riciclo.model.Gestore;
import it.smartcampuslab.riciclo.model.Istituzione;
import it.smartcampuslab.riciclo.model.Profile;
import it.smartcampuslab.riciclo.model.PuntoRaccolta;
import it.smartcampuslab.riciclo.model.SysProfile;
import it.smartcampuslab.riciclo.utils.LocationHelper;
import it.smartcampuslab.riciclo.utils.PreferenceUtils;

/**
 * @author raman
 */
@SuppressLint("DefaultLocale")
public class RifiutiHelper {

	private static final String UTENZA_NON_DOMESTICA = "utenza non domestica";
	private static final String UTENZA_DOMESTICA = "utenza domestica";

	public static final int DB_VERSION = 14;

	private static final String TUT_PREFS = "tutorial preference";

	private static final String FIRST_LAUNCH_MENU_PREFS = "first launch menu preference";
	private static final String FIRST_LAUNCH_HOME_PREFS = "first launch home preference";

	private static final String TOUR_PREFS = "tour preference";

	private static RifiutiHelper mHelper = null;

	private static String FIRST_LAUNCH_DOVEBUTTO_PREFS = "first launch dovebutto preference";

	protected Context mContext = null;
	private DBHelper dbHelper = null;
	private Profile mProfile = null;
	private List<String> mAreas = null;
	private List<String> mComuni = null;

	public static LocationHelper locationHelper = null;

	private Map<String, Integer> colorMap = null;
	private Map<String, Drawable> typeColorMap;
	private Map<String, Drawable> tipiRifiutoDrawablesMap = null;
	private Map<String, Integer> tipiPuntoMarkerMap = null;

	/**
	 * Initialize data access layer support
	 * 
	 * @param ctx
	 * @throws IOException
	 */
	public static void init(Context ctx) throws IOException {
		mHelper = new RifiutiHelper(ctx);
		NotesHelper.init(ctx);

		if (locationHelper == null) {
			locationHelper = new LocationHelper(ctx);
		}
		// TODO replace test data
		// setProfile(new Profile("test", "utenza domestica",
		// "Bleggio Superiore", "via Dante", "1", "Bleggio Superiore"));
	}

	public static void setProfile(Profile profile) {
		mHelper.mProfile = profile;
		mHelper.updateUserAreas();
	}

	public static Profile getProfile() {
		return mHelper.mProfile;
	}

	public static Collection<String> getComuni() {
		return mHelper.mComuni;
	}

	/**
	 * @param ctx
	 * @throws IOException
	 */
	private RifiutiHelper(Context ctx) throws IOException {
		super();
		this.mContext = ctx;
		dbHelper = DBHelper.createDataBase(ctx, DB_VERSION);
		dbHelper.close();
	};

	public static RifiutiHelper getInstance() {
		return mHelper;
	}

	public static DBHelper getDBHelper() {
		return mHelper.dbHelper;
	}

	/*
	 * Read SysProfiles
	 */
	public static List<SysProfile> readSysProfiles() {
		SQLiteDatabase db = mHelper.dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = db.rawQuery("SELECT * FROM profili", null);
			List<SysProfile> result = new ArrayList<SysProfile>();
			if (cursor != null) {
				cursor.moveToFirst();
				for (int i = 0; i < cursor.getCount(); i++) {
					SysProfile sysProfile = new SysProfile();
					sysProfile.setProfilo(cursor.getString(cursor.getColumnIndex("profilo")));
					sysProfile.setTipologiaUtenza(cursor.getString(cursor.getColumnIndex("tipologiaUtenza")));
					sysProfile.setDescrizione(cursor.getString(cursor.getColumnIndex("descrizione")));
					result.add(sysProfile);
					cursor.moveToNext();
				}
			}
			return result;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
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
			if (cursor != null) {
				cursor.close();
			}
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
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public static List<DatiTipologiaRaccolta> readTipologiaRaccoltaPerPuntoRaccolta(PuntoRaccolta puntoRaccolta) {
		SQLiteDatabase db = mHelper.dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try {
			String query = "SELECT DISTINCT tipologiaRaccolta, colore FROM raccolta WHERE area IN "
					+ getAreeForQuery(mHelper.mAreas) + " AND tipologiaUtenza = '" + mHelper.mProfile.getUtenza()
					+ "' AND tipologiaPuntoRaccolta = '" + puntoRaccolta.getTipologiaPuntiRaccolta() + "'";
			// filter per puntoRaccolta attributes
			String filter = "";
			if ((puntoRaccolta.isGettoniera() != null && !puntoRaccolta.isGettoniera())
					&& (puntoRaccolta.isResiduo() != null && !puntoRaccolta.isResiduo())) {
				filter += "tipologiaRaccolta != 'Residuo'";
			}
			if (puntoRaccolta.isImbCarta() != null && !puntoRaccolta.isImbCarta()) {
				if (filter.length() > 0)
					filter += " AND ";
				filter += "tipologiaRaccolta != 'Carta, cartone e cartoni per bevande'";
			}
			if (puntoRaccolta.isImbPlMet() != null && !puntoRaccolta.isImbPlMet()) {
				if (filter.length() > 0)
					filter += " AND ";
				filter += "tipologiaRaccolta != 'Imballaggi in plastica e metallo'";
			}
			if (puntoRaccolta.isImbVetro() != null && !puntoRaccolta.isImbVetro()) {
				if (filter.length() > 0)
					filter += " AND ";
				filter += "tipologiaRaccolta != 'Imballaggi in vetro'";
			}
			if (puntoRaccolta.isOrganico() != null && !puntoRaccolta.isOrganico()) {
				if (filter.length() > 0)
					filter += " AND ";
				filter += "tipologiaRaccolta != 'Organico'";
			}
			if (puntoRaccolta.isIndumenti() != null && !puntoRaccolta.isIndumenti()) {
				if (filter.length() > 0)
					filter += " AND ";
				filter += "tipologiaRaccolta != 'Indumenti usati'";
			}
			if (filter.length() > 0) {
				query += " AND (" + filter + ")";
			}
			cursor = db.rawQuery(query, null);
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
	 * @param preferredOnly
	 *            specify whether to show all or only the preferred for the user
	 *            comunity
	 * @return
	 * @throws Exception
	 */
	public static List<PuntoRaccolta> getPuntiRaccoltaPerTipoRaccolta(String tipoRaccolta, boolean preferredOnly)
			throws Exception {
		return getPuntiRaccoltaPerTipo(tipoRaccolta, null, preferredOnly);
	}

	/**
	 * Read all 'punti raccolta' that correspond to the specified 'tipo rifiuto'
	 * 
	 * @param tipoRifiuto
	 * @param preferredOnly
	 *            specify whether to show all or only the preferred for the user
	 *            comunity
	 * @return
	 * @throws Exception
	 */
	public static List<PuntoRaccolta> getPuntiRaccoltaPerTipoRifiuto(String tipoRifiuto, boolean preferredOnly)
			throws Exception {
		return getPuntiRaccoltaPerTipo(null, tipoRifiuto, preferredOnly);
	}

	private static List<PuntoRaccolta> getPuntiRaccoltaPerTipo(String tipoRaccolta, String tipoRifiuto, boolean preferredOnly)
			throws Exception {
		SQLiteDatabase db = mHelper.dbHelper.getReadableDatabase();
		Cursor cursor = null;
		// hook for special places
		boolean includeSpecial = false;
		// if (mHelper.mProfile.getUtenza().equals(UTENZA_OCCASIONALE) &&
		// ("residuo".equalsIgnoreCase(tipoRaccolta) ||
		// "residuo".equalsIgnoreCase(tipoRifiuto))) {
		// includeSpecial = true;
		// }

		try {
			String aree = getAreeForQuery(mHelper.mAreas);
			String query = "SELECT DISTINCT "
					+ "puntiRaccolta.area, "
					+ "puntiRaccolta.tipologiaPuntiRaccolta,"
					+ "puntiRaccolta.tipologiaUtenza,"
					+ "puntiRaccolta.localizzazione,"
					+ "puntiRaccolta.indirizzo,"
					+ "puntiRaccolta.dettaglioIndirizzo,"
					+ " puntiRaccolta.gettoniera, puntiRaccolta.residuo, puntiRaccolta.imbCarta, puntiRaccolta.imbPlMet, puntiRaccolta.organico, puntiRaccolta.imbVetro, puntiRaccolta.indumenti, note"
					+ " FROM puntiRaccolta "
					+ "	INNER JOIN raccolta ON puntiRaccolta.tipologiaPuntiRaccolta = raccolta.tipologiaPuntoRaccolta AND raccolta.tipologiaUtenza = puntiRaccolta.tipologiaUtenza "
					+ " WHERE puntiRaccolta.area IN "
					+ aree
					+ " AND raccolta.area IN "
					+ aree
					+ (preferredOnly ? (" AND (puntiRaccolta.indirizzo IN " + getAreeForQuery(mHelper.mComuni))
							+ (includeSpecial ? " OR puntiRaccolta.gettoniera = 'True'" : "")
							+ " OR puntiRaccolta.tipologiaPuntiRaccolta = 'CRM')" : "")
					+ " AND puntiRaccolta.tipologiaUtenza = '" + mHelper.mProfile.getUtenza() + "'";
			String selector = " AND "
					+ (tipoRaccolta != null ? ("raccolta.tipologiaRaccolta = '" + tipoRaccolta + "'")
							: ("raccolta.tipologiaRifiuto = '" + tipoRifiuto + "'"));
			query += selector;
			// filter per puntoRaccolta attributes
			query += createPuntoRaccoltaAttributeFilter();

			cursor = db.rawQuery(query, null);
			List<PuntoRaccolta> result = extractListFromCursor(cursor);
			return result;
		} finally {
			if (cursor != null)
				cursor.close();
		}
	}

	/**
	 * @return
	 */
	public static String createPuntoRaccoltaAttributeFilter() {
		return " AND (" + "(raccolta.tipologiaRaccolta != 'Residuo') OR "
				+ "(puntiRaccolta.residuo = 'True' AND puntiRaccolta.tipologiaUtenza='utenza domestica') OR "
				+ "(puntiRaccolta.gettoniera = 'True' AND puntiRaccolta.tipologiaUtenza='utenza occasionale') OR "
				+ "(puntiRaccolta.residuo = '')" + ")" + " AND ("
				+ "(raccolta.tipologiaRaccolta != 'Carta, cartone e cartoni per bevande') OR "
				+ "(puntiRaccolta.imbCarta = 'True') OR (puntiRaccolta.imbCarta = '')" + ")" + " AND ("
				+ "(raccolta.tipologiaRaccolta != 'Imballaggi in plastica e metallo') OR "
				+ "(puntiRaccolta.imbPlMet = 'True') OR (puntiRaccolta.imbPlMet = '')" + ")" + " AND ("
				+ "(raccolta.tipologiaRaccolta != 'Imballaggi in vetro') OR "
				+ "(puntiRaccolta.imbVetro = 'True') OR (puntiRaccolta.imbVetro = '')" + ")" + " AND ("
				+ "(raccolta.tipologiaRaccolta != 'Indumenti usati') OR "
				+ "(puntiRaccolta.indumenti = 'True') OR (puntiRaccolta.indumenti = '')" + ")" + " AND ("
				+ "(raccolta.tipologiaRaccolta != 'Organico') OR "
				+ "(puntiRaccolta.organico = 'True') OR (puntiRaccolta.organico = '')" + ")";

	}

	/**
	 * Read all 'punti raccolta' for the user profile.
	 * 
	 * @param preferredOnly
	 *            specify whether to show all or only the preferred for the user
	 *            comunity
	 * 
	 * @return
	 * @throws Exception
	 */
	public static List<PuntoRaccolta> getPuntiRaccolta(boolean preferredOnly) throws Exception {
		SQLiteDatabase db = mHelper.dbHelper.getReadableDatabase();
		boolean includeSpecial = false;// UTENZA_OCCASIONALE.equalsIgnoreCase(mHelper.mProfile.getUtenza());
		Cursor cursor = null;
		try {
			String aree = getAreeForQuery(mHelper.mAreas);
			String query = "SELECT DISTINCT "
					+ "area, "
					+ "tipologiaPuntiRaccolta,"
					+ "tipologiaUtenza,"
					+ "localizzazione,"
					+ "indirizzo, dettaglioIndirizzo,"
					+ " gettoniera, residuo, imbCarta, imbPlMet, organico, imbVetro, indumenti, note"
					+ " FROM puntiRaccolta "
					+ " WHERE puntiRaccolta.area IN "
					+ aree
					+ (preferredOnly ? (" AND (puntiRaccolta.indirizzo IN " + getAreeForQuery(mHelper.mComuni))
							+ (includeSpecial ? " OR puntiRaccolta.gettoniera = 'True'" : "")
							+ " OR puntiRaccolta.tipologiaPuntiRaccolta = 'CRM')" : "")
					+ " AND puntiRaccolta.tipologiaUtenza = \"" + mHelper.mProfile.getUtenza() + "\"";

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
				pr.setDettaglioIndirizzo(cursor.getString(cursor.getColumnIndex("dettaglioIndirizzo")));
				pr.setGettoniera(getBValue(cursor.getString(cursor.getColumnIndex("gettoniera"))));
				pr.setImbCarta(getBValue(cursor.getString(cursor.getColumnIndex("imbCarta"))));
				pr.setImbPlMet(getBValue(cursor.getString(cursor.getColumnIndex("imbPlMet"))));
				pr.setImbVetro(getBValue(cursor.getString(cursor.getColumnIndex("imbVetro"))));
				pr.setIndumenti(getBValue(cursor.getString(cursor.getColumnIndex("indumenti"))));
				pr.setOrganico(getBValue(cursor.getString(cursor.getColumnIndex("organico"))));
				pr.setResiduo(getBValue(cursor.getString(cursor.getColumnIndex("residuo"))));
				pr.setNote(cursor.getString(cursor.getColumnIndex("note")));
				result.add(pr);
				cursor.moveToNext();
			}
		}
		return result;
	}

	private static Boolean getBValue(String val) {
		if ("True".equals(val))
			return true;
		if ("False".equals(val))
			return false;
		return null;
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
			List<DatiTipologiaRaccolta> result = processDatiTipologiaRaccoltaCursor(cursor);
			return result;
		} finally {
			if (cursor != null)
				cursor.close();
		}

	}

	public static List<DatiTipologiaRaccolta> getDatiTipologiaRaccoltaPerTipologiaRaccolta(String tipoRaccolta) {
		SQLiteDatabase db = mHelper.dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try {
			String aree = getAreeForQuery(mHelper.mAreas);
			String query = "SELECT DISTINCT ra.tipologiaRaccolta, ra.tipologiaPuntoRaccolta, ra.colore, ra.infoRaccolta "
					+ "FROM raccolta ra WHERE " + "ra.tipologiaRaccolta = \"" + tipoRaccolta + "\" AND " + "ra.area in " + aree
					+ " AND ra.tipologiaUtenza = '" + mHelper.mProfile.getUtenza() + "'";
			cursor = db.rawQuery(query, null);
			List<DatiTipologiaRaccolta> result = processDatiTipologiaRaccoltaCursor(cursor);
			return result;
		} finally {
			if (cursor != null)
				cursor.close();
		}

	}

	public static List<DatiTipologiaRaccolta> getDatiTipologiaRaccoltaPerTipologiaRifiuto(String tipoRifiuto) {
		SQLiteDatabase db = mHelper.dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try {
			String aree = getAreeForQuery(mHelper.mAreas);
			String query = "SELECT DISTINCT ra.tipologiaRaccolta, ra.tipologiaPuntoRaccolta, ra.colore, ra.infoRaccolta "
					+ "FROM raccolta ra WHERE " + "ra.tipologiaRifiuto = \"" + tipoRifiuto + "\" AND " + "ra.area in " + aree
					+ " AND ra.tipologiaUtenza = '" + mHelper.mProfile.getUtenza() + "'";
			cursor = db.rawQuery(query, null);
			List<DatiTipologiaRaccolta> result = processDatiTipologiaRaccoltaCursor(cursor);
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
	public static List<DatiTipologiaRaccolta> processDatiTipologiaRaccoltaCursor(Cursor cursor) {
		List<DatiTipologiaRaccolta> result = new ArrayList<DatiTipologiaRaccolta>();
		if (cursor != null) {
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++) {
				DatiTipologiaRaccolta dtr = new DatiTipologiaRaccolta();
				dtr.setColore(cursor.getString(cursor.getColumnIndex("colore")));
				dtr.setInfo(cursor.getString(cursor.getColumnIndex("infoRaccolta")));
				dtr.setTipologiaPuntoRaccolta(cursor.getString(cursor.getColumnIndex("tipologiaPuntoRaccolta")));
				dtr.setTipologiaRaccolta(cursor.getString(cursor.getColumnIndex("tipologiaRaccolta")));
				result.add(dtr);
				cursor.moveToNext();
			}
		}
		return result;
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
			String date = Calendario.toDateStr(Calendar.getInstance());
			String query = "SELECT DISTINCT dataDa, dataA, il, dalle, alle FROM puntiRaccolta WHERE indirizzo = \""
					+ pr.getIndirizzo() + "\" AND tipologiaUtenza = \"" + pr.getTipologiaUtenza() + "\" AND area = \""
					+ pr.getArea() + "\"" + " AND (puntiRaccolta.dataDa IS NOT NULL AND puntiRaccolta.dataDa != '')"
					+ " AND (puntiRaccolta.dataA IS NOT NULL AND puntiRaccolta.dataA != '')"
					+ " AND (puntiRaccolta.il IS NOT NULL AND puntiRaccolta.il != '')" + " AND (puntiRaccolta.dataDa < '"
					+ date + "' AND puntiRaccolta.dataA > '" + date + "')";
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
	private void updateUserAreas() {
		String area = mProfile.getArea();
		SQLiteDatabase db = mHelper.dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try {
			List<String> areaResult = new ArrayList<String>();
			List<String> comuneResult = new ArrayList<String>();
			while (area != null && area.trim().length() > 0) {
				areaResult.add(area);
				String query = "SELECT parent,comune FROM aree WHERE nome = \"" + area + "\"";
				cursor = db.rawQuery(query, null);
				if (cursor != null) {
					cursor.moveToFirst();
					area = cursor.getString(0);
					String comune = cursor.getString(1);
					if (comune != null && comune.length() > 0) {
						comuneResult.add(comune);
					}
				}
			}
			mAreas = areaResult;
			mComuni = comuneResult;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public static String getUserAreas(Profile profile) {
		String area = profile.getArea();
		List<String> areaResult = new ArrayList<String>();
		SQLiteDatabase db = mHelper.dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try {
			while (area != null && area.trim().length() > 0) {
				areaResult.add(area);
				String query = "SELECT parent,comune FROM aree WHERE nome = \"" + area + "\"";
				cursor = db.rawQuery(query, null);
				if (cursor != null) {
					cursor.moveToFirst();
					area = cursor.getString(0);
				}
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return getAreeForQuery(areaResult);
	}

	public static List<List<CalendarioItem>> getCalendarsForMonth(Calendar ref) {
		return getCalendarsForMonth(ref, null, null);
	}

	/**
	 * Get calendar records for the month defined by the specified date
	 * 
	 * @param ref
	 * @return
	 */
	public static List<List<CalendarioItem>> getCalendarsForMonth(Calendar ref, Profile profile, String aree) {
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
			if (profile == null) {
				profile = mHelper.mProfile;
			}
			if (aree == null) {
				aree = getAreeForQuery(mHelper.mAreas);
			}
			String query = "SELECT DISTINCT "
					+ "puntiRaccolta.*, raccolta.colore FROM puntiRaccolta "
					+ "	INNER JOIN raccolta ON puntiRaccolta.tipologiaPuntiRaccolta = raccolta.tipologiaPuntoRaccolta AND raccolta.tipologiaUtenza = puntiRaccolta.tipologiaUtenza "
					+ " WHERE (puntiRaccolta.dataDa IS NOT NULL AND puntiRaccolta.dataDa != '')"
					+ " AND (puntiRaccolta.dataA IS NOT NULL AND puntiRaccolta.dataA != '')"
					+ " AND (puntiRaccolta.il IS NOT NULL AND puntiRaccolta.il != '')" + " AND puntiRaccolta.area IN " + aree
					+ " AND raccolta.area IN " + aree + " AND puntiRaccolta.tipologiaUtenza = \"" + profile.getUtenza() + "\"";
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
					pr.setDettaglioIndirizzo(cursor.getString(cursor.getColumnIndex("dettaglioIndirizzo")));
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
			if (cursor != null) {
				cursor.close();
			}
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
			String query = "SELECT nome,parent,localita FROM aree WHERE nome LIKE \"%" + comune + "%\"";
			cursor = db.rawQuery(query, null);
			while (cursor.moveToNext()) {
				Area tmp = new Area();
				tmp.setNome(cursor.getString(cursor.getColumnIndex("nome")));
				tmp.setLocalita(cursor.getString(cursor.getColumnIndex("localita")));
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
		if (areas == null) {
			return null;
		}

		String aree = "(";
		for (String area : areas) {
			aree += "'" + area + "',";
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

	private Map<String, Drawable> getTypeColorMap(Context ctx) {
		if (typeColorMap == null) {
			typeColorMap = new HashMap<String, Drawable>();
			String[] array = ctx.getResources().getStringArray(R.array.type_icon_color_strings);
			TypedArray valueArray = ctx.getResources().obtainTypedArray(R.array.type_icon_color_drawables);
			for (int i = 0; i < array.length; i++) {
				typeColorMap.put(array[i].toLowerCase(), valueArray.getDrawable(i));
			}
			valueArray.recycle();
		}
		return typeColorMap;
	}

	public static int getColorResource(Context ctx, String color) {
		return mHelper.getColorMap(ctx).get(color);
	}

	public static Drawable getTypeColorResource(Context ctx, String type, String color) {
		Drawable d = mHelper.getTypeColorMap(ctx).get((type + " " + color).toLowerCase());
		if (d == null)
			d = mHelper.getTypeColorMap(ctx).get(type.toLowerCase());
		return d;
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

	private Map<String, Integer> getTipiPuntoMarkerMap(Context ctx) {
		if (tipiPuntoMarkerMap == null) {
			tipiPuntoMarkerMap = new HashMap<String, Integer>();
			String[] array = ctx.getResources().getStringArray(R.array.type_marker_strings);
			TypedArray valueArray = ctx.getResources().obtainTypedArray(R.array.type_marker_drawables);
			for (int i = 0; i < array.length; i++) {
				tipiPuntoMarkerMap.put(array[i].toLowerCase(Locale.getDefault()), valueArray.getResourceId(i, 0));
			}
			valueArray.recycle();
		}
		return tipiPuntoMarkerMap;
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
			String query = "SELECT nome,parent,localita FROM aree WHERE nome = '" + string + "'";
			cursor = db.rawQuery(query, null);
			cursor.moveToFirst();
			Area tmp = new Area();
			tmp.setNome(cursor.getString(cursor.getColumnIndex("nome")));
			tmp.setLocalita(cursor.getString(cursor.getColumnIndex("localita")));
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

			String query = "SELECT nome,parent,localita FROM aree " + "WHERE " + "localita IS NOT NULL AND localita != '' "
					+ "ORDER BY localita";
			cursor = db.rawQuery(query, null);
			List<Area> result = new ArrayList<Area>();
			while (cursor.moveToNext()) {
				Area tmp = new Area();
				tmp.setNome(cursor.getString(cursor.getColumnIndex("nome")));
				tmp.setLocalita(cursor.getString(cursor.getColumnIndex("localita")));
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
	public static List<Gestore> getGestori() {
		SQLiteDatabase db = mHelper.dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try {
			String query = "SELECT * FROM gestori WHERE ragioneSociale IN (SELECT gestore FROM aree WHERE nome = '"
					+ mHelper.mProfile.getArea() + "')";
			cursor = db.rawQuery(query, null);
			List<Gestore> result = new ArrayList<Gestore>();
			while (cursor.moveToNext()) {
				Gestore g = new Gestore();
				g.setRagioneSociale(cursor.getString(cursor.getColumnIndex("ragioneSociale")));
				g.setIndirizzo(cursor.getString(cursor.getColumnIndex("indirizzo")));
				g.setUfficio(cursor.getString(cursor.getColumnIndex("ufficio")));
				g.setOrarioUfficio(cursor.getString(cursor.getColumnIndex("orarioUfficio")));
				g.setDescrizione(cursor.getString(cursor.getColumnIndex("descrizione")));
				g.setEmail(cursor.getString(cursor.getColumnIndex("email")));
				g.setTelefono(cursor.getString(cursor.getColumnIndex("telefono")));
				g.setFax(cursor.getString(cursor.getColumnIndex("fax")));
				g.setSitoWeb(cursor.getString(cursor.getColumnIndex("sitoWeb")));
				result.add(g);
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
	public static List<Istituzione> getIstituzioni() {
		SQLiteDatabase db = mHelper.dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try {
			String query = "SELECT * FROM istituzioni WHERE nome IN (SELECT istituzione FROM aree WHERE nome = '"
					+ mHelper.mProfile.getArea() + "')";
			cursor = db.rawQuery(query, null);
			List<Istituzione> result = new ArrayList<Istituzione>();
			while (cursor.moveToNext()) {
				Istituzione i = new Istituzione();
				i.setTipologia(cursor.getString(cursor.getColumnIndex("tipologia")));
				i.setNome(cursor.getString(cursor.getColumnIndex("nome")));
				i.setDescrizione(cursor.getString(cursor.getColumnIndex("descrizione")));
				i.setIndirizzo(cursor.getString(cursor.getColumnIndex("indirizzo")));
				i.setUfficio(cursor.getString(cursor.getColumnIndex("ufficio")));
				i.setOrarioUfficio(cursor.getString(cursor.getColumnIndex("orarioUfficio")));
				i.setEmail(cursor.getString(cursor.getColumnIndex("email")));
				i.setTelefono(cursor.getString(cursor.getColumnIndex("telefono")));
				i.setPec(cursor.getString(cursor.getColumnIndex("pec")));
				i.setSitoIstituzionale(cursor.getString(cursor.getColumnIndex("sitoIstituzionale")));
				i.setFax(cursor.getString(cursor.getColumnIndex("fax")));
				result.add(i);
			}
			return result;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

	}

	/**
	 * This method converts dp unit to equivalent pixels, depending on device
	 * density.
	 * 
	 * @param dp
	 *            A value in dp (density independent pixels) unit. Which we need
	 *            to convert into pixels
	 * @param context
	 *            Context to get resources and device specific display metrics
	 * @return A float value to represent px equivalent to dp depending on
	 *         device density
	 */
	public static int convertDpToPixel(float dp, Context context) {
		// Resources resources = context.getResources();
		// DisplayMetrics metrics = resources.getDisplayMetrics();
		// float px = dp * (metrics.densityDpi / 160f);
		// return px;
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
	}

	/**
	 * This method converts device specific pixels to density independent
	 * pixels.
	 * 
	 * @param px
	 *            A value in px (pixels) unit. Which we need to convert into db
	 * @param context
	 *            Context to get resources and device specific display metrics
	 * @return A float value to represent dp equivalent to px value
	 */
	public static float convertPixelsToDp(float px, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float dp = px / (metrics.densityDpi / 160f);
		return dp;
	}

	public static SharedPreferences getTutorialPreferences(Context ctx) {
		SharedPreferences out = ctx.getSharedPreferences(TUT_PREFS, Context.MODE_PRIVATE);
		return out;
	}

	public static boolean isFirstLaunchMenu(Context ctx) {
		return getTutorialPreferences(ctx).getBoolean(FIRST_LAUNCH_MENU_PREFS, true);
	}

	public static void disableFirstLaunchMenu(Context ctx) {
		Editor edit = getTutorialPreferences(ctx).edit();
		edit.putBoolean(FIRST_LAUNCH_MENU_PREFS, false);
		edit.commit();
	}

	public static boolean isFirstLaunchHome(Context ctx) {
		return getTutorialPreferences(ctx).getBoolean(FIRST_LAUNCH_HOME_PREFS, true);
	}

	public static void disableFirstLaunchHome(Context ctx) {
		Editor edit = getTutorialPreferences(ctx).edit();
		edit.putBoolean(FIRST_LAUNCH_HOME_PREFS, false);
		edit.commit();
	}

	public static void resetTutorialDoveLoButto(Context ctx) {
		Editor edit = getTutorialPreferences(ctx).edit();
		edit.putBoolean(FIRST_LAUNCH_DOVEBUTTO_PREFS, true);
		edit.commit();
	}

	public static boolean isFirstLaunchDoveLoButto(Context ctx) {
		return getTutorialPreferences(ctx).getBoolean(FIRST_LAUNCH_DOVEBUTTO_PREFS, true);
	}

	public static void disableFirstLaunchDoveLoButto(Context ctx) {
		Editor edit = getTutorialPreferences(ctx).edit();
		edit.putBoolean(FIRST_LAUNCH_DOVEBUTTO_PREFS, false);
		edit.commit();
	}

	public static void setWantTour(Context ctx, boolean want) {
		Editor edit = getTutorialPreferences(ctx).edit();
		edit.putBoolean(TOUR_PREFS, want);
		edit.commit();
	}

	/**
	 * @param item
	 * @return
	 */
	public static int getMarkerIcon(PuntoRaccolta item) {
		return mHelper.getTipiPuntoMarkerMap(mHelper.mContext).get(
				item.getTipologiaPuntiRaccolta().toLowerCase(Locale.getDefault()));
	}

	/**
	 * Method to avoid a bug on pre ICS
	 * 
	 * @see https://code.google.com/p/android/issues/detail?id=27112
	 */
	public static SparseBooleanArray copySparseBooleanArray(SparseBooleanArray sba) {
		SparseBooleanArray out = new SparseBooleanArray(sba.size());
		for (int i = 0; i < sba.size(); i++) {
			out.append(sba.keyAt(i), sba.valueAt(i));
		}
		return out;
	}

	private enum WeekDay {
		LUN, MAR, MER, GIO, VEN, SAB, DOM
	};

	@SuppressLint("DefaultLocale")
	public static Comparator<Calendario> calendarioComparator = new Comparator<Calendario>() {
		@Override
		public int compare(Calendario c1, Calendario c2) {
			int result = 0;

			WeekDay c1day = WeekDay.valueOf(c1.getIl().toUpperCase(Locale.ITALY).subSequence(0, 3).toString());
			WeekDay c2day = WeekDay.valueOf(c2.getIl().toUpperCase(Locale.ITALY).subSequence(0, 3).toString());

			result = c1day.compareTo(c2day);

			if (result == 0) {
				Calendar c1cal = Calendar.getInstance();
				Calendar c2cal = (Calendar) c1cal.clone();

				String[] c1time = c1.getDalle().split(":");
				c1cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(c1time[0]));
				c1cal.set(Calendar.MINUTE, Integer.parseInt(c1time[1]));
				String[] c2time = c2.getDalle().split(":");
				c2cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(c2time[0]));
				c2cal.set(Calendar.MINUTE, Integer.parseInt(c2time[1]));
				result = c1cal.compareTo(c2cal);
			}

			return result;
		}
	};

	/**
	 * @param tipoUtenza
	 * @return
	 */
	public static List<Area> readAreasForTipoUtenza(String tipoUtenza) {
		// TODO correct this to take into account really used areas from
		// puntiraccolta
		SQLiteDatabase db = mHelper.dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try {
			String cond = UTENZA_DOMESTICA.equalsIgnoreCase(tipoUtenza) ? "AND utenzaDomestica = 'True'" : UTENZA_NON_DOMESTICA
					.equalsIgnoreCase(tipoUtenza) ? "AND utenzaNonDomestica = 'True'" : "AND utenzaOccasionale = 'True'";

			String query = "SELECT nome,parent,localita FROM aree " + "WHERE localita IS NOT NULL AND localita != '' " + cond
					+ "ORDER BY localita";
			cursor = db.rawQuery(query, null);
			List<Area> result = new ArrayList<Area>();
			while (cursor.moveToNext()) {
				Area tmp = new Area();
				tmp.setNome(cursor.getString(cursor.getColumnIndex("nome")));
				tmp.setLocalita(cursor.getString(cursor.getColumnIndex("localita")));
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
	 * Check for each profile that the profile area exist. If it doesn't, 
	 * search for area mapping in special array and update. If not found, remove profile.
	 * @param list 
	 * @param mainActivity
	 */
	public static void validateProfiles(Context ctx, List<Profile> list) {
		SQLiteDatabase db = mHelper.dbHelper.getReadableDatabase();
		String[] removedAreas = ctx.getResources().getStringArray(R.array.removed_areas);
		String[] newAreas = ctx.getResources().getStringArray(R.array.new_areas);
		Map<String,String> areas = new HashMap<String, String>(removedAreas.length);
		for (int i = 0; i < removedAreas.length; i++) {
			String old = removedAreas[i];
			areas.put(old, newAreas[i]);
		}
		boolean hasChanges = false;
		Cursor cursor = null;
		try {
			for (Iterator<Profile> iterator = list.iterator(); iterator.hasNext();) {
				Profile p = iterator.next();
				if (areas.get(p.getArea()) != null) {
					p.setArea(areas.get(p.getArea()));
					hasChanges = true;
					String query = "SELECT comune FROM aree WHERE nome = \"" + p.getArea() + "\"";
					cursor = db.rawQuery(query, null);
					if (cursor.moveToFirst()) {
						p.setComune(cursor.getString(0));
					}
					continue;
				}
				String query = "SELECT nome FROM aree WHERE nome = \"" + p.getArea() + "\"";
				cursor = db.rawQuery(query, null);
				if (cursor.getCount() <= 0) {
					iterator.remove();
					hasChanges = true;
				}
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		if (hasChanges) {
			PreferenceUtils.updateProfiles(ctx, list);
		}
	}

}
