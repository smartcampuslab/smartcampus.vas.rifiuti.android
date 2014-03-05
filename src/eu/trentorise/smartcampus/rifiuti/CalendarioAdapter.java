package eu.trentorise.smartcampus.rifiuti;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import eu.trentorise.smartcampus.rifiuti.model.Calendario;

public class CalendarioAdapter extends ArrayAdapter<Calendario>{
	private Context context;
	private int layoutResourceId;
	private List<Calendario> calendari;

	
	public CalendarioAdapter(Context context, int resource, List<Calendario> objects) {
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
		e.mTxtCalendario = (TextView) row.findViewById(R.id.calendario_data);

		e.calendario = getItem(position);
		e.mTxtCalendario.setText(e.calendario.getIl() + " - " + e.calendario.getDalle()+ " - " + e.calendario.getAlle());

		return row;
	}

	private class RifiutoPlaceholder {
		private Calendario calendario;
		private TextView mTxtCalendario;
	}
}
