package eu.trentorise.smartcampus.rifiuti;

import java.io.IOException;
import java.util.List;

import com.github.espiandev.showcaseview.TutorialHelper;
import com.github.espiandev.showcaseview.TutorialHelper.TutorialProvider;
import com.github.espiandev.showcaseview.TutorialItem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import eu.trentorise.smartcampus.rifiuti.custom.ExpandedGridView;
import eu.trentorise.smartcampus.rifiuti.custom.ExpandedListView;
import eu.trentorise.smartcampus.rifiuti.data.RifiutiHelper;
import eu.trentorise.smartcampus.rifiuti.utils.ArgUtils;
import eu.trentorise.smartcampus.rifiuti.utils.KeyboardUtils;
import eu.trentorise.smartcampus.rifiuti.utils.PreferenceUtils;

public class DoveLoButtoFragment extends Fragment implements TutorialProvider {

	private static final int NUM_COLUMNS = 3;

	protected static final int THRESHOLD = 3;

	private static final int DEFAULT_DISTANCE = 60;
	private static final int DEFAULT_WIDTH = 50;

	private EditText doveLoButtoSearchField;
	private ImageButton doveLoButtoSearchButton;
	private ExpandedListView doveLoButtoResultsList;
	private ExpandedGridView tipiRifiutiGrid;
	private List<String> tipiRifiutiEntries;
	private ArrayAdapter<String> doveLoButtoAdapter;
	private TipiRifiutiAdapter tipiRifiutiAdapter;
	private TutorialHelper mTutorialHelper;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		try {
			if (RifiutiHelper.getInstance() == null) {
				RifiutiHelper.init(getActivity());
				RifiutiHelper.setProfile(PreferenceUtils.getProfile(
						getActivity(), PreferenceUtils
								.getCurrentProfilePosition(getActivity())));
			}
			tipiRifiutiEntries = RifiutiHelper.readTipologiaRifiuti();// getResources().getStringArray(R.array.tipirifiuti_entries);

		} catch (IOException e) {
			Log.e(DoveLoButtoFragment.class.getName(), e.toString());
			getFragmentManager().popBackStack();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup viewGroup = (ViewGroup) inflater.inflate(
				R.layout.fragment_dovelobutto, container, false);
		doveLoButtoSearchField = (EditText) viewGroup
				.findViewById(R.id.dovelobutto_search_tv);
		doveLoButtoSearchButton = (ImageButton) viewGroup
				.findViewById(R.id.dovelobutto_search_btn);
		doveLoButtoResultsList = (ExpandedListView) viewGroup
				.findViewById(R.id.dovelobutto_results);
		tipiRifiutiGrid = (ExpandedGridView) viewGroup
				.findViewById(R.id.tipirifiuti_grid);
		tipiRifiutiGrid.setNumColumns(NUM_COLUMNS);
		return viewGroup;
	}

	@Override
	public void onStart() {
		super.onStart();

		doveLoButtoAdapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1);
		doveLoButtoResultsList.setAdapter(doveLoButtoAdapter);

		doveLoButtoSearchButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				doveLoButtoSearchField.setText(R.string.empty);
			}
		});

		doveLoButtoSearchField.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.toString().trim().length() > 0) {
					doveLoButtoSearchButton
							.setImageDrawable(getResources()
									.getDrawable(
											android.R.drawable.ic_menu_close_clear_cancel));
				} else {
					doveLoButtoSearchButton.setImageDrawable(getResources()
							.getDrawable(R.drawable.ic_search));
				}

				if (s.toString().trim().length() < THRESHOLD) {
					doveLoButtoResultsList.setVisibility(View.GONE);
				} else {
					doveLoButtoAdapter.clear();
					List<String> suggestions = RifiutiHelper.getRifiuti(s
							.toString().trim());
					for (String suggestion : suggestions) {
						doveLoButtoAdapter.add(suggestion);
					}
					doveLoButtoAdapter.notifyDataSetChanged();
					doveLoButtoResultsList.setVisibility(View.VISIBLE);
				}
			}
		});

		doveLoButtoResultsList
				.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						KeyboardUtils.hideKeyboard(getActivity(), parent);
						FragmentTransaction fragmentTransaction = getActivity()
								.getSupportFragmentManager().beginTransaction();
						RifiutoDetailsFragment fragment = new RifiutoDetailsFragment();
						Bundle args = new Bundle();
						args.putString(ArgUtils.ARGUMENT_RIFIUTO,
								doveLoButtoAdapter.getItem(position));
						fragment.setArguments(args);
						fragmentTransaction
								.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
						fragmentTransaction.replace(R.id.content_frame,
								fragment, "rifiuti");
						fragmentTransaction.addToBackStack(fragment.getTag());
						fragmentTransaction.commit();

					}
				});

		tipiRifiutiAdapter = new TipiRifiutiAdapter(getActivity(),
				R.layout.tipirifiuti_entry, tipiRifiutiEntries);
		tipiRifiutiGrid.setAdapter(tipiRifiutiAdapter);
		tipiRifiutiGrid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getActivity(),
						RifiutiManagerContainerActivity.class);
				intent.putExtra(ArgUtils.ARGUMENT_TIPOLOGIA_RIFIUTO,
						tipiRifiutiEntries.get(position));
				startActivity(intent);
			}
		});

		getView().postDelayed(new Runnable() {

			@Override
			public void run() {
				if (RifiutiHelper.isFirstLaunchDoveLoButto(getActivity())) {
					mTutorialHelper = new TutorialHelper(getActivity(),
							DoveLoButtoFragment.this);
					mTutorialHelper.showTutorials();
				}
			}
		}, 100);

	}

	@Override
	public void onTutorialCancelled() {
		RifiutiHelper.disableFirstLaunchDoveLoButto(getActivity());
	}

	@Override
	public void onTutorialFinished() {
		RifiutiHelper.disableFirstLaunchDoveLoButto(getActivity());
	}

	@Override
	public TutorialItem getItemAt(int pos) {
		View v = null;
		int[] location = new int[2];
		switch (pos) {
		case 0:
			v = getView().findViewById(R.id.dovelobutto_search_btn);
			if (v.isShown()) {
				v.getLocationOnScreen(location);
				location[1] -= (int) RifiutiHelper.convertDpToPixel(
						DEFAULT_WIDTH / 5, getActivity());
				return new TutorialItem("search", location, v.getWidth(),
						"asd0", "asd0");
			}
			return null;
		case 1:
			v = tipiRifiutiGrid.getChildAt(4);
			if (v != null) {
				v.getLocationOnScreen(location);
				return new TutorialItem("tipo", location, v.getWidth(), "asd1",
						"asd");
			}
			return null;
		case 2:
			location[0] = (int) RifiutiHelper.convertDpToPixel(
					DEFAULT_DISTANCE / 10, getActivity());
			location[1] = (int) RifiutiHelper.convertDpToPixel(
					DEFAULT_DISTANCE, getActivity());
			return new TutorialItem("note", location,
					(int) RifiutiHelper.convertDpToPixel(DEFAULT_WIDTH,
							getActivity()), "asd2", "asd");
		case 3:
			location[0] = (int) (getActivity().getWindowManager()
					.getDefaultDisplay().getWidth() - RifiutiHelper
					.convertDpToPixel(DEFAULT_WIDTH + DEFAULT_WIDTH / 10,
							getActivity()));
			location[1] = (int) RifiutiHelper.convertDpToPixel(DEFAULT_DISTANCE
					- DEFAULT_DISTANCE / 12, getActivity());
			return new TutorialItem("calendario", location,
					(int) RifiutiHelper.convertDpToPixel(DEFAULT_WIDTH
							+ DEFAULT_WIDTH / 6, getActivity()), "asd3", "asd");
		case 4:
			location[0] = 0;
			location[1] = (int) RifiutiHelper.convertDpToPixel(
					DEFAULT_DISTANCE / 2.5f, getActivity());
			return new TutorialItem("menu", location,
					(int) RifiutiHelper.convertDpToPixel(DEFAULT_WIDTH,
							getActivity()), "Menu", "asd");

		}
		return null;
	}

	@Override
	public int size() {
		return 5;
	}
	
	
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		//super.onActivityResult(requestCode, resultCode, data);
		tutorialActivityFinishedOrCanceled(requestCode, resultCode, data);
	}

	public void tutorialActivityFinishedOrCanceled(int requestCode,int resultCode,Intent data){
		RifiutiHelper.disableFirstLaunchDoveLoButto(getActivity().getApplicationContext());
	}

}
