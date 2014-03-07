package eu.trentorise.smartcampus.rifiuti;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import eu.trentorise.smartcampus.rifiuti.model.CalendarioAgendaEntry;
import eu.trentorise.smartcampus.rifiuti.model.CalendarioEvent;

public class CalendarAgendaAdapter extends ArrayAdapter<CalendarioAgendaEntry> {

	private static final String TIME_FORMAT = "H:mm";

	private int resource;

	public CalendarAgendaAdapter(Context context, int resource, List<CalendarioAgendaEntry> objects) {
		super(context, resource, objects);
		this.resource = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		Holder holder;

		CalendarioAgendaEntry cae = getItem(position);

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

		holder.title.setText(cae.getPuntoRaccolta().getIndirizzo());

		String description = "";
		for (int i = 0; i < cae.getEvents().size(); i++) {
			CalendarioEvent event = cae.getEvents().get(i);
			description += getContext().getResources().getString(R.string.from_time_to_time, event.getStartDate(TIME_FORMAT),
					event.getEndDate(TIME_FORMAT));
			if (i + 1 != cae.getEvents().size()) {
				description += "\n";
			}
		}
		holder.description.setText(description);

		return row;
	}

	private class Holder {
		TextView separator;
		TextView title;
		TextView description;
	}

}
