package eu.trentorise.smartcampus.rifiuti;

import java.util.HashMap;

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
import eu.trentorise.smartcampus.rifiuti.data.RifiutiHelper;
import eu.trentorise.smartcampus.rifiuti.model.Gestore;
import eu.trentorise.smartcampus.rifiuti.model.Istituzione;

public class ContactContainerFragment extends Fragment {

	private String[] mPagerTitles;
	private ViewPager mPager;
	private ContantsPagerAdapter mPagerAdapter;
	private ActionBarActivity abActivity = null;

	private HashMap<String,String> istituzione = null;
	private HashMap<String,String> gestore = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPagerTitles = getResources().getStringArray(R.array.contacts_container_titles);
		abActivity = (ActionBarActivity) getActivity();

		setHasOptionsMenu(true);

		abActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		abActivity.getSupportActionBar().setHomeButtonEnabled(true);
		
		istituzione = getData(RifiutiHelper.getIstituzione());
		gestore = getData(RifiutiHelper.getGestore());
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
	   	        	    updateTitle(position);
	                }
	            });
		return viewGroup;
	}

	@Override
	public void onStart() {
		super.onStart();
		abActivity.getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		mPagerAdapter = new ContantsPagerAdapter(getChildFragmentManager());
		mPager.setAdapter(mPagerAdapter);
		// Create a tab listener that is called when the user changes tabs.
	    ActionBar.TabListener tabListener = new ActionBar.TabListener() {
	        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
	        	 mPager.setCurrentItem(tab.getPosition());
	        }
	        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
	        }
	        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
	        }
	    };
	    abActivity.getSupportActionBar().removeAllTabs();
	    for (int i = 0; i < mPagerTitles.length; i++) {
	        abActivity.getSupportActionBar().addTab(
	        		abActivity.getSupportActionBar().newTab()
	                        .setText(mPagerTitles[i])
	                        .setTag("contacts"+i)
	                        .setTabListener(tabListener));
	    }

		mPager.setCurrentItem(0);
		updateTitle(0);
	}

	@Override
	public void onStop() {
		super.onStop();
		abActivity.getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
	}
	
	/**
	 * Adapter for the home viewPager
	 */
	private class ContantsPagerAdapter extends FragmentStatePagerAdapter {
		public ContantsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				return ContactsFragment.newInstance(istituzione);
			case 1:
				return ContactsFragment.newInstance(gestore);
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


	private HashMap<String, String> getData(Istituzione i) {
		HashMap<String, String> res = new HashMap<String, String>();
		res.put("name", i.getNome());
		res.put("description", i.getDescrizione());
		res.put("address", null);
		res.put("email", i.getEmail());
		res.put("phone", i.getTelefono());
		return res;
	}
	private HashMap<String, String> getData(Gestore i) {
		HashMap<String, String> res = new HashMap<String, String>();
		res.put("name", i.getRagioneSociale());
		res.put("description", i.getDescrizione());
		res.put("address", null);
		res.put("email", i.getEmail());
		res.put("phone", i.getTelefono());
		return res;
	}
	private void updateTitle(int position) {
		String name = position == 0 ? istituzione.get("name") : gestore.get("name");
		abActivity.getSupportActionBar().setTitle(abActivity.getString(R.string.contacts_title, name));
	}
}
