package it.smartcampuslab.riciclo.model;

import java.util.ArrayList;
import java.util.List;

public class CalendarioAgendaEntry {

	private PuntoRaccolta puntoRaccolta;
	private List<CalendarioEvent> events;

	public CalendarioAgendaEntry() {
	}

	public CalendarioAgendaEntry(PuntoRaccolta puntoRaccolta) {
		setPuntoRaccolta(puntoRaccolta);
		setEvents(new ArrayList<CalendarioEvent>());
	}

	public CalendarioAgendaEntry(PuntoRaccolta puntoRaccolta, List<CalendarioEvent> events) {
		setPuntoRaccolta(puntoRaccolta);
		setEvents(events);
	}

	public PuntoRaccolta getPuntoRaccolta() {
		return puntoRaccolta;
	}

	public void setPuntoRaccolta(PuntoRaccolta puntoRaccolta) {
		this.puntoRaccolta = puntoRaccolta;
	}

	public List<CalendarioEvent> getEvents() {
		return events;
	}

	public void setEvents(List<CalendarioEvent> events) {
		this.events = events;
	}

}
