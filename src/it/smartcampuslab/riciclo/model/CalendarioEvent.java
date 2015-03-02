package it.smartcampuslab.riciclo.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.tyczj.extendedcalendarview.Event;

public class CalendarioEvent extends Event {
	private static final long serialVersionUID = 6833705803370618543L;

	private CalendarioItem calendarioItem;
	private Date startMillis, endMillis;

	
	public CalendarioEvent(long startMills, long endMills, CalendarioItem calendarioItem) {
		super(startMills, endMills);
		setCalendarioItem(calendarioItem);
		this.startMillis = new Date(startMills);
		this.endMillis = new Date(endMills);
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

	public String getStartDate(SimpleDateFormat timeformatter) {
		String date = timeformatter.format(startMillis);
		return date;
	}

	public String getEndDate(SimpleDateFormat timeformatter) {
		String date = timeformatter.format(endMillis);
		return date;
	}
}
