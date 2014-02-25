package eu.trentorise.smartcampus.rifiuti;

import java.util.List;

import org.json.JSONException;

import eu.trentorise.smartcampus.rifiuti.model.Profile;
import eu.trentorise.smartcampus.rifiuti.utils.PreferenceUtils;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ProfilesListFragment extends ListFragment {

	private List<Profile> mProfiles;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mProfiles = PreferenceUtils.getProfiles(getActivity());
		setListAdapter(new ProfileAdapter(getActivity(), mProfiles));
		setEmptyText(getString(R.string.niente_profili));

	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ProfileFragment pf;
		try {
			pf = ProfileFragment.newIstance(mProfiles.get(position));
			ft.replace(R.id.content_frame, pf);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commit();
		} catch (Exception e) {
			Log.e(ProfilesListFragment.class.getName(), e.toString());
		}
	}

	private class ProfileAdapter extends ArrayAdapter<Profile> {

		public ProfileAdapter(Context context, List<Profile> objects) {
			super(context, R.layout.profile_row, objects);

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Profile tmp = getItem(position);
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.profile_row, parent,
						false);
			}
			TextView name = (TextView) convertView
					.findViewById(R.id.row_profile_name);
			if (!name.getText().toString().equals(tmp.getName()))
				name.setText(tmp.getName());
			TextView stuff = (TextView) convertView
					.findViewById(R.id.row_profile_stuff);
			if (!stuff.getText().toString().equals(tmp.toString()))
				stuff.setText(tmp.toString());
			return convertView;
		}

	}
}
