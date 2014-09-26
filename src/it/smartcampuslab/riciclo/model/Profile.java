package it.smartcampuslab.riciclo.model;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Profile implements Serializable {

	private static final long serialVersionUID = -2983888114579592139L;
	private static final String KEY_NAME = "name";
	private static final String KEY_UTENZA = "utenza";
	private static final String KEY_PROFILO = "profilo";
	private static final String KEY_COMUNE = "comune";
	private static final String KEY_VIA = "via";
	private static final String KEY_CIVICO = "civico";
	private static final String KEY_AREA = "area";

	private String mName;
	private String mUtenza;
	private String mProfilo;
	private String mComune;
	private String mVia;
	private String mNCivico;
	private String mArea;

	public Profile() {
	}

	public Profile(String mName, String mProfilo, String mUtenza, String mComune, String mVia, String mNCivico, String mArea) {
		super();
		this.mName = mName;
		this.mProfilo = mProfilo;
		this.mUtenza = mUtenza;
		this.mComune = mComune;
		this.mVia = mVia;
		this.mNCivico = mNCivico;
		this.mArea = mArea;
	}

	public Profile(Profile from) {
		this.mName = from.getName();
		this.mProfilo = from.getProfilo();
		this.mUtenza = from.getUtenza();
		this.mComune = from.getComune();
		this.mVia = from.getVia();
		this.mNCivico = from.getNCivico();
		this.mArea = from.getArea();
	}

	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put(KEY_NAME, mName);
		json.put(KEY_PROFILO, mProfilo);
		json.put(KEY_UTENZA, mUtenza);
		json.put(KEY_COMUNE, mComune);
		json.put(KEY_VIA, mVia);
		json.put(KEY_CIVICO, mNCivico);
		json.put(KEY_AREA, mArea);
		return json;

	}

	public static Profile fromJSON(JSONObject json) {
		Profile profile = new Profile();
		try {
			profile.setName(json.getString(KEY_NAME));
			if (json.has(KEY_PROFILO)) {
				profile.setProfilo(json.getString(KEY_PROFILO));
			}
			profile.setUtenza(json.getString(KEY_UTENZA));
			profile.setComune(json.getString(KEY_COMUNE));
			if (json.has(KEY_VIA)) {
				profile.setVia(json.getString(KEY_VIA));
			}
			if (json.has(KEY_CIVICO)) {
				profile.setNCivico(json.getString(KEY_CIVICO));
			}
			profile.setArea(json.getString(KEY_AREA));
		} catch (JSONException e) {
			Log.e(Profile.class.getName(), e.toString());
			return null;
		}
		return profile;
	}

	@Override
	public String toString() {
		return (mProfilo == null ? "" : mProfilo + ": ") + mComune + (mVia == null ? "" : ", " + mVia)
				+ (mNCivico == null ? "" : ", " + mNCivico);
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		this.mName = name;
	}

	public String getUtenza() {
		return mUtenza;
	}

	public void setUtenza(String utenza) {
		this.mUtenza = utenza;
	}

	public String getProfilo() {
		return mProfilo;
	}

	public void setProfilo(String profilo) {
		this.mProfilo = profilo;
	}

	public String getComune() {
		return mComune;
	}

	public void setComune(String comune) {
		this.mComune = comune;
	}

	public String getVia() {
		return mVia;
	}

	public void setVia(String via) {
		this.mVia = via;
	}

	public String getNCivico() {
		return mNCivico;
	}

	public void setNCivico(String nCivico) {
		this.mNCivico = nCivico;
	}

	public String getArea() {
		return mArea;
	}

	public void setArea(String area) {
		mArea = area;
	}

}
