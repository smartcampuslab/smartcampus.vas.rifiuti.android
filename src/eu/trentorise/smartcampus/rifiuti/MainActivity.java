package eu.trentorise.smartcampus.rifiuti;

import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import eu.trentorise.smartcampus.rifiuti.data.RifiutiHelper;
import eu.trentorise.smartcampus.rifiuti.model.Profile;
import eu.trentorise.smartcampus.rifiuti.utils.LocationUtils;
import eu.trentorise.smartcampus.rifiuti.utils.LocationUtils.ErrorType;
import eu.trentorise.smartcampus.rifiuti.utils.LocationUtils.ILocation;
import eu.trentorise.smartcampus.rifiuti.utils.PreferenceUtils;
import eu.trentorise.smartcampus.rifiuti.utils.onBackListener;

public class MainActivity extends ActionBarActivity implements ActionBar.OnNavigationListener, ILocation {

	private int mContentFrameId;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private LocationUtils mLocUtils;

	// private TutorialHelper mTutorialHelper = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main);

		mContentFrameId = R.id.content_frame;
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		mDrawerList.setAdapter(new DrawerArrayAdapter(this, R.layout.drawer_entry, getResources().getStringArray(
				R.array.drawer_entries_strings)));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		// mTutorialHelper = new ListViewTutorialHelper(this,
		// mNavDrawerTutorialProvider);

		addNavDrawerButton();

