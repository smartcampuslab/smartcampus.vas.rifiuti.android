package it.smartcampuslab.riciclo;

import it.smartcampuslab.riciclo.utils.ArgUtils;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;

public class RifiutoDetailsActivity extends ActionBarActivity {

	private int mContentFrameId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_rifiutodetails);
		mContentFrameId = R.id.content_frame;

		Intent intent = getIntent();
		if (intent.hasExtra(ArgUtils.ARGUMENT_RIFIUTO)) {
			RifiutoDetailsFragment fragment = new RifiutoDetailsFragment();
			Bundle args = new Bundle();
			args.putString(ArgUtils.ARGUMENT_RIFIUTO, intent.getStringExtra(ArgUtils.ARGUMENT_RIFIUTO));
			fragment.setArguments(args);
			getSupportFragmentManager().beginTransaction().replace(mContentFrameId, fragment).commit();
		}
	}

}
