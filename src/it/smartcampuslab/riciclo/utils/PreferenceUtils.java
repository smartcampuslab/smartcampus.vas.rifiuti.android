package it.smartcampuslab.riciclo.utils;

import it.smartcampuslab.riciclo.data.NotesHelper;
import it.smartcampuslab.riciclo.model.Profile;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class PreferenceUtils {

	// key for the profile's preferences (the container)
	private final static String PROFILE_PREF_KEY = "profile";

	private final static String PROFILE_IN_USE_INDEX_KEY = "index_prof_in_use";
	private final static String ALL_PROFILES_KEY = "profiles";

	private static SharedPreferences getSharedPreferences(Context ctx, String pref_key) {
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
			Log.e(PreferenceUtils.class.getName(), e.toString());
			Log.e(PreferenceUtils.class.getName(), "wrong position");
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
			Log.e(PreferenceUtils.class.getName(), e.toString());
		} catch (NullPointerException e) {
			Log.e(PreferenceUtils.class.getName(), e.toString());
			Log.e(PreferenceUtils.class.getName(), "Non ci sono profili, dovrebbe essercene sempre almeno uno!");
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
	public static void setCurrentProfilePosition(Context ctx, int position) {
		SharedPreferences sp = getProfilePreference(ctx);
		sp.edit().putInt(PROFILE_IN_USE_INDEX_KEY, position).commit();
	}

	/**
	 * 
	 * @param ctx
	 * @param p
	 *            he profile that must be added
	 * @throws JSONException
	 * @throws ProfileNameExists
	 * @throws Exception
	 */
	public static int addProfile(Context ctx, Profile p) throws JSONException, ProfileNameExistsException {
		List<Profile> profiles = getProfiles(ctx);
		if (profilePosition(p, profiles) < 0) {
			profiles.add(p);
		} else {
			throw new ProfileNameExistsException();
		}
		JSONArray jsonArr = new JSONArray();
		for (Profile tmp : profiles) {
			jsonArr.put(tmp.toJSON());
		}
		SharedPreferences sp = getProfilePreference(ctx);
		sp.edit().putString(ALL_PROFILES_KEY, jsonArr.toString()).commit();
		return profiles.size() - 1;
	}

	/**
	 * 
	 * @param ctx
	 * @param position
	 *            of the profile that should be removed
	 * @throws Exception
	 *             if there's just one profile
	 */
	public static void removeProfile(Context ctx, int position) throws Exception {
		List<Profile> profiles = getProfiles(ctx);
		Profile p = getProfile(ctx, position);
		if (profiles.size() > 1) {
			int curr = getCurrentProfilePosition(ctx);
			if (position < curr) {
				setCurrentProfilePosition(ctx, curr - 1);
			}
			profiles.remove(position);
			JSONArray jsonArr = new JSONArray();
			try {
				for (Profile tmp : profiles) {
					jsonArr.put(tmp.toJSON());
				}
			} catch (JSONException e) {
				Log.w(PreferenceUtils.class.getName(), e.toString());
			}
			SharedPreferences sp = getProfilePreference(ctx);
			sp.edit().putString(ALL_PROFILES_KEY, jsonArr.toString()).commit();
			NotesHelper.deleteNotes(p.getName());

		} else {
			throw new LastElementException();
		}
	}

	/**
	 * Update all the profiles in one shot
	 * @param newProfiles
	 */
	public static void updateProfiles(Context ctx, List<Profile> newProfiles) {
		JSONArray jsonArr = new JSONArray();
		try {
			for (Profile tmp : newProfiles) {
				jsonArr.put(tmp.toJSON());
			}
		} catch (JSONException e) {
			Log.w(PreferenceUtils.class.getName(), e.toString());
		}
		SharedPreferences sp = getProfilePreference(ctx);
		sp.edit().putString(ALL_PROFILES_KEY, jsonArr.toString()).commit();
	}
	
	/**
	 * 
	 * @param ctx
	 * @param position
	 *            of the profile that should be updated
	 * @throws ProfileNameExistsException
	 */
	public static void editProfile(Context ctx, int position, Profile newProfile, Profile oldProfile)
			throws ProfileNameExistsException {
		List<Profile> profiles = getProfiles(ctx);
		int oldPos = profilePosition(newProfile, profiles);
		if (oldPos == position || oldPos < 0) {
			profiles.set(position, newProfile);
		} else {
			throw new ProfileNameExistsException();
		}
		JSONArray jsonArr = new JSONArray();
		try {
			for (Profile tmp : profiles) {
				jsonArr.put(tmp.toJSON());
			}
		} catch (JSONException e) {
			Log.w(PreferenceUtils.class.getName(), e.toString());
		}
		SharedPreferences sp = getProfilePreference(ctx);
		sp.edit().putString(ALL_PROFILES_KEY, jsonArr.toString()).commit();
	}

	private static boolean isDifferent(Profile oldProfile, Profile newProfile) {
		if (!oldProfile.getComune().equals(newProfile.getComune())) {
			return true;
		}
		if (!oldProfile.getArea().equals(newProfile.getArea())) {
			return true;
		}
		if (!oldProfile.getName().equals(newProfile.getName())) {
			return true;
		}
		// if (!oldProfile.getNCivico().equals(newProfile.getNCivico())) {
		// return true;
		// }
		// if (!oldProfile.getVia().equals(newProfile.getVia())) {
		// return true;
		// }
		if (!oldProfile.getProfilo().equals(newProfile.getProfilo())) {
			return true;
		}
		if (!oldProfile.getUtenza().equals(newProfile.getUtenza())) {
			return true;
		}
		return false;
	}

	private static int profilePosition(Profile p, List<Profile> profiles) {
		int i = 0;
		for (Profile cmp : profiles) {
			if (p.getName().trim().toLowerCase(Locale.getDefault())
					.equals(cmp.getName().trim().toLowerCase(Locale.getDefault()))) {
				return i;
			}
			i++;
		}
		return -1;
	}

	public static class ProfileNameExistsException extends Exception {
		private static final long serialVersionUID = 1L;
	};

	public static class LastElementException extends Exception {
		private static final long serialVersionUID = 1L;
	};
}
