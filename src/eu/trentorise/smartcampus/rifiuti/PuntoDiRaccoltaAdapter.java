package eu.trentorise.smartcampus.rifiuti;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import eu.trentorise.smartcampus.rifiuti.model.PuntoRaccolta;

public class PuntoDiRaccoltaAdapter extends ArrayAdapter<PuntoRaccolta> {
	private Context context;
	private int layoutResourceId;

	public PuntoDiRaccoltaAdapter(Context context, int resource, List<PuntoRaccolta> objects) {
		super(context, resource, objects);
		this.context = context;
		this.layoutResourceId = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView row;

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = (TextView) inflater.inflate(layoutResourceId, parent, false);
		} else {
			row = (TextView) convertView;
		}

		PuntoRaccolta pr = getItem(position);
		row.setText(context.getString(R.string.puntoraccolta_list_item, pr.getTipologiaPuntiRaccolta(), pr.dettaglio()));

		return row;
	}
}
