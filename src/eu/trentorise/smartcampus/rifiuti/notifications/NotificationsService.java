package eu.trentorise.smartcampus.rifiuti.notifications;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import eu.trentorise.smartcampus.rifiuti.MainActivity;
import eu.trentorise.smartcampus.rifiuti.R;
import eu.trentorise.smartcampus.rifiuti.data.RifiutiHelper;
import eu.trentorise.smartcampus.rifiuti.model.CalendarioItem;
import eu.trentorise.smartcampus.rifiuti.model.Profile;
import eu.trentorise.smartcampus.rifiuti.utils.ArgUtils;
import eu.trentorise.smartcampus.rifiuti.utils.PreferenceUtils;

public class NotificationsService extends Service {

	private static final String TAG = "NotificationsService";
	private static final String PORTA_A_PORTA = "Porta a porta";

	private NotificationManager mNM;

	@Override
	public void onCreate() {
		try {
			RifiutiHelper.init(getApplicationContext());
		} catch (IOException e) {
			Log.e("NotificationsService", e.getMessage());
		}
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// Display a notification about us starting. We put an icon in the
		// status bar.
		showNotification();

		return Service.START_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		// TODO
		Toast.makeText(getApplicationContext(), "TODO: destroy service", Toast.LENGTH_SHORT).show();
	}

	/**
	 * Show a notification while this service is running.
	 */
	private void showNotification() {
		Calendar tomorrowCal = Calendar.getInstance(Locale.getDefault());

		// TODO: [developing] comment next line if tomorrow has no events
		tomorrowCal.add(Calendar.DAY_OF_MONTH, 1);

		List<Profile> profiles = PreferenceUtils.getProfiles(getApplicationContext());
		if (profiles.isEmpty()) {
			return;
		}

		for (int p = 0; p < profiles.size(); p++) {
			Profile profile = profiles.get(p);
			boolean profileHasPortaAporta = false;
			String aree = RifiutiHelper.getUserAreas(profile);
			List<List<CalendarioItem>> calendarsForMonth = RifiutiHelper.getCalendarsForMonth(tomorrowCal, profile, aree);
			for (int i = 0; i < calendarsForMonth.size(); i++) {
				List<CalendarioItem> items = calendarsForMonth.get(i);
				for (int j = 0; j < items.size(); j++) {
					CalendarioItem item = items.get(j);
					if (item.getPoint().getTipologiaPuntiRaccolta().startsWith(PORTA_A_PORTA)) {
						profileHasPortaAporta = true;
						break;
					}
				}
				if (profileHasPortaAporta) {
					break;
				}
			}

			if (profileHasPortaAporta) {
				String contentTitle = "100% Riciclo";
				String contentText = "Domani a " + profile.getArea() + "...";
				List<String> lines = new ArrayList<String>();

				List<CalendarioItem> tomorrowItems = calendarsForMonth.get(tomorrowCal.get(Calendar.DAY_OF_MONTH) - 1);
				for (CalendarioItem item : tomorrowItems) {
					if (item.getPoint().getTipologiaPuntiRaccolta().startsWith(PORTA_A_PORTA)) {
						lines.add(item.getPoint().getTipologiaPuntiRaccolta());
					}
				}

				if (lines.size() > 0) {
					// TODO
					String sb = "";
					for (String s : lines) {
						if (sb.length() > 0) {
							sb += ", ";
						}
						sb += s;
					}
					Log.e(TAG, sb.toString());

					NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setAutoCancel(true)
							.setSmallIcon(R.drawable.ic_stat_notify_riciclo).setContentTitle(contentTitle)
							.setContentText(contentText);

					NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
					inboxStyle.setBigContentTitle(contentTitle);
					for (int i = 0; i < lines.size(); i++) {
						inboxStyle.addLine(lines.get(i));
					}
					mBuilder.setStyle(inboxStyle);

					// Intent intent = new Intent(getApplicationContext(),
					// SplashScreenActivity.class);
					Intent intent = new Intent(getApplicationContext(), MainActivity.class);
					intent.putExtra(ArgUtils.ARGUMENT_CALENDAR_TOMORROW, tomorrowCal);
					intent.putExtra(ArgUtils.ARGUMENT_PROFILE, profile);
					PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,
							PendingIntent.FLAG_UPDATE_CURRENT);
					mBuilder.setContentIntent(pendingIntent);

					Notification notification = mBuilder.build();
					notification.defaults |= Notification.DEFAULT_SOUND;
					// notification.defaults |= Notification.DEFAULT_VIBRATE;

					mNM.notify(null, p, notification);
				}
			}
		}
	}
}
