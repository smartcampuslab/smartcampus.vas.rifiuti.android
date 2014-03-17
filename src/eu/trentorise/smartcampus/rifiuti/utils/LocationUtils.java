package eu.trentorise.smartcampus.rifiuti.utils;

import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import eu.trentorise.smartcampus.rifiuti.model.PuntoRaccolta;


public class LocationUtils {
	public enum ErrorType {
		DISABLED, ENABLED, TIME_FINISHED, NOT_ENOUGH_ACCURACY
	}

	public enum LocationType {
		NETWORK, GPS
	}

	public interface ILocation {
		public void onLocationChaged(Location l);

		public void onErrorOccured(ErrorType ex, String provider);

		public void onStatusChanged(String provider, boolean isActive);
	}

	private LocationManager mLocationManager;
	private LocationListener mLocationListener;
	private ILocation mLocationInterface;
	private LocationType mType;
	private long mTimeInterval = 1000;
	private float mMinDistance;
	private Timer mTimer;

	public LocationUtils(Context ctx, ILocation ilocation, LocationType type) {
		this.mLocationInterface = ilocation;
		this.mType = type;
		initializeLocationService(ctx);
	}

	public LocationUtils(Context ctx, ILocation ilocation) {
		this.mLocationInterface = ilocation;
		this.mType = LocationType.NETWORK;
		initializeLocationService(ctx);
	}

	public LocationUtils(Context ctx, ILocation ilocation, LocationType type,
			long timeInterval, float minDistance) {
		super();
		this.mLocationInterface = ilocation;
		this.mType = type;
		this.mTimeInterval = timeInterval;
		this.mMinDistance = minDistance;
		mLocationInterface = ilocation;
		initializeLocationService(ctx);
	}

	public long getPollingTimeInterval() {
		return mTimeInterval;
	}

	public LocationType getLocationType() {
		return mType;
	}

	/**
	 * Replace the interval between gps's calls This number has an huge impact
	 * on battery life.
	 * 
	 * @param mTimeInterval
	 */
	public void setPollingTimeInterval(long mTimeInterval) {
		this.mTimeInterval = mTimeInterval;
		updateLocationUpdates();
	}

	public float getMinDistance() {
		return mMinDistance;
	}

	public static Location getLastKnownLocation(Context ctx) {
		LocationManager lm = (LocationManager) ctx.getApplicationContext()
				.getSystemService(Context.LOCATION_SERVICE);
		return lm.getLastKnownLocation("network");
	}

	/**
	 * Replace the minimum valid distance to look for this number should be
	 * between (inclusive) 10 and 100
	 * 
	 * @param minDistance
	 * @return if the operation is valid
	 */
	public boolean setMinDistance(float minDistance) {
		this.mMinDistance = minDistance;
		if (mMinDistance >= 10 && mMinDistance <= 100) {
			updateLocationUpdates();
			return true;
		}
		return false;
	}

	private void updateLocationUpdates() {
		mLocationManager.removeUpdates(mLocationListener);
		activateUpdates();
	}

	private void initializeLocationService(Context ctx) {
		mLocationManager = (LocationManager) ctx.getApplicationContext()
				.getSystemService(Context.LOCATION_SERVICE);
		mLocationListener = new MyLocationListener();
		activateUpdates();
	}

	public void setTimer(int seconds) {
		mTimer = new Timer();
		mTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (mLocationListener != null)
					mLocationManager.removeUpdates(mLocationListener);
				mLocationInterface.onErrorOccured(ErrorType.TIME_FINISHED,
						"null");
			}
		}, mTimeInterval * seconds);
	}

	private void activateUpdates() {
		if (mType == LocationType.NETWORK) {
			mMinDistance = 50;
			mLocationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, mTimeInterval,
					mMinDistance, mLocationListener);
		} else {
			mMinDistance = 15;
			mLocationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, mTimeInterval, mMinDistance,
					mLocationListener);
		}
	}

	public void close() {
		if (mLocationListener != null) {
			mLocationManager.removeUpdates(mLocationListener);
			mLocationListener = null;
			if (mTimer != null)
				mTimer.cancel();
		}
	}

	private class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			if (location.getAccuracy() <= mMinDistance) {
				mLocationInterface.onLocationChaged(location);
			} else {
				mLocationInterface.onErrorOccured(
						ErrorType.NOT_ENOUGH_ACCURACY, "accuracy");
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
			mLocationInterface.onErrorOccured(ErrorType.DISABLED, provider);
		}

		@Override
		public void onProviderEnabled(String provider) {
			mLocationInterface.onErrorOccured(ErrorType.ENABLED, provider);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			mLocationInterface.onStatusChanged(provider,
					status == LocationProvider.AVAILABLE);
		}

	}

	public static class PRDistanceComparator implements Comparator<PuntoRaccolta> {
		private Location myLocation = null;

		/**
		 * @param myLocation
		 */
		public PRDistanceComparator(Location myLocation) {
			super();
			this.myLocation = myLocation;
		}

		@Override
		public int compare(PuntoRaccolta lhs, PuntoRaccolta rhs) {
			if (myLocation == null) return lhs.getIndirizzo().compareTo(rhs.getIndirizzo());
			Location p1Location = new Location("");
			p1Location.setLatitude(lhs.location()[0]);
			p1Location.setLongitude(lhs.location()[1]);
			float p1distance = myLocation.distanceTo(p1Location);

			Location p2Location = new Location("");
			p2Location.setLatitude(rhs.location()[0]);
			p2Location.setLongitude(rhs.location()[1]);
			float p2distance = myLocation.distanceTo(p2Location);

			return ((Float) p1distance).compareTo((Float) p2distance);
		}
	}
}