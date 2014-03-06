package eu.trentorise.smartcampus.rifiuti;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.tyczj.extendedcalendarview.Day;

import eu.trentorise.smartcampus.rifiuti.utils.ArgUtils;

public class CalendarAgendaActivity extends ActionBarActivity {

	private int mContentFrameId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_calendaragenda);
		mContentFrameId = R.id.content_frame;

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		Intent intent = getIntent();
		if (intent.hasExtra(ArgUtils.ARGUMENT_CALENDAR_DAY)) {
			Day day = (Day) intent.getSerializableExtra(ArgUtils.ARGUMENT_CALENDAR_DAY);
			Fragment fragment = new CalendarAgendaFragment();
			Bundle bundle = new Bundle();
			bundle.putSerializable(ArgUtils.ARGUMENT_CALENDAR_DAY, day);
			fragment.setArguments(bundle);
			getSupportFragmentManager().beginTransaction().replace(mContentFrameId, fragment).commit();
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
		return super.onOptionsItemSelected(item);
	}

}