		RadioGroup rg = (RadioGroup) findViewById(R.id.profile_rg);
		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				Integer i = (Integer) findViewById(R.id.profile_rg).findViewById(checkedId).getTag();
				if (i != null) {
					try {
						PreferenceUtils.setCurrentProfilePosition(MainActivity.this, i);
						setCurrentProfile();
						Fragment fragment = getSupportFragmentManager().findFragmentById(mContentFrameId);
						if (fragment != null) {
							Fragment newFragment = getFragment(fragment);
							getSupportFragmentManager().beginTransaction().replace(mContentFrameId, newFragment).commit();
						}
						// mDrawerLayout.closeDrawer(findViewById(R.id.drawer_wrapper));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		try {
			RifiutiHelper.init(this.getApplicationContext());
			if (PreferenceUtils.getProfiles(this).isEmpty()) {
				lockDrawer();
				loadFragment(8);
				Toast.makeText(this, getString(R.string.toast_no_prof), Toast.LENGTH_SHORT).show();
			} else {
				prepareNavDropdown(true);
			}
		} catch (Exception e) {
			Toast.makeText(this, R.string.app_failure_setup, Toast.LENGTH_LONG).show();
			e.printStackTrace();
			finish();
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onBackPressed() {

		Fragment f = getSupportFragmentManager().findFragmentById(R.id.content_frame);
		if (f instanceof onBackListener) {
			((onBackListener) f).onBack();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// we can't open the drawer if it's locked
		if (mDrawerLayout.getDrawerLockMode(Gravity.START) != DrawerLayout.LOCK_MODE_LOCKED_CLOSED) {
			if (mDrawerToggle.onOptionsItemSelected(item)) {
				return true;
			}
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onNavigationItemSelected(int arg0, long arg1) {
		try {
			PreferenceUtils.setCurrentProfilePosition(this, arg0);
			setCurrentProfile();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private void setCurrentProfile() {
		if (PreferenceUtils.getCurrentProfilePosition(this) < 0) {
			RifiutiHelper.setProfile(PreferenceUtils.getProfile(this, 0));
		} else {
			RifiutiHelper.setProfile(PreferenceUtils.getProfile(this, PreferenceUtils.getCurrentProfilePosition(this)));
		}
	}

	public void prepareNavDropdown(boolean loadHome) {
		List<Profile> profiles = PreferenceUtils.getProfiles(this);
		if (profiles.size() > 1) {
			findViewById(R.id.curr_profile_tv).setVisibility(View.GONE);
			RadioGroup rg = (RadioGroup) findViewById(R.id.profile_rg);
			rg.setVisibility(View.VISIBLE);
			rg.removeAllViews();
			int curr = PreferenceUtils.getCurrentProfilePosition(this);
			int i = 0;
			for (Profile p : profiles) {
				RadioButton rb = new RadioButton(this);
				rb.setText(p.getName());
				rb.setTextColor(getResources().getColor(android.R.color.white));
				rb.setTag(i);
				rg.addView(rb);
				if (i == curr) {
					rb.setChecked(true);
				} else {
					rb.setChecked(false);
				}
				i++;
			}
		} else {
			((TextView) findViewById(R.id.curr_profile_tv)).setText(profiles.get(0).getName());
			findViewById(R.id.curr_profile_tv).setVisibility(View.VISIBLE);
			findViewById(R.id.profile_rg).setVisibility(View.GONE);
		}
		mDrawerToggle.syncState();
		if (loadHome) {
			unlockDrawer();
			showDrawer();
			setCurrentProfile();
			loadFragment(0);
		}
	}

	private void addNavDrawerButton() {
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
			}

			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				if (RifiutiHelper.isFirstLaunchMenu(MainActivity.this)) {
					// mTutorialHelper.showTutorials();
					RifiutiHelper.disableFirstLaunchMenu(MainActivity.this);
				}
			}

			public void onDrawerSlide(View drawerView, float slideOffset) {
				super.onDrawerSlide(drawerView, slideOffset);

				if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
					mDrawerLayout.bringChildToFront(drawerView);
					mDrawerLayout.requestLayout();
					mDrawerLayout.setScrimColor(Color.TRANSPARENT);
				}
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
	}

	/**
	 * @param fragment
	 * @return
	 */
	private Fragment getFragment(Fragment fragment) {
		try {
			return (Fragment) fragment.getClass().newInstance();
		} catch (Exception e) {
			return fragment;
		}
		// if (fragment instanceof HomeFragment) {
		// return 0;
		// }
		// if (fragment instanceof MapFragment) {
		// return 1;
		// }
		// if (fragment instanceof TipoRaccoltaListFragment) {
		// return 2;
		// }
		// if (fragment instanceof ProfilesListFragment) {
		// return 3;
		// }
		// if (fragment instanceof FeedbackFragment) {
		// return 4;
		// }
		// if (fragment instanceof ContactContainerFragment) {
		// return 5;
		// }
		// if (fragment instanceof InfoFragment) {
		// return 7;
		// }
		//
		// return 0;
	}

	private void loadFragment(int position) {
		Fragment fragment = null;

		switch (position) {
		case 0:
			fragment = new HomeFragment();
			break;
		case 1:
			fragment = new MapFragment();
			break;
		case 2:
			fragment = new TipoRaccoltaListFragment();
			break;
		case 3:
			fragment = new ProfilesListFragment();
			break;
		case 4:
			fragment = new FeedbackFragment();
			break;
		case 5:
			fragment = new ContactContainerFragment();
			break;
		case 6:
			prepareTutorial();
			fragment = new HomeFragment();
			break;
		case 7:
			fragment = new InfoFragment();
			break;
		case 8:
			fragment = new ProfileFragment();
			break;
		default:
			fragment = new DummyFragment();
			break;
		}

		if (fragment != null) {
			// Insert the fragment by replacing any existing fragment
			getSupportFragmentManager().beginTransaction().replace(mContentFrameId, fragment).commit();
			// Highlight the selected item, update the title, close the drawer
			mDrawerList.setItemChecked(position, true);
			// setTitle(mPlanetTitles[position]);
			mDrawerLayout.closeDrawer(findViewById(R.id.drawer_wrapper));
		}
	}

	private void prepareTutorial() {
		RifiutiHelper.resetTutorialDoveLoButto(getApplicationContext());
	}

	// USE WITH CARE!!
	public void lockDrawer() {
		if (mDrawerLayout != null) {
			mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
			getSupportActionBar().setHomeButtonEnabled(false);
		}
	}

	// USE WITH CARE!!
	public void unlockDrawer() {
		if (mDrawerLayout != null) {
			mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
			getSupportActionBar().setHomeButtonEnabled(true);
		}
	}

	// USE WITH CARE!!
	public void hideDrawer() {
		if (mDrawerToggle != null) {
			mDrawerToggle.setDrawerIndicatorEnabled(false);
		}
	}

	// USE WITH CARE!!
	public void showDrawer() {
		if (mDrawerToggle != null) {
			mDrawerToggle.setDrawerIndicatorEnabled(true);
		}
	}

	/**
	 * Drawer item click listener
	 */
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			loadFragment(position);
		}
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		Fragment f = getSupportFragmentManager().findFragmentById(mContentFrameId);
		if (f != null && f instanceof DoveLoButtoFragment && f.isVisible()) {
			getSupportFragmentManager().findFragmentById(mContentFrameId).onActivityResult(arg0, arg1, arg2);
		}
		super.onActivityResult(arg0, arg1, arg2);
	}

	@Override
	public void onLocationChaged(Location l) {
		RifiutiHelper.setCurrentLocation(l);
	}

	@Override
	public void onErrorOccured(ErrorType ex, String provider) {
	}

	@Override
	public void onStatusChanged(String provider, boolean isActive) {
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mLocUtils != null) {
			mLocUtils.close();
			mLocUtils = null;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		mLocUtils = new LocationUtils(this, this);
	}
	// @Override
	// protected void onActivityResult(int requestCode, int resultCode, Intent
	// data) {
	// super.onActivityResult(requestCode, resultCode, data);
	// mTutorialHelper.onTutorialActivityResult(requestCode, resultCode, data);
	//
	// }
	// private TutorialProvider mNavDrawerTutorialProvider = new
	// TutorialProvider() {
	//
	// TutorialItem[] tutorial = new TutorialItem[]{
	// new TutorialItem("home", null, 0, R.string.home_title,
	// R.string.home_tut),
	// new TutorialItem("punti di raccolta", null, 0,
	// R.string.punti_raccolta_title, R.string.punti_raccolta_tut),
	// new TutorialItem("tipi di raccolta", null, 0,
	// R.string.tipi_raccolta_title, R.string.tipi_raccolta_tut),
	// new TutorialItem("gestione profili", null, 0,
	// R.string.gestione_profili_title, R.string.gestione_profili_tut),
	// new TutorialItem("segnala", null, 0, R.string.segnala_title,
	// R.string.segnala_tut),
	// new TutorialItem("contatti", null, 0, R.string.contatti_title,
	// R.string.contatti_tut),
	// new TutorialItem("tutorial", null, 0, R.string.tutorial_title,
	// R.string.tutorial_tut),
	// new TutorialItem("info", null, 0, R.string.info_title, R.string.info_tut)
	// };
	//
	//
	// @Override
	// public void onTutorialFinished() {
	// mDrawerLayout.closeDrawer(findViewById(R.id.drawer_wrapper));
	// }
	//
	// @Override
	// public void onTutorialCancelled() {
	// mDrawerLayout.closeDrawer(findViewById(R.id.drawer_wrapper));
	// }
	//
	// @Override
	// public TutorialItem getItemAt(int i) {
	// ListViewTutorialHelper.fillTutorialItemParamsWithCorrection(tutorial[i],
	// i, mDrawerList, R.id.drawer_menu_icon,0,-12);
	// return tutorial[i];
	// }
	//
	// @Override
	// public int size() {
	// return tutorial.length;
	// }
	// };

}
