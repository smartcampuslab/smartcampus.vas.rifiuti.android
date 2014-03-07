package eu.trentorise.smartcampus.rifiuti;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class RifiutoAdapter extends ArrayAdapter<String>{
	private Context context;
	private int layoutResourceId;
	private List<String> rifiuti;
	
	public RifiutoAdapter(Context context, int resource, List<String> objects) {
		super(context, resource, objects);
		this.context = context;
		this.layoutResourceId=resource;
		this.rifiuti=objects;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		RifiutoPlaceholder e = null;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(layoutResourceId, parent, false);
			e = new RifiutoPlaceholder();
			e.mTxtRifiuto = (TextView) row.findViewById(R.id.rifiuto_placeholder_title);
			row.setTag(e);
		} else {
			e = (RifiutoPlaceholder) row.getTag();
		}
		e.rifiuto = getItem(position);
		e.mTxtRifiuto.setText(e.rifiuto);

		return row;
	}
	private class RifiutoPlaceholder{
		private String rifiuto;
		private TextView mTxtRifiuto;
	}

}
