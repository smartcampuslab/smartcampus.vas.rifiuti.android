package it.smartcampuslab.riciclo;

import it.smartcampuslab.riciclo.custom.RifiutiCalendarView;
import it.smartcampuslab.riciclo.data.RifiutiEventsSource;
import it.smartcampuslab.riciclo.model.CalendarioEvent;
import it.smartcampuslab.riciclo.utils.ArgUtils;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.tyczj.extendedcalendarview.Day;

public class CalendarFragment extends Fragment {

	private RifiutiCalendarView calendarView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_calendar, container, false);
		calendarView = (RifiutiCalendarView) viewGroup.findViewById(R.id.calendar_view);
		calendarView.setDuplicatesAvoided(true);
		calendarView.setMonthTextBackgroundColor(getResources().getColor(R.color.rifiuti_light));
		calendarView.setTodayColor(getResources().getColor(R.color.rifiuti_green_middle));
		calendarView.setCalendarEventsSource(new RifiutiEventsSource());
		return viewGroup;
	}

	@Override
	public void onStart() {
		super.onStart();

		calendarView.setOnDayClickListener(new RifiutiCalendarView.OnDayClickListener<CalendarioEvent>() {
			@Override
			public void onDayClicked(AdapterView<?> adapter, View view, int position, long id, Day<CalendarioEvent> day) {
				if (day.getEventsCount() > 0) {
					Intent intent = new Intent(getActivity(), CalendarAgendaActivity.class);
					intent.putExtra(ArgUtils.ARGUMENT_CALENDAR_DAY, day);
					startActivity(intent);
				}
			}
		});
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.calendar_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_today) {
			calendarView.goToToday();
		} else {
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

}
