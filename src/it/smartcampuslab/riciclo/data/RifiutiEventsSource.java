package it.smartcampuslab.riciclo.data;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.SparseArray;

import com.tyczj.extendedcalendarview.EventsSource;

import it.smartcampuslab.riciclo.R;
import it.smartcampuslab.riciclo.model.CalendarioEvent;
import it.smartcampuslab.riciclo.model.CalendarioItem;

public class RifiutiEventsSource implements EventsSource<CalendarioEvent> {

	private Map<String, Integer> calendarEventsColors;

	public RifiutiEventsSource(Context ctx) {
		calendarEventsColors = new HashMap<String, Integer>();
		String[] array = ctx.getResources().getStringArray(R.array.calendar_events_strings);
		TypedArray valueArray = ctx.getResources().obtainTypedArray(R.array.calendar_events_colors);
		for (int i = 0; i < array.length; i++) {
			calendarEventsColors.put(array[i].toLowerCase(Locale.getDefault()),
					Integer.valueOf((valueArray.getColor(i, Color.GRAY))));
		}
		valueArray.recycle();
	}

	public SparseArray<Collection<CalendarioEvent>> getEventsByMonth(Calendar calendar) {
		Calendar cal = (Calendar) calendar.clone();
		SparseArray<Collection<CalendarioEvent>> eventsByMonth = new SparseArray<Collection<CalendarioEvent>>(
				cal.getActualMaximum(Calendar.DAY_OF_MONTH));

		/*
		 * TEST PURPOSES ONLY
		 */
		// testFiller(cal, eventsByMonth);
		/*
		 * end TEST PURPOSES ONLY
		 */

		filler(cal, eventsByMonth);

		return eventsByMonth;
	}

	private void filler(Calendar cal, SparseArray<Collection<CalendarioEvent>> eventsByMonth) {
		List<List<CalendarioItem>> data = RifiutiHelper.getCalendarsForMonth(cal);
		for (int i = 0; i < data.size(); i++) {
			List<CalendarioEvent> events = new ArrayList<CalendarioEvent>();
			cal.set(Calendar.DAY_OF_MONTH, i + 1);
			for (CalendarioItem item : data.get(i)) {
				CalendarioEvent event = null;
				long start = 0, end = 0;
				try {
					start = item.getCalendar().start(cal);
					end = item.getCalendar().end(cal);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				event = new CalendarioEvent(start, end, item);
				event.setName(item.getPoint().getTipologiaPuntiRaccolta());
				event.setDescription(item.getPoint().dettaglio());
				event.setLocation(item.getPoint().getLocalizzazione());
				event.setCalendarioItem(item);
				if (!item.getColor().equals("")) {
					Integer colorInteger = calendarEventsColors.get(item.getColor().toLowerCase(Locale.getDefault()));
					if (colorInteger != null) {
						event.setColor(colorInteger);
					}
				}
				events.add(event);
			}
			eventsByMonth.append(i + 1, events);
		}

	}

	// private void testFiller(Calendar cal, SparseArray<Collection<Event>>
	// eventsByMonth) {
	// /*
	// * TEST PURPOSES ONLY
	// */
	// int max = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
	//
	// for (int dc = 1; dc <= max; dc++) {
	// if (dc % 3 != 0) {
	// continue;
	// }
	//
	// List<Event> dayEvents = new ArrayList<Event>();
	//
	// int emax = dc % 2 != 0 ? 10 : 3;
	//
	// for (int i = 0; i < emax; i++) {
	// Event event = new Event(i + 1, 1392509133, 1393509133);
	//
	// switch (i % 2) {
	// case 0:
	// event.setColor("red");
	// break;
	// case 1:
	// event.setColor("blue");
	// break;
	// default:
	// event.setColor("gray");
	// break;
	// }
	//
	// dayEvents.add(event);
	// }
	//
	// eventsByMonth.append(dc, dayEvents);
	// }
	// }

}
