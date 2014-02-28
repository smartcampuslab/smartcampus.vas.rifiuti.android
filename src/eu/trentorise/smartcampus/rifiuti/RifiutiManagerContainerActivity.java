package eu.trentorise.smartcampus.rifiuti;

import eu.trentorise.smartcampus.rifiuti.utils.ArgUtils;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

public class RifiutiManagerContainerActivity extends ActionBarActivity {

	private int mContentFrameId;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private String tipologiaRifiuti = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		setContentView(R.layout.activity_rifiuti_container);

		mContentFrameId = R.id.content_frame;

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		Intent intent = getIntent();
		if (intent.getStringExtra(ArgUtils.ARGUMENT_TIPOLOGIA_RIFIUTO)!=null)
			tipologiaRifiuti = intent.getStringExtra(ArgUtils.ARGUMENT_TIPOLOGIA_RIFIUTO);
		loadRifiutiFragment();

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	private void loadRifiutiFragment() {
		Fragment fragment = null;
		fragment = new RifiutiManagerContainerFragment();
		if (fragment != null) {
			Bundle bundle = new Bundle();
			bundle.putString(ArgUtils.ARGUMENT_TIPOLOGIA_RIFIUTO, tipologiaRifiuti);
			fragment.setArguments(bundle);
			//passo il parametro (carta, accendino, ...)
			// Insert the fragment by replacing any existing fragment
			getSupportFragmentManager().beginTransaction().replace(mContentFrameId, fragment).commit();

		}
	}

}
