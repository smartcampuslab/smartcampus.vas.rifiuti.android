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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
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

	private SwipeRefreshLayout calendarAgendaSRL;
	private ListView calendarAgendaList;
	// private Day<CalendarioEvent> calendarAgendaDay;
	private Calendar calendarAgendaCal;
	private CalendarAgendaAdapter calendarAgendaAdapter;
	private List<Long> loadedMonths = new ArrayList<Long>();

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
		calendarAgendaSRL = (SwipeRefreshLayout) aView.findViewById(R.id.calendaragenda_srl);
		calendarAgendaList = (ListView) calendarAgendaSRL.findViewById(R.id.calendaragenda_list);
		viewSwitcher.addView(aView);

		return view;
	}

	@Override
	public void onStart() {
		super.onStart();

		loadMonth(null);

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

		calendarAgendaList.setOnScrollListener(new OnScrollListener() {

			private boolean scrolled = false;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL && !scrolled) {
					scrolled = true;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (scrolled && totalItemCount > 0) {
					if ((firstVisibleItem + visibleItemCount) == totalItemCount) {
						if (!calendarAgendaSRL.isRefreshing()) {
							calendarAgendaSRL.setRefreshing(true);
							loadNextMonth();
						}
					}
				}
			}
		});

		calendarAgendaSRL.setColorSchemeResources(R.color.rifiuti_green_light_o50, R.color.rifiuti_green_light,
				R.color.rifiuti_green_middle, R.color.rifiuti_green_dark);

		calendarAgendaSRL.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				Log.e("SwipeRefreshLayout", "SwipeRefreshLayout onRefresh");
				loadPreviousMonth();
			}
		});
	}

	private void loadPreviousMonth() {
		// TODO: load previous month
		// new Handler().postDelayed(new Runnable() {
		// @Override
		// public void run() {
		// Toast.makeText(getActivity(), "LOADED", Toast.LENGTH_SHORT).show();
		// calendarAgendaSRL.setRefreshing(false);
		// }
		// }, 3000);

		Calendar cal = Calendar.getInstance(Locale.getDefault());
		cal.setTimeInMillis(loadedMonths.get(0));
		cal.add(Calendar.MONTH, -1);
		loadMonth(cal);
	}

	private void loadNextMonth() {
		// TODO: load next month
		// new Handler().postDelayed(new Runnable() {
		// @Override
		// public void run() {
		// Toast.makeText(getActivity(), "LOADED", Toast.LENGTH_SHORT).show();
		// calendarAgendaSRL.setRefreshing(false);
		// }
		// }, 3000);

		Calendar cal = Calendar.getInstance(Locale.getDefault());
		cal.setTimeInMillis(loadedMonths.get(loadedMonths.size() - 1));
		cal.add(Calendar.MONTH, 1);
		loadMonth(cal);
	}

	private void loadMonth(Calendar cal) {
		if (cal == null) {
			cal = calendarAgendaCal;
		}

		if (loadedMonths.contains(cal.getTimeInMillis())) {
			if (calendarAgendaSRL.isRefreshing()) {
				calendarAgendaSRL.setRefreshing(false);
			}
			return;
		}

		List<CalendarioAgendaEntry> caeList = new ArrayList<CalendarioAgendaEntry>();

		SparseArray<Collection<CalendarioEvent>> eventsSparseArray = rifiutiEventsSource.getEventsByMonth(cal);

		for (int d = 1; d <= cal.getActualMaximum(Calendar.DAY_OF_MONTH); d++) {
			Collection<CalendarioEvent> coll = eventsSparseArray.get(d);

			// calendar for this day
			Calendar calday = Calendar.getInstance(Locale.getDefault());
			calday.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), d);
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
			calendarAgendaAdapter = new CalendarAgendaAdapter(getActivity(), R.layout.calendaragenda_row);
			calendarAgendaAdapter.addAll(caeList);
			loadedMonths.add(cal.getTimeInMillis());
			calendarAgendaList.setAdapter(calendarAgendaAdapter);
		} else {
			boolean before = false;
			if (loadedMonths.get(0) > cal.getTimeInMillis()) {
				// add before
				before = true;
				calendarAgendaAdapter.addAllAtBeginning(caeList);
				loadedMonths.add(0, cal.getTimeInMillis());
			} else if (loadedMonths.get(loadedMonths.size() - 1) < cal.getTimeInMillis()) {
				// add after
				calendarAgendaAdapter.addAll(caeList);
				loadedMonths.add(cal.getTimeInMillis());
			}

			// save index and top position
			int index = calendarAgendaList.getFirstVisiblePosition();
			if (before) {
				index += caeList.size();
			}
			View v = calendarAgendaList.getChildAt(0);
			int top = (v == null) ? 0 : v.getTop();
			// notify dataset changed
			calendarAgendaAdapter.notifyDataSetChanged();
			// restore
			calendarAgendaList.setSelectionFromTop(index, top);
		}

		if (calendarAgendaSRL.isRefreshing()) {
			calendarAgendaSRL.setRefreshing(false);
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
			if (viewSwitcher.getDisplayedChild() == VIEW_CALENDAR) {
				calendarView.goToToday();
			} else if (viewSwitcher.getDisplayedChild() == VIEW_AGENDA) {
				goToAgendaToday();
			}
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

	private void goToAgendaToday() {
		Integer todayPosition = calendarAgendaAdapter.getTodayPosition();
		if (todayPosition != null) {
			// View v = calendarAgendaList.getChildAt(0);
			// int top = (v == null) ? 0 : v.getTop();
			// calendarAgendaList.setSelectionFromTop(calendarAgendaAdapter.getTodayPosition(),
			// top);
			calendarAgendaList.smoothScrollToPosition(calendarAgendaAdapter.getTodayPosition());
		}
	}

}
