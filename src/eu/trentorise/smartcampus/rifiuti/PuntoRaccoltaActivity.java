package eu.trentorise.smartcampus.rifiuti;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import eu.trentorise.smartcampus.rifiuti.model.PuntoRaccolta;
import eu.trentorise.smartcampus.rifiuti.utils.ArgUtils;

public class PuntoRaccoltaActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_punto_raccolta);
		loadDetailsFragment();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
			return true;
		}
		return false;
	}

	private void loadDetailsFragment() {
		PuntoRaccolta pr = (PuntoRaccolta) getIntent().getSerializableExtra(ArgUtils.ARGUMENT_PUNTO_DI_RACCOLTA);
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		PuntoDiRaccoltaDetailFragment fragment = new PuntoDiRaccoltaDetailFragment();
		Bundle args = new Bundle();
		args.putSerializable(ArgUtils.ARGUMENT_PUNTO_DI_RACCOLTA, pr);
		fragment.setArguments(args);
		fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		fragmentTransaction.replace(R.id.content_frame, fragment);
		fragmentTransaction.commit();
	}

}
