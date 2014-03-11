package eu.trentorise.smartcampus.rifiuti;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import eu.trentorise.smartcampus.rifiuti.model.DatiTipologiaRaccolta;

public class TipologieAdapter extends ArrayAdapter<DatiTipologiaRaccolta> {
	private Context context;
	private int layoutResourceId;

	public TipologieAdapter(Context context, int resource, List<DatiTipologiaRaccolta> objects) {
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

		DatiTipologiaRaccolta tipologia = getItem(position);
		row.setText(tipologia.getTipologiaRaccolta());

		return row;
	}

}
