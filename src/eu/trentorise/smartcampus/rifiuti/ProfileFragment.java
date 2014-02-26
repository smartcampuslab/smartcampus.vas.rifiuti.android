package eu.trentorise.smartcampus.rifiuti;

import org.json.JSONException;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Toast;
import android.widget.ViewSwitcher;
import eu.trentorise.smartcampus.rifiuti.model.Profile;
import eu.trentorise.smartcampus.rifiuti.model.Profile.Utenza;
import eu.trentorise.smartcampus.rifiuti.utils.PreferenceUtils;
import eu.trentorise.smartcampus.rifiuti.utils.ValidatorHelper;

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
			if (mProfile != null)
				menu.getItem(1).setVisible(true);
			menu.getItem(2).setVisible(true);
		}
		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_edit)
			switchMode();
		else if (item.getItemId() == R.id.action_save) {
			Profile newProfile;
			try {
				newProfile = getNewProfile();
				addOrModify(newProfile);
				switchMode();
			} catch (InvalidNameExeption e) {
				ValidatorHelper.highlight(getActivity(), mETNome, null);
			} catch (InvalidUtenzaExeption e) {
				ValidatorHelper.highlight(getActivity(), mETUtenza, null);
			} catch (InvalidAreaExeption e) {
				ValidatorHelper.highlight(getActivity(), mETArea, null);
			} catch (InvalidViaExeption e) {
				ValidatorHelper.highlight(getActivity(), mETVia, null);
			} catch (InvalidNCivicoExeption e) {
				ValidatorHelper.highlight(getActivity(), mETNCiv, null);
			} catch (InvalidComuneExeption e) {
				ValidatorHelper.highlight(getActivity(), mETComune, null);
			}

		} else if (item.getItemId() == R.id.action_delete) {
			if (mProfile != null) {
				showConfirmAndDelete();
				switchMode();
			}

		} else
			return super.onOptionsItemSelected(item);

		((ActionBarActivity) getActivity()).supportInvalidateOptionsMenu();
		return true;
	}

	private void addOrModify(Profile newProfile) {
		// if it's a new one
		if (mProfile == null) {
			try {
				PreferenceUtils.addProfile(getActivity(), newProfile);
				getFragmentManager().popBackStack();
			} catch (JSONException e) {
				Log.e(ProfileFragment.class.getName(), e.toString());
			}
		} else {
			PreferenceUtils.editProfile(getActivity(),
					getArguments().getInt(PROFILE_INDEX_KEY), newProfile);
			mProfile = newProfile;
			setContent();

		}
	}

	private Profile getNewProfile() throws InvalidNameExeption,
			InvalidUtenzaExeption, InvalidAreaExeption, InvalidViaExeption,
			InvalidNCivicoExeption, InvalidComuneExeption {
		// because it might be that some fields were left as they had been.
		// create the profile from the saved one
		Profile p = null;
		if (mProfile != null)
			p = new Profile(mProfile);
		else
			// if it's a new one, every field is required
			p = new Profile();
		// TODO sanity check with the db
		if (mETNome.getText().toString().trim().length() > 0)
			p.setName(mETNome.getText().toString());
		else if (mProfile == null)
			throw new InvalidNameExeption();
		if (mETUtenza.getText().toString().trim().length() > 0)
			p.setUtenza(Utenza.valueOf(mETUtenza.getText().toString()));
		else if (mProfile == null)
			throw new InvalidUtenzaExeption();
		if (mETArea.getText().toString().trim().length() > 0)
			p.setArea(mETArea.getText().toString());
		else if (mProfile == null)
			throw new InvalidAreaExeption();
		if (mETVia.getText().toString().trim().length() > 0)
			p.setVia(mETVia.getText().toString());
		else if (mProfile == null)
			throw new InvalidViaExeption();
		if (mETNCiv.getText().toString().trim().length() > 0)
			p.setNCivico(mETNCiv.getText().toString());
		else if (mProfile == null)
			throw new InvalidNCivicoExeption();
		if (mETComune.getText().toString().trim().length() > 0)
			p.setComune(mETComune.getText().toString());
		else if (mProfile == null)
			throw new InvalidComuneExeption();
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

			if (mProfile != null) {
				mETArea.setHint(mProfile.getArea());
				mETComune.setHint(mProfile.getComune());
				mETNCiv.setHint(mProfile.getNCivico());
				mETNome.setHint(mProfile.getName());
				mETVia.setHint(mProfile.getVia());
			}

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
		mTVNome.setText(mProfile.getName());
		mTVArea.setText(mProfile.getArea());
		mTVComune.setText(mProfile.getComune());
		mTVVia.setText(mProfile.getVia());
		mTVUtenza.setText(mProfile.getUtenza().toString());
		mTVNCiv.setText(mProfile.getNCivico());
	}

	private void showConfirmAndDelete() {
		AlertDialog.Builder build = createConfirmDialog();
		build.setPositiveButton(getString(android.R.string.ok),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							PreferenceUtils.removeProfile(getActivity(),
									getArguments().getInt(PROFILE_INDEX_KEY));

						} catch (Exception e) {
							Toast.makeText(getActivity(),
									getString(R.string.err_delete_profilo),
									Toast.LENGTH_SHORT).show();
						}
						getFragmentManager().popBackStack();
					}
				});
		build.show();
	}

	private AlertDialog.Builder createConfirmDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(getString(R.string.dialog_confirm_title));
		builder.setMessage(getString(R.string.dialog_confirm_msg));
		builder.setNeutralButton(getString(android.R.string.cancel),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		return builder;
	}

	private static class InvalidNameExeption extends Exception {
	}

	private static class InvalidComuneExeption extends Exception {
	}

	private static class InvalidViaExeption extends Exception {
	}

	private static class InvalidAreaExeption extends Exception {
	}

	private static class InvalidNCivicoExeption extends Exception {
	}

	private static class InvalidUtenzaExeption extends Exception {
	}

}
