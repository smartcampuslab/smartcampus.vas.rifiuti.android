package it.smartcampuslab.riciclo;

import it.smartcampuslab.riciclo.data.RifiutiHelper;
import it.smartcampuslab.riciclo.model.Gestore;
import it.smartcampuslab.riciclo.model.Istituzione;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ContactsListFragment extends ListFragment {

	private ArrayList<HashMap<String, String>> data;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		loadData();
	}

	private void loadData() {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				data = new ArrayList<HashMap<String, String>>();
				data.addAll(getIstituzioniData(RifiutiHelper.getIstituzioni()));
				data.addAll(getGestoriData(RifiutiHelper.getGestori()));

				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						setListAdapter(new SimpleAdapter(getActivity(), data, R.layout.contacts_group, new String[] { "name" },
								new int[] { R.id.title_tv }));
					}
				});
				return null;
			}
		}.execute();
	}

	@Override
	public void onStart() {
		super.onStart();
		if (getActivity() instanceof MainActivity) {
			((MainActivity) getActivity()).unlockDrawer();
			((MainActivity) getActivity()).showDrawerIndicator();
			((MainActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
			getActivity().supportInvalidateOptionsMenu();
		}
		getListView().setDivider(getResources().getDrawable(R.color.rifiuti_light));
		getListView().setDividerHeight(1);
		((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(R.string.contacts_title);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			getActivity().finish();
			return true;
		}
		return false;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.addToBackStack(null);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.replace(R.id.content_frame, ContactsFragment.newInstance(data, position));
		ft.commit();
	}

	private ArrayList<HashMap<String, String>> getIstituzioniData(List<Istituzione> list) {
		ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
		for (Istituzione i : list) {
			HashMap<String, String> res = new HashMap<String, String>();
			res.put("name", i.getUfficio());
			res.put("description", i.getDescrizione());
			res.put("email", i.getEmail());
			res.put("phone", i.getTelefono());
			res.put("web", i.getSitoIstituzionale());
			res.put("pec", i.getPec());
			res.put("fax", i.getFax());
			res.put("address", i.getIndirizzo());
			res.put("office", i.getUfficio());
			res.put("opening", i.getOrarioUfficio());
			data.add(res);
		}
		return data;
	}

	private ArrayList<HashMap<String, String>> getGestoriData(List<Gestore> list) {
		ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
		for (Gestore i : list) {
			HashMap<String, String> res = new HashMap<String, String>();
			res.put("name", i.getRagioneSociale());
			res.put("description", i.getDescrizione());
			res.put("email", i.getEmail());
			res.put("phone", i.getTelefono());
			res.put("web", i.getSitoWeb());
			res.put("fax", i.getFax());
			res.put("address", i.getIndirizzo());
			res.put("office", i.getUfficio());
			res.put("opening", i.getOrarioUfficio());
			data.add(res);
		}
		return data;
	}

}