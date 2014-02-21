package eu.trentorise.smartcampus.rifiuti;

import java.util.Arrays;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;

public class DoveLoButtoFragment extends Fragment {

	private EditText doveLoButtoSearchField;
	private ImageButton doveLoButtoSearchButton;
	private ListView doveLoButtoResults;
	private GridView tipiRifiutiGrid;
	// private GridLayout tipiRifiutiGrid;

	private String[] tipiRifiutiEntries;

	private List<String> RESULTS_TEST = Arrays.asList("Risultato", "Altro risultato", "Ancora uno", "Numero quattro",
			"El Cinco", "Franco Baresi");

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tipiRifiutiEntries = getResources().getStringArray(R.array.tipirifiuti_entries);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_dovelobutto, container, false);
		doveLoButtoSearchField = (EditText) viewGroup.findViewById(R.id.dovelobutto_search_tv);
		doveLoButtoSearchButton = (ImageButton) viewGroup.findViewById(R.id.dovelobutto_search_btn);
		doveLoButtoResults = (ListView) viewGroup.findViewById(R.id.dovelobutto_results);
		tipiRifiutiGrid = (GridView) viewGroup.findViewById(R.id.tipirifiuti_grid);
		// tipiRifiutiGrid = (GridLayout)
		// viewGroup.findViewById(R.id.tipirifiuti_grid);
		return viewGroup;
	}

	@Override
	public void onStart() {
		super.onStart();

		doveLoButtoResults
				.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, RESULTS_TEST));

		// doveLoButtoResults.setOnTouchListener(new OnTouchListener() {
		// // Setting on Touch Listener for handling the touch inside
		// // ScrollView
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		// // Disallow the touch request for parent scroll on touch of
		// // child view
		// v.getParent().requestDisallowInterceptTouchEvent(true);
		// return false;
		// }
		// });

		setListViewHeightBasedOnChildren(doveLoButtoResults);

		doveLoButtoSearchButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				doveLoButtoSearchField.setText(R.string.empty);
			}
		});

		doveLoButtoSearchField.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.toString().trim().length() == 0) {
					doveLoButtoResults.setVisibility(View.GONE);
					doveLoButtoSearchButton.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_search));
				} else {
					doveLoButtoResults.setVisibility(View.VISIBLE);
					doveLoButtoSearchButton.setImageDrawable(getResources().getDrawable(
							android.R.drawable.ic_menu_close_clear_cancel));
				}
			}
		});

		tipiRifiutiGrid.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.tipirifiuti_entry, tipiRifiutiEntries));

		/**
		 * fill Tipi di rifiuti gridLayout
		 */
		// for (String tipiRifiutiEntry : tipiRifiutiEntries) {
		// TextView textView = (TextView)
		// getActivity().getLayoutInflater().inflate(R.layout.tipirifiuti_entry,
		// null);
		// textView.setText(tipiRifiutiEntry);
		// tipiRifiutiGrid.addView(textView);
		// }
	}

	private void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter != null) {
			int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.UNSPECIFIED);
			int totalHeight = 0;
			View view = null;
			for (int i = 0; i < listAdapter.getCount(); i++) {
				view = listAdapter.getView(i, view, listView);
				if (i == 0) {
					view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
				}
				view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
				totalHeight += view.getMeasuredHeight();
			}
			ViewGroup.LayoutParams params = listView.getLayoutParams();
			params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
			listView.setLayoutParams(params);
			listView.requestLayout();
		}
	}

	// private void setGridViewHeightBasedOnChildren(GridView gridView) {
	// ListAdapter listAdapter = gridView.getAdapter();
	// if (listAdapter == null) {
	// return;
	// }
	//
	// int desiredWidth = MeasureSpec.makeMeasureSpec(gridView.getWidth(),
	// MeasureSpec.UNSPECIFIED);
	// int totalHeight = 0;
	// View view = null;
	// for (int i = 0; i < listAdapter.getCount(); i++) {
	// view = listAdapter.getView(i, view, gridView);
	// if (i == 0) {
	// view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth,
	// ViewGroup.LayoutParams.WRAP_CONTENT));
	// }
	//
	// view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
	// totalHeight += view.getMeasuredHeight();
	// }
	// ViewGroup.LayoutParams params = gridView.getLayoutParams();
	// params.height = totalHeight + (gridView.getDividerHeight() *
	// (listAdapter.getCount() - 1));
	// gridView.setLayoutParams(params);
	// gridView.requestLayout();
	// }

}
