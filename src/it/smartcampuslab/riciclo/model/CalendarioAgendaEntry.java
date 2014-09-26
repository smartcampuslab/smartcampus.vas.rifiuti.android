package it.smartcampuslab.riciclo.model;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CalendarioAgendaEntry {

	private Calendar calendar;
	private Map<String, Map<PuntoRaccolta, List<CalendarioEvent>>> eventsMap;

	public CalendarioAgendaEntry() {
	}

	public CalendarioAgendaEntry(Calendar calendar) {
		setCalendar(calendar);
		setEventsMap(new LinkedHashMap<String, Map<PuntoRaccolta, List<CalendarioEvent>>>());
	}

	public CalendarioAgendaEntry(Calendar calendar, Map<String, Map<PuntoRaccolta, List<CalendarioEvent>>> eventsMap) {
		setCalendar(calendar);
		setEventsMap(eventsMap);
	}

	public Calendar getCalendar() {
		return calendar;
	}

	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}

	public Map<String, Map<PuntoRaccolta, List<CalendarioEvent>>> getEventsMap() {
		return eventsMap;
	}

	public void setEventsMap(Map<String, Map<PuntoRaccolta, List<CalendarioEvent>>> eventsMap) {
		this.eventsMap = eventsMap;
	}

}
