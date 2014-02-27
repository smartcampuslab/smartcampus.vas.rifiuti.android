package eu.trentorise.smartcampus.rifiuti;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HomeFragment extends Fragment {

	private String[] mPagerTitles;
	private ViewPager mPager;
	private HomePagerAdapter mPagerAdapter;
	private PagerTabStrip mPagerStrip;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPagerTitles = getResources().getStringArray(R.array.home_pager_titles);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);

		mPager = (ViewPager) viewGroup.findViewById(R.id.pager);

		mPagerStrip = (PagerTabStrip) viewGroup.findViewById(R.id.pagerStrip);
		mPagerStrip.setDrawFullUnderline(false);
		mPagerStrip.setTabIndicatorColor(getResources().getColor(android.R.color.darker_gray));
		// mPagerStrip.setTextColor(getResources().getColor(R.color.gray_dark));
		// mPagerStrip.setTextSpacing(48);
		// mPagerStrip.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

		return viewGroup;
	}

	@Override
	public void onStart() {
		super.onStart();
		mPagerAdapter = new HomePagerAdapter(getChildFragmentManager());
		mPager.setAdapter(mPagerAdapter);

		// Page "Dove lo butto?" is default
		// mPager.setCurrentItem(1);

		// Page "Calendar" is default
		mPager.setCurrentItem(2);
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
