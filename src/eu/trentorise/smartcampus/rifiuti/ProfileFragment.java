package eu.trentorise.smartcampus.rifiuti;

import org.json.JSONException;

import eu.trentorise.smartcampus.rifiuti.model.Profile;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class ProfileFragment extends Fragment {


	private final static String PROFILE_JSON_STRING_KEY ="profile_json";

	public static ProfileFragment newIstance(Profile profile) throws Exception {
		ProfileFragment pf = new ProfileFragment();
		Bundle b = new Bundle();
		try {
			b.putString(PROFILE_JSON_STRING_KEY , profile.toJSON().toString());
		} catch (JSONException e) {
			throw new Exception("wrong profile"+profile.toString());
		}
		pf.setArguments(b);
		return pf;
	}

}
