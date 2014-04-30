package it.smartcampuslab.riciclo;

import it.smartcampuslab.riciclo.data.RifiutiHelper;
import it.smartcampuslab.riciclo.utils.ArgUtils;
import it.smartcampuslab.riciclo.utils.PreferenceUtils;

import java.util.List;

import com.google.android.gms.internal.bu;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class RifiutiListFragment extends ListFragment {

	private String tipologiaRaccolta = null;
	private String tipologiaRifiuto = null;
	private List<String> rifiuti = null;

	private ActionBarActivity abActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);
	}

	public static RifiutiListFragment newIstanceTipologiaRaccolta(String raccolta) {

		RifiutiListFragment rf = new RifiutiListFragment();
		Bundle b = new Bundle();
		b.putString(ArgUtils.ARGUMENT_TIPOLOGIA_RACCOLTA, raccolta);
		rf.setArguments(b);
		return rf;
	}

	public static RifiutiListFragment newIstanceTipologiaRifiuto(String rifiuto) {
		RifiutiListFragment rf = new RifiutiListFragment();
		Bundle b = new Bundle();
		b.putString(ArgUtils.ARGUMENT_TIPOLOGIA_RIFIUTO, rifiuto);
		rf.setArguments(b);
		return rf;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup viewGroup = (ViewGroup) inflater.inflate(
				R.layout.fragment_rifiuti_list, container, false);
		return viewGroup;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		FragmentTransaction fragmentTransaction = getActivity()
				.getSupportFragmentManager().beginTransaction();
		RifiutoDetailsFragment fragment = new RifiutoDetailsFragment();

		Bundle args = new Bundle();
		// if (tipologiaRaccolta != null)
		// args.putString(ArgUtils.ARGUMENT_TIPOLOGIA_RACCOLTA,
		// tipologiaRaccolta);
		// else if (tipologiaRifiuto != null)
		// args.putString(ArgUtils.ARGUMENT_TIPOLOGIA_RIFIUTO,
		// tipologiaRifiuto);
		args.putString(ArgUtils.ARGUMENT_RIFIUTO, rifiuti.get(position));
		fragment.setArguments(args);
		fragmentTransaction
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		// fragmentTransaction.detach(this);
		fragmentTransaction.replace(R.id.content_frame, fragment, "rifiuti");
		fragmentTransaction.addToBackStack(fragment.getTag());
		fragmentTransaction.commit();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		abActivity = (ActionBarActivity) getActivity();
		abActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		abActivity.getSupportActionBar().setHomeButtonEnabled(true);

		Bundle bundle = getArguments();
		if(bundle==null)
			bundle=getArguments();
		if(bundle==null)
			bundle = savedInstanceState;
		if (bundle.containsKey(ArgUtils.ARGUMENT_TIPOLOGIA_RACCOLTA))
			tipologiaRaccolta = bundle
					.getString(ArgUtils.ARGUMENT_TIPOLOGIA_RACCOLTA);
		if (bundle.containsKey(ArgUtils.ARGUMENT_TIPOLOGIA_RIFIUTO))
			tipologiaRifiuto = bundle
					.getString(ArgUtils.ARGUMENT_TIPOLOGIA_RIFIUTO);

		try {
			if (RifiutiHelper.getInstance() == null) {
				RifiutiHelper.init(getActivity());
				RifiutiHelper.setProfile(PreferenceUtils.getProfile(
						getActivity(), PreferenceUtils
								.getCurrentProfilePosition(getActivity())));
			}
			if (tipologiaRaccolta != null) {
				rifiuti = RifiutiHelper
						.getRifiutoPerTipoRaccolta(tipologiaRaccolta);
			} else if (tipologiaRifiuto != null) {
				rifiuti = RifiutiHelper
						.getRifiutoPerTipoRifiuti(tipologiaRifiuto);
			}
		} catch (Exception e) {
			Log.e(getClass().getSimpleName(), e.getMessage());
		}

		RifiutoAdapter adapter = new RifiutoAdapter(getActivity(), android.R.layout.simple_list_item_1, rifiuti);
		setListAdapter(adapter);
	}


	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putAll(getArguments());
		super.onSaveInstanceState(outState);
	}
	
}
