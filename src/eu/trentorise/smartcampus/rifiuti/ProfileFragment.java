package eu.trentorise.smartcampus.rifiuti;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;
import eu.trentorise.smartcampus.rifiuti.data.RifiutiHelper;
import eu.trentorise.smartcampus.rifiuti.geo.OSMAddress;
import eu.trentorise.smartcampus.rifiuti.geo.OSMGeocoder;
import eu.trentorise.smartcampus.rifiuti.model.Area;
import eu.trentorise.smartcampus.rifiuti.model.Profile;
import eu.trentorise.smartcampus.rifiuti.model.SysProfile;
import eu.trentorise.smartcampus.rifiuti.utils.KeyboardUtils;
import eu.trentorise.smartcampus.rifiuti.utils.PreferenceUtils;
import eu.trentorise.smartcampus.rifiuti.utils.PreferenceUtils.ProfileNameExistsException;
import eu.trentorise.smartcampus.rifiuti.utils.ValidatorHelper;
import eu.trentorise.smartcampus.rifiuti.utils.onBackListener;

public class ProfileFragment extends Fragment implements onBackListener {

	// private MessageHandler messageHandler = null;

	private enum MODE {
		VIEW, EDIT
	}

	private ActionBarActivity abActivity;

	private static final String SAVE_MODE = "save_mode";
	private static final String SAVE_NAME = "save_mode";
	private static final String SAVE_COMUNE = "save_mode";
	private static final String SAVE_VIA = "save_mode";
	private static final String SAVE_NCIV = "save_mode";
	private static final String SAVE_UTENZA = "save_mode";
	private static final String SAVE_AREA = "save_mode";

	private TextView mTVNome, mTVComune, mTVVia, mTVNCiv, /** mTVArea, */
	mTVUtenza;
	private EditText mETNome, mETVia, mETNCiv;
	/** mETArea, */
	// private AutoCompleteTextView mACTVComune;
	private ImageButton mUtenzaHelpButton;
	private Spinner mAreaSpinner, mUtenzaSpinner;
	private RadioGroup mUtenzaRadioGroup;

	private ViewSwitcher mVSNome, mVSComune, mVSVia, mVSNCiv, /** mVSArea, */
	mVSUtenza;

	private Profile mProfile;
	private MODE mActiveMode;
	// private String mLastString = "";
	// private AsyncTask mCurrentTask;

	private List<Area> comuneAreas = null;
	private List<String> comuneAreasNames = null;

	private Area area = null;

	private String[] saved;

	// protected boolean selected = false;

	public final static String PROFILE_INDEX_KEY = "profile_index";

	public static ProfileFragment newInstance(int position) {
		ProfileFragment pf = new ProfileFragment();
		Bundle b = new Bundle();
		b.putInt(PROFILE_INDEX_KEY, position);
		pf.setArguments(b);
		return pf;
	}

	private boolean isFirstProfile() {
		return getArguments() == null || !getArguments().containsKey(PROFILE_INDEX_KEY);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		if (savedInstanceState != null) {
			mActiveMode = (savedInstanceState.getInt(SAVE_MODE) == 0) ? MODE.VIEW : MODE.EDIT;
			if (mActiveMode == MODE.VIEW) {
				getArguments().putInt(PROFILE_INDEX_KEY, savedInstanceState.getInt(PROFILE_INDEX_KEY));
			} else {
				saved = new String[] { savedInstanceState.getString(SAVE_AREA), savedInstanceState.getString(SAVE_COMUNE),
						savedInstanceState.getString(SAVE_NAME), savedInstanceState.getString(SAVE_NCIV),
						savedInstanceState.getString(SAVE_UTENZA), savedInstanceState.getString(SAVE_VIA) };
			}
		} else {
			mActiveMode = MODE.VIEW;
		}

		// messageHandler = new MessageHandler();
	}

