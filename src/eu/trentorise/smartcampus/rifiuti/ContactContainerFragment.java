package eu.trentorise.smartcampus.rifiuti;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

	private ArrayList<HashMap<String,String>> istituzioni = null;
	private ArrayList<HashMap<String,String>> gestori = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);

		List<Istituzione> i = RifiutiHelper.getIstituzioni();
		istituzioni = getIstituzioniData(i);
		gestori = getGestoriData(RifiutiHelper.getGestori());
		mPagerTitles = new String[]{i.get(0).getTipologia(), getResources().getString(R.string.contacts_container_org)};
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
		return viewGroup;
	}

	@Override
	public void onStart() {
		super.onStart();
		abActivity.getSupportActionBar().setTitle(R.string.contacts_title);
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
				return ContactsFragment.newInstance(istituzioni);
			case 1:
				return ContactsFragment.newInstance(gestori);
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


	private ArrayList<HashMap<String, String>> getIstituzioniData(List<Istituzione> list) {
		ArrayList<HashMap<String,String>> data = new ArrayList<HashMap<String,String>>();
		for (Istituzione i : list) {
			HashMap<String, String> res = new HashMap<String, String>();
			res.put("name", i.getNome());
			res.put("description", i.getDescrizione());
			res.put("email", i.getEmail());
			res.put("phone", i.getTelefono());
			res.put("web", i.getSitoIstituzionale());
			res.put("pec", i.getPec());
			res.put("fax", i.getFax());
			res.put("address", i.getIndirizzo());
			res.put("office", i.getUfficio());
			res.put("opening", i.getOrarioUfficio());
			data.add(res);
		}
		return data;
	}
	private ArrayList<HashMap<String, String>> getGestoriData(List<Gestore> list) {
		ArrayList<HashMap<String,String>> data = new ArrayList<HashMap<String,String>>();
		for (Gestore i : list) {
			HashMap<String, String> res = new HashMap<String, String>();
			res.put("name", i.getRagioneSociale());
			res.put("description", i.getDescrizione());
			res.put("email", i.getEmail());
			res.put("phone", i.getTelefono());
			res.put("web", i.getSitoWeb());
			res.put("fax", i.getFax());
			res.put("address", i.getIndirizzo());
			res.put("office", i.getUfficio());
			res.put("opening", i.getOrarioUfficio());
			data.add(res);
		}
		return data;
	}
}
