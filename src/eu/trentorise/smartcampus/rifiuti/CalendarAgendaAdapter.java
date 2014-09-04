package eu.trentorise.smartcampus.rifiuti;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import eu.trentorise.smartcampus.rifiuti.data.RifiutiHelper;
import eu.trentorise.smartcampus.rifiuti.model.CalendarioAgendaEntry;
import eu.trentorise.smartcampus.rifiuti.model.CalendarioEvent;
import eu.trentorise.smartcampus.rifiuti.model.DatiTipologiaRaccolta;
import eu.trentorise.smartcampus.rifiuti.model.PuntoRaccolta;

public class CalendarAgendaAdapter extends ArrayAdapter<CalendarioAgendaEntry> {

	private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE d MMMM yyyy", Locale.getDefault());
	private static final String TIME_FORMAT = "H:mm";
	private static final String TIPOLOGIA_PORTA_A_PORTA = "Porta a porta";
	private List<DatiTipologiaRaccolta> tipologiaRaccoltaList = RifiutiHelper.readTipologiaRaccolta();

	private Calendar todayCalendar = Calendar.getInstance(Locale.getDefault());

	private Context mContext;
	private int resource;

	public CalendarAgendaAdapter(Context context, int resource) {
		super(context, resource);
		this.mContext = context;
		this.resource = resource;
	}

	public CalendarAgendaAdapter(Context context, int resource, List<CalendarioAgendaEntry> objects) {
		super(context, resource, objects);
		this.mContext = context;
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
			holder.date = (TextView) row.findViewById(R.id.date_textview);
			holder.prsLayout = (LinearLayout) row.findViewById(R.id.prs_layout);
			row.setTag(holder);
		} else {
			holder = (Holder) row.getTag();
		}

		// remove custom views
		holder.prsLayout.removeAllViews();

		// hide empty days
		if (cae.getEventsMap().isEmpty()) {
			holder.date.setVisibility(View.GONE);
			return row;
		} else {
			holder.date.setVisibility(View.VISIBLE);
		}

		String dateString = dateFormatter.format(cae.getCalendar().getTime());
		holder.date.setText(dateString);

		// highlight today
		if (dateString.equals(dateFormatter.format(todayCalendar.getTime()))) {
			holder.date.setTextColor(mContext.getResources().getColor(R.color.rifiuti_green_middle));
			holder.date.setTypeface(null, Typeface.BOLD);
		} else {
			holder.date.setTextColor(mContext.getResources().getColor(android.R.color.black));
			holder.date.setTypeface(null, Typeface.NORMAL);
		}

		for (String tpr : cae.getEventsMap().keySet()) {
			View tprRow = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.calendaragenda_tpr_row, null);
			TextView tprTextView = (TextView) tprRow.findViewById(R.id.tpr_textview);
			// TextView descTextView = (TextView)
			// tprRow.findViewById(R.id.desc_textview);
			LinearLayout tprLinearLayout = (LinearLayout) tprRow.findViewById(R.id.tpr_linearlayout);

			tprTextView.setText(tpr);

			Map<PuntoRaccolta, List<CalendarioEvent>> map = cae.getEventsMap().get(tpr);
			for (PuntoRaccolta pr : map.keySet()) {
				// TextView prTextView = (TextView) ((Activity)
				// getContext()).getLayoutInflater().inflate(
				// R.layout.calendaragenda_pr_row, null);

				TextView prTextView = new TextView(mContext);
				prTextView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

				// if (desc.length() > 0) {
				// desc += "\n";
				// }

				String desc = "";
				Drawable drawable = null;

				if (pr.dettaglio() != null && pr.dettaglio().length() > 0) {
					desc += pr.dettaglio() + "\n";
				} else {
					desc += pr.getNote() + "\n";
				}

				for (DatiTipologiaRaccolta dtr : tipologiaRaccoltaList) {
					if (dtr.getTipologiaPuntoRaccolta().equalsIgnoreCase(pr.getTipologiaPuntiRaccolta())) {
						drawable = RifiutiHelper.getTypeColorResource(mContext, dtr.getTipologiaPuntoRaccolta(),
								dtr.getColore());
						break;
					}
				}

				List<CalendarioEvent> eventsList = map.get(pr);
				for (int j = 0; j < eventsList.size(); j++) {
					CalendarioEvent event = eventsList.get(j);
					String startDate = event.getStartDate(TIME_FORMAT);
					String endDate = event.getEndDate(TIME_FORMAT);
					if (!startDate.equals(endDate)) {
						desc += getContext().getResources().getString(R.string.from_time_to_time, startDate, endDate);
						if (j + 1 != eventsList.size()) {
							desc += "\n";
						}
					}
				}

				prTextView.setText(desc);
				if (drawable != null) {
					prTextView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
					prTextView.setGravity(Gravity.CENTER_VERTICAL);
				}

				tprLinearLayout.addView(prTextView);
			}

			// descTextView.setText(desc);

			holder.prsLayout.addView(tprRow);
		}

		return row;
	}

	private class Holder {
		TextView date;
		LinearLayout prsLayout;
	}

}
