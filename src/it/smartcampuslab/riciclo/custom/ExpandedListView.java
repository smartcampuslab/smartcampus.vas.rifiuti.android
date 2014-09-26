package it.smartcampuslab.riciclo.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ListView;

public class ExpandedListView extends ListView {

	/**
	 * This ListView expands to its maximum height
	 */
	
	public ExpandedListView(Context context) {
		super(context);
	}

	public ExpandedListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ExpandedListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(MEASURED_SIZE_MASK, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
		ViewGroup.LayoutParams params = getLayoutParams();
		params.height = getMeasuredHeight();
	}
}