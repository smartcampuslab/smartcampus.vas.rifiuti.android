package eu.trentorise.smartcampus.rifiuti;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.tyczj.extendedcalendarview.Day;

import eu.trentorise.smartcampus.rifiuti.model.CalendarioEvent;
import eu.trentorise.smartcampus.rifiuti.utils.ArgUtils;

public class CalendarAgendaFragment extends Fragment {

	private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE dd/MM/yyyy", Locale.getDefault());

	private ActionBarActivity abActivity;
	private Day<CalendarioEvent> day;
	private Calendar cal;

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		abActivity = (ActionBarActivity) getActivity();

		setHasOptionsMenu(true);
		abActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		abActivity.getSupportActionBar().setHomeButtonEnabled(true);

		if (getArguments().containsKey(ArgUtils.ARGUMENT_CALENDAR_DAY)) {
			day = (Day<CalendarioEvent>) getArguments().getSerializable(ArgUtils.ARGUMENT_CALENDAR_DAY);
			cal = Calendar.getInstance(Locale.getDefault());
			cal.set(day.getYear(), day.getMonth(), day.getDay());
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

		abActivity.getSupportActionBar().setTitle(dateFormatter.format(cal.getTime()));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			abActivity.finish();
			return true;
		}
		return false;
	}

}
