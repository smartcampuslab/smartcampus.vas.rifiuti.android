package eu.trentorise.smartcampus.rifiuti;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import eu.trentorise.smartcampus.rifiuti.utils.ArgUtils;

public class RifiutiManagerContainerFragment extends Fragment {

	private String[] mPagerTitles;
	private ViewPager mPager;
	private HomePagerAdapter mPagerAdapter;
	private String tipologiaRifiuto = null;
	private String tipologiaRaccolta = null;
	private ActionBarActivity abActivity = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		abActivity = (ActionBarActivity) getActivity();
		abActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		abActivity.getSupportActionBar().setHomeButtonEnabled(true);
	}



	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			abActivity.finish();
			return true;
		}
		return false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_rifiuti_container, container, false);

		mPager = (ViewPager) viewGroup.findViewById(R.id.pager);
		mPager.setOnPageChangeListener(
	            new ViewPager.SimpleOnPageChangeListener() {
	                @Override
	                public void onPageSelected(int position) {
	                    // When swiping between pages, select the
	                    // corresponding tab.
	                    abActivity.getSupportActionBar().setSelectedNavigationItem(position);
	                }
	            });
//
//		mPagerStrip = (PagerTabStrip) viewGroup.findViewById(R.id.pagerStrip);
//		mPagerStrip.setDrawFullUnderline(false);
//		mPagerStrip.setTabIndicatorColor(getResources().getColor(android.R.color.darker_gray));
		// mPagerStrip.setTextColor(getResources().getColor(R.color.gray_dark));
		// mPagerStrip.setTextSpacing(48);
		// mPagerStrip.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

		return viewGroup;
	}

	@Override
	public void onStart() {
		super.onStart();

		// prendo il parametro e setto il parametro di default
		Bundle bundle = getArguments();
		mPagerTitles = getResources().getStringArray(R.array.rifiuti_container_titles);
		if (bundle.containsKey(ArgUtils.ARGUMENT_TIPOLOGIA_RIFIUTO) && bundle.getString(ArgUtils.ARGUMENT_TIPOLOGIA_RIFIUTO) != null) {
			tipologiaRifiuto = bundle.getString(ArgUtils.ARGUMENT_TIPOLOGIA_RIFIUTO);
			String[] mPagerTitlesInv = new String[mPagerTitles.length];
			for (int i = 0; i < mPagerTitlesInv.length; i++) {
				mPagerTitlesInv[i] = mPagerTitles[mPagerTitlesInv.length-1-i];
			}
			mPagerTitles = mPagerTitlesInv;
		}
		else if (bundle.containsKey(ArgUtils.ARGUMENT_TIPOLOGIA_RACCOLTA)) {
			tipologiaRaccolta = bundle.getString(ArgUtils.ARGUMENT_TIPOLOGIA_RACCOLTA);
		}

		abActivity.getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		mPagerAdapter = new HomePagerAdapter(getChildFragmentManager());
		mPager.setAdapter(mPagerAdapter);
		
		// Create a tab listener that is called when the user changes tabs.
	    ActionBar.TabListener tabListener = new ActionBar.TabListener() {
	        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
	        	 mPager.setCurrentItem(tab.getPosition());
	        }

	        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
	            // hide the given tab
	        }

	        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
	            // probably ignore this event
	        }
	    };
	    abActivity.getSupportActionBar().removeAllTabs();
	    for (int i = 0; i < mPagerTitles.length; i++) {
	        abActivity.getSupportActionBar().addTab(
	        		abActivity.getSupportActionBar().newTab()
	                        .setText(mPagerTitles[i])
	                        .setTabListener(tabListener));
	    }

		mPager.setCurrentItem(0);
		// if (bundle.containsKey(ArgUtils.ARGUMENT_PUNTO_DI_RACCOLTA))
		// mPager.setCurrentItem(1);
		if (tipologiaRifiuto != null) {
			abActivity.getSupportActionBar().setTitle(
					abActivity.getString(R.string.tipo_di_rifiuto_title) );
			abActivity.getSupportActionBar().setSubtitle(tipologiaRifiuto);
		} else if (tipologiaRaccolta != null) {
			abActivity.getSupportActionBar().setTitle(
					abActivity.getString(R.string.tipo_di_raccolta_title));
			abActivity.getSupportActionBar().setSubtitle(tipologiaRaccolta);

		}

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
					return PuntiDiRaccoltaListFragment.newIstanceTipologiaRifiuto(tipologiaRifiuto);
				} else if (tipologiaRaccolta != null) {
					return RifiutiListFragment.newIstanceTipologiaRaccolta(tipologiaRaccolta);
				}
			case 1:
				if (tipologiaRifiuto != null) {
					return RifiutiListFragment.newIstanceTipologiaRifiuto(tipologiaRifiuto);
				} else if (tipologiaRaccolta != null) {
					return PuntiDiRaccoltaListFragment.newIstanceTipologiaRaccolta(tipologiaRaccolta);
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
			switch (position) {
			case 0:
				if (tipologiaRifiuto != null) {
					return mPagerTitles[1];
				} else if (tipologiaRaccolta != null) {
					return mPagerTitles[0];
				}
			case 1:
				if (tipologiaRifiuto != null) {
					return mPagerTitles[0];
				} else if (tipologiaRaccolta != null) {
					return mPagerTitles[0];
				}
			}
			return mPagerTitles[position];
		}
	}

}
