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
	private boolean showTipoPuntoRaccolta;

	public TipologieAdapter(Context context, int resource, List<DatiTipologiaRaccolta> objects, boolean showTipoPuntoRaccolta) {
		super(context, resource, objects);
		this.context = context;
		this.layoutResourceId = resource;
		this.showTipoPuntoRaccolta = showTipoPuntoRaccolta;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		RifiutoPlaceholder e = null;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		row = inflater.inflate(layoutResourceId, parent, false);
		e = new RifiutoPlaceholder();
		e.mTxtTipologia = (TextView) row.findViewById(R.id.tipologiaraccolta);
		e.mTxtInfo = (TextView) row.findViewById(R.id.tipologiaraccolta_info);
		e.tipologia = getItem(position);
		if (showTipoPuntoRaccolta && e.tipologia.getTipologiaPuntoRaccolta() != null) {
			e.mTxtTipologia.setText(context.getResources().getString(R.string.tipologia_raccolta_ext,
					e.tipologia.getTipologiaPuntoRaccolta(), e.tipologia.getTipologiaRaccolta()));
		} else {
			e.mTxtTipologia.setText(e.tipologia.getTipologiaRaccolta());
		}
		String info = e.tipologia.getInfo();
		if (info != null && info.length() > 0) {
			e.mTxtInfo.setText(info);
			e.mTxtInfo.setVisibility(View.VISIBLE);
		} else {
			e.mTxtInfo.setVisibility(View.GONE);
		}

		return row;
	}

	private class RifiutoPlaceholder {
		private DatiTipologiaRaccolta tipologia;
		private TextView mTxtTipologia;
		private TextView mTxtInfo;
	}
}
