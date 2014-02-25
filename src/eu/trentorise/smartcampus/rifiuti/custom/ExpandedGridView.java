package eu.trentorise.smartcampus.rifiuti.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.GridView;

public class ExpandedGridView extends GridView {

	/**
	 * This GridView expands to its maximum height
	 */

	public ExpandedGridView(Context context) {
		super(context);
	}

	public ExpandedGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ExpandedGridView(Context context, AttributeSet attrs, int defStyle) {
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