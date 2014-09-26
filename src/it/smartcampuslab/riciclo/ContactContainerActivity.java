package it.smartcampuslab.riciclo;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;

public class ContactContainerActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_contact_container);
	}

}