	private void updateAreas(String tipoUtenza) {
		comuneAreas = RifiutiHelper.readAreasForTipoUtenza(tipoUtenza);
		comuneAreasNames = new ArrayList<String>(comuneAreas.size());
		for (Area a : comuneAreas) {
			comuneAreasNames.add(a.getLocalita());
		}
		comuneAreas.add(0, null);
		comuneAreasNames.add(0, getString(R.string.profilo_comune_placeholder));
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		abActivity = (ActionBarActivity) getActivity();
		abActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		abActivity.getSupportActionBar().setHomeButtonEnabled(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_profile, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();
		initializeViews();

		if (mActiveMode == MODE.VIEW) {
			if (!isFirstProfile()) {
				mProfile = PreferenceUtils.getProfile(getActivity(), getArguments().getInt(PROFILE_INDEX_KEY));
				if (mProfile != null) {
					setContent();
				} else {
					switchMode();
				}
			} else {
				switchMode();
			}
		}

		if (abActivity instanceof MainActivity) {
			((MainActivity) abActivity).hideDrawerIndicator();
			((MainActivity) abActivity).lockDrawer();
			abActivity.getSupportActionBar().setHomeButtonEnabled(true);
			abActivity.supportInvalidateOptionsMenu();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mProfile == null) {
			// getActivity().setProgressBarIndeterminateVisibility(true);
			RifiutiHelper.locationHelper.start();
		}
	}

	@Override
	public void onStop() {
		super.onPause();
		// getActivity().setProgressBarIndeterminateVisibility(false);
		RifiutiHelper.locationHelper.stop();
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
			if (mProfile != null
					&& PreferenceUtils.getCurrentProfilePosition(getActivity()) != getArguments().getInt(PROFILE_INDEX_KEY))
				menu.getItem(1).setVisible(true);
			else
				menu.getItem(1).setVisible(false);
			menu.getItem(2).setVisible(false);
		} else {
			menu.getItem(0).setVisible(false);
			menu.getItem(2).setVisible(true);
			menu.getItem(1).setVisible(false);
		}
		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			// getFragmentManager().beginTransaction()
			// .replace(R.id.content_frame, new HomeFragment())
			// .addToBackStack(null).commit();
			onBack();
		} else if (item.getItemId() == R.id.action_edit) {
			switchMode();
		} else if (item.getItemId() == R.id.action_save) {
			Profile newProfile;
			try {
				newProfile = getNewProfile();
				addOrModify(newProfile);
				KeyboardUtils.hideKeyboard(abActivity, getView());
				if (getActivity() instanceof MainActivity) {
					((MainActivity) getActivity()).populateProfilesList(isFirstProfile());
				}
			} catch (InvalidNameExeption e) {
				ValidatorHelper.highlight(getActivity(), mETNome, null);
			} catch (InvalidUtenzaExeption e) {
				ValidatorHelper.highlight(getActivity(), mUtenzaSpinner, null);
			} catch (InvalidAreaExeption e) {
				ValidatorHelper.highlight(getActivity(), mAreaSpinner, getResources().getString(R.string.err_unknown_area));
			} catch (InvalidViaExeption e) {
				ValidatorHelper.highlight(getActivity(), mETVia, null);
			} catch (InvalidNCivicoExeption e) {
				ValidatorHelper.highlight(getActivity(), mETNCiv, null);
			} catch (InvalidComuneExeption e) {
				ValidatorHelper.highlight(getActivity(), mAreaSpinner, null);
			}

		} else if (item.getItemId() == R.id.action_delete) {
			if (mProfile != null) {
				showConfirmAndDelete();
			}

		} else {
			return super.onOptionsItemSelected(item);
		}

		((ActionBarActivity) getActivity()).supportInvalidateOptionsMenu();
		return true;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		if (mActiveMode == MODE.VIEW) {
			outState.putInt(SAVE_MODE, 0);
			outState.putInt(PROFILE_INDEX_KEY, getArguments().getInt(PROFILE_INDEX_KEY));
		} else {
			outState.putInt(SAVE_MODE, 1);
			// outState.putString(SAVE_AREA, mETArea.getText().toString());
			if (area != null) {
				outState.putString(SAVE_AREA, area.getNome());
			}
			// outState.putString(SAVE_COMUNE,
			// mARCTVComune.getText().toString());
			if (area != null) {
				outState.putString(SAVE_COMUNE, area.getLocalita());
			}
			outState.putString(SAVE_NAME, mETNome.getText().toString());
			outState.putString(SAVE_NCIV, mETNCiv.getText().toString());
			outState.putString(SAVE_UTENZA, mUtenzaSpinner.getSelectedItem().toString());
			outState.putString(SAVE_VIA, mETVia.getText().toString());
		}

