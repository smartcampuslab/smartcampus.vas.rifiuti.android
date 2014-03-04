package eu.trentorise.smartcampus.rifiuti;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tyczj.extendedcalendarview.CalendarDay;

import eu.trentorise.smartcampus.rifiuti.utils.ArgUtils;

public class CalendarAgendaFragment extends Fragment {

	private CalendarDay calendarDay;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ArgUtils.ARGUMENT_CALENDAR_DAY)) {
			calendarDay = (CalendarDay) getArguments().getSerializable(ArgUtils.ARGUMENT_CALENDAR_DAY);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_calendaragenda, container, false);
		return viewGroup;
	}

	@Override
	public void onStart() {
		super.onStart();

		((ActionBarActivity) getActivity()).getSupportActionBar().setTitle("" + calendarDay.getDay());
	}

}
