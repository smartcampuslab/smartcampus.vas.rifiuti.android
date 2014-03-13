package eu.trentorise.smartcampus.rifiuti;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.github.espiandev.showcaseview.ListViewTutorialHelper;
import com.github.espiandev.showcaseview.TutorialHelper;
import com.github.espiandev.showcaseview.TutorialHelper.TutorialProvider;
import com.github.espiandev.showcaseview.TutorialItem;

import eu.trentorise.smartcampus.rifiuti.data.NotesHelper;
import eu.trentorise.smartcampus.rifiuti.data.RifiutiHelper;

public class HomeFragment extends Fragment {

	private static final String PAGER_CURRENT_ITEM = "pagerCurrentItem";

	private String[] mPagerTitles;
	private ViewPager mPager;
	private HomePagerAdapter mPagerAdapter;
	private PagerTabStrip mPagerStrip;
	private ActionBarActivity abActivity;
	private Integer pagerPreviousItem = null;
	public static DrawerLayout mDrawerLayout;
	public static ListView mDrawerList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPagerTitles = getResources().getStringArray(R.array.home_pager_titles);
		setHasOptionsMenu(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		abActivity = (ActionBarActivity) getActivity();
		abActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		abActivity.getSupportActionBar().setHomeButtonEnabled(true);

		if (RifiutiHelper.isFirstLaunchHome(getActivity())) {

			RifiutiHelper.disableFirstLaunchHome(getActivity());
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup viewGroup = (ViewGroup) inflater.inflate(
				R.layout.fragment_home, container, false);

		mPager = (ViewPager) viewGroup.findViewById(R.id.pager);

		mPagerStrip = (PagerTabStrip) viewGroup.findViewById(R.id.pagerStrip);
		mPagerStrip.setDrawFullUnderline(false);
		mPagerStrip.setTabIndicatorColor(getResources().getColor(
				R.color.rifiuti_green_dark));
		// mPagerStrip.setTextColor(getResources().getColor(R.color.gray_dark));
		// mPagerStrip.setTextSpacing(48);
		// mPagerStrip.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

		mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				if (NotesHelper.notesActionMode != null)
					NotesHelper.notesActionMode.finish();
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				if (NotesHelper.notesActionMode != null)
					NotesHelper.notesActionMode.finish();
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		if (savedInstanceState != null
				&& savedInstanceState.containsKey(PAGER_CURRENT_ITEM)) {
			pagerPreviousItem = savedInstanceState.getInt(PAGER_CURRENT_ITEM);
		}

		return viewGroup;
	}

	@Override
	public void onStart() {
		super.onStart();
		mPagerAdapter = new HomePagerAdapter(getChildFragmentManager());
		mPager.setAdapter(mPagerAdapter);

		// Page "Dove lo butto?" is default
		mPager.setCurrentItem(pagerPreviousItem != null ? pagerPreviousItem : 1);

		// Page "Calendar" is default
		// mPager.setCurrentItem(2);
		abActivity.getSupportActionBar().setTitle(
				abActivity.getString(R.string.application_title));
	}

	@Override
	public void onPause() {
		super.onPause();
		pagerPreviousItem = mPager.getCurrentItem();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(PAGER_CURRENT_ITEM, mPager.getCurrentItem());
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		int index = mPager.getCurrentItem();
		Fragment f = mPagerAdapter.getItem(index);
		if ( f!=null && f instanceof DoveLoButtoFragment  )
			((DoveLoButtoFragment)f).tutorialActivityFinishedOrCanceled(requestCode, resultCode, data);
		return;
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
				return new CalendarFragment();
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
