package it.smartcampuslab.riciclo;

import it.smartcampuslab.riciclo.R;
import it.smartcampuslab.riciclo.data.RifiutiHelper;
import it.smartcampuslab.riciclo.model.CalendarioAgendaEntry;
import it.smartcampuslab.riciclo.model.CalendarioEvent;
import it.smartcampuslab.riciclo.model.DatiTipologiaRaccolta;
import it.smartcampuslab.riciclo.model.PuntoRaccolta;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CalendarAgendaAdapter extends ArrayAdapter<CalendarioAgendaEntry> {

	private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE d MMMM yyyy", Locale.getDefault());
	private static final String TIME_FORMAT = "H:mm";
	private List<DatiTipologiaRaccolta> tipologiaRaccoltaList = RifiutiHelper.readTipologiaRaccolta();

	private Calendar todayCalendar = Calendar.getInstance(Locale.getDefault());

	private Context mContext;
	private int resource;

	private Integer selected;

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

	public void addAllAtBeginning(List<CalendarioAgendaEntry> itemsList) {
		Collections.reverse(itemsList);
		for (CalendarioAgendaEntry c : itemsList) {
			this.insert(c, 0);
		}
	}
	
	public void addAllAtEnd(List<CalendarioAgendaEntry> itemsList) {
		for (CalendarioAgendaEntry c : itemsList) {
			this.add(c);
		}
	}

	public Integer getDayPosition(Calendar cal) {
		for (int i = 0; i < getCount(); i++) {
			CalendarioAgendaEntry cae = getItem(i);
			boolean sameYear = cae.getCalendar().get(Calendar.YEAR) == cal.get(Calendar.YEAR);
			boolean sameMonth = cae.getCalendar().get(Calendar.MONTH) == cal.get(Calendar.MONTH);
			boolean sameDay = cae.getCalendar().get(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH);

			if (sameYear && sameMonth && sameDay) {
				return i;
			}
		}
		return null;
	}

	public Integer getTodayPosition() {
		return getDayPosition(todayCalendar);
	}

	public void setSelected(Integer selected) {
		this.selected = selected;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		Holder holder;

		CalendarioAgendaEntry cae = getItem(position);

		if (row == null) {
			row = ((Activity) getContext()).getLayoutInflater().inflate(resource, parent, false);
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
			holder.prsLayout.setVisibility(View.GONE);
			return row;
		} else {
			holder.date.setVisibility(View.VISIBLE);
			holder.prsLayout.setVisibility(View.VISIBLE);
		}

		String dateString = dateFormatter.format(cae.getCalendar().getTime());
		holder.date.setText(dateString);

		// highlight today
		if (dateString.equals(dateFormatter.format(todayCalendar.getTime()))) {
			holder.date.setTextColor(mContext.getResources().getColor(R.color.rifiuti_green_middle));
			holder.date.setTypeface(null, Typeface.BOLD);
		} else if (cae.getCalendar().before(todayCalendar)) {
			// previous days
			holder.date.setTextColor(mContext.getResources().getColor(R.color.rifiuti_middle));
			holder.date.setTypeface(null, Typeface.NORMAL);
		} else {
			// next days
			holder.date.setTextColor(mContext.getResources().getColor(android.R.color.black));
			holder.date.setTypeface(null, Typeface.NORMAL);
		}

		for (String tpr : cae.getEventsMap().keySet()) {
			View tprRow = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.calendaragenda_tpr_row,
					holder.prsLayout, false);
			TextView tprTextView = (TextView) tprRow.findViewById(R.id.tpr_textview);
			// TextView descTextView = (TextView)
			// tprRow.findViewById(R.id.desc_textview);
			LinearLayout tprLinearLayout = (LinearLayout) tprRow.findViewById(R.id.tpr_linearlayout);

			tprTextView.setText(tpr);

			Map<PuntoRaccolta, List<CalendarioEvent>> map = cae.getEventsMap().get(tpr);
			for (PuntoRaccolta pr : map.keySet()) {
				// TextView prTextView = (TextView) ((Activity)
				// getContext()).getLayoutInflater().inflate(
				// R.layout.calendaragenda_pr_row, tprLinearLayout, false);

				TextView prTextView = new TextView(mContext);
				prTextView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				prTextView.setGravity(Gravity.CENTER_VERTICAL);

				String desc = "";
				Drawable drawable = null;

				if (pr.dettaglio() != null && pr.dettaglio().length() > 0) {
					desc += pr.dettaglio();
				} else {
					desc += pr.getNote();
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
						if (desc.length() > 0) {
							desc += "\n";
						}
						desc += getContext().getResources().getString(R.string.from_time_to_time, startDate, endDate);
						if (j + 1 != eventsList.size()) {
							desc += "\n";
						}
					}
				}

				prTextView.setText(desc);
				if (drawable != null) {
					prTextView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
					prTextView.setCompoundDrawablePadding(mContext.getResources().getDimensionPixelSize(
							R.dimen.fragment_horizontal_margin));
				}

				tprLinearLayout.addView(prTextView);
			}

			// descTextView.setText(desc);

			holder.prsLayout.addView(tprRow);
		}

		final TransitionDrawable prsLayoutBackground = (TransitionDrawable) holder.prsLayout.getBackground();
		if (selected != null && selected == position) {
			int fadein = 500;
			int wait = 500 + fadein;
			final int fadeout = 1000;
			prsLayoutBackground.startTransition(fadein);
			holder.prsLayout.postDelayed(new Runnable() {
				@Override
				public void run() {
					prsLayoutBackground.reverseTransition(fadeout);
				}
			}, wait);
		} else {
			prsLayoutBackground.resetTransition();
		}

		selected = null;

		return row;
	}

	private class Holder {
		TextView date;
		LinearLayout prsLayout;
	}

}
