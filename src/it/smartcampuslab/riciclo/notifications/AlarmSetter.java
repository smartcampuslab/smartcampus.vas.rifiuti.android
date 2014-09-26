package it.smartcampuslab.riciclo.notifications;

import java.util.Calendar;
import java.util.Locale;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmSetter extends BroadcastReceiver {

	private static String TAG = "ALARM SETTER";

	private static String ACTION = "it.smartcampuslab.rifiuti.ACTION_ALARM";
	private static int defaultHour = 15;
	private static int defaultMinutes = 0;

	private static AlarmManager alarmManager;
	private static PendingIntent alarmPendingIntent;

	@Override
	public void onReceive(Context context, Intent intent) {
		// String mountState = Environment.getExternalStorageState();
		// if (!mountState.equals(Environment.MEDIA_MOUNTED)) {}

		setAlarmForNotificationsService(context);
	}

	public static void setAlarmForNotificationsService(Context context) {
		alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		Intent serviceIntent = new Intent(context, NotificationsService.class);
		serviceIntent.setAction(ACTION);

		alarmPendingIntent = PendingIntent.getService(context, 0, serviceIntent, PendingIntent.FLAG_NO_CREATE);

		if (alarmPendingIntent == null) {
			alarmPendingIntent = PendingIntent.getService(context, 0, serviceIntent, PendingIntent.FLAG_CANCEL_CURRENT);

			Calendar notificationsTimeCal = Calendar.getInstance(Locale.getDefault());
			if (notificationsTimeCal.get(Calendar.HOUR_OF_DAY) > defaultHour) {
				notificationsTimeCal.add(Calendar.DAY_OF_MONTH, 1);
			}
			notificationsTimeCal.set(Calendar.HOUR_OF_DAY, defaultHour);
			notificationsTimeCal.set(Calendar.MINUTE, defaultMinutes);

			// Only for testing, comment calendar adjustment above and use this
			// to set alarm 10 seconds after boot
			// notificationsTimeCal.add(Calendar.SECOND, 10);
			Log.d(TAG, notificationsTimeCal.toString());

			alarmManager.cancel(alarmPendingIntent);
			alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, notificationsTimeCal.getTimeInMillis(),
					AlarmManager.INTERVAL_DAY, alarmPendingIntent);
		}
	}

	public static void cancelAlarmForNotificationsService() {
		if (alarmManager != null && alarmPendingIntent != null) {
			alarmManager.cancel(alarmPendingIntent);
			Log.d(TAG, "Alarm canceled!");
		}
	}

}
