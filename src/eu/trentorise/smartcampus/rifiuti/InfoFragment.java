package eu.trentorise.smartcampus.rifiuti;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class InfoFragment extends Fragment {

	private ActionBarActivity abActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_info, container, false);
		return viewGroup;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		abActivity = (ActionBarActivity) getActivity();
		// abActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		abActivity.getSupportActionBar().setHomeButtonEnabled(true);
		abActivity.getSupportActionBar().setTitle(abActivity.getString(R.string.informazioni_title));
	}

	@Override
	public void onStart() {
		super.onStart();
		ImageView smartcampus = (ImageView) getActivity().findViewById(R.id.smartcampus);
		smartcampus.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.addCategory(Intent.CATEGORY_BROWSABLE);
				intent.setData(Uri.parse("http://www.smartcampuslab.it/"));
				startActivity(intent);
			}
		});
		ImageView cct = (ImageView) getActivity().findViewById(R.id.cct);
		cct.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.addCategory(Intent.CATEGORY_BROWSABLE);
				intent.setData(Uri.parse("http://www.comunitrentini.it/"));
				startActivity(intent);
			}
		});
		ImageView ladurner = (ImageView) getActivity().findViewById(R.id.ladurner);
		ladurner.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.addCategory(Intent.CATEGORY_BROWSABLE);
				intent.setData(Uri.parse("http://www.ladurnerambiente.it"));
				startActivity(intent);
			}
		});
		ImageView fbk = (ImageView) getActivity().findViewById(R.id.fbk);
		fbk.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.addCategory(Intent.CATEGORY_BROWSABLE);
				intent.setData(Uri.parse("https://www.fbk.eu/"));
				startActivity(intent);
			}
		});

		ImageView feedback = (ImageView) getActivity().findViewById(R.id.info_feedback_img);
		feedback.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Fragment fragment = new FeedbackFragment();
				getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();

			}
		});
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

}
