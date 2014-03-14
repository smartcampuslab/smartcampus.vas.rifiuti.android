package eu.trentorise.smartcampus.rifiuti;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.tyczj.extendedcalendarview.Day;
import com.tyczj.extendedcalendarview.Event;

import eu.trentorise.smartcampus.rifiuti.model.CalendarioAgendaEntry;
import eu.trentorise.smartcampus.rifiuti.model.CalendarioEvent;
import eu.trentorise.smartcampus.rifiuti.model.PuntoRaccolta;
import eu.trentorise.smartcampus.rifiuti.utils.ArgUtils;

public class CalendarAgendaFragment extends ListFragment {

	private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE d MMMM yyyy", Locale.getDefault());

	private ActionBarActivity abActivity;
	private Day<CalendarioEvent> day;
	private Calendar cal;
	private CalendarAgendaAdapter adapter;

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true);

		if (getArguments().containsKey(ArgUtils.ARGUMENT_CALENDAR_DAY)) {
			day = (Day<CalendarioEvent>) getArguments().getSerializable(ArgUtils.ARGUMENT_CALENDAR_DAY);
			cal = Calendar.getInstance(Locale.getDefault());
			cal.set(day.getYear(), day.getMonth(), day.getDay());
		}
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		abActivity = (ActionBarActivity) getActivity();
		abActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		abActivity.getSupportActionBar().setHomeButtonEnabled(true);
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

		Map<String, Map<PuntoRaccolta, List<CalendarioEvent>>> tipologiaPuntiRaccoltaMap = new LinkedHashMap<String, Map<PuntoRaccolta, List<CalendarioEvent>>>();
		for (Event ev : day.getEvents()) {
			CalendarioEvent event = (CalendarioEvent) ev;

			String tipologiaPuntiRaccolta = event.getCalendarioItem().getPoint().getTipologiaPuntiRaccolta();
			if (!tipologiaPuntiRaccoltaMap.containsKey(tipologiaPuntiRaccolta)) {
				tipologiaPuntiRaccoltaMap
						.put(tipologiaPuntiRaccolta, new LinkedHashMap<PuntoRaccolta, List<CalendarioEvent>>());
			}
			Map<PuntoRaccolta, List<CalendarioEvent>> puntiRaccoltaEventsMap = tipologiaPuntiRaccoltaMap
					.get(tipologiaPuntiRaccolta);

			PuntoRaccolta puntoRaccolta = event.getCalendarioItem().getPoint();
			if (!puntiRaccoltaEventsMap.containsKey(puntoRaccolta)) {
				puntiRaccoltaEventsMap.put(puntoRaccolta, new ArrayList<CalendarioEvent>());
			}
			List<CalendarioEvent> eventsList = puntiRaccoltaEventsMap.get(puntoRaccolta);

			if (!eventsList.contains(event)) {
				eventsList.add(event);
			}
		}

		List<CalendarioAgendaEntry> caeList = new ArrayList<CalendarioAgendaEntry>();
		for (Map<PuntoRaccolta, List<CalendarioEvent>> map : tipologiaPuntiRaccoltaMap.values()) {
			for (PuntoRaccolta pr : map.keySet()) {
				CalendarioAgendaEntry entry = new CalendarioAgendaEntry(pr, map.get(pr));
				caeList.add(entry);
			}
		}

		adapter = new CalendarAgendaAdapter(getActivity(), R.layout.calendaragenda_row, caeList);
		setListAdapter(adapter);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			abActivity.finish();
			return true;
		}
		return false;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(getActivity(), PuntoRaccoltaActivity.class);
		i.putExtra(ArgUtils.ARGUMENT_PUNTO_DI_RACCOLTA, adapter.getItem(position).getPuntoRaccolta());
		startActivity(i);
	}
}
