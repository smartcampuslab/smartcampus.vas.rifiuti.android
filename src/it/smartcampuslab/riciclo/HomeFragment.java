package it.smartcampuslab.riciclo;

import it.smartcampuslab.riciclo.data.NotesHelper;
import it.smartcampuslab.riciclo.utils.ArgUtils;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class HomeFragment extends Fragment {

	// private static final String PAGER_CURRENT_ITEM = "pagerCurrentItem";

	private String[] mPagerTitles;
	private ViewPager mPager;
	private HomePagerAdapter mPagerAdapter;
	private PagerTabStrip mPagerStrip;
	private ActionBarActivity abActivity;
	// private Integer pagerPreviousItem = null;
	public static DrawerLayout mDrawerLayout;
	public static ListView mDrawerList;

	private Bundle intentBundle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		mPagerTitles = getResources().getStringArray(R.array.home_pager_titles);

		intentBundle = this.getArguments();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		abActivity = (ActionBarActivity) getActivity();
		// abActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		abActivity.getSupportActionBar().setHomeButtonEnabled(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);

		mPager = (ViewPager) viewGroup.findViewById(R.id.pager);

		mPagerStrip = (PagerTabStrip) viewGroup.findViewById(R.id.pagerStrip);
		mPagerStrip.setDrawFullUnderline(false);
		mPagerStrip.setTabIndicatorColor(getResources().getColor(R.color.rifiuti_green_dark));
		// mPagerStrip.setTextColor(getResources().getColor(R.color.gray_dark));
		// mPagerStrip.setTextSpacing(48);
		// mPagerStrip.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

		mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				if (NotesHelper.notesActionMode != null) {
					NotesHelper.notesActionMode.finish();
				}
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}

			@Override
			public void onPageScrollStateChanged(int position) {
			}
		});

		// if (savedInstanceState != null &&
		// savedInstanceState.containsKey(PAGER_CURRENT_ITEM)) {
		// pagerPreviousItem = savedInstanceState.getInt(PAGER_CURRENT_ITEM);
		// }

		return viewGroup;
	}

	@Override
	public void onStart() {
		super.onStart();
		mPagerAdapter = new HomePagerAdapter(getChildFragmentManager());
		mPager.setAdapter(mPagerAdapter);

		if (intentBundle != null && intentBundle.containsKey(ArgUtils.ARGUMENT_CALENDAR_TOMORROW)) {
			// Page "Calendar"
			mPager.setCurrentItem(2);
		} else {
			// mPager.setCurrentItem(pagerPreviousItem != null ?
			// pagerPreviousItem : 1);
			// Page "Dove lo butto?" is default
			mPager.setCurrentItem(1);
		}

		abActivity.getSupportActionBar().setTitle(abActivity.getString(R.string.application_title));
	}

	@Override
	public void onPause() {
		super.onPause();
		// pagerPreviousItem = mPager.getCurrentItem();
		intentBundle = null;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// outState.putInt(PAGER_CURRENT_ITEM, mPager.getCurrentItem());
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		int index = mPager.getCurrentItem();
		Fragment f = mPagerAdapter.getItem(index);
		f.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * Adapter for the home viewPager
	 */
	private class HomePagerAdapter extends FragmentStatePagerAdapter {
		public HomePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				return new NotesListFragment();
			case 1:
				return new DoveLoButtoFragment();
			case 2:
				Fragment fragment = new CalendarFragment();
				if (intentBundle != null && intentBundle.containsKey(ArgUtils.ARGUMENT_CALENDAR_TOMORROW)) {
					fragment.setArguments(intentBundle);
				}
				return fragment;
			default:
				return new DummyFragment();
			}
		}

		@Override
		public int getCount() {
			return mPagerTitles.length;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return mPagerTitles[position];
		}
	}
}
