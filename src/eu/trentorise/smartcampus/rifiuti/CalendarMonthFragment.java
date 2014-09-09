package eu.trentorise.smartcampus.rifiuti;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.tyczj.extendedcalendarview.Day;

import eu.trentorise.smartcampus.rifiuti.custom.RifiutiCalendarView;
import eu.trentorise.smartcampus.rifiuti.data.RifiutiEventsSource;
import eu.trentorise.smartcampus.rifiuti.model.CalendarioEvent;
import eu.trentorise.smartcampus.rifiuti.utils.ArgUtils;

public class CalendarMonthFragment extends Fragment {

	private RifiutiCalendarView calendarView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = (View) inflater.inflate(R.layout.fragment_calendarmonth, container, false);
		calendarView = (RifiutiCalendarView) view.findViewById(R.id.calendar_view);
		calendarView.setDuplicatesAvoided(true); // default is false
		calendarView.setMonthTextBackgroundColor(getResources().getColor(R.color.rifiuti_light));
		calendarView.setTodayColor(getResources().getColor(R.color.rifiuti_green_middle));
		calendarView.setCalendarEventsSource(new RifiutiEventsSource(this.getActivity()));
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();

		calendarView.setOnDayClickListener(new RifiutiCalendarView.OnDayClickListener<CalendarioEvent>() {
			@Override
			public void onDayClicked(AdapterView<?> adapter, View view, int position, long id, Day<CalendarioEvent> day) {
				if (day.getEventsCount() > 0) {
					Intent intent = new Intent(getActivity(), CalendarDayActivity.class);
					intent.putExtra(ArgUtils.ARGUMENT_CALENDAR_DAY, day);
					startActivity(intent);
				}
			}
		});
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		Log.e("OUCH", "onCreateOptionsMenu CalendarFragment");
		inflater.inflate(R.menu.calendarmonth_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.cal_today:
			calendarView.goToToday();
			break;
		case R.id.cal_switch:
			// TODO: change fragment
			getFragmentManager().beginTransaction().replace(R.id.calendar_container, new CalendarAgendaFragment()).commit();
			break;
		default:
		}

		return super.onOptionsItemSelected(item);
	}

}
