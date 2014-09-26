package it.smartcampuslab.riciclo;

import it.smartcampuslab.riciclo.R;
import it.smartcampuslab.riciclo.model.CalendarioDayEntry;
import it.smartcampuslab.riciclo.model.CalendarioEvent;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CalendarDayAdapter extends ArrayAdapter<CalendarioDayEntry> {

	private static final String TIME_FORMAT = "H:mm";

	private int resource;

	public CalendarDayAdapter(Context context, int resource, List<CalendarioDayEntry> objects) {
		super(context, resource, objects);
		this.resource = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		Holder holder;

		CalendarioDayEntry cae = getItem(position);

		if (row == null) {
			row = ((Activity) getContext()).getLayoutInflater().inflate(resource, null);
			holder = new Holder();
			holder.separator = (TextView) row.findViewById(R.id.separator_textView);
			holder.title = (TextView) row.findViewById(R.id.title_textView);
			holder.description = (TextView) row.findViewById(R.id.description_textView);
			row.setTag(holder);
		} else {
			holder = (Holder) row.getTag();
		}

		String tipoPuntoDiRaccolta = cae.getPuntoRaccolta().getTipologiaPuntiRaccolta();
		if (position == 0 || !tipoPuntoDiRaccolta.equals(getItem(position - 1).getPuntoRaccolta().getTipologiaPuntiRaccolta())) {
			holder.separator.setText(cae.getPuntoRaccolta().getTipologiaPuntiRaccolta());
			holder.separator.setVisibility(View.VISIBLE);
		} else {
			holder.separator.setVisibility(View.GONE);
		}

		String title = cae.getPuntoRaccolta().dettaglio();

		String description = "";
		for (int i = 0; i < cae.getEvents().size(); i++) {
			CalendarioEvent event = cae.getEvents().get(i);
			String startDate = event.getStartDate(TIME_FORMAT);
			String endDate = event.getEndDate(TIME_FORMAT);
			if (!startDate.equals(endDate)) {
				description += getContext().getResources().getString(R.string.from_time_to_time, startDate, endDate);
				if (i + 1 != cae.getEvents().size()) {
					description += "\n";
				}
			}
		}
		holder.description.setText(description);

		if (title.length() == 0) {
			holder.title.setVisibility(View.GONE);
		} else {
			holder.title.setText(title);
			holder.title.setVisibility(View.VISIBLE);
		}

		if (description.length() == 0) {
			holder.description.setVisibility(View.GONE);
		} else {
			holder.description.setText(description);
			holder.description.setVisibility(View.VISIBLE);
		}

		return row;
	}

	private class Holder {
		TextView separator;
		TextView title;
		TextView description;
	}

}
