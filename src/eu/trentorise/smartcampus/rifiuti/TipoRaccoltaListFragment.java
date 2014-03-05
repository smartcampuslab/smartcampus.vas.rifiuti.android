package eu.trentorise.smartcampus.rifiuti;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import eu.trentorise.smartcampus.rifiuti.data.RifiutiHelper;
import eu.trentorise.smartcampus.rifiuti.model.DatiTipologiaRaccolta;

public class TipoRaccoltaListFragment extends ListFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		List<DatiTipologiaRaccolta> types = RifiutiHelper.readTipologiaRaccolta();
		setListAdapter(new TipoRaccoltaAdapter(getActivity(), types));
		setEmptyText(getString(R.string.niente_tipi_raccolta));
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
//		FragmentManager fm = getFragmentManager();
//		FragmentTransaction ft = fm.beginTransaction();
//		ProfileFragment pf;
//		pf = ProfileFragment.newIstance(position);
//		ft.addToBackStack(null);
//		ft.setCustomAnimations(R.anim.enter,R.anim.exit,R.anim.popenter,R.anim.popexit);
//		ft.replace(R.id.content_frame, pf);
//		ft.commit();
	}

	private class TipoRaccoltaAdapter extends ArrayAdapter<DatiTipologiaRaccolta> {

		public TipoRaccoltaAdapter(Context context, List<DatiTipologiaRaccolta> objects) {
			super(context, R.layout.tipo_raccolta_row, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			DatiTipologiaRaccolta tmp = getItem(position);
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.tipo_raccolta_row, parent,
						false);
			}
			TextView name = (TextView) convertView.findViewById(R.id.row_type_name);
			name.setText(tmp.getTipologiaRaccolta());
			TextView type = (TextView) convertView.findViewById(R.id.row_sub_type);
			type.setText(tmp.getTipologiaPuntoRaccolta());
			View colorView = convertView.findViewById(R.id.color_view);
			if (tmp.getColore() != null && tmp.getColore().length() > 0) {
				colorView.setVisibility(View.VISIBLE);
				colorView.setBackgroundColor(RifiutiHelper.getColorResource(getActivity(), tmp.getColore()));
			} else {
				colorView.setVisibility(View.GONE);
			}
			return convertView;
		}

	}
}
