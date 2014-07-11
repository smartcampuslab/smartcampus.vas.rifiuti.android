package eu.trentorise.smartcampus.rifiuti.tutorial;

import android.view.View;

import com.github.espiandev.showcaseview.BaseTutorialActivity;

import eu.trentorise.smartcampus.rifiuti.data.RifiutiHelper;

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
