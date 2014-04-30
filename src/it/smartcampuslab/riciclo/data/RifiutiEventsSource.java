package it.smartcampuslab.riciclo.data;

import it.smartcampuslab.riciclo.model.CalendarioEvent;
import it.smartcampuslab.riciclo.model.CalendarioItem;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import android.util.SparseArray;

import com.tyczj.extendedcalendarview.EventsSource;

public class RifiutiEventsSource implements EventsSource<CalendarioEvent> {

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
				// TODO: color?
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
