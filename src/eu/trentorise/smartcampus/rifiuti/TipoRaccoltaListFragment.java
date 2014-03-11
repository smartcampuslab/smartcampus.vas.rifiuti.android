package eu.trentorise.smartcampus.rifiuti;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import eu.trentorise.smartcampus.rifiuti.data.RifiutiHelper;
import eu.trentorise.smartcampus.rifiuti.model.DatiTipologiaRaccolta;
import eu.trentorise.smartcampus.rifiuti.utils.ArgUtils;

public class TipoRaccoltaListFragment extends ListFragment {

	private List<List<DatiTipologiaRaccolta>> mTypes = null;
	private ActionBarActivity abActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_tipi_list, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		abActivity = (ActionBarActivity) getActivity();

		abActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		abActivity.getSupportActionBar().setHomeButtonEnabled(true);

		List<DatiTipologiaRaccolta> alltypes = RifiutiHelper
				.readTipologiaRaccolta();
		mTypes = new ArrayList<List<DatiTipologiaRaccolta>>();
		LinkedHashMap<String, List<DatiTipologiaRaccolta>> map = new LinkedHashMap<String, List<DatiTipologiaRaccolta>>();
		for (DatiTipologiaRaccolta dtr : alltypes) {
			List<DatiTipologiaRaccolta> list = map.get(dtr
					.getTipologiaRaccolta());
			if (list == null) {
				list = new ArrayList<DatiTipologiaRaccolta>();
				map.put(dtr.getTipologiaRaccolta(), list);
			}
			list.add(dtr);
		}
		for (Entry<String, List<DatiTipologiaRaccolta>> entry : map.entrySet()) {
			mTypes.add(entry.getValue());
		}
		setListAdapter(new TipoRaccoltaAdapter(getActivity(), mTypes));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_add) {
			goToDetail(-1);
		} else
			return super.onOptionsItemSelected(item);
		((ActionBarActivity) getActivity()).supportInvalidateOptionsMenu();
		return true;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		goToDetail(position);

	}

	private void goToDetail(int position) {
		Intent intent = new Intent(getActivity(),
				RifiutiManagerContainerActivity.class);
		intent.putExtra(ArgUtils.ARGUMENT_TIPOLOGIA_RACCOLTA,
				mTypes.get(position).get(0).getTipologiaRaccolta());
		startActivity(intent);

		// FragmentManager fm = getFragmentManager();
		// FragmentTransaction ft = fm.beginTransaction();
		// ProfileFragment pf;
		// pf = ProfileFragment.newIstance(position);
		// ft.addToBackStack(null);
		// ft.setCustomAnimations(R.anim.enter,R.anim.exit,R.anim.popenter,R.anim.popexit);
		// ft.replace(R.id.content_frame, pf);
		// ft.commit();
	}

	private class TipoRaccoltaAdapter extends
			ArrayAdapter<List<DatiTipologiaRaccolta>> {

		public TipoRaccoltaAdapter(Context context,
				List<List<DatiTipologiaRaccolta>> objects) {
			super(context, R.layout.tipo_raccolta_row, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			List<DatiTipologiaRaccolta> tmpList = getItem(position);
			DatiTipologiaRaccolta tmp = tmpList.get(0);
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.tipo_raccolta_row,
						parent, false);
			}
			TextView name = (TextView) convertView
					.findViewById(R.id.row_type_name);
			name.setText(tmp.getTipologiaRaccolta());
			LinearLayout types = (LinearLayout) convertView
					.findViewById(R.id.row_sub_type);
			types.removeAllViews();
			for (DatiTipologiaRaccolta dtr : tmpList) {
				TextView tv = (TextView) getActivity().getLayoutInflater()
						.inflate(R.layout.tipo_punto_raccolta_row, null);
				tv.setText(dtr.getTipologiaPuntoRaccolta());
				types.addView(tv);
				if (dtr.getColore() != null && dtr.getColore().length() > 0) {
					((GradientDrawable) tv.getBackground())
							.setColor(RifiutiHelper.getColorResource(
									getActivity(), tmp.getColore()));
				} else {
					((GradientDrawable) tv.getBackground())
							.setColor(getResources().getColor(
									R.color.rifiuti_light));
				}
			}

			// type.setText(tmp.getTipologiaPuntoRaccolta());
			// View colorView = convertView.findViewById(R.id.color_view);
			// if (tmp.getColore() != null && tmp.getColore().length() > 0) {
			// colorView.setVisibility(View.VISIBLE);
			// ((GradientDrawable)colorView.getBackground()).setColor(RifiutiHelper.getColorResource(getActivity(),
			// tmp.getColore()));
			// } else {
			// colorView.setVisibility(View.GONE);
			// }
			return convertView;
		}

	}

	@Override
	public void onStart() {
		super.onStart();
		abActivity.getSupportActionBar().setTitle(
				abActivity.getString(R.string.tipologiediraccolta_label));
	}
}