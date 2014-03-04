package eu.trentorise.smartcampus.rifiuti.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.util.Log;
import android.util.SparseArray;

import com.tyczj.extendedcalendarview.CalendarEvent;
import com.tyczj.extendedcalendarview.CalendarEventsSource;

import eu.trentorise.smartcampus.rifiuti.model.Calendario;
import eu.trentorise.smartcampus.rifiuti.model.PuntoRaccolta;

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
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		int max = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

		Map<PuntoRaccolta, List<Calendario>> calendariMap = new HashMap<PuntoRaccolta, List<Calendario>>();

		// get 'punti di raccolta'
		List<PuntoRaccolta> puntiRaccoltaList;
		try {
			puntiRaccoltaList = RifiutiHelper.getPuntiRaccolta();
		} catch (Exception e) {
			Log.e(getClass().getSimpleName(), e.getMessage());
			return;
		}

		// get calendars for every 'punto di raccolta'
		for (PuntoRaccolta pr : puntiRaccoltaList) {
			calendariMap.put(pr, RifiutiHelper.getCalendars(pr));
		}

		for (int dc = 1; dc <= max; dc++) {
			cal.set(Calendar.DAY_OF_MONTH, dc);
			List<CalendarEvent> events = new ArrayList<CalendarEvent>();

			for (PuntoRaccolta pr : calendariMap.keySet()) {
				List<Calendario> calendariList = calendariMap.get(pr);

				for (Calendario c : calendariList) {
					Integer dayOfWeek = getDayOfWeekByString(c.getIl());
					if (dayOfWeek == null || cal.get(Calendar.DAY_OF_WEEK) != dayOfWeek) {
						continue;
					}

					Calendar calDa = Calendar.getInstance();
					try {
						calDa.setTime(sdfDate.parse(c.getDataDa()));
					} catch (ParseException e) {
						Log.e(getClass().getSimpleName(), pr.getTipologiaPuntiRaccolta() + " (" + pr.getIndirizzo() + ") - "
								+ e.getMessage());
						continue;
					}
					calDa.set(Calendar.HOUR_OF_DAY, 0);
					calDa.set(Calendar.MINUTE, 0);
					calDa.set(Calendar.SECOND, 0);
					calDa.set(Calendar.MILLISECOND, 0);
					Calendar calA = Calendar.getInstance();
					try {
						calA.setTime(sdfDate.parse(c.getDataA()));
					} catch (ParseException e) {
						Log.e(getClass().getSimpleName(), pr.getTipologiaPuntiRaccolta() + " (" + pr.getIndirizzo() + ") - "
								+ e.getMessage());
						continue;
					}
					calA.set(Calendar.HOUR_OF_DAY, 23);
					calA.set(Calendar.MINUTE, 59);
					calA.set(Calendar.SECOND, 59);
					calA.set(Calendar.MILLISECOND, 999);

					if (cal.after(calDa) && cal.before(calA)) {
						Calendar cale = Calendar.getInstance(Locale.getDefault());
						String[] dalleSplit = c.getDalle().split(":");
						cale.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH),
								Integer.parseInt(dalleSplit[0]), Integer.parseInt(dalleSplit[1]));
						long dalle = cale.getTimeInMillis();
						String[] alleSplit = c.getDalle().split(":");
						cale.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH),
								Integer.parseInt(alleSplit[0]), Integer.parseInt(alleSplit[1]));
						long alle = cale.getTimeInMillis();

						CalendarEvent event = new CalendarEvent(0, dalle, alle);
						event.setTitle(pr.getTipologiaPuntiRaccolta());
						event.setDescription(pr.getIndirizzo());
						event.setLocation(pr.getLocalizzazione());
						// TODO: color?
						events.add(event);
					}
				}
			}

			eventsByMonth.append(dc, events);
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

	private Integer getDayOfWeekByString(String dayString) {
		if (dayString.startsWith("lun")) {
			return Calendar.MONDAY;
		} else if (dayString.startsWith("mar")) {
			return Calendar.TUESDAY;
		} else if (dayString.startsWith("mer")) {
			return Calendar.WEDNESDAY;
		} else if (dayString.startsWith("gio")) {
			return Calendar.THURSDAY;
		} else if (dayString.startsWith("ven")) {
			return Calendar.FRIDAY;
		} else if (dayString.startsWith("sab")) {
			return Calendar.SATURDAY;
		} else if (dayString.startsWith("dom")) {
			return Calendar.SUNDAY;
		}
		return null;
	}
}
