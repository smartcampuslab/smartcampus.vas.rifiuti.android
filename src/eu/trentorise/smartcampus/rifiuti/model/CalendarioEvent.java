package eu.trentorise.smartcampus.rifiuti.model;

import com.tyczj.extendedcalendarview.Event;

public class CalendarioEvent extends Event {
	private static final long serialVersionUID = 6833705803370618543L;

	private CalendarioItem calendarioItem;

	public CalendarioEvent(long startMills, long endMills, CalendarioItem calendarioItem) {
		super(startMills, endMills);
		setCalendarioItem(calendarioItem);
	}

	/**
	 * @return the calendarioItem
	 */
	public CalendarioItem getCalendarioItem() {
		return calendarioItem;
	}

	/**
	 * @param calendarioItem
	 *            the calendarioItem to set
	 */
	public void setCalendarioItem(CalendarioItem calendarioItem) {
		this.calendarioItem = calendarioItem;
	}

}
