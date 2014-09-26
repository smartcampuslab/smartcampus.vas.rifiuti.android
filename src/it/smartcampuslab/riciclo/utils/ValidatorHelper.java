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

package it.smartcampuslab.riciclo.utils;

import it.smartcampuslab.riciclo.R;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

/**
 * Validate input fields providing feedback to the user in case of errors.
 * 
 * @author raman
 * 
 */
public class ValidatorHelper {

	/**
	 * Highlight the invalid field with the animation and show the toast
	 * message.
	 * 
	 * @param ctx
	 * @param view
	 *            field containing incorrect value
	 * @param text
	 *            message to show to the user
	 */
	public static void highlight(Context ctx, View view, String text) {
		Toast toast = null;
		if (text != null)
			toast = Toast.makeText(ctx, text, Toast.LENGTH_LONG);
		if (view != null) {
			int[] coords = new int[2];
			view.clearFocus();
			view.requestFocus();
			view.getLocationOnScreen(coords);
			if (toast != null)
				toast.setGravity(Gravity.TOP | Gravity.LEFT, coords[0],
						coords[1]);
			animate(ctx, view, R.anim.shake);
		}
		if(toast!=null)
			toast.show();
	}

	/**
	 * Allows you to perform an animation on a passed view.
	 * 
	 * @param context
	 * @param view
	 * @param animationId
	 */
	public static void animate(Context context, View view, int animationId) {
		Animation animation = AnimationUtils
				.loadAnimation(context, animationId);
		view.startAnimation(animation);
	}
}
