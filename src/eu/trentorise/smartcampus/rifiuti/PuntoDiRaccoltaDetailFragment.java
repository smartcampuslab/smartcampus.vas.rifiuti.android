package eu.trentorise.smartcampus.rifiuti;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import eu.trentorise.smartcampus.rifiuti.data.RifiutiHelper;
import eu.trentorise.smartcampus.rifiuti.model.Calendario;
import eu.trentorise.smartcampus.rifiuti.model.DatiTipologiaRaccolta;
import eu.trentorise.smartcampus.rifiuti.model.PuntoRaccolta;
import eu.trentorise.smartcampus.rifiuti.utils.ArgUtils;
import eu.trentorise.smartcampus.rifiuti.utils.PreferenceUtils;

public class PuntoDiRaccoltaDetailFragment extends Fragment {

	private final boolean USE_GOOGLE_MAPS = true;
	private final int GET_LOCATION_WAIT_TIME = 10000;

	private PuntoRaccolta puntoDiRaccolta = null;
	private List<Calendario> orariList = null;
	private List<DatiTipologiaRaccolta> tipologieList = null;
	private ActionBarActivity abActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_puntodiraccolta_details, container, false);
		return viewGroup;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		abActivity = (ActionBarActivity) getActivity();
		abActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		abActivity.getSupportActionBar().setHomeButtonEnabled(true);

		Bundle bundle = getArguments();
		if (bundle == null) {
			bundle = savedInstanceState;
		}
		if (bundle.containsKey(ArgUtils.ARGUMENT_PUNTO_DI_RACCOLTA)) {
			puntoDiRaccolta = (PuntoRaccolta) bundle.getSerializable(ArgUtils.ARGUMENT_PUNTO_DI_RACCOLTA);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		abActivity.getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		abActivity.getSupportActionBar().setTitle(
				abActivity.getString(R.string.puntiraccoltadetails_abtitle,
						abActivity.getString(R.string.punto_di_raccolta_title)));
		abActivity.getSupportActionBar().setSubtitle(
				abActivity.getString(R.string.puntiraccoltadetails_absubtitle, puntoDiRaccolta.getTipologiaPuntiRaccolta(),
						puntoDiRaccolta.getArea()));

		try {
			// get lista orari per punto di raccolta
			if (RifiutiHelper.getInstance() == null) {
				RifiutiHelper.init(getActivity());
				RifiutiHelper.setProfile(PreferenceUtils.getProfile(getActivity(),
						PreferenceUtils.getCurrentProfilePosition(getActivity())));
			}
			orariList = RifiutiHelper.getCalendars(puntoDiRaccolta);
			Collections.sort(orariList, RifiutiHelper.calendarioComparator);
			// get lista tipologie per punto di raccolta
			tipologieList = RifiutiHelper.readTipologiaRaccoltaPerTipologiaPuntoRaccolta(puntoDiRaccolta
					.getTipologiaPuntiRaccolta());
		} catch (Exception e) {
			Log.e(getClass().getSimpleName(), e.getMessage());
		}

		TextView mDettagli = (TextView) getActivity().findViewById(R.id.puntodiraccolta_dettagli);
		mDettagli.setText(puntoDiRaccolta.dettaglio());

		ImageView mappa = (ImageView) getActivity().findViewById(R.id.map_dettagli);
		mappa.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
				MapFragment fragment = new MapFragment();
				Bundle args = new Bundle();
				ArrayList<PuntoRaccolta> list = new ArrayList<PuntoRaccolta>();
				list.add(puntoDiRaccolta);
				args.putSerializable(ArgUtils.ARGUMENT_PUNTO_DI_RACCOLTA, list);
				fragment.setArguments(args);
				fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				fragmentTransaction.replace(R.id.content_frame, fragment, "map");
				fragmentTransaction.addToBackStack(fragment.getTag());
				fragmentTransaction.commit();
			}
		});

		// directions
		ImageView directionsBtn = (ImageView) getView().findViewById(R.id.puntodiraccolta_directions);
		directionsBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (puntoDiRaccolta.getLocalizzazione() != null) {
					bringMeThere(puntoDiRaccolta);
				} else {
					Toast.makeText(getActivity(), R.string.pdr_non_trovato_toast, Toast.LENGTH_SHORT).show();
				}
			}
		});

		if (orariList == null || orariList.isEmpty()) {
			LinearLayout orariLayout = (LinearLayout) getActivity().findViewById(R.id.puntodiraccolta_orari_layout);
			orariLayout.setVisibility(View.GONE);
		} else {
			String orariText = new String();
			for (int i = 0; i < orariList.size(); i++) {
				Calendario orario = orariList.get(i);

				if (i > 0 && orario.getIl().equals(orariList.get(i - 1).getIl())) {
					// same day of the week
					orariText += getActivity().getString(R.string.puntiraccoltadetails_orari_2, orario.getDalle(),
							orario.getAlle());
				} else {
					if (i > 0) {
						orariText += "\n";
					}
					orariText += getActivity().getString(R.string.puntiraccoltadetails_orari, orario.getIl(),
							orario.getDalle(), orario.getAlle());
				}
			}
			TextView orariTextView = (TextView) getActivity().findViewById(R.id.puntodiraccolta_orari_textview);
			orariTextView.setText(orariText);
		}

		ListView mTipologieRaccolta = (ListView) getActivity().findViewById(R.id.puntodiraccolta_listatipologie);
		TipologieAdapter tipologieAdapter = new TipologieAdapter(getActivity(), R.layout.tipologiaraccolta_adapter,
				tipologieList, false);

		mTipologieRaccolta.setAdapter(tipologieAdapter);

		mTipologieRaccolta.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
				RifiutiManagerContainerFragment fragment = new RifiutiManagerContainerFragment();
				Bundle bundle = new Bundle();

				bundle.putString(ArgUtils.ARGUMENT_TIPOLOGIA_RACCOLTA, tipologieList.get(arg2).getTipologiaRaccolta());
				fragment.setArguments(bundle);
				fragmentTransaction.replace(R.id.content_frame, fragment, "tipologiaraccolta");
				fragmentTransaction.addToBackStack(fragment.getTag());
				fragmentTransaction.commit();
			}
		});

		// ScrollView sv = (ScrollView)
		// getActivity().findViewById(R.id.puntodiraccolta_dettagli_scrollview);
		// sv.focusSearch(ScrollView.FOCUS_UP);
	}

	@Override
	public void onResume() {
		super.onResume();
		RifiutiHelper.locationHelper.start();
	}

	@Override
	public void onStop() {
		super.onStop();
		RifiutiHelper.locationHelper.stop();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			abActivity.onBackPressed();
			return true;
		}
		return false;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putAll(getArguments());
		super.onSaveInstanceState(outState);
	}

	private void bringMeThere(PuntoRaccolta pdr) {
		if (USE_GOOGLE_MAPS) {
			callAppForDirectionsGmaps();
		} else {
			try {
				getActivity().getPackageManager().getApplicationInfo("eu.trentorise.smartcampus.viaggiatrento", 0);
			} catch (NameNotFoundException e) {
				Intent intent = new Intent(Intent.ACTION_VIEW,
						Uri.parse("market://details?id=eu.trentorise.smartcampus.viaggiatrento"));
				getActivity().startActivity(intent);
				return;
			}
			new LocalizeAsyncTask().execute();
		}

	}

	private void callAppForDirections() {
		Address from = new Address(Locale.getDefault());
		if (RifiutiHelper.locationHelper.getLocation() != null) {
			Location location = RifiutiHelper.locationHelper.getLocation();
			from.setLatitude(location.getLatitude());
			from.setLongitude(location.getLongitude());
		}
		LatLng latLng = null;
		Address to = new Address(Locale.getDefault());
		double[] coords = puntoDiRaccolta.location();
		latLng = new LatLng(coords[0], coords[1]);
		to.setLatitude(latLng.latitude);
		to.setLongitude(latLng.longitude);

		Intent intent = new Intent(getString(R.string.direction_intent));
		if (from.hasLatitude() && from.hasLongitude()) {
			intent.putExtra(getString(R.string.from_for_direction), from);
		}
		intent.putExtra(getString(R.string.to_for_direction), to);
		try {
			getActivity().startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(getActivity(), getString(R.string.toast_app_not_installed), Toast.LENGTH_LONG).show();
		}
	}

	private void callAppForDirectionsGmaps() {
		Address from = new Address(Locale.getDefault());
		// if (RifiutiHelper.locationHelper.getLocation() != null) {
		// Location location = RifiutiHelper.locationHelper.getLocation();
		// from.setLatitude(location.getLatitude());
		// from.setLongitude(location.getLongitude());
		// }
		LatLng latLng = null;
		Address to = new Address(Locale.getDefault());
		double[] coords = puntoDiRaccolta.location();
		latLng = new LatLng(coords[0], coords[1]);
		to.setLatitude(latLng.latitude);
		to.setLongitude(latLng.longitude);

		String url;
		if (from.hasLatitude() && from.hasLongitude()) {
			url = "http://maps.google.com/maps?saddr=" + from.getLatitude() + "," + from.getLongitude() + "&daddr="
					+ to.getLatitude() + "," + to.getLongitude();
		} else {
			url = "http://maps.google.com/maps?daddr=" + to.getLatitude() + "," + to.getLongitude();
		}

		Intent navigation = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		startActivity(navigation);
	}

	private class LocalizeAsyncTask extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress = null;

		@Override
		protected void onPreExecute() {
			if (getActivity() == null) {
				return;
			}

			progress = ProgressDialog.show(getActivity(), "", getString(R.string.geocoding), true);
		}

		@Override
		protected Void doInBackground(Void... params) {
			long start = System.currentTimeMillis();
			while (RifiutiHelper.locationHelper.getLocation() == null
					&& System.currentTimeMillis() - start < GET_LOCATION_WAIT_TIME) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			callAppForDirections();
			if (progress != null) {
				progress.cancel();
			}
		}
	}
}
