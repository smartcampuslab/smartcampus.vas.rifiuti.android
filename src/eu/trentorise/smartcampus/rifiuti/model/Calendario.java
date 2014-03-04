package eu.trentorise.smartcampus.rifiuti.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class Calendario {
	
	private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");

	private String dataDa;
	private String dataA;
	private String il;
	private String dalle;
	private String alle;

	public String getDataDa() {
		return dataDa;
	}

	public void setDataDa(String dataDa) {
		this.dataDa = dataDa;
	}

	public String getDataA() {
		return dataA;
	}

	public void setDataA(String dataA) {
		this.dataA = dataA;
	}

	public String getIl() {
		return il;
	}

	public void setIl(String il) {
		this.il = il;
	}

	public String getDalle() {
		return dalle;
	}

	public void setDalle(String dalle) {
		this.dalle = dalle;
	}

	public String getAlle() {
		return alle;
	}

	public void setAlle(String alle) {
		this.alle = alle;
	}

	/**
	 * @return Calendar.DAY_OF_WEEK value corresponding to the object day
	 */
	public Integer asCalendarDayOfWeek() {
		if (il.startsWith("lun")) {
			return Calendar.MONDAY;
		} else if (il.startsWith("mar")) {
			return Calendar.TUESDAY;
		} else if (il.startsWith("mer")) {
			return Calendar.WEDNESDAY;
		} else if (il.startsWith("gio")) {
			return Calendar.THURSDAY;
		} else if (il.startsWith("ven")) {
			return Calendar.FRIDAY;
		} else if (il.startsWith("sab")) {
			return Calendar.SATURDAY;
		} else if (il.startsWith("dom")) {
			return Calendar.SUNDAY;
		}
		return null;
	}

	/**
	 * @param c
	 * @return true if the specified date fits within the calendar interval
	 */
	public boolean contains(Calendar c) {
		String s = dateFormatter.format(c.getTime());
		return s.compareTo(dataA) <=0 && dataDa.compareTo(s) <= 0;
	}

	/**
	 * @param cal 
	 * @return
	 * @throws ParseException 
	 */
	public long end(Calendar cal) throws ParseException {
		return mergeDate(cal, timeFormatter.parse(alle));
	}

	/**
	 * @param cal 
	 * @return
	 * @throws ParseException 
	 */
	public long start(Calendar cal) throws ParseException {
		return mergeDate(cal, timeFormatter.parse(dalle));
	}

	/**
	 * @param cal
	 * @param d
	 * @return
	 */
	private long mergeDate(Calendar cal, Date d) {
		Calendar tmp = Calendar.getInstance();
		tmp.setTime(d);
		tmp.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
		return tmp.getTimeInMillis();
	}
}
