package eu.trentorise.smartcampus.rifiuti;

import net.simonvt.calendarview.CalendarView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CalendarFragment extends Fragment {

	private CalendarView calendarView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_calendar, container, false);
		calendarView = (CalendarView) viewGroup.findViewById(R.id.calendarView);
		return viewGroup;
	}

	@Override
	public void onStart() {
		super.onStart();
	}

}
