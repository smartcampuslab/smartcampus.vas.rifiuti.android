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
import eu.trentorise.smartcampus.rifiuti.utils.ArgUtils;

public class RifiutiManagerContainerFragment extends Fragment {

	private String[] mPagerTitles;
	private ViewPager mPager;
	private HomePagerAdapter mPagerAdapter;
	private PagerTabStrip mPagerStrip;
	private String tipologiaRifiuto = null;
	private String tipologiaRaccolta = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPagerTitles = getResources().getStringArray(R.array.rifiuti_container_titles);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_rifiuti_container, container, false);

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
		// prendo il parametro e setto il parametro di default
		Bundle bundle = getArguments();
		if (bundle.containsKey(ArgUtils.ARGUMENT_TIPOLOGIA_RIFIUTO)) {
			tipologiaRifiuto = bundle.getString(ArgUtils.ARGUMENT_TIPOLOGIA_RIFIUTO);
		}
		if (bundle.containsKey(ArgUtils.ARGUMENT_TIPOLOGIA_RACCOLTA)) {
			tipologiaRaccolta = bundle.getString(ArgUtils.ARGUMENT_TIPOLOGIA_RACCOLTA);
		}
		mPager.setCurrentItem(0);
		// if (bundle.containsKey(ArgUtils.ARGUMENT_PUNTO_DI_RACCOLTA))
		// mPager.setCurrentItem(1);
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
				if (tipologiaRifiuto != null) {
					return new RifiutiListFragment().newIstanceTipologiaRifiuto(tipologiaRifiuto);
				} else if (tipologiaRaccolta != null) {
					return new RifiutiListFragment().newIstanceTipologiaRaccolta(tipologiaRaccolta);
				}
			case 1:
				if (tipologiaRifiuto != null) { 
					return new PuntiDiRaccoltaListFragment().newIstanceTipologiaRifiuto(tipologiaRifiuto);
				} else if (tipologiaRaccolta != null) {
					return new PuntiDiRaccoltaListFragment().newIstanceTipologiaRaccolta(tipologiaRaccolta);
				}
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
