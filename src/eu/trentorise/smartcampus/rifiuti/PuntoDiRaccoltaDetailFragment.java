package eu.trentorise.smartcampus.rifiuti;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.location.Address;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import eu.trentorise.smartcampus.rifiuti.data.RifiutiHelper;
import eu.trentorise.smartcampus.rifiuti.model.Calendario;
import eu.trentorise.smartcampus.rifiuti.model.DatiTipologiaRaccolta;
import eu.trentorise.smartcampus.rifiuti.model.PuntoRaccolta;
import eu.trentorise.smartcampus.rifiuti.utils.ArgUtils;

public class PuntoDiRaccoltaDetailFragment extends Fragment {

	private PuntoRaccolta puntoDiRaccolta = null;
	private List<Calendario> calendario = null;
	private List<DatiTipologiaRaccolta> tipologie = null;
	private ActionBarActivity abActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		abActivity = (ActionBarActivity) getActivity();

		setHasOptionsMenu(true);

		if (abActivity != null) {
			abActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			abActivity.getSupportActionBar().setHomeButtonEnabled(true);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_puntodiraccolta_details, container, false);
		return viewGroup;
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
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Bundle bundle = getArguments();
		if (bundle.containsKey(ArgUtils.ARGUMENT_PUNTO_DI_RACCOLTA))
			puntoDiRaccolta = (PuntoRaccolta) bundle.getSerializable(ArgUtils.ARGUMENT_PUNTO_DI_RACCOLTA);
		try {
			// get lista orari per punto di raccolta
			calendario = RifiutiHelper.getCalendars(puntoDiRaccolta);
			// get lista tipologie per punto di raccolta
			tipologie = RifiutiHelper.readTipologiaRaccoltaPerTipologiaPuntoRaccolta(puntoDiRaccolta
					.getTipologiaPuntiRaccolta());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TextView mDettagli = (TextView) getActivity().findViewById(R.id.puntodiraccolta_dettagli);
		mDettagli.setText(puntoDiRaccolta.getIndirizzo());
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
		ListView mOrari = (ListView) getActivity().findViewById(R.id.puntodiraccolta_listaorari);
		CalendarioAdapter calendarAdapter = new CalendarioAdapter(getActivity(), R.layout.calendario_adapter,
				calendario);
		mOrari.setAdapter(calendarAdapter);

		ListView mTipologieRaccolta = (ListView) getActivity().findViewById(R.id.puntodiraccolta_listatipologie);
		TipologieAdapter tipologieAdapter = new TipologieAdapter(getActivity(), R.layout.tipologiaraccolta_adapter,
				tipologie);
		mTipologieRaccolta.setAdapter(tipologieAdapter);

		mTipologieRaccolta.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
				RifiutiManagerContainerFragment fragment = new RifiutiManagerContainerFragment();
				Bundle bundle = new Bundle();
				bundle.putString(ArgUtils.ARGUMENT_TIPOLOGIA_RACCOLTA, tipologie.get(arg2).getTipologiaRaccolta());
				fragment.setArguments(bundle);
				fragmentTransaction.replace(R.id.content_frame, fragment, "tipologiaraccolta");
				fragmentTransaction.addToBackStack(fragment.getTag());
				fragmentTransaction.commit();
			}
		});
	}

	private void bringMeThere(PuntoRaccolta pdr) {
		AlertDialog.Builder builder;

		builder = new AlertDialog.Builder(getActivity());

		callBringMeThere();

		return;
	}

	protected void callBringMeThere() {
		LatLng latLng = null;
		Address to = new Address(Locale.getDefault());
		String[] splittedLatLong=puntoDiRaccolta.getLocalizzazione().split(",");
		latLng = new LatLng(Double.parseDouble(splittedLatLong[0]),Double.parseDouble(splittedLatLong[1]));
		to.setLatitude(latLng.latitude);
		to.setLongitude(latLng.longitude);
		Address from = null;
//		GeoPoint mylocation = MapManager.requestMyLocation(getActivity());
//		if (mylocation != null) {
//			from = new Address(Locale.getDefault());
//			from.setLatitude(mylocation.getLatitudeE6() / 1E6);
//			from.setLongitude(mylocation.getLongitudeE6() / 1E6);
//		}
//		DTHelper.bringmethere(getActivity(), from, to);

	}
	
	@Override
	public void onStart() {
		super.onStart();
		abActivity.getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		abActivity.getSupportActionBar().setTitle(
				abActivity.getString(R.string.punto_di_raccolta_title) + " : "
						+ puntoDiRaccolta.getTipologiaPuntiRaccolta() + " " + puntoDiRaccolta.getArea());

	}
	
	
}
