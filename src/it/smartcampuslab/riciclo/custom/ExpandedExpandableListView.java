package it.smartcampuslab.riciclo.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

public class ExpandedExpandableListView extends ExpandableListView {

	/**
	 * This ListView expands to its maximum height
	 */
	
	public ExpandedExpandableListView(Context context) {
		super(context);
	}

	public ExpandedExpandableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ExpandedExpandableListView(Context context, AttributeSet attrs, int defStyle) {
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