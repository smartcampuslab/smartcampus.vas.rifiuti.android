package it.smartcampuslab.riciclo;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DrawerArrayAdapter extends ArrayAdapter<String> {

	private Context context;
	private int resource;
	private TypedArray drawables;

	public DrawerArrayAdapter(Context context, int resource, String[] objects) {
		super(context, resource, objects);
		this.context = context;
		this.resource = resource;
		drawables = context.getResources().obtainTypedArray(R.array.drawer_entries_drawables);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
//		TextView entry;
//		Holder holder;
//
//		if (convertView == null) {
//			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
//			entry = (TextView) inflater.inflate(resource, parent, false);
//			holder = new Holder();
//			holder.text = getItem(position);
//			holder.drawable = drawables.getDrawable(position);
//			entry.setTag(holder);
//		} else {
//			entry = (TextView) convertView;
//			holder = (Holder) entry.getTag();
//		}
//
//		entry.setText(holder.text);
//		entry.setCompoundDrawablesWithIntrinsicBounds(holder.drawable, null, null, null);
//		return entry;
		View row = convertView;

		Holder holder;

		if (convertView == null) {
			row = ((Activity) getContext()).getLayoutInflater().inflate(resource, null);
			holder = new Holder();
			holder.text = (TextView) row.findViewById(R.id.drawer_menu_item);
			holder.drawable = (ImageView) row.findViewById(R.id.drawer_menu_icon);
			row.setTag(holder);
		} else {
			holder = (Holder) row.getTag();
		}

		holder.text.setText(getItem(position));
		holder.drawable.setImageDrawable(drawables.getDrawable(position));
//		holder.setCompoundDrawablesWithIntrinsicBounds(holder.drawable, null, null, null);
		return row;
	}

	private class Holder {
		TextView text;
		ImageView drawable;
	}
	
//	private class Holder {
//		String text;
//		Drawable drawable;
//	}
}
