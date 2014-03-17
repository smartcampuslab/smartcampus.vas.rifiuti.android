package eu.trentorise.smartcampus.rifiuti;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import eu.trentorise.smartcampus.rifiuti.data.RifiutiHelper;
import eu.trentorise.smartcampus.rifiuti.model.DatiTipologiaRaccolta;
import eu.trentorise.smartcampus.rifiuti.model.PuntoRaccolta;
import eu.trentorise.smartcampus.rifiuti.utils.ArgUtils;

public class PuntiDiRaccoltaListFragment extends Fragment {

	private String tipologiaRaccolta = null;
	private String tipologiaRifiuto = null;
	List<PuntoRaccolta> puntiDiRaccolta = null;
	boolean hasMenu = false;
	private ActionBarActivity abActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	public static PuntiDiRaccoltaListFragment newIstanceTipologiaRaccolta(String raccolta) {
		PuntiDiRaccoltaListFragment rf = new PuntiDiRaccoltaListFragment();
		Bundle b = new Bundle();
		b.putString(ArgUtils.ARGUMENT_TIPOLOGIA_RACCOLTA, raccolta);
		rf.setArguments(b);
		return rf;
	}

	public static PuntiDiRaccoltaListFragment newIstanceTipologiaRifiuto(String rifiuto) {
		PuntiDiRaccoltaListFragment rf = new PuntiDiRaccoltaListFragment();
		Bundle b = new Bundle();
		b.putString(ArgUtils.ARGUMENT_TIPOLOGIA_RIFIUTO, rifiuto);
		rf.setArguments(b);
		return rf;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_puntiraccolta_list, container, false);
		return viewGroup;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		abActivity = (ActionBarActivity) getActivity();
		abActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		abActivity.getSupportActionBar().setHomeButtonEnabled(true);
		Bundle bundle = getArguments();
		if (bundle != null) {
			if (bundle.containsKey(ArgUtils.ARGUMENT_TIPOLOGIA_RACCOLTA)) {
				tipologiaRaccolta = bundle.getString(ArgUtils.ARGUMENT_TIPOLOGIA_RACCOLTA);
			}
			if (bundle.containsKey(ArgUtils.ARGUMENT_TIPOLOGIA_RIFIUTO)) {
				tipologiaRifiuto = bundle.getString(ArgUtils.ARGUMENT_TIPOLOGIA_RIFIUTO);
			}
		}

		List<DatiTipologiaRaccolta> data = null;
		try {
			if (tipologiaRaccolta != null) {
				puntiDiRaccolta = RifiutiHelper.getPuntiRaccoltaPerTipoRaccolta(tipologiaRaccolta);
				data = RifiutiHelper.getDatiTipologiaRaccoltaPerTipologiaRaccolta(tipologiaRaccolta);
			} else if (tipologiaRifiuto != null) {
				puntiDiRaccolta = RifiutiHelper.getPuntiRaccoltaPerTipoRifiuto(tipologiaRifiuto);
				data = RifiutiHelper.getDatiTipologiaRaccoltaPerTipologiaRifiuto(tipologiaRifiuto);
			} else {
				puntiDiRaccolta = RifiutiHelper.getPuntiRaccolta();
				hasMenu = true;
				setHasOptionsMenu(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			puntiDiRaccolta = new ArrayList<PuntoRaccolta>();
		}

		ExpandableListView elv = (ExpandableListView) getView().findViewById(android.R.id.list);
		final PuntoDiRaccoltaGroupAdapter adapter = new PuntoDiRaccoltaGroupAdapter(getActivity(), R.layout.puntoraccolta_info_group, R.layout.puntoraccolta_row, puntiDiRaccolta, data);
		// android.R.layout.simple_list_item_1
		elv.setAdapter(adapter);
		elv.setOnChildClickListener(new OnChildClickListener() {
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				Intent i = new Intent(getActivity(), PuntoRaccoltaActivity.class);
				i.putExtra(ArgUtils.ARGUMENT_PUNTO_DI_RACCOLTA, adapter.getChild(groupPosition, childPosition));
				startActivity(i);
				return true;
			}
		});
	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		if (hasMenu) {
			inflater.inflate(R.menu.point_menu, menu);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_map:
			MapFragment rf = new MapFragment();
			getFragmentManager().beginTransaction().replace(R.id.content_frame, rf).commit();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
	}

}
