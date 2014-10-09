package it.smartcampuslab.riciclo;

import it.smartcampuslab.riciclo.R;
import it.smartcampuslab.riciclo.custom.RifiutiCalendarView;
import it.smartcampuslab.riciclo.data.RifiutiEventsSource;
import it.smartcampuslab.riciclo.model.CalendarioAgendaEntry;
import it.smartcampuslab.riciclo.model.CalendarioEvent;
import it.smartcampuslab.riciclo.model.PuntoRaccolta;
import it.smartcampuslab.riciclo.utils.ArgUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
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

public class CalendarFragment extends Fragment {

	private static final int VIEW_CALENDARMONTH = 0;
	private static final int VIEW_CALENDARAGENDA = 1;

	private static final String TIPOLOGIA_PORTA_A_PORTA = "Porta a porta";

	private ViewSwitcher viewSwitcher;

	private RifiutiCalendarView calendarView;
	private RifiutiEventsSource rifiutiEventsSource;

	private SwipeRefreshLayout calendarAgendaSRL;
	private ListView calendarAgendaList;
	private View calendarAgendaEmptyView;
	private Calendar monthCalDefault;
	private CalendarAgendaAdapter calendarAgendaAdapter;
	private Calendar lastAgendaDayViewed;

	private List<Long> loadedMonths = new ArrayList<Long>();

	private Bundle intentBundle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		intentBundle = this.getArguments();

		rifiutiEventsSource = new RifiutiEventsSource(this.getActivity());
		monthCalDefault = Calendar.getInstance(Locale.getDefault());
		monthCalDefault.set(Calendar.DAY_OF_MONTH, 1);
		monthCalDefault.set(Calendar.HOUR_OF_DAY, 0);
		monthCalDefault.set(Calendar.MINUTE, 0);
		monthCalDefault.set(Calendar.SECOND, 0);
		monthCalDefault.set(Calendar.MILLISECOND, 0);
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
		calendarAgendaEmptyView = aView.findViewById(R.id.calendaragenda_empty);
		// calendarAgendaList.setEmptyView(calendarAgendaEmptyView);
		viewSwitcher.addView(aView);

