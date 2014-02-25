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

	// key for the profile's preferences (the container)
	private final static String PROFILE_PREF_KEY = "profile";

	private final static String PROFILE_IN_USE_INDEX_KEY = "index_prof_in_use";
	private final static String ALL_PROFILES_KEY = "profiles";

	private static SharedPreferences getSharedPreferences(Context ctx,
			String pref_key) {
		return ctx.getSharedPreferences(pref_key, Context.MODE_PRIVATE);
	}

	private static SharedPreferences getProfilePreference(Context ctx) {
		return getSharedPreferences(ctx, PROFILE_PREF_KEY);
	}

	/**
	 * 
	 * @param all
	 *            stored profiles' names
	 * @param ctx 
	 * @return the requested profile, null otherwise
	 */
	public static Profile getProfile(Context ctx, int position) {
		SharedPreferences sp = getProfilePreference(ctx);
		String jsonArrString = sp.getString(ALL_PROFILES_KEY, null);
		try {
			JSONArray jsonArr = new JSONArray(jsonArrString);
			return Profile.fromJSON(jsonArr.getJSONObject(position));
		} catch (Exception e) {
			Log.e(Profile.class.getName(), e.toString());
			Log.e(Profile.class.getName(), "wrong position");
		}
		return null;
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
		} catch (NullPointerException e) {
			Log.e(Profile.class.getName(), e.toString());
			Log.e(Profile.class.getName(),
					"Non ci sono profili, dovrebbe essercene sempre almeno uno!");
		}

		return out;
	}

	/**
	 * 
	 * @param ctx
	 *            the Context of the app
	 * @return profile in use position, -1 otherwise
	 */
	public static int getCurrentProfilePosition(Context ctx) {
		SharedPreferences sp = getProfilePreference(ctx);
		return sp.getInt(PROFILE_IN_USE_INDEX_KEY, -1);
	}

	/**
	 * 
	 * @param ctx
	 * @param position
	 *            of the profile inside the list
	 * @param saveProfile
	 *            true if you want to store a copy of the whole profile
	 * @throws Exception
	 *             if the position is wrong
	 */
	public static void setCurrentProfilePosition(Context ctx, int position)
			throws Exception {
		SharedPreferences sp = getProfilePreference(ctx);
		sp.edit().putInt(PROFILE_IN_USE_INDEX_KEY, position).commit();
	}

	/**
	 * 
	 * @param ctx
	 * @param p he profile that must be added
	 * @throws JSONException
	 * @throws Exception
	 */
	public static void addProfile(Context ctx, Profile p) throws JSONException {
		List<Profile> profiles = getProfiles(ctx);
		profiles.add(p);
		JSONArray jsonArr = new JSONArray();
		for (Profile tmp : profiles) {
			jsonArr.put(tmp.toJSON());
		}
		SharedPreferences sp = getProfilePreference(ctx);
		sp.edit().putString(ALL_PROFILES_KEY, jsonArr.toString()).commit();
	}
}
