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

package it.smartcampuslab.riciclo;

import it.smartcampuslab.riciclo.data.RifiutiHelper;

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

/**
 * @author raman
 * 
 */
public class FeedbackFragment extends Fragment {

	private Location mLocation;
	private boolean useLocation = false;
	private String imageUri = null;

	private int IMG_HEIGHT = 150;// dp

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
		((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.feedback_title));

		Button send = (Button) getView().findViewById(R.id.feedback_btn);
		send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", getString(R.string.feedback_to), null));
				intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_subject));
				String text = ((EditText) getView().findViewById(R.id.feedback_text_et)).getText().toString();
				if (RifiutiHelper.locationHelper.getLocation() != null) {
					mLocation = RifiutiHelper.locationHelper.getLocation();
					text += " \n\n[" + mLocation.getLatitude() + "," + mLocation.getLongitude() + "]";
					Log.e(getClass().getSimpleName(), "Feedback text: " + text);
				}
				intent.putExtra(Intent.EXTRA_TEXT, text);
				if (imageUri != null) {
					// intent.setType("application/image");
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
		if (check.isChecked()) {
			useLocation = true;
		}
		check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				useLocation = isChecked;
				if (isChecked) {
					// getActivity().setProgressBarIndeterminateVisibility(true);
					RifiutiHelper.locationHelper.start();
				} else {
					RifiutiHelper.locationHelper.stop();
					// getActivity().setProgressBarIndeterminateVisibility(false);
				}
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		if (useLocation) {
			RifiutiHelper.locationHelper.start();
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		RifiutiHelper.locationHelper.stop();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 100) {
			if (resultCode != 0) {
				// Uri imgUri = data.getData();
				File mediaStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

				File fi = new File(mediaStorageDir + File.separator + "tmpImg.jpg");
				if (fi.exists()) {
					try {
						Uri imgUri = Uri.parse(android.provider.MediaStore.Images.Media.insertImage(getActivity()
								.getContentResolver(), fi.getAbsolutePath(), null, null));
						imageUri = imgUri.toString();
						final BitmapFactory.Options options = new BitmapFactory.Options();
						options.inJustDecodeBounds = true;
						BitmapFactory.decodeFile(fi.getAbsolutePath(), options);

						options.inSampleSize = calculateInSampleSize(options, dpToPx(IMG_HEIGHT), dpToPx(IMG_HEIGHT));
						options.inJustDecodeBounds = false;
						Bitmap myBitmap = BitmapFactory.decodeFile(fi.getAbsolutePath(), options);

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

	private void startCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File mediaStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mediaStorageDir + File.separator + "tmpImg.jpg")));
		startActivityForResult(Intent.createChooser(intent, getString(R.string.feedback_capture)), 100);
	}

	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		int ref = Math.max(reqHeight, reqWidth);

		if (height > ref || width > ref) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > ref || (halfWidth / inSampleSize) > ref) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	public int dpToPx(int dp) {
		float density = getResources().getDisplayMetrics().density;
		return Math.round((float) dp * density);
	}
}