		return view;
	}

	@Override
	public void onStart() {
		super.onStart();

		loadMonth(null);

		/*
		 * onDayCliclListener for calendarView
		 */
		calendarView.setOnDayClickListener(new RifiutiCalendarView.OnDayClickListener<CalendarioEvent>() {
			@Override
			public void onDayClicked(AdapterView<?> adapter, View view, int position, long id, Day<CalendarioEvent> day) {
				if (day.getEventsCount() > 0) {
					/*
					 * CalendarDay
					 */
					// Intent intent = new Intent(getActivity(),
					// CalendarDayActivity.class);
					// intent.putExtra(ArgUtils.ARGUMENT_CALENDAR_DAY, day);
					// startActivity(intent);

					/*
					 * CalendarAgenda
					 */
					Calendar dayCal = Calendar.getInstance(Locale.getDefault());
					dayCal.setTimeInMillis(monthCalDefault.getTimeInMillis());
					dayCal.set(day.getYear(), day.getMonth(), day.getDay());
					showCalendarAgenda(dayCal, true);
					getActivity().supportInvalidateOptionsMenu();
				}
			}
		});

		/*
		 * onScollListener for calendarAgendaList
		 */
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

		/*
		 * colors for swipeRefreshLayout
		 */
		calendarAgendaSRL.setColorSchemeResources(R.color.rifiuti_green_light_o50, R.color.rifiuti_green_light,
				R.color.rifiuti_green_middle, R.color.rifiuti_green_dark);

		/*
		 * onRefreshListener for swipeRefreshLayout
		 */
		calendarAgendaSRL.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				loadPreviousMonth();
			}
		});

		/*
		 * If value is passed go to this day on agenda
		 */
		if (intentBundle != null && intentBundle.containsKey(ArgUtils.ARGUMENT_CALENDAR_TOMORROW)) {
			Calendar goToDayFromIntent = (Calendar) intentBundle.get(ArgUtils.ARGUMENT_CALENDAR_TOMORROW);
			showCalendarAgenda(goToDayFromIntent, true);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (viewSwitcher.getDisplayedChild() == VIEW_CALENDARMONTH) {
			inflater.inflate(R.menu.calendarmonth_menu, menu);
		} else if (viewSwitcher.getDisplayedChild() == VIEW_CALENDARAGENDA) {
			inflater.inflate(R.menu.calendaragenda_menu, menu);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.cal_today:
			calendarView.goToToday();
			goToAgendaToday();
			break;
		case R.id.cal_switch:
			if (viewSwitcher.getDisplayedChild() == VIEW_CALENDARMONTH) {
				showCalendarAgenda(calendarView.getCurrentCalendar(), false);
			} else if (viewSwitcher.getDisplayedChild() == VIEW_CALENDARAGENDA) {
				showCalendarMonth();
			}
			break;
		}

		getActivity().supportInvalidateOptionsMenu();

		return super.onOptionsItemSelected(item);
	}

	private Long loadPreviousMonth() {
		Calendar cal = Calendar.getInstance(Locale.getDefault());
		cal.setTimeInMillis(loadedMonths.get(0));
		cal.add(Calendar.MONTH, -1);
		return loadMonth(cal);
	}

	private Long loadNextMonth() {
		Calendar cal = Calendar.getInstance(Locale.getDefault());
		cal.setTimeInMillis(loadedMonths.get(loadedMonths.size() - 1));
		cal.add(Calendar.MONTH, 1);
		return loadMonth(cal);
	}

	private Long loadMonth(Calendar cal) {
		if (cal == null) {
			cal = Calendar.getInstance();
			cal.setTimeInMillis(monthCalDefault.getTimeInMillis());
		}

		if (loadedMonths.contains(cal.getTimeInMillis())) {
			if (calendarAgendaSRL.isRefreshing()) {
				calendarAgendaSRL.setRefreshing(false);
			}
			return null;
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
				caeList.add(cae);
			}

			// caeList.add(cae);
		}

		if (calendarAgendaAdapter == null) {
			calendarAgendaAdapter = new CalendarAgendaAdapter(getActivity(), R.layout.calendaragenda_row);
			// calendarAgendaAdapter.addAll(caeList);
			calendarAgendaAdapter.addAllAtEnd(caeList);
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
				// calendarAgendaAdapter.addAll(caeList);
				calendarAgendaAdapter.addAllAtEnd(caeList);
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

		if (calendarAgendaAdapter.isEmpty()) {
			calendarAgendaSRL.setVisibility(View.GONE);
			calendarAgendaEmptyView.setVisibility(View.VISIBLE);
		} else {
			calendarAgendaSRL.setVisibility(View.VISIBLE);
			calendarAgendaEmptyView.setVisibility(View.GONE);
		}

		return cal.getTimeInMillis();
	}

	private void showCalendarMonth() {
		if (!calendarAgendaAdapter.isEmpty()) {
			int firstVisiblePosition = calendarAgendaList.getFirstVisiblePosition();
			if (calendarAgendaList.getChildAt(0).getTop() < 0 && firstVisiblePosition + 1 < calendarAgendaAdapter.getCount()) {
				firstVisiblePosition++;
			}
			lastAgendaDayViewed = calendarAgendaAdapter.getItem(firstVisiblePosition).getCalendar();

			Calendar goToMonthCalendar = Calendar.getInstance(Locale.getDefault());
			goToMonthCalendar.setTimeInMillis(monthCalDefault.getTimeInMillis());
			goToMonthCalendar.set(Calendar.YEAR, lastAgendaDayViewed.get(Calendar.YEAR));
			goToMonthCalendar.set(Calendar.MONTH, lastAgendaDayViewed.get(Calendar.MONTH));

			calendarView.goToMonth(goToMonthCalendar);
		}

		viewSwitcher.showPrevious();
	}

	private void showCalendarAgenda(final Calendar cal, boolean forceDay) {
		Calendar monthCal = Calendar.getInstance(Locale.getDefault());
		monthCal.setTimeInMillis(monthCalDefault.getTimeInMillis());
		monthCal.set(Calendar.YEAR, cal.get(Calendar.YEAR));
		monthCal.set(Calendar.MONTH, cal.get(Calendar.MONTH));

		while (!loadedMonths.contains(monthCal.getTimeInMillis())) {
			if (monthCal.getTimeInMillis() < loadedMonths.get(0)) {
				// before
				loadPreviousMonth();
			} else if (monthCal.getTimeInMillis() > loadedMonths.get(loadedMonths.size() - 1)) {
				// after
				loadNextMonth();
			}
		}

		if (!forceDay && lastAgendaDayViewed == null && cal.get(Calendar.YEAR) == monthCalDefault.get(Calendar.YEAR)
				&& cal.get(Calendar.MONTH) == monthCalDefault.get(Calendar.MONTH)) {
			// show today
			goToAgendaToday();
		} else if (!forceDay && lastAgendaDayViewed != null && cal.get(Calendar.YEAR) == lastAgendaDayViewed.get(Calendar.YEAR)
				&& cal.get(Calendar.MONTH) == lastAgendaDayViewed.get(Calendar.MONTH)) {
			// leave the agenda at the same day (aka: do nothing)
		} else {
			goToAgendaDay(cal);
		}

		viewSwitcher.showNext();
	}

	private void goToAgendaDay(Calendar cal) {
		Integer dayPosition;
		if (cal != null) {
			dayPosition = calendarAgendaAdapter.getDayPosition(cal);
			calendarAgendaAdapter.setSelected(dayPosition);
		} else {
			dayPosition = calendarAgendaAdapter.getTodayPosition();
		}

		if (dayPosition != null) {
			// View v = calendarAgendaList.getChildAt(0);
			// int top = (v == null) ? 0 : v.getTop();
			// calendarAgendaList.setSelectionFromTop(dayPosition, top);
			calendarAgendaList.setSelection(dayPosition);
			// calendarAgendaList.smoothScrollToPosition(dayPosition);
		}
	}

	private void goToAgendaToday() {
		goToAgendaDay(null);
	}

}
