package eu.trentorise.smartcampus.rifiuti.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import eu.trentorise.smartcampus.rifiuti.model.Profile;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

public class PreferenceUtils {

	private final static String PROFILE_PREF_KEY = "profile";
	private final static String PROFILE_IN_USE_KEY = "in use";
	private final static String ALL_PROFILES_KEY = "profiles";

	private SharedPreferences getSharedPreferences(Context ctx, String pref_key) {
		return ctx.getSharedPreferences(pref_key, Context.MODE_PRIVATE);
	}

	public List<Profile> getProfiles(Context ctx) {
		SharedPreferences sp = getSharedPreferences(ctx, PROFILE_PREF_KEY);
		String jsonArrString = sp.getString(ALL_PROFILES_KEY, null);
		JSONArray jsonArr;
		try {
			jsonArr = new JSONArray(jsonArrString);
			List<Profile> out = new ArrayList<Profile>();
			for (int i = 0; i < jsonArr.length(); i++) {
				out.add(Profile.fromJSON(jsonArr.getJSONObject(i)));
			}
			return out;
		} catch (JSONException e) {
			Log.e(Profile.class.getName(), e.toString());
		}

		return null;
	}

	/**
	 * 
	 * @param ctx
	 *            the Context of the app
	 * @return the profile in use, null otherwise
	 */
	public Profile getProfileInUse(Context ctx) {
		SharedPreferences sp = getSharedPreferences(ctx, PROFILE_PREF_KEY);
		String jsonString = sp.getString(PROFILE_IN_USE_KEY, null);
		if (jsonString != null) {
			JSONObject obj;
			try {
				obj = new JSONObject(jsonString);
				return Profile.fromJSON(obj);
			} catch (JSONException e) {
				Log.e(Profile.class.getName(), e.toString());
			}
		}
		return null;
	}

	public void useProfile(Context ctx, int position) throws Exception {
		List<Profile> profiles = getProfiles(ctx);
		try {
			SharedPreferences sp = getSharedPreferences(ctx, PROFILE_PREF_KEY);
			Editor editor = sp.edit();
			if (profiles != null) {
				editor.putString(PROFILE_IN_USE_KEY, profiles.get(position)
						.toJSON().toString());
			} else {
				throw new Exception("wrong position");
			}
			editor.commit();
		} catch (JSONException e) {
			Log.e(Profile.class.getName(), e.toString());
			throw new Exception("wrong position");
		}
	}
	
	public void addProfile(Context ctx, int position) throws Exception {
		throw new Exception("Not implemented");
	}
}
