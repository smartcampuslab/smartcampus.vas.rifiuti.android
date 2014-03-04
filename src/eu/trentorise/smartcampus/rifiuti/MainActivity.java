package eu.trentorise.smartcampus.rifiuti;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import eu.trentorise.smartcampus.rifiuti.data.RifiutiHelper;
import eu.trentorise.smartcampus.rifiuti.model.Profile;
import eu.trentorise.smartcampus.rifiuti.utils.PreferenceUtils;

public class MainActivity extends ActionBarActivity implements
		ActionBar.OnNavigationListener {

	private int mContentFrameId;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		setContentView(R.layout.activity_main);

		mContentFrameId = R.id.content_frame;
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		mDrawerList.setAdapter(new DrawerArrayAdapter(this,
				R.layout.drawer_entry, getResources().getStringArray(
						R.array.drawer_entries_strings)));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		addNavDrawerButton();

		try {
			RifiutiHelper.init(this.getApplicationContext());
			if (PreferenceUtils.getProfiles(this).isEmpty()) {
				loadFragment(5);
			} else {
				prepareNavDropdown();
				setCurrentProfile();
				loadFragment(0);
			}

		} catch (Exception e) {
			Toast.makeText(this, R.string.app_failure_setup, Toast.LENGTH_LONG)
					.show();
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
		if (PreferenceUtils.getCurrentProfilePosition(this) < 0)
			RifiutiHelper.setProfile(PreferenceUtils.getProfile(this, 0));
		else
			RifiutiHelper.setProfile(PreferenceUtils.getProfile(this,
					PreferenceUtils.getCurrentProfilePosition(this)));
	}

	public void prepareNavDropdown() {
		List<Profile> profiles = PreferenceUtils.getProfiles(this);
		SpinnerAdapter adapter = new ArrayAdapter<Profile>(this,
				android.R.layout.simple_spinner_dropdown_item, profiles){

					@Override
					public View getView(int position, View convertView,
							ViewGroup parent) {
						if(convertView==null){
							LayoutInflater inflater = getLayoutInflater();
							convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent,false);
						}
						((TextView)convertView).setText(getItem(position).getName());
						return convertView;
					}
			
		};
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setListNavigationCallbacks(adapter, this);
	}

	private void addNavDrawerButton() {
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
			}

			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
	}

	private void loadFragment(int position) {
		Fragment fragment = null;

		switch (position) {
		case 0:
			fragment = new HomeFragment();
			break;
		case 5:
			fragment = new ProfilesListFragment();
			break;
		default:
			fragment = new DummyFragment();
			break;
		}

		if (fragment != null) {
			// Insert the fragment by replacing any existing fragment
			getSupportFragmentManager().beginTransaction()
					.replace(mContentFrameId, fragment).commit();
			// Highlight the selected item, update the title, close the drawer
			mDrawerList.setItemChecked(position, true);
			// setTitle(mPlanetTitles[position]);
			mDrawerLayout.closeDrawer(mDrawerList);
		}
	}

	// USE WITH CARE!!
	public void lockDrawer() {
		if (mDrawerLayout != null) {
			mDrawerLayout
					.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
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

	/**
	 * Drawer item click listener
	 */
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			loadFragment(position);
		}
	}

}
