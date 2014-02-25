package eu.trentorise.smartcampus.rifiuti;

import java.security.InvalidAlgorithmParameterException;

import org.json.JSONException;
import org.json.JSONObject;

import eu.trentorise.smartcampus.rifiuti.model.Profile;
import eu.trentorise.smartcampus.rifiuti.model.Profile.Utenza;
import eu.trentorise.smartcampus.rifiuti.utils.PreferenceUtils;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class ProfileFragment extends Fragment {

	private enum MODE {
		VIEW, EDIT
	}

	private TextView mTVNome, mTVComune, mTVVia, mTVNCiv, mTVArea, mTVUtenza;
	private EditText mETNome, mETComune, mETVia, mETNCiv, mETArea, mETUtenza;
	private ViewSwitcher mVSNome, mVSComune, mVSVia, mVSNCiv, mVSArea,
			mVSUtenza;

	private Profile mProfile;
	private MODE mActiveMode;

	private final static String PROFILE_INDEX_KEY = "profile_index";

	public static ProfileFragment newIstance(int position) {
		ProfileFragment pf = new ProfileFragment();
		Bundle b = new Bundle();
		b.putInt(PROFILE_INDEX_KEY, position);
		pf.setArguments(b);
		return pf;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		mActiveMode = MODE.VIEW;
		((ActionBarActivity) getActivity()).getSupportActionBar()
				.setHomeButtonEnabled(true);
		((ActionBarActivity) getActivity()).getSupportActionBar()
				.setDisplayHomeAsUpEnabled(true);
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

		if (getArguments() != null
				&& getArguments().containsKey(PROFILE_INDEX_KEY)) {
			mProfile = PreferenceUtils.getProfile(getActivity(), getArguments()
					.getInt(PROFILE_INDEX_KEY));
			if (mProfile != null)
				setContent();
			else
				switchMode();

		} else {
			switchMode();
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.profile, menu);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		if (mActiveMode == MODE.VIEW) {
			menu.getItem(0).setVisible(true);
			menu.getItem(1).setVisible(false);
			menu.getItem(2).setVisible(false);
		} else {
			menu.getItem(0).setVisible(false);
			menu.getItem(1).setVisible(true);
			menu.getItem(2).setVisible(true);
		}
		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_edit) {
			switchMode();
		} else if (item.getItemId() == R.id.action_save) {
			Profile newProfile = getNewProfile();
			if (mProfile == null) {
				try {
					PreferenceUtils.addProfile(getActivity(), newProfile);
					getFragmentManager().popBackStack();
				} catch (JSONException e) {
					Log.e(ProfileFragment.class.getName(), e.toString());
				}
			} else {
				PreferenceUtils.editProfile(getActivity(), getArguments()
						.getInt(PROFILE_INDEX_KEY), newProfile);
				mProfile= newProfile;
				setContent();
				
			}
			switchMode();
		} else if (item.getItemId() == R.id.action_delete) {
			if (mProfile != null) {
				PreferenceUtils.removeProfile(getActivity(), getArguments()
						.getInt(PROFILE_INDEX_KEY));
				switchMode();
			}
			getFragmentManager().popBackStack();

		} else
			return super.onOptionsItemSelected(item);
		((ActionBarActivity) getActivity()).supportInvalidateOptionsMenu();
		return true;
	}

	private Profile getNewProfile() {
		Profile p = new Profile();
		//TODO sanity check
		p.setName(mETNome.getText().toString());
		p.setUtenza(Utenza.valueOf(mETUtenza.getText().toString()));
		p.setArea(mETArea.getText().toString());
		p.setVia(mETVia.getText().toString());
		p.setNCivico(mETNCiv.getText().toString());
		p.setComune(mETComune.getText().toString());
		return p;
	}

	private void switchMode() {
		if (mActiveMode == MODE.VIEW) {
			mActiveMode = MODE.EDIT;
			mVSArea.showNext();
			mVSComune.showNext();
			mVSVia.showNext();
			mVSNome.showNext();
			mVSUtenza.showNext();
			mVSNCiv.showNext();
			//TODO set hints from profile's field
		} else {
			mActiveMode = MODE.VIEW;
			mVSArea.showPrevious();
			mVSComune.showPrevious();
			mVSVia.showPrevious();
			mVSNome.showPrevious();
			mVSUtenza.showPrevious();
			mVSNCiv.showPrevious();
		}
	}

	private void initializeViews() {

		mTVArea = (TextView) getView().findViewById(R.id.profile_area_tv);
		mTVComune = (TextView) getView().findViewById(R.id.profile_comune_tv);
		mTVNome = (TextView) getView().findViewById(R.id.profile_name_tv);
		mTVVia = (TextView) getView().findViewById(R.id.profile_indirizzo_tv);
		mTVUtenza = (TextView) getView().findViewById(R.id.profile_utenza_tv);
		mTVNCiv = (TextView) getView().findViewById(R.id.profile_nciv_tv);

		mETArea = (EditText) getView().findViewById(R.id.profile_area_et);
		mETComune = (EditText) getView().findViewById(R.id.profile_comune_et);
		mETNome = (EditText) getView().findViewById(R.id.profile_name_et);
		mETVia = (EditText) getView().findViewById(R.id.profile_indirizzo_et);
		mETUtenza = (EditText) getView().findViewById(R.id.profile_utenza_et);
		mETNCiv = (EditText) getView().findViewById(R.id.profile_nciv_et);

		mVSArea = (ViewSwitcher) getView().findViewById(R.id.profile_area_vs);
		mVSComune = (ViewSwitcher) getView().findViewById(
				R.id.profile_comune_vs);
		mVSNome = (ViewSwitcher) getView().findViewById(R.id.profile_name_vs);
		mVSVia = (ViewSwitcher) getView().findViewById(
				R.id.profile_indirizzo_vs);
		mVSUtenza = (ViewSwitcher) getView().findViewById(
				R.id.profile_utenza_vs);
		mVSNCiv = (ViewSwitcher) getView().findViewById(R.id.profile_nciv_vs);
	}

	private void setContent() {
		mTVArea.setText(mProfile.getArea());
		mTVComune.setText(mProfile.getComune());
		mTVVia.setText(mProfile.getVia());
		mTVUtenza.setText(mProfile.getUtenza().toString());
		mTVNCiv.setText(mProfile.getNCivico());
	}

}
