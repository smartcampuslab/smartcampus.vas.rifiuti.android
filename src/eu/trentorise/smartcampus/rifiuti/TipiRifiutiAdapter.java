package eu.trentorise.smartcampus.rifiuti;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import eu.trentorise.smartcampus.rifiuti.data.RifiutiHelper;

public class TipiRifiutiAdapter extends ArrayAdapter<String> {

	private int resource;

	public TipiRifiutiAdapter(Context context, int resource, List<String> objects) {
		super(context, resource, objects);
		this.resource = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView row;

		String tipoRifiuto = getItem(position);

		if (convertView == null) {
			row = (TextView) ((Activity) getContext()).getLayoutInflater().inflate(resource, null);
		} else {
			row = (TextView) convertView;
		}

		row.setText(tipoRifiuto);
		Drawable drawable = RifiutiHelper.getTipiRifiutoDrawable(getContext(), tipoRifiuto);
		if (drawable == null) {
			drawable = getContext().getResources().getDrawable(R.drawable.event_square);
		}
		row.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);

		return row;
	}

}
