package eu.trentorise.smartcampus.rifiuti;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
	private TutorialHelper mTutorialHelper = null;
	public static DrawerLayout mDrawerLayout;
	private boolean tutorialHasOpened = false;
	public static ListView mDrawerList;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPagerTitles = getResources().getStringArray(R.array.home_pager_titles);
		abActivity = (ActionBarActivity) getActivity();
		setHasOptionsMenu(true);
		abActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		abActivity.getSupportActionBar().setHomeButtonEnabled(true);
		
		mTutorialHelper = new ListViewTutorialHelper(RifiutiTutorialActivity.this, mTutorialProvider);

		if (RifiutiHelper.isFirstLaunch(getActivity())) {
			openNavDrawerIfNeeded();
			
			showTourDialog();
			RifiutiHelper.disableFirstLaunch(getActivity());
		}
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

		if (savedInstanceState != null && savedInstanceState.containsKey(PAGER_CURRENT_ITEM)) {
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
		abActivity.getSupportActionBar().setTitle(abActivity.getString(R.string.application_title));
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
private TutorialProvider mTutorialProvider = new TutorialProvider() {
		
		TutorialItem[] tutorial = new TutorialItem[]{
				new TutorialItem("home", null, 0, R.string.home_title, R.string.home_tut),
				new TutorialItem("punti raccolta", null, 0, R.string.punti_raccolta_title, R.string.punti_raccolta_tut),
				new TutorialItem("tipi raccolta", null, 0, R.string.tipi_raccolta_title, R.string.tipi_raccolta_tut),
				new TutorialItem("gestione profili", null, 0, R.string.gestione_profili_title, R.string.gestione_profili_tut),
				new TutorialItem("segnala", null, 0, R.string.segnala_title, R.string.segnala_tut),
				new TutorialItem("contatti", null, 0, R.string.contatti_title, R.string.contatti_tut),
				new TutorialItem("tutorial", null, 0, R.string.tutorial_title, R.string.tutorial_tut),
				new TutorialItem("info", null, 0, R.string.info_title, R.string.info_tut),



}; 
		@Override
		public void onTutorialFinished() {
			mDrawerLayout.closeDrawer(mDrawerList);
		}
		
		@Override
		public void onTutorialCancelled() {
			mDrawerLayout.closeDrawer(mDrawerList);
		}
		
		@Override
		public TutorialItem getItemAt(int i) {
			ListViewTutorialHelper.fillTutorialItemParams(tutorial[i], i, mDrawerList, R.id.drawer_menu_item);
			return tutorial[i];
		}
		
		@Override
		public int size() {
			return tutorial.length;
		}
	};
	
	private boolean openNavDrawerIfNeeded() {
		DrawerLayout mDrawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
		if (!mDrawerLayout.isDrawerOpen(Gravity.LEFT))
		{
			tutorialHasOpened=true;
			mDrawerLayout.openDrawer(Gravity.LEFT);
			return true;
			
		}
		return false;
	}
	
	private void showTourDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setMessage("bla bla")
				.setPositiveButton(getString(R.string.begin_tut), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						RifiutiHelper.setWantTour(getActivity(), true);
						mTutorialHelper.showTutorials();
//						showTutorial();
					}
				}).setNeutralButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						RifiutiHelper.setWantTour(getActivity(), false);
						dialog.dismiss();
					}
				});
		builder.create().show();
	}
}
