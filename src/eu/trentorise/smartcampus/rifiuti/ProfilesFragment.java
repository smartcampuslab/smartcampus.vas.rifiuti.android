package eu.trentorise.smartcampus.rifiuti;

import java.util.List;

import eu.trentorise.smartcampus.rifiuti.model.Profile;
import eu.trentorise.smartcampus.rifiuti.utils.PreferenceUtils;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ProfilesFragment extends ListFragment {

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		List<Profile> profiles = PreferenceUtils.getProfiles(getActivity());
		setListAdapter(new ProfileAdapter(getActivity(), profiles));
		setEmptyText(getString(R.string.niente_profili));

	}

	private class ProfileAdapter extends ArrayAdapter<Profile> {

		public ProfileAdapter(Context context, List<Profile> objects) {
			super(context, R.layout.profile_row, objects);

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Profile tmp =getItem(position);
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.profile_row, parent);
			}
			TextView name = (TextView) convertView.findViewById(R.id.row_profile_name);
			if(!name.getText().toString().equals(tmp.getName()))
				name.setText(tmp.getName());
			TextView stuff = (TextView) convertView.findViewById(R.id.row_profile_stuff);
			if(!stuff.getText().toString().equals(tmp.toString()))
				stuff.setText(tmp.toString());
			return convertView;
		}

	}

}
