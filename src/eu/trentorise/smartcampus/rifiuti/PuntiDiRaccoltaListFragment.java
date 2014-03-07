package eu.trentorise.smartcampus.rifiuti;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import eu.trentorise.smartcampus.rifiuti.data.RifiutiHelper;
import eu.trentorise.smartcampus.rifiuti.model.PuntoRaccolta;
import eu.trentorise.smartcampus.rifiuti.utils.ArgUtils;

public class PuntiDiRaccoltaListFragment extends ListFragment {

	private String tipologiaRaccolta = null;
	private String tipologiaRifiuto = null;
	List<PuntoRaccolta> puntiDiRaccolta = null;
	boolean hasMenu = false;
	private ActionBarActivity abActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		abActivity = (ActionBarActivity) getActivity();

		setHasOptionsMenu(true);

		abActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		abActivity.getSupportActionBar().setHomeButtonEnabled(true);
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
		ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_rifiuti_list, container, false);
		return viewGroup;
	}

	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
		PuntoDiRaccoltaDetailFragment fragment = new PuntoDiRaccoltaDetailFragment();

		Bundle args = new Bundle();
		args.putSerializable(ArgUtils.ARGUMENT_PUNTO_DI_RACCOLTA, puntiDiRaccolta.get(position));
		fragment.setArguments(args);
		fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		// fragmentTransaction.detach(this);
		fragmentTransaction.replace(R.id.content_frame, fragment, "puntodiraccolta");
		fragmentTransaction.addToBackStack(fragment.getTag());
		fragmentTransaction.commit();
	}
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Bundle bundle = getArguments();
		if (bundle != null) {
			if (bundle.containsKey(ArgUtils.ARGUMENT_TIPOLOGIA_RACCOLTA))
				tipologiaRaccolta = bundle.getString(ArgUtils.ARGUMENT_TIPOLOGIA_RACCOLTA);
			if (bundle.containsKey(ArgUtils.ARGUMENT_TIPOLOGIA_RIFIUTO))
				tipologiaRifiuto = bundle.getString(ArgUtils.ARGUMENT_TIPOLOGIA_RIFIUTO);
		}
		
		try {
			if (tipologiaRaccolta != null) {
				puntiDiRaccolta=RifiutiHelper.getPuntiRaccoltaPerTipoRaccolta(tipologiaRaccolta);
			} else if (tipologiaRifiuto != null) {
				puntiDiRaccolta=RifiutiHelper.getPuntiRaccoltaPerTipoRifiuto(tipologiaRifiuto);
			} else {
				puntiDiRaccolta=RifiutiHelper.getPuntiRaccolta();
				hasMenu = true;
				setHasOptionsMenu(true);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			puntiDiRaccolta = new ArrayList<PuntoRaccolta>();
		}
		PuntoDiRaccoltaAdapter adapter = new PuntoDiRaccoltaAdapter(getActivity(), R.layout.puntodiraccolta_adapter, puntiDiRaccolta);
		setListAdapter(adapter); 
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
		}
		return super.onOptionsItemSelected(item);
	}

	
	@Override
	public void onStart() {
		super.onStart();

	}
}
