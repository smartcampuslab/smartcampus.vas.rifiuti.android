package eu.trentorise.smartcampus.rifiuti.model;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Profile implements Serializable {

	public enum Utenza {
		PRIVATO(0), TURISTA(1), STAGIONALE(2), AZIENDA(3);

		private int mIndex;

		private Utenza(int index) {
			this.mIndex = index;
		}

		public int value() {
			return mIndex;
		}
		
	}

	private static final long serialVersionUID = -2983888114579592139L;
	private static final String KEY_NAME = "name";
	private static final String KEY_UTENZA = "utenza";
	private static final String KEY_COMUNE = "comune";
	private static final String KEY_VIA = "via";
	private static final String KEY_CIVICO = "civico";
	private static final String KEY_AREA = "area";

	private String mName;
	private Utenza mUtenza;
	private String mComune;
	private String mVia;
	private String mNCivico;
	private String mArea;
	
	public Profile() {
	}

	public Profile(String mName, Utenza mUtenza, String mComune, String mVia,
			String mNCivico, String mArea) {
		super();
		this.mName = mName;
		this.mUtenza = mUtenza;
		this.mComune = mComune;
		this.mVia = mVia;
		this.mNCivico = mNCivico;
		this.mArea = mArea;
	}

	

	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put(KEY_NAME, mName);
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
			profile.setUtenza(Utenza.valueOf(json.getString(KEY_UTENZA)));
			profile.setComune(json.getString(KEY_COMUNE));
			profile.setVia(json.getString(KEY_VIA));
			profile.setNCivico(json.getString(KEY_CIVICO));
			profile.setArea(json.getString(KEY_AREA));
		} catch (JSONException e) {
			Log.e(Profile.class.getName(), e.toString());
			return null;
		}
		return profile;
	}
	
	
	
	@Override
	public String toString() {
		return mComune+","+mVia+","+mNCivico+" ,"+mArea+","+mUtenza;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		this.mName = name;
	}

	public Utenza getUtenza() {
		return mUtenza;
	}

	public void setUtenza(Utenza utenza) {
		this.mUtenza = utenza;
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
