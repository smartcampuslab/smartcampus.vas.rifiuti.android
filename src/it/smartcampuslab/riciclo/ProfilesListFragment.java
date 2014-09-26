package it.smartcampuslab.riciclo;

import it.smartcampuslab.riciclo.model.Profile;
import it.smartcampuslab.riciclo.utils.PreferenceUtils;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ProfilesListFragment extends ListFragment {

	private ActionBarActivity abActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_profiles_list, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		abActivity = (ActionBarActivity) getActivity();
		abActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		abActivity.getSupportActionBar().setHomeButtonEnabled(true);

		List<Profile> profiles = PreferenceUtils.getProfiles(getActivity());
		setListAdapter(new ProfileAdapter(getActivity(), profiles));
		// setEmptyText(getString(R.string.niente_profili));

		// we can't let the user switch fragment without having a profile
		if (getActivity() instanceof MainActivity) {
			if (profiles.isEmpty()) {
				((MainActivity) getActivity()).lockDrawer();
			} else {
				((MainActivity) getActivity()).unlockDrawer();
			}
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		abActivity.getSupportActionBar().setTitle(abActivity.getString(R.string.profili_title));

		if (abActivity instanceof MainActivity) {
			((MainActivity) abActivity).unlockDrawer();
			((MainActivity) abActivity).showDrawerIndicator();
			abActivity.getSupportActionBar().setHomeButtonEnabled(true);
			abActivity.supportInvalidateOptionsMenu();
		}
		getListView().setPadding(0, 4, 0, 0);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.profiles_list, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_add) {
			goToDetail(-1);
		} else {
			return super.onOptionsItemSelected(item);
		}
		((ActionBarActivity) getActivity()).supportInvalidateOptionsMenu();
		return true;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		goToDetail(position);

	}

	private void goToDetail(int position) {
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ProfileFragment pf;
		pf = ProfileFragment.newInstance(position);
		ft.addToBackStack(null);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.replace(R.id.content_frame, pf);
		ft.commit();
	}

	private class ProfileAdapter extends ArrayAdapter<Profile> {

		public ProfileAdapter(Context context, List<Profile> objects) {
			super(context, R.layout.profile_row, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Profile tmp = getItem(position);
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.profile_row, parent, false);
			}
			TextView name = (TextView) convertView.findViewById(R.id.row_profile_name);
			if (!name.getText().toString().equals(tmp.getName()))
				name.setText(tmp.getName());
			TextView stuff = (TextView) convertView.findViewById(R.id.row_profile_stuff);
			if (!stuff.getText().toString().equals(tmp.toString()))
				stuff.setText(tmp.toString());
			return convertView;
		}

	}
}
