package eu.trentorise.smartcampus.rifiuti;

import java.util.Arrays;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import eu.trentorise.smartcampus.rifiuti.custom.ExpandedGridView;
import eu.trentorise.smartcampus.rifiuti.custom.ExpandedListView;
import eu.trentorise.smartcampus.rifiuti.data.RifiutiHelper;

public class DoveLoButtoFragment extends Fragment {

	private static final int NUM_COLUMNS = 3;

	private EditText doveLoButtoSearchField;
	private ImageButton doveLoButtoSearchButton;
	private ExpandedListView doveLoButtoResultsList;
	private ExpandedGridView tipiRifiutiGrid;
	private List<String> tipiRifiutiEntries;

	private List<String> RESULTS_TEST = Arrays.asList("Risultato", "Altro risultato", "Ancora uno", "Numero quattro",
			"El Cinco", "Franco Baresi");

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tipiRifiutiEntries = RifiutiHelper.readTipologiaRifiuti();//getResources().getStringArray(R.array.tipirifiuti_entries);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_dovelobutto, container, false);
		doveLoButtoSearchField = (EditText) viewGroup.findViewById(R.id.dovelobutto_search_tv);
		doveLoButtoSearchButton = (ImageButton) viewGroup.findViewById(R.id.dovelobutto_search_btn);
		doveLoButtoResultsList = (ExpandedListView) viewGroup.findViewById(R.id.dovelobutto_results);
		tipiRifiutiGrid = (ExpandedGridView) viewGroup.findViewById(R.id.tipirifiuti_grid);
		tipiRifiutiGrid.setNumColumns(NUM_COLUMNS);
		return viewGroup;
	}

	@Override
	public void onStart() {
		super.onStart();

		doveLoButtoResultsList.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
				RESULTS_TEST));

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
					doveLoButtoResultsList.setVisibility(View.GONE);
					doveLoButtoSearchButton.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_search));
				} else {
					doveLoButtoResultsList.setVisibility(View.VISIBLE);
					doveLoButtoSearchButton.setImageDrawable(getResources().getDrawable(
							android.R.drawable.ic_menu_close_clear_cancel));
				}
			}
		});

		tipiRifiutiGrid.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.tipirifiuti_entry, tipiRifiutiEntries));
	}

}
