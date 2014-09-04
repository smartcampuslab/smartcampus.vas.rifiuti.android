package eu.trentorise.smartcampus.rifiuti.model;

import java.util.ArrayList;
import java.util.List;

public class CalendarioDayEntry {

	private PuntoRaccolta puntoRaccolta;
	private List<CalendarioEvent> events;

	public CalendarioDayEntry() {
	}

	public CalendarioDayEntry(PuntoRaccolta puntoRaccolta) {
		setPuntoRaccolta(puntoRaccolta);
		setEvents(new ArrayList<CalendarioEvent>());
	}

	public CalendarioDayEntry(PuntoRaccolta puntoRaccolta, List<CalendarioEvent> events) {
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
