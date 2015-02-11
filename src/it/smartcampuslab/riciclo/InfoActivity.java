package it.smartcampuslab.riciclo;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class InfoActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.pull_up, android.R.anim.fade_out);
		setContentView(R.layout.info_credits);
		getSupportActionBar().hide();

		TextView version = (TextView) findViewById(R.id.credits_version);
		try {
			PackageInfo info = getApplication().getPackageManager().getPackageInfo(getApplication().getPackageName(), 0);
			version.setText(getString(R.string.version, info.versionName));
		} catch (NameNotFoundException e) {
			version.setVisibility(View.GONE);
		}

		ImageButton closeCreditsBtn = (ImageButton) findViewById(R.id.close_credits);
		closeCreditsBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		ImageView smartcampus = (ImageView) findViewById(R.id.smartcampus);
		smartcampus.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.addCategory(Intent.CATEGORY_BROWSABLE);
				intent.setData(Uri.parse("http://www.smartcampuslab.it/"));
				startActivity(intent);
			}
		});
		ImageView cct = (ImageView) findViewById(R.id.cct);
		cct.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.addCategory(Intent.CATEGORY_BROWSABLE);
				intent.setData(Uri.parse("http://www.comunitrentini.it/"));
				startActivity(intent);
			}
		});
		ImageView ladurner = (ImageView) findViewById(R.id.ladurner);
		ladurner.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.addCategory(Intent.CATEGORY_BROWSABLE);
				intent.setData(Uri.parse("http://www.ladurnerambiente.it"));
				startActivity(intent);
			}
		});
		ImageView fbk = (ImageView) findViewById(R.id.fbk);
		fbk.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.addCategory(Intent.CATEGORY_BROWSABLE);
				intent.setData(Uri.parse("https://www.fbk.eu/"));
				startActivity(intent);
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
		this.overridePendingTransition(android.R.anim.fade_in, R.anim.push_down);
	}

}
