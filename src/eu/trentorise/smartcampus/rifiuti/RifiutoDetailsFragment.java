package eu.trentorise.smartcampus.rifiuti;

import java.util.List;

import eu.trentorise.smartcampus.rifiuti.data.RifiutiHelper;
import eu.trentorise.smartcampus.rifiuti.model.PuntoRaccolta;
import eu.trentorise.smartcampus.rifiuti.utils.ArgUtils;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class RifiutoDetailsFragment extends Fragment {
	private String rifiuto = null;
	private String tipologiaRifiuto = null;
	List<PuntoRaccolta> puntiDiRaccolta = null;
	private ActionBarActivity abActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		abActivity = (ActionBarActivity) getActivity();

		setHasOptionsMenu(true);

		abActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		abActivity.getSupportActionBar().setHomeButtonEnabled(true);
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_rifiuto_details, container, false);
		return viewGroup;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Bundle bundle = getArguments();
		if (bundle.containsKey(ArgUtils.ARGUMENT_RIFIUTO))
			rifiuto = bundle.getString(ArgUtils.ARGUMENT_RIFIUTO);

		try {
			// if (tipologiaRaccolta != null) {
			tipologiaRifiuto = RifiutiHelper.getTipoRifiuto(rifiuto);
			if (tipologiaRifiuto != null) {
				puntiDiRaccolta = RifiutiHelper.getPuntiRaccoltaPerTipoRifiuto(tipologiaRifiuto);
			}
			// } else if (tipologiaRifiuto != null) {
			// list = RifiutiHelper.getRifiutoPerTipoRifiuti(tipologiaRifiuto);
			// }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// setListAdapter(adapter);
		TextView tipoRifiuto = (TextView) getActivity().findViewById(R.id.tipoRifiutoLabel);
		tipoRifiuto.setText(tipologiaRifiuto);
		PuntoDiRaccoltaAdapter adapter = new PuntoDiRaccoltaAdapter(getActivity(), R.layout.rifiuto_adapter,
				puntiDiRaccolta);
		ListView listPuntiRaccolta = (ListView) getActivity().findViewById(R.id.puntoraccolta_list);
		listPuntiRaccolta.setAdapter(adapter);
		listPuntiRaccolta.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
				PuntoDiRaccoltaDetailFragment fragment = new PuntoDiRaccoltaDetailFragment();

				Bundle args = new Bundle();
				args.putSerializable(ArgUtils.ARGUMENT_PUNTO_DI_RACCOLTA, puntiDiRaccolta.get(arg2));
				fragment.setArguments(args);
				fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				// fragmentTransaction.detach(this);
				fragmentTransaction.replace(R.id.content_frame, fragment, "puntodiraccolta");
				fragmentTransaction.addToBackStack(fragment.getTag());
				fragmentTransaction.commit();
			}
		});
	}

	@Override
	public void onStart() {
		super.onStart();
		abActivity.getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		if (rifiuto != null)
			{
			abActivity.getSupportActionBar().setTitle(abActivity.getString(R.string.rifiuto_title) + " : " + rifiuto);
			}
		else if (tipologiaRifiuto!= null){
			abActivity.getSupportActionBar().setTitle(abActivity.getString(R.string.tipo_di_rifiuto_title) + " : " + tipologiaRifiuto);

		}

	}
}