		super.onSaveInstanceState(outState);
	}

	public void onBack() {
		// if the fragment was started to create first profile, exit
		if ((isFirstProfile()) && getActivity() instanceof MainActivity) {
			getActivity().finish();
		}
		KeyboardUtils.hideKeyboard(abActivity, getView());
		getActivity().setProgressBarIndeterminateVisibility(false);
		getFragmentManager().popBackStack();
	}

	private boolean isInDB(Location l) {
		// TODO check with db
		return true;
	}

	private void addOrModify(Profile newProfile) {
		// if it's a new one
		try {
			if (mProfile == null) {
				int newPos = PreferenceUtils.addProfile(getActivity(), newProfile);
				if (isFirstProfile()) {
					PreferenceUtils.setCurrentProfilePosition(getActivity(), 0);
				} else {
					mProfile = newProfile;
					getArguments().putInt(PROFILE_INDEX_KEY, newPos);
					setContent();
				}
			} else {
				PreferenceUtils.editProfile(getActivity(), getArguments().getInt(PROFILE_INDEX_KEY), newProfile, mProfile);
				mProfile = newProfile;
				setContent();
			}
			switchMode();
		} catch (ProfileNameExistsException e) {
			Log.e(ProfileFragment.class.getName(), "profile's name exists:" + newProfile.getName());
			Toast.makeText(getActivity(), getString(R.string.err_prof_name), Toast.LENGTH_SHORT).show();

		} catch (JSONException e) {
			Log.e(ProfileFragment.class.getName(), e.toString());
			Toast.makeText(getActivity(), getString(R.string.err_everything_go_lazy), Toast.LENGTH_SHORT).show();
		}
	}

	private Profile getNewProfile() throws InvalidNameExeption, InvalidUtenzaExeption, InvalidAreaExeption, InvalidViaExeption,
			InvalidNCivicoExeption, InvalidComuneExeption {
		// because it might be that some fields were left as they had been.
		// create the profile from the saved one
		Profile p = null;
		if (mProfile != null) {
			p = new Profile(mProfile);
		} else {
			// if it's a new one, every field is required
			p = new Profile();
		}

		if (mETNome.getText().toString().trim().length() > 0) {
			p.setName(mETNome.getText().toString());
		} else if (mProfile == null) {
			throw new InvalidNameExeption();
		}

		// if (mUtenzaSpinner.getSelectedItem().toString().trim().length() > 0)
		// {
		// p.setUtenza(mUtenzaSpinner.getSelectedItem().toString());
		// } else if (mProfile == null) {
		// throw new InvalidUtenzaExeption();
		// }

		int checkedRadioButtonId = mUtenzaRadioGroup.getCheckedRadioButtonId();
		if (checkedRadioButtonId > -1) {
			SysProfile sysProfile = (SysProfile) mUtenzaRadioGroup.findViewById(checkedRadioButtonId).getTag();
			p.setUtenza(sysProfile.getTipologiaUtenza());
			p.setProfilo(sysProfile.getProfilo());
		} else if (mProfile == null) {
			throw new InvalidUtenzaExeption();
		}

		if (area != null) {
			p.setArea(area.getNome());
			p.setComune(area.getLocalita());
		} else {
			throw new InvalidAreaExeption();
		}

		// if (mETArea.getText().toString().trim().length() > 0) {
		// p.setArea(mETArea.getText().toString());
		// }
		// else if (mProfile == null)
		// throw new InvalidAreaExeption();
		// if (mETVia.getText().toString().trim().length() > 0)
		// p.setVia(mETVia.getText().toString());
		// else if (mProfile == null)
		// throw new InvalidViaExeption();
		// if (mETNCiv.getText().toString().trim().length() > 0)
		// p.setNCivico(mETNCiv.getText().toString());
		// else if (mProfile == null)
		// throw new InvalidNCivicoExeption();
		// if (mACTVComune.getText().toString().trim().length() > 0)
		// p.setComune(mACTVComune.getText().toString());
		// else if (mProfile == null)
		// throw new InvalidComuneExeption();

		return p;
	}

	private void switchMode() {
		if (mActiveMode == MODE.VIEW) {
			mActiveMode = MODE.EDIT;
			// mVSArea.showNext();
			mVSComune.showNext();
			mVSVia.showNext();
			mVSNome.showNext();
			mVSUtenza.showNext();
			mVSNCiv.showNext();

			if (mProfile != null) {
				// mETArea.setHint(mProfile.getArea());
				// mACTVComune.setHint(mProfile.getComune());
				mETNCiv.setHint(mProfile.getNCivico());
				mETNome.setHint(mProfile.getName());
				mETVia.setHint(mProfile.getVia());
			}

		} else {
			mActiveMode = MODE.VIEW;
			// mVSArea.showPrevious();
			mVSComune.showPrevious();
			mVSVia.showPrevious();
			mVSNome.showPrevious();
			mVSUtenza.showPrevious();
			mVSNCiv.showPrevious();
		}
	}

	private void initializeViews() {
		mTVNome = (TextView) getView().findViewById(R.id.profile_name_tv);
		mTVUtenza = (TextView) getView().findViewById(R.id.profile_utenza_tv);
		mTVComune = (TextView) getView().findViewById(R.id.profile_comune_tv);
		mTVVia = (TextView) getView().findViewById(R.id.profile_indirizzo_tv);
		mTVNCiv = (TextView) getView().findViewById(R.id.profile_nciv_tv);

		mUtenzaHelpButton = (ImageButton) getView().findViewById(R.id.profile_utenza_helpbutton);

		mETNome = (EditText) getView().findViewById(R.id.profile_name_et);
		// mUtenzaSpinner = (Spinner)
		// getView().findViewById(R.id.profile_utenza_spinner);
		mUtenzaRadioGroup = (RadioGroup) getView().findViewById(R.id.profile_utenza_radiogroup);
		mAreaSpinner = (Spinner) getView().findViewById(R.id.profile_comune_spinner);
		mETVia = (EditText) getView().findViewById(R.id.profile_indirizzo_et);
		mETNCiv = (EditText) getView().findViewById(R.id.profile_nciv_et);

		mVSNome = (ViewSwitcher) getView().findViewById(R.id.profile_name_vs);
		mVSUtenza = (ViewSwitcher) getView().findViewById(R.id.profile_utenza_vs);
		mVSComune = (ViewSwitcher) getView().findViewById(R.id.profile_comune_vs);
		mVSVia = (ViewSwitcher) getView().findViewById(R.id.profile_indirizzo_vs);
		mVSNCiv = (ViewSwitcher) getView().findViewById(R.id.profile_nciv_vs);

		/*
		 * populate mUtenzaRadioGroup
		 */
		List<SysProfile> sysProfiles = RifiutiHelper.readSysProfiles();
		for (int i = 0; i < sysProfiles.size(); i++) {
			SysProfile sp = sysProfiles.get(i);
			RadioButton rb = new RadioButton(getActivity());
			rb.setId(i);
			rb.setText(sp.getProfilo());
			rb.setTag(sp);
			mUtenzaRadioGroup.addView(rb);
		}

		mUtenzaHelpButton.getDrawable().setColorFilter(getResources().getColor(R.color.rifiuti_green_middle),
				PorterDuff.Mode.SRC_ATOP);

		/*
		 * Listeners
		 */
		mUtenzaHelpButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				// builder.setTitle(R.string.profilo_utenza_help_title);

				ScrollView dialogScrollView = (ScrollView) getActivity().getLayoutInflater().inflate(
						R.layout.dialog_utenzahelp, null);

				LinearLayout dialogLayout = (LinearLayout) dialogScrollView.findViewById(R.id.dialog_layout);

				for (SysProfile sp : RifiutiHelper.readSysProfiles()) {
					LinearLayout rowLayout = (LinearLayout) getActivity().getLayoutInflater().inflate(
							R.layout.dialog_utenzahelp_row, null);
					TextView titleTv = (TextView) rowLayout.findViewById(R.id.dialog_utenzahelp_row_title);
					TextView descTv = (TextView) rowLayout.findViewById(R.id.dialog_utenzahelp_row_description);
					titleTv.setText(sp.getProfilo());
					descTv.setText(sp.getDescrizione());
					dialogLayout.addView(rowLayout);
				}

				builder.setView(dialogScrollView);

				builder.setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

				builder.create().show();
			}
		});

		mUtenzaRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				SysProfile checkedSysProfile = (SysProfile) mUtenzaRadioGroup.findViewById(checkedId).getTag();
				updateAreas(checkedSysProfile.getTipologiaUtenza());
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_row, comuneAreasNames);
				mAreaSpinner.setAdapter(adapter);

				if (mProfile != null) {
					for (int i = 1; i < comuneAreas.size(); i++) {
						if (mProfile.getArea().equals(comuneAreas.get(i).getNome())) {
							mAreaSpinner.setSelection(i);
							break;
						}
					}
				}
			}
		});

		// default: first checked
		((RadioButton) mUtenzaRadioGroup.getChildAt(0)).setChecked(true);

		// mUtenzaSpinner.setOnItemSelectedListener(new OnItemSelectedListener()
		// {
		// public void onItemSelected(AdapterView<?> parent, View view, int pos,
		// long id) {
		// updateAreas(mUtenzaSpinner.getItemAtPosition(pos).toString());
		// ArrayAdapter<String> adapter = new
		// ArrayAdapter<String>(getActivity(), R.layout.spinner_row,
		// comuneAreasNames);
		// mAreaSpinner.setAdapter(adapter);
		//
		// if (mProfile != null) {
		// for (int i = 1; i < comuneAreas.size(); i++) {
		// if (mProfile.getArea().equals(comuneAreas.get(i).getNome())) {
		// mAreaSpinner.setSelection(i);
		// break;
		// }
		// }
		// }
		// }
		//
		// @Override
		// public void onNothingSelected(AdapterView<?> arg0) {
		// }
		// });

		mAreaSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				if (pos > 0) {
					area = comuneAreas.get(pos);
				}
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		if (saved != null) {
			area = RifiutiHelper.findArea(saved[0]);
			for (int i = 0; i < comuneAreas.size(); i++) {
				if (saved[0].equals(comuneAreas.get(i).getNome())) {
					mAreaSpinner.setSelection(i);
					break;
				}
			}
			mETNome.setText(saved[2]);
			mETNCiv.setText(saved[3]);
			// for (int i = 0; i < mUtenzaSpinner.getCount(); i++) {
			// if (saved[4].equals(mUtenzaSpinner.getItemAtPosition(i))) {
			// mUtenzaSpinner.setSelection(i);
			// break;
			// }
			// }
			mETVia.setText(saved[5]);
			saved = null;
		}
	}

	private void setContent() {
		area = RifiutiHelper.findArea(mProfile.getArea());

		mTVNome.setText(mProfile.getName());
		// String[] utenze = getResources().getStringArray(R.array.utenze);
		// for (int i = 0; i < utenze.length; i++) {
		// if (mProfile.getUtenza().equals(utenze[i])) {
		// mUtenzaSpinner.setSelection(i);
		// }
		// }
		for (int i = 0; i < mUtenzaRadioGroup.getChildCount(); i++) {
			RadioButton rb = (RadioButton) mUtenzaRadioGroup.getChildAt(i);
			if (mProfile.getProfilo() != null && mProfile.getProfilo().equals(((SysProfile) rb.getTag()).getProfilo())) {
				rb.setChecked(true);
			}
		}

		mTVComune.setText(mProfile.getComune());
		mTVVia.setText(mProfile.getVia());
		if (mProfile.getProfilo() != null) {
			mTVUtenza.setText(mProfile.getProfilo().toString());
		}
		mTVNCiv.setText(mProfile.getNCivico());
	}

	private void showConfirmAndDelete() {
		AlertDialog.Builder build = createConfirmDialog();
		build.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				try {
					PreferenceUtils.removeProfile(getActivity(), getArguments().getInt(PROFILE_INDEX_KEY));
					if (getActivity() instanceof MainActivity)
						((MainActivity) getActivity()).populateProfilesList(false);

				} catch (Exception e) {
					Toast.makeText(getActivity(), getString(R.string.err_delete_profilo), Toast.LENGTH_SHORT).show();
				}
				onBack();
			}
		});
		build.show();
	}

	private AlertDialog.Builder createConfirmDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(getString(R.string.dialog_confirm_title));
		builder.setMessage(getString(R.string.dialog_confirm_msg));
		builder.setNeutralButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		return builder;
	}

	private class GeocoderTask extends AsyncTask<Location, OSMAddress, Void> {

		@Override
		protected Void doInBackground(Location... params) {
			try {
				Location l = params[0];
				OSMGeocoder geo = new OSMGeocoder(getActivity());
				final List<OSMAddress> addresses = geo.getFromLocation(l.getLatitude(), l.getLongitude(), null);
				publishProgress(addresses.get(0));

			} catch (Exception e) {
				Log.e(ProfileFragment.class.getName(), e.toString());
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(OSMAddress... values) {
			super.onProgressUpdate(values);
			final OSMAddress address = values[0];
			// the user might have clicked back
			if (getFragmentManager() != null && getFragmentManager().findFragmentById(R.id.content_frame) != null
					&& getFragmentManager().findFragmentById(R.id.content_frame) instanceof ProfileFragment) {
				for (int i = 0; i < comuneAreasNames.size(); i++) {
					if (address.city().equalsIgnoreCase(comuneAreasNames.get(i))) {
						final int pos = i;
						AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
						builder.setTitle(getString(R.string.dialog_gps_title));
						String msg = String.format(getString(R.string.dialog_gps_msg), address.getStreet(),
								(address.getHousenumber() != null) ? address.getHousenumber() : "", address.city());
						builder.setMessage(msg);
						builder.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								mAreaSpinner.setSelection(pos);
								// mACTVComune.setText(address.city());
								mETVia.setText(address.getStreet());
								mETNCiv.setText(address.getHousenumber());
							}
						});
						builder.setNeutralButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						});
						builder.create().show();
					}
				}

			}
			if (getActivity() != null)
				getActivity().setProgressBarIndeterminateVisibility(false);

		}

	}

	// private class LoadAreasTask extends AsyncTask<String, Area, Void> {
	//
	// @Override
	// protected Void doInBackground(String... params) {
	// List<Area> areas = RifiutiHelper.readAreas(params[0]);
	// Area[] comuni = new Area[areas.size()];
	// if (!this.isCancelled()) {
	// areas.toArray(comuni);
	// publishProgress(comuni);
	// }
	// return null;
	// }
	//
	// @Override
	// protected void onProgressUpdate(Area... values) {
	// super.onProgressUpdate(values);
	// // the user might have clicked back
	//
	// if (getFragmentManager().findFragmentById(R.id.content_frame) != null
	// && getFragmentManager().findFragmentById(R.id.content_frame) instanceof
	// ProfileFragment) {
	// ArrayAdapter<Area> adapter = new ArrayAdapter<Area>(getActivity(), -1,
	// values) {
	//
	// @Override
	// public View getView(int position, View convertView, ViewGroup parent) {
	// if (convertView == null) {
	// LayoutInflater inflater = getActivity().getLayoutInflater();
	// convertView = inflater.inflate(android.R.layout.simple_list_item_1,
	// parent, false);
	// }
	// ((TextView) convertView).setText(getItem(position).getComune());
	// convertView.setTag(getItem(position));
	// return convertView;
	// }
	//
	// };
	// mACTVComune.setAdapter(adapter);
	// mACTVComune.setOnItemClickListener(new AdapterView.OnItemClickListener()
	// {
	//
	// @Override
	// public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long
	// arg3) {
	// selected = true;
	// mACTVComune.setText(((Area) arg1.getTag()).getComune());
	// area = (Area) arg1.getTag();
	// //mETArea.setText(((Area) arg1.getTag()).getNome());
	// }
	// });
	// mACTVComune.showDropDown();
	// }
	// }
	//
	// }

	private static class InvalidNameExeption extends Exception {
		private static final long serialVersionUID = 1L;
	}

	private static class InvalidComuneExeption extends Exception {
		private static final long serialVersionUID = 1L;
	}

	private static class InvalidViaExeption extends Exception {
		private static final long serialVersionUID = 1L;
	}

	private static class InvalidAreaExeption extends Exception {
		private static final long serialVersionUID = 1L;
	}

	private static class InvalidNCivicoExeption extends Exception {
		private static final long serialVersionUID = 1L;
	}

	private static class InvalidUtenzaExeption extends Exception {
		private static final long serialVersionUID = 1L;
	}

	// private TextWatcher mTextListener = new TextWatcher() {
	//
	// @Override
	// public void onTextChanged(CharSequence s, int start, int before, int
	// count) {
	// if (!selected && s.length() > 1) {
	// Message msg = Message.obtain(messageHandler, 1, s.toString());
	// messageHandler.sendMessageDelayed(msg, 200);
	// // if (mCurrentTask != null)
	// // mCurrentTask.cancel(true);
	// // mCurrentTask = new LoadAreasTask().execute(s.toString());
	// }
	// if (selected) selected = false;
	// }
	//
	// @Override
	// public void beforeTextChanged(CharSequence s, int start, int count, int
	// after) {
	// area = null;
	// messageHandler.removeMessages(1);
	// }
	//
	// @Override
	// public void afterTextChanged(Editable s) {
	//
	// }
	// };
	//
	// private class MessageHandler extends Handler {
	//
	// @Override
	// public void handleMessage(Message msg) {
	// String enteredText = (String) msg.obj;
	// new LoadAreasTask().execute(enteredText);
	// }
	// }
	//
}
