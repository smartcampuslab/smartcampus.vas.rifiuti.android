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
	
	//key for the profile's preferences (the container)
	private final static String PROFILE_PREF_KEY = "profile";
	
	private final static String PROFILE_IN_USE_KEY = "in use";
	private final static String ALL_PROFILES_KEY = "profiles";

	private static SharedPreferences getSharedPreferences(Context ctx, String pref_key) {
		return ctx.getSharedPreferences(pref_key, Context.MODE_PRIVATE);
	}
	
	private static SharedPreferences getProfilePreference(Context ctx){
		return getSharedPreferences(ctx, PROFILE_PREF_KEY);
	}
	
	/**
	 * 
	 * @param ctx
	 * @return all stored profiles
	 */
	public static List<Profile> getProfiles(Context ctx) {
		SharedPreferences sp = getProfilePreference(ctx);
		String jsonArrString = sp.getString(ALL_PROFILES_KEY, null);
		List<Profile> out = new ArrayList<Profile>();
		JSONArray jsonArr;
		try {
			jsonArr = new JSONArray(jsonArrString);
			for (int i = 0; i < jsonArr.length(); i++) {
				out.add(Profile.fromJSON(jsonArr.getJSONObject(i)));
			}
			return out;
		} catch (JSONException e) {
			Log.e(Profile.class.getName(), e.toString());
		}catch (NullPointerException e) {
			Log.e(Profile.class.getName(), e.toString());
			Log.e(Profile.class.getName(), "Non ci sono profili, dovrebbe essercene sempre almeno uno!");
		}

		return out;
	}

	/**
	 * 
	 * @param ctx
	 *            the Context of the app
	 * @return the profile in use, null otherwise
	 */
	public static Profile getProfileInUse(Context ctx) {
		SharedPreferences sp = getProfilePreference(ctx);
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
	
	/**
	 * 
	 * @param ctx
	 * @param position of the choosen profile
	 * @throws Exception wrong position
	 */
	public static void setProfileInUse(Context ctx, int position) throws Exception {
		List<Profile> profiles = getProfiles(ctx);
		try {
			SharedPreferences sp = getProfilePreference(ctx);
			Editor editor = sp.edit();
				editor.putString(PROFILE_IN_USE_KEY, profiles.get(position)
						.toJSON().toString());
			editor.commit();
		} catch (JSONException e) {
			Log.e(Profile.class.getName(), e.toString());
			throw new Exception("wrong position");
		}
	}
	
	/**
	 * 
	 * @param ctx
	 * @param p the profile that must be added
	 * @throws Exception
	 */
	public static void addProfile(Context ctx, Profile p) throws Exception {
		List<Profile> profiles = getProfiles(ctx);
		profiles.add(p);
		JSONArray jsonArr = new JSONArray();
		for(Profile tmp : profiles){
			jsonArr.put(tmp.toJSON());
		}
		SharedPreferences sp = getProfilePreference(ctx);
		sp.edit().putString(ALL_PROFILES_KEY, jsonArr.toString()).commit();
	}
}
