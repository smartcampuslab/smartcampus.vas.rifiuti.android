package it.smartcampuslab.riciclo;

import it.smartcampuslab.riciclo.data.RifiutiHelper;
import it.smartcampuslab.riciclo.model.DatiTipologiaRaccolta;
import it.smartcampuslab.riciclo.model.PuntoRaccolta;
import it.smartcampuslab.riciclo.utils.ArgUtils;
import it.smartcampuslab.riciclo.utils.PreferenceUtils;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

public class RifiutoDetailsFragment extends Fragment {
	private String rifiuto = null;
	private String tipologiaRifiuto = null;
	List<PuntoRaccolta> puntiDiRaccolta = null;
	private ActionBarActivity abActivity;
	private List<DatiTipologiaRaccolta> datiTipologiaRaccoltaList;
	private boolean mPreferredOnly = true;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_rifiuto_details, container, false);
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
		if (bundle.containsKey(ArgUtils.ARGUMENT_RIFIUTO)) {
			rifiuto = bundle.getString(ArgUtils.ARGUMENT_RIFIUTO);
		}
	}

	@Override
	public void onStart() {
		super.onStart();

		abActivity.getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		if (rifiuto != null) {
			abActivity.getSupportActionBar().setTitle(abActivity.getString(R.string.rifiuto_title));
			abActivity.getSupportActionBar().setSubtitle(rifiuto);
		} else if (tipologiaRifiuto != null) {
			abActivity.getSupportActionBar().setTitle(
					abActivity.getString(R.string.tipo_di_rifiuto_title));
			abActivity.getSupportActionBar().setSubtitle( rifiuto);
			
		}

		try {
			// if (tipologiaRaccolta != null) {
			if (RifiutiHelper.getInstance() == null) {
				RifiutiHelper.init(getActivity());
				RifiutiHelper.setProfile(PreferenceUtils.getProfile(getActivity(),
						PreferenceUtils.getCurrentProfilePosition(getActivity())));
			}
			tipologiaRifiuto = RifiutiHelper.getTipoRifiuto(rifiuto);
			if (tipologiaRifiuto != null) {
				puntiDiRaccolta = RifiutiHelper.getPuntiRaccoltaPerTipoRifiuto(tipologiaRifiuto, mPreferredOnly);
			}
			datiTipologiaRaccoltaList = RifiutiHelper.getDatiTipologiaRaccolta(rifiuto);
			// } else if (tipologiaRifiuto != null) {
			// list = RifiutiHelper.getRifiutoPerTipoRifiuti(tipologiaRifiuto);
			// }
		} catch (Exception e) {
			Log.e(getClass().getSimpleName(), e.getMessage());
		}
		// setListAdapter(adapter);

//		ListView mTipologieRaccolta = (ListView) getView().findViewById(R.id.tiporaccolta_list);
//		TipologieAdapter tipologieAdapter = new TipologieAdapter(getActivity(), R.layout.tipologiaraccolta_adapter,
//				datiTipologiaRaccoltaList, true);
//		mTipologieRaccolta.setAdapter(tipologieAdapter);
//
//		mTipologieRaccolta.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//				FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
//				RifiutiManagerContainerFragment fragment = new RifiutiManagerContainerFragment();
//				Bundle bundle = new Bundle();
//
//				bundle.putString(ArgUtils.ARGUMENT_TIPOLOGIA_RACCOLTA, datiTipologiaRaccoltaList.get(arg2)
//						.getTipologiaRaccolta());
//				fragment.setArguments(bundle);
//				fragmentTransaction.replace(R.id.content_frame, fragment, "tipologiaraccolta");
//				fragmentTransaction.addToBackStack(fragment.getTag());
//				fragmentTransaction.commit();
//			}
//		});

		final PuntoDiRaccoltaGroupAdapter adapter = new PuntoDiRaccoltaGroupAdapter(getActivity(), R.layout.puntoraccolta_info_group,  R.layout.puntoraccolta_row,
				puntiDiRaccolta, datiTipologiaRaccoltaList);
		ExpandableListView listPuntiRaccolta = (ExpandableListView) getView().findViewById(R.id.puntoraccolta_list);
		listPuntiRaccolta.setAdapter(adapter);
		listPuntiRaccolta.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				Intent i = new Intent(getActivity(), PuntoRaccoltaActivity.class);
				i.putExtra(ArgUtils.ARGUMENT_PUNTO_DI_RACCOLTA, adapter.getChild(groupPosition, childPosition));
				startActivity(i);
				return true;
			}
		});
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putAll(getArguments());
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			abActivity.finish();
			return true;
		}
		return false;
	}

}
