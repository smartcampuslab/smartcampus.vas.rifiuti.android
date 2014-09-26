package it.smartcampuslab.riciclo;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class RifiutoAdapter extends ArrayAdapter<String> {
	private Context context;
	private int layoutResourceId;

	public RifiutoAdapter(Context context, int resource, List<String> objects) {
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

		String rifiuto = getItem(position);
		row.setText(rifiuto);

		return row;
	}

}
