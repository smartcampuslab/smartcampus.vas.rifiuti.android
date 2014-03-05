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
	private List<PuntoRaccolta> puntiDiRaccolta;

	public PuntoDiRaccoltaAdapter(Context context, int resource, List<PuntoRaccolta> objects) {
		super(context, resource, objects);
		this.context = context;
		this.layoutResourceId = resource;
		this.puntiDiRaccolta = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		RifiutoPlaceholder e = null;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		row = inflater.inflate(layoutResourceId, parent, false);
		e = new RifiutoPlaceholder();
		e.mTxtPuntoDiRaccolta = (TextView) row.findViewById(R.id.rifiuto_placeholder_title);

		e.puntoDiRaccolta = getItem(position);
		e.mTxtPuntoDiRaccolta.setText(e.puntoDiRaccolta.getTipologiaPuntiRaccolta() + " - "
				+ e.puntoDiRaccolta.getIndirizzo());

		return row;
	}

	private class RifiutoPlaceholder {
		private PuntoRaccolta puntoDiRaccolta;
		private TextView mTxtPuntoDiRaccolta;
	}
}
