package eu.trentorise.smartcampus.rifiuti;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ViewSwitcher;

import com.tyczj.extendedcalendarview.Day;

import eu.trentorise.smartcampus.rifiuti.custom.RifiutiCalendarView;
import eu.trentorise.smartcampus.rifiuti.data.RifiutiEventsSource;
import eu.trentorise.smartcampus.rifiuti.model.CalendarioAgendaEntry;
import eu.trentorise.smartcampus.rifiuti.model.CalendarioEvent;
import eu.trentorise.smartcampus.rifiuti.model.PuntoRaccolta;
import eu.trentorise.smartcampus.rifiuti.utils.ArgUtils;

public class CalendarFragment extends Fragment {

	private static final int VIEW_CALENDAR = 0;
	private static final int VIEW_AGENDA = 1;

	private static final String TIPOLOGIA_PORTA_A_PORTA = "Porta a porta";

	private ViewSwitcher viewSwitcher;
	private RifiutiCalendarView calendarView;
	private RifiutiEventsSource rifiutiEventsSource;

	private ListView calendarAgendaList;
	private Day<CalendarioEvent> calendarAgendaDay;
	private Calendar calendarAgendaCal;
	private CalendarAgendaAdapter calendarAgendaAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		rifiutiEventsSource = new RifiutiEventsSource(this.getActivity());
		calendarAgendaCal = Calendar.getInstance(Locale.getDefault());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		/*
		 * with ViewSwitcher
		 */
		View view = (View) inflater.inflate(R.layout.fragment_calendar, container, false);
		viewSwitcher = (ViewSwitcher) view.findViewById(R.id.calendar_container);

		// calendar
		View cView = (View) inflater.inflate(R.layout.fragment_calendarmonth, container, false);
		calendarView = (RifiutiCalendarView) cView.findViewById(R.id.calendar_view);
		calendarView.setDuplicatesAvoided(true); // default is false
		calendarView.setMonthTextBackgroundColor(getResources().getColor(R.color.rifiuti_light));
		calendarView.setTodayColor(getResources().getColor(R.color.rifiuti_green_middle));
		calendarView.setCalendarEventsSource(rifiutiEventsSource);
		viewSwitcher.addView(cView);

		// agenda
		View aView = (View) inflater.inflate(R.layout.fragment_calendaragenda, container, false);
		calendarAgendaList = (ListView) aView.findViewById(R.id.calendaragenda_list);
		viewSwitcher.addView(aView);

		return view;
	}

	@Override
	public void onStart() {
		super.onStart();

		populateAgenda();

		calendarView.setOnDayClickListener(new RifiutiCalendarView.OnDayClickListener<CalendarioEvent>() {
			@Override
			public void onDayClicked(AdapterView<?> adapter, View view, int position, long id, Day<CalendarioEvent> day) {
				if (day.getEventsCount() > 0) {
					/*
					 * CalendarDay
					 */
					Intent intent = new Intent(getActivity(), CalendarDayActivity.class);
					intent.putExtra(ArgUtils.ARGUMENT_CALENDAR_DAY, day);
					startActivity(intent);

					/*
					 * CalendarAgenda
					 */
					// calendarAgendaDay = day;
					// calendarAgendaCal.set(day.getYear(), day.getMonth(),
					// day.getDay());
					// populateAgenda();
					// viewSwitcher.showNext();
					// getActivity().supportInvalidateOptionsMenu();
				}
			}
		});
	}

	private void populateAgenda() {
		List<CalendarioAgendaEntry> caeList = new ArrayList<CalendarioAgendaEntry>();

		SparseArray<Collection<CalendarioEvent>> eventsSparseArray = rifiutiEventsSource.getEventsByMonth(calendarAgendaCal);

		for (int d = 1; d <= calendarAgendaCal.getActualMaximum(Calendar.DAY_OF_MONTH); d++) {
			Collection<CalendarioEvent> coll = eventsSparseArray.get(d);

			// calendar for this day
			Calendar calday = Calendar.getInstance(Locale.getDefault());
			calday.set(calendarAgendaCal.get(Calendar.YEAR), calendarAgendaCal.get(Calendar.MONTH), d);
			CalendarioAgendaEntry cae = new CalendarioAgendaEntry(calday);

			if (coll != null && !coll.isEmpty()) {
				Map<String, Map<PuntoRaccolta, List<CalendarioEvent>>> tipologiaPuntiRaccoltaMap = new LinkedHashMap<String, Map<PuntoRaccolta, List<CalendarioEvent>>>();

				for (CalendarioEvent ev : coll) {
					CalendarioEvent event = (CalendarioEvent) ev;

					String tipologiaPuntiRaccolta = event.getCalendarioItem().getPoint().getTipologiaPuntiRaccolta();

					// TODO: workaround for "Porta a porta"
					String portaAportaNote = null;
					if (tipologiaPuntiRaccolta.startsWith(TIPOLOGIA_PORTA_A_PORTA)) {
						portaAportaNote = tipologiaPuntiRaccolta.replace(TIPOLOGIA_PORTA_A_PORTA, "").trim();
						tipologiaPuntiRaccolta = TIPOLOGIA_PORTA_A_PORTA;
					}
					//

					if (!tipologiaPuntiRaccoltaMap.containsKey(tipologiaPuntiRaccolta)) {
						tipologiaPuntiRaccoltaMap.put(tipologiaPuntiRaccolta,
								new LinkedHashMap<PuntoRaccolta, List<CalendarioEvent>>());
					}
					Map<PuntoRaccolta, List<CalendarioEvent>> puntiRaccoltaEventsMap = tipologiaPuntiRaccoltaMap
							.get(tipologiaPuntiRaccolta);

					PuntoRaccolta puntoRaccolta = event.getCalendarioItem().getPoint();

					// TODO: workaround for "Porta a porta"
					if (portaAportaNote != null) {
						puntoRaccolta.setNote(portaAportaNote);
					}
					//

					if (!puntiRaccoltaEventsMap.containsKey(puntoRaccolta)) {
						puntiRaccoltaEventsMap.put(puntoRaccolta, new ArrayList<CalendarioEvent>());
					}
					List<CalendarioEvent> eventsList = puntiRaccoltaEventsMap.get(puntoRaccolta);

					if (!eventsList.contains(event)) {
						eventsList.add(event);
					}
				}

				cae.setEventsMap(tipologiaPuntiRaccoltaMap);
			}

			caeList.add(cae);
		}

		if (calendarAgendaAdapter == null) {
			calendarAgendaAdapter = new CalendarAgendaAdapter(getActivity(), R.layout.calendaragenda_row, caeList);
			calendarAgendaList.setAdapter(calendarAgendaAdapter);
		} else {
			calendarAgendaAdapter.clear();
			calendarAgendaAdapter.addAll(caeList);
			calendarAgendaAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (viewSwitcher.getDisplayedChild() == VIEW_CALENDAR) {
			inflater.inflate(R.menu.calendar_menu, menu);
		} else if (viewSwitcher.getDisplayedChild() == VIEW_AGENDA) {
			inflater.inflate(R.menu.calendaragenda_menu, menu);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.cal_today:
			calendarView.goToToday();
			break;
		case R.id.cal_switch:
			if (viewSwitcher.getDisplayedChild() == VIEW_CALENDAR) {
				viewSwitcher.showNext();
			} else if (viewSwitcher.getDisplayedChild() == VIEW_AGENDA) {
				viewSwitcher.showPrevious();
			}
			break;
		default:
			// do nothing
		}

		getActivity().supportInvalidateOptionsMenu();

		return super.onOptionsItemSelected(item);
	}

}
