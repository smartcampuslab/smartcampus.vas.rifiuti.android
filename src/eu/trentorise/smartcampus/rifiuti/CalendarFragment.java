package eu.trentorise.smartcampus.rifiuti;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.tyczj.extendedcalendarview.Day;

import eu.trentorise.smartcampus.rifiuti.custom.RifiutiCalendarView;
import eu.trentorise.smartcampus.rifiuti.data.RifiutiEventsSource;
import eu.trentorise.smartcampus.rifiuti.model.CalendarioEvent;
import eu.trentorise.smartcampus.rifiuti.utils.ArgUtils;

public class CalendarFragment extends Fragment {

	private RifiutiCalendarView calendarView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_calendar, container, false);
		calendarView = (RifiutiCalendarView) viewGroup.findViewById(R.id.calendar_view);
		calendarView.setDuplicatesAvoided(true);
		calendarView.setMonthTextBackgroundColor(getResources().getColor(R.color.gray_light));
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
}
