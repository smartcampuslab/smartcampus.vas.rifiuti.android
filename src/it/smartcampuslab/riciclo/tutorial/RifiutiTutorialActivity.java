package it.smartcampuslab.riciclo.tutorial;

import it.smartcampuslab.riciclo.data.RifiutiHelper;
import android.view.View;

import com.github.espiandev.showcaseview.BaseTutorialActivity;

public class RifiutiTutorialActivity extends BaseTutorialActivity {

	@Override
	public void skipTutorial(View v) {
		RifiutiHelper.setWantTour(this, false);
		this.mShowcaseView.hide();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

		// maybe we want to navigate back in tutorials?
		skipTutorial(null);
	}

}
