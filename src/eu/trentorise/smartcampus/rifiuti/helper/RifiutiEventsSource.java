package eu.trentorise.smartcampus.rifiuti.helper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import android.util.SparseArray;

import com.tyczj.extendedcalendarview.CalendarEvent;
import com.tyczj.extendedcalendarview.CalendarEventsSource;

public class RifiutiEventsSource implements CalendarEventsSource {

	public SparseArray<Collection<CalendarEvent>> getEventsByMonth(int month) {
		Calendar cal = Calendar.getInstance(Locale.getDefault());
		cal.set(Calendar.MONTH, month);
		int max = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		
		SparseArray<Collection<CalendarEvent>> eventsByMonth = new SparseArray<Collection<CalendarEvent>>(max);

		/*
		 * TEST PURPOSES ONLY
		 */
		for (int dc = 1; dc <= max; dc++) {
			if (dc % 3 != 0) {
				continue;
			}
			
			List<CalendarEvent> dayEvents = new ArrayList<CalendarEvent>();

			for (int i = 0; i < 5; i++) {
				CalendarEvent event = new CalendarEvent(i + 1, 1392509133, 1393509133);

				switch (i) {
				case 0:
					event.setColor("red");
					break;
				case 1:
					event.setColor("maroon");
					break;
				case 2:
					event.setColor("blue");
					break;
				case 3:
					event.setColor("green");
					break;
				case 4:
					event.setColor("purple");
					break;
				default:
					event.setColor("gray");
					break;
				}

				dayEvents.add(event);
			}

			eventsByMonth.append(dc, dayEvents);
		}
		/*
		 * end TEST PURPOSES ONLY
		 */

		return eventsByMonth;
	}
}
