package eu.trentorise.smartcampus.rifiuti;

import org.json.JSONException;
import org.json.JSONObject;

import eu.trentorise.smartcampus.rifiuti.model.Profile;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class ProfileFragment extends Fragment {

	private TextView mTVNome, mTVComune, mTVIndirizzo, mTVArea, mTVUtenza;
	private EditText mETNome, mETComune, mETIndirizzo, mETArea, mETUtenza;
	private ViewSwitcher mVSNome, mVSComune, mVSIndirizzo, mVSArea, mVSUtenza;

	private Profile mProfile;

	private final static String PROFILE_JSON_STRING_KEY = "profile_json";

	public static ProfileFragment newIstance(Profile profile) throws Exception {
		ProfileFragment pf = new ProfileFragment();
		Bundle b = new Bundle();
		try {
			b.putString(PROFILE_JSON_STRING_KEY, profile.toJSON().toString());
		} catch (JSONException e) {
			throw new Exception("wrong profile" + profile.toString());
		}
		pf.setArguments(b);
		return pf;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_profile, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();
		initializeViews();

		try {
			if (getArguments() != null
					&& getArguments().containsKey(PROFILE_JSON_STRING_KEY)) {
				mProfile = Profile.fromJSON(new JSONObject(getArguments()
						.getString(PROFILE_JSON_STRING_KEY)));
				setContent();
			}
		} catch (JSONException e) {
			Log.e(ProfileFragment.class.getName(),e.toString());
		}
	}

	private void initializeViews() {

		mTVArea = (TextView) getView().findViewById(R.id.profile_area_tv);
		mTVComune = (TextView) getView().findViewById(R.id.profile_comune_tv);
		mTVNome = (TextView) getView().findViewById(R.id.profile_name_tv);
		mTVIndirizzo = (TextView) getView().findViewById(
				R.id.profile_indirizzo_tv);
		mTVUtenza = (TextView) getView().findViewById(R.id.profile_utenza_tv);

		mETArea = (EditText) getView().findViewById(R.id.profile_area_et);
		mETComune = (EditText) getView().findViewById(R.id.profile_comune_et);
		mETNome = (EditText) getView().findViewById(R.id.profile_name_et);
		mETIndirizzo = (EditText) getView().findViewById(
				R.id.profile_indirizzo_et);
		mETUtenza = (EditText) getView().findViewById(R.id.profile_utenza_et);

		mVSArea = (ViewSwitcher) getView().findViewById(R.id.profile_area_vs);
		mVSComune = (ViewSwitcher) getView().findViewById(
				R.id.profile_comune_vs);
		mVSNome = (ViewSwitcher) getView().findViewById(R.id.profile_name_vs);
		mVSIndirizzo = (ViewSwitcher) getView().findViewById(
				R.id.profile_indirizzo_vs);
		mVSUtenza = (ViewSwitcher) getView().findViewById(
				R.id.profile_utenza_vs);
	}

	private void setContent() {
		mTVArea.setText(mProfile.getArea());
		mTVComune.setText(mProfile.getComune());
		mTVIndirizzo.setText(mProfile.getVia()+", "+mProfile.getNCivico());
		mTVUtenza.setText(mProfile.getUtenza().toString());
	}

}
