package eu.trentorise.smartcampus.rifiuti.custom;

import android.content.Context;
import android.util.AttributeSet;

import com.tyczj.extendedcalendarview.ExtendedCalendarView;

import eu.trentorise.smartcampus.rifiuti.model.CalendarioEvent;

public class RifiutiCalendarView extends ExtendedCalendarView<CalendarioEvent> {

	public RifiutiCalendarView(Context context) {
		super(context);
	}

	public RifiutiCalendarView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RifiutiCalendarView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

}
