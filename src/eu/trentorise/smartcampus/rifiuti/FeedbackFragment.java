/*******************************************************************************
 * Copyright 2012-2013 Trento RISE
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either   express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package eu.trentorise.smartcampus.rifiuti;

import java.io.File;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import eu.trentorise.smartcampus.rifiuti.utils.LocationUtils;
import eu.trentorise.smartcampus.rifiuti.utils.LocationUtils.ErrorType;
import eu.trentorise.smartcampus.rifiuti.utils.LocationUtils.ILocation;

/**
 * @author raman
 *
 */
public class FeedbackFragment extends Fragment implements ILocation {

	private LocationUtils mLocUtils;

	private Location mLocation;
	private boolean useLocation = false;
	private String imageUri = null;
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable("location", mLocation);
		outState.putBoolean("useLocation", useLocation);
		outState.putString("image", imageUri);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey("location")) {
				mLocation = savedInstanceState.getParcelable("location");
			}
			if (savedInstanceState.containsKey("useLocation")) {
				useLocation = savedInstanceState.getBoolean("useLocation");
			}
			if (savedInstanceState.containsKey("image")) {
				imageUri = savedInstanceState.getString("image");
			}
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_feedback, container, false);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		((ActionBarActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.feedback_title));

		Button send = (Button) getView().findViewById(R.id.feedback_btn);
		send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_SENDTO, 
						Uri.fromParts("mailto",getString(R.string.feedback_to), null));
				intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_subject));
				String text = ((EditText)getView().findViewById(R.id.feedback_text_et)).getText().toString();
				if (mLocation != null) {
					text +=" \n\n["+mLocation.getLatitude()+","+mLocation.getLongitude()+"]";
				}
				intent.putExtra(Intent.EXTRA_TEXT, text);
				if (imageUri != null) {
//					intent.setType("application/image");
					intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(imageUri));
				}
				getActivity().startActivity(Intent.createChooser(intent, getString(R.string.feedback_mail)));
			}
		});
		
		OnClickListener clickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				startCamera();
			}
		};
		getView().findViewById(R.id.feedback_img).setOnClickListener(clickListener);
		getView().findViewById(R.id.feedback_img_text).setOnClickListener(clickListener);
		
		CheckBox check = (CheckBox) getView().findViewById(R.id.feedback_gps);
		check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				useLocation = isChecked;
				if (isChecked) {
					getActivity().setProgressBarIndeterminateVisibility(true);
					mLocUtils = new LocationUtils(getActivity(), FeedbackFragment.this);
				} else {
					mLocUtils = null;
					getActivity().setProgressBarIndeterminateVisibility(false);
				}		
			}
		});
	}
	
	@Override
	public void onLocationChaged(Location l) {
		Log.i(ProfileFragment.class.getName(), l.toString());
		mLocation = l;
		getActivity().setProgressBarIndeterminateVisibility(false);
		if (mLocUtils != null) {
			mLocUtils.close();
			mLocUtils = null;
		}
	}
	@Override
	public void onErrorOccured(ErrorType ex, String provider) {
		// Do nothing, the user should just type what it wants
		Log.e(ProfileFragment.class.getName(), "Provider:" + provider + "\nErrorType:" + ex);
	}
	@Override
	public void onStatusChanged(String provider, boolean isActive) {
		if (!isActive) {
			Toast.makeText(getActivity(), getString(R.string.err_gps_off), Toast.LENGTH_SHORT).show();
			setGPS(false);
		}
	}

	/**
	 * 
	 */
	private void setGPS(boolean state) {
		useLocation = state;
		((CheckBox)getView().findViewById(R.id.feedback_gps)).setChecked(state);
	}

	@Override
	public void onPause() {
		super.onPause();
		getActivity().setProgressBarIndeterminateVisibility(false);
		if (mLocUtils != null) {
			mLocUtils.close();
			mLocUtils = null;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		// if checked and location == null restart
		if (useLocation && mLocation == null) {
			getActivity().setProgressBarIndeterminateVisibility(true);
			mLocUtils = new LocationUtils(getActivity(), this);
		}
	}

	private void startCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		File mediaStorageDir = Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

		intent.putExtra(
				MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(new File(mediaStorageDir + File.separator + "tmpImg.jpg")));

		startActivityForResult(
				Intent.createChooser(intent, getString(R.string.feedback_capture)), 100);

	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 100) {
			if (resultCode != 0) {
				// Uri imgUri = data.getData();
				File mediaStorageDir = 
						Environment
								.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

				File fi = new File(mediaStorageDir + File.separator + "tmpImg.jpg");
				if (fi.exists()) {
					try {
						Uri imgUri = Uri
								.parse(android.provider.MediaStore.Images.Media
										.insertImage(getActivity().getContentResolver(),
												fi.getAbsolutePath(), null, null));
						imageUri = imgUri.toString();
					    Bitmap myBitmap = BitmapFactory.decodeFile(fi.getAbsolutePath());
						ImageView myImage = (ImageView) getView().findViewById(R.id.feedback_img_result);
						myImage.setVisibility(View.VISIBLE);
						myImage.setImageBitmap(myBitmap);
					} catch (Exception e) {
						e.printStackTrace();
						Log.e("CaptureHelper", "Error reading image");
					}
				}
			}

		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
