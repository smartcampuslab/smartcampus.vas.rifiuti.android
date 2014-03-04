package eu.trentorise.smartcampus.rifiuti.data;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import android.util.SparseArray;

import com.tyczj.extendedcalendarview.CalendarEvent;
import com.tyczj.extendedcalendarview.CalendarEventsSource;

import eu.trentorise.smartcampus.rifiuti.model.CalendarioItem;

public class RifiutiEventsSource implements CalendarEventsSource {

	public SparseArray<Collection<CalendarEvent>> getEventsByMonth(int month) {
		Calendar cal = Calendar.getInstance(Locale.getDefault());
		cal.set(Calendar.MONTH, month);

		SparseArray<Collection<CalendarEvent>> eventsByMonth = new SparseArray<Collection<CalendarEvent>>(
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

	private void filler(Calendar cal, SparseArray<Collection<CalendarEvent>> eventsByMonth) {
		List<List<CalendarioItem>> data = RifiutiHelper.getCalendarsForMonth(cal);
		for (int i = 0; i < data.size(); i++) {
			List<CalendarEvent> events = new ArrayList<CalendarEvent>();
			cal.set(Calendar.DAY_OF_MONTH, i+1);
			for (CalendarioItem item : data.get(i)) {
				CalendarEvent event = null;
				long start = 0, end = 0;
				try {
					start = item.getCalendar().start(cal);
					end = item.getCalendar().end(cal);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				event = new CalendarEvent(0, start, end);
				event.setTitle(item.getPoint().getTipologiaPuntiRaccolta());
				event.setDescription(item.getPoint().getIndirizzo());
				event.setLocation(item.getPoint().getLocalizzazione());
				// TODO: color?
				events.add(event);
				
			}
			eventsByMonth.append(i+1, events);
		}
		
	}

	private void testFiller(Calendar cal, SparseArray<Collection<CalendarEvent>> eventsByMonth) {
		/*
		 * TEST PURPOSES ONLY
		 */
		int max = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

		for (int dc = 1; dc <= max; dc++) {
			if (dc % 3 != 0) {
				continue;
			}

			List<CalendarEvent> dayEvents = new ArrayList<CalendarEvent>();

			int emax = dc % 2 != 0 ? 10 : 3;

			for (int i = 0; i < emax; i++) {
				CalendarEvent event = new CalendarEvent(i + 1, 1392509133, 1393509133);

				switch (i % 2) {
				case 0:
					event.setColor("red");
					break;
				case 1:
					event.setColor("blue");
					break;
				default:
					event.setColor("gray");
					break;
				}

				dayEvents.add(event);
			}

			eventsByMonth.append(dc, dayEvents);
		}
	}

}
