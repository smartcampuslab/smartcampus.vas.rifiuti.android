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
	private List<DatiTipologiaRaccolta> calendari;

	
	public TipologieAdapter(Context context, int resource, List<DatiTipologiaRaccolta> objects) {
		super(context, resource, objects);
		this.context = context;
		this.layoutResourceId = resource;
		this.calendari = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		RifiutoPlaceholder e = null;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		row = inflater.inflate(layoutResourceId, parent, false);
		e = new RifiutoPlaceholder();
		e.mTxtTipologia = (TextView) row.findViewById(R.id.tipologiaraccolta_descrizione);
		e.tipologia = getItem(position);
		e.mTxtTipologia.setText(e.tipologia.getTipologiaRaccolta());

		return row;
	}

	private class RifiutoPlaceholder {
		private DatiTipologiaRaccolta tipologia;
		private TextView mTxtTipologia;
	}
}
