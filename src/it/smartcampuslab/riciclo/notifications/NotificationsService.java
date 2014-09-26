package it.smartcampuslab.riciclo.notifications;

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
import it.smartcampuslab.riciclo.MainActivity;
import it.smartcampuslab.riciclo.R;
import it.smartcampuslab.riciclo.data.RifiutiHelper;
import it.smartcampuslab.riciclo.model.CalendarioItem;
import it.smartcampuslab.riciclo.model.Profile;
import it.smartcampuslab.riciclo.utils.ArgUtils;
import it.smartcampuslab.riciclo.utils.PreferenceUtils;

public class NotificationsService extends Service {

	private static final String TAG = "NotificationsService";
	private static final String PORTA_A_PORTA = "Porta a porta";
	private NotificationManager mNM;

	@Override
	public void onCreate() {
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		try {
			RifiutiHelper.init(getApplicationContext());
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		checkCalendar();
		return Service.START_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	/**
	 * Show a notification while this service is running.
	 */
	private void checkCalendar() {
		Calendar tomorrowCal = Calendar.getInstance(Locale.getDefault());

		// TODO: [developing] comment next line if tomorrow has no events
		tomorrowCal.add(Calendar.DAY_OF_MONTH, 1);

		List<Profile> profiles = PreferenceUtils.getProfiles(getApplicationContext());

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
				String contentTitle = getResources().getString(R.string.notifications_title, profile.getComune());
				String contentText = "";
				List<String> lines = new ArrayList<String>();

				List<CalendarioItem> tomorrowItems = calendarsForMonth.get(tomorrowCal.get(Calendar.DAY_OF_MONTH) - 1);
				for (CalendarioItem item : tomorrowItems) {
					if (item.getPoint().getTipologiaPuntiRaccolta().startsWith(PORTA_A_PORTA)) {
						lines.add(item.getPoint().getTipologiaPuntiRaccolta());
					}
				}

				if (lines.size() > 0) {
					NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
					mBuilder.setAutoCancel(true);
					mBuilder.setSmallIcon(R.drawable.ic_stat_notify_riciclo);
					mBuilder.setContentTitle(contentTitle);

					if (lines.size() == 1) {
						contentText = lines.get(0);
					} else {
						StringBuilder sb = new StringBuilder();
						for (int sc = 0; sc < lines.size(); sc++) {
							String s = lines.get(sc);
							if (sc == 0) {
								// first, complete
								sb.append(s);
							} else {
								// others, remove "Porta a porta"
								sb.append(",");
								sb.append(s.replace(PORTA_A_PORTA, ""));
							}
						}
						contentText = sb.toString();
					}
					mBuilder.setContentText(contentText);

					NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
					inboxStyle.setBigContentTitle(contentTitle);
					for (String s : lines) {
						inboxStyle.addLine(s);
					}
					mBuilder.setStyle(inboxStyle);

					Intent intent = new Intent(getApplicationContext(), MainActivity.class);
					intent.putExtra(ArgUtils.ARGUMENT_CALENDAR_TOMORROW, tomorrowCal);
					intent.putExtra(ArgUtils.ARGUMENT_PROFILE, profile);
					PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,
							PendingIntent.FLAG_UPDATE_CURRENT);
					mBuilder.setContentIntent(pendingIntent);

					Notification notification = mBuilder.build();
					notification.defaults |= Notification.DEFAULT_SOUND;
					notification.defaults |= Notification.DEFAULT_VIBRATE;

					mNM.notify(null, p, notification);
				}
			}
		}

		stopSelf();
	}
}
