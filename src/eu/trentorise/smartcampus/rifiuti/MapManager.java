package eu.trentorise.smartcampus.rifiuti;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.location.Location;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import eu.trentorise.smartcampus.rifiuti.data.RifiutiHelper;
import eu.trentorise.smartcampus.rifiuti.model.PuntoRaccolta;

public class MapManager {
	private static final int MAX_ZOOM = 18;
	public static final int ZOOM_DEFAULT = 15;
	public static final LatLng DEFAULT_POINT = new LatLng(45.89096, 11.04014); // Rovereto
	public static final int MAX_VISIBLE_DISTANCE = 20;

	// private static Map<Marker,PuntoRaccolta> puntiDiRaccolta = new
	// HashMap<Marker, PuntoRaccolta>();
	// private static Map<MarkerOptions,PuntoRaccolta> markerPunti = new
	// HashMap<MarkerOptions,PuntoRaccolta>();

	/*
	 * CLUSTERING
	 */
	public static class ClusteringHelper {
		private static final String TAG = "MapManager.ClusteringHelper";

		private static final int DENSITY_X = 5;
		private static final int DENSITY_Y = 5;

		private static List<List<List<PuntoRaccolta>>> grid = new ArrayList<List<List<PuntoRaccolta>>>();
		private static SparseArray<int[]> item2group = new SparseArray<int[]>();

		public synchronized static <T extends PuntoRaccolta> List<MarkerOptions> cluster(Context mContext, GoogleMap map,
				Collection<T> objects) {
			item2group.clear();
			// puntiDiRaccolta.clear();
			// markerPunti.clear();
			// 2D array with some configurable, fixed density
			grid.clear();

			for (int i = 0; i <= DENSITY_X; i++) {
				ArrayList<List<PuntoRaccolta>> column = new ArrayList<List<PuntoRaccolta>>(DENSITY_Y + 1);
				for (int j = 0; j <= DENSITY_Y; j++) {
					column.add(new ArrayList<PuntoRaccolta>());
				}
				grid.add(column);
			}

			LatLng lu = map.getProjection().getVisibleRegion().farLeft;
			LatLng rd = map.getProjection().getVisibleRegion().nearRight;
			int step = (int) (Math.abs((lu.longitude * 1E6) - (rd.longitude * 1E6)) / DENSITY_X);

			// compute leftmost bound of the affected grid:
			// this is the bound of the leftmost grid cell that intersects
			// with the visible part
			int startX = (int) ((lu.longitude * 1E6) - ((lu.longitude * 1E6) % step));
			if (lu.longitude < 0) {
				startX -= step;
			}
			// compute bottom bound of the affected grid
			int startY = (int) ((rd.latitude * 1E6) - ((rd.latitude * 1E6) % step));
			if (lu.latitude < 0) {
				startY -= step;
			}
			int endX = startX + (DENSITY_X + 1) * step;
			int endY = startY + (DENSITY_Y + 1) * step;

			int idx = 0;
			try {
				for (PuntoRaccolta basicObject : objects) {
					LatLng objLatLng = getLatLngFromBasicObject(basicObject);

					if (objLatLng != null && (objLatLng.longitude * 1E6) >= startX && (objLatLng.longitude * 1E6) <= endX
							&& (objLatLng.latitude * 1E6) >= startY && (objLatLng.latitude * 1E6) <= endY) {
						int binX = (int) (Math.abs((objLatLng.longitude * 1E6) - startX) / step);
						int binY = (int) (Math.abs((objLatLng.latitude * 1E6) - startY) / step);

						item2group.put(idx, new int[] { binX, binY });
						// just push the reference
						grid.get(binX).get(binY).add(basicObject);
					}
					idx++;
				}
			} catch (ConcurrentModificationException ex) {
				Log.e(TAG, ex.toString());
			}

			if (maxZoom(map)) {
				for (int i = 0; i < grid.size(); i++) {
					for (int j = 0; j < grid.get(0).size(); j++) {
						List<PuntoRaccolta> curr = grid.get(i).get(j);
						if (curr.size() == 0)
							continue;

						if (i > 0) {
							if (checkDistanceAndMerge(i - 1, j, curr))
								continue;
						}
						if (j > 0) {
							if (checkDistanceAndMerge(i, j - 1, curr))
								continue;
						}
						if (i > 0 && j > 0) {
							if (checkDistanceAndMerge(i - 1, j - 1, curr))
								continue;
						}
					}
				}
			}

			// generate markers
			List<MarkerOptions> markers = new ArrayList<MarkerOptions>();

			for (int i = 0; i < grid.size(); i++) {
				for (int j = 0; j < grid.get(i).size(); j++) {
					List<PuntoRaccolta> markerList = grid.get(i).get(j);
					if (markerList.size() > 1) {
						markers.add(createGroupMarker(mContext, map, markerList, i, j));
					} else if (markerList.size() == 1) {
						// draw single marker
						markers.add(createSingleMarker(markerList.get(0), i, j));
					}
				}
			}

			return markers;
		}

		public static boolean maxZoom(GoogleMap map) {
			return map.getCameraPosition().zoom >= MAX_ZOOM;// map.getMaxZoomLevel();
		}

		private static LatLng getLatLngFromBasicObject(PuntoRaccolta object) {
			LatLng latLng = null;
			double[] coords = object.location();
			latLng = new LatLng(coords[0],coords[1]);
			return latLng;
		}

		public static void render(GoogleMap map, List<MarkerOptions> markers) {
			for (MarkerOptions mo : markers) {
				map.addMarker(mo);
				// Marker marker = map.addMarker(mo);
				// puntiDiRaccolta.put(marker, markerPunti.get(mo));
			}
		}

		private static MarkerOptions createSingleMarker(PuntoRaccolta item, int x, int y) {
			LatLng latLng = getLatLngFromBasicObject(item);
			int markerIcon = RifiutiHelper.getMarkerIcon(item);
			MarkerOptions marker = new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(markerIcon)).title(x + ":" + y);
			// markerPunti.put(marker, item);
			return marker;
		}

		private static MarkerOptions createGroupMarker(Context mContext, GoogleMap map, List<PuntoRaccolta> markerList, int x,
				int y) {
			PuntoRaccolta item = markerList.get(0);
			LatLng latLng = getLatLngFromBasicObject(item);

			int markerIcon = R.drawable.ic_marker_p_generic;

			BitmapDescriptor bd = BitmapDescriptorFactory.fromBitmap(writeOnMarker(mContext, markerIcon,
					Integer.toString(markerList.size())));
			MarkerOptions marker = new MarkerOptions().position(latLng).icon(bd).title(x + ":" + y);
			return marker;
		}

		private static Bitmap writeOnMarker(Context mContext, int drawableId, String text) {
			float scale = mContext.getResources().getDisplayMetrics().density;

			Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), drawableId).copy(Bitmap.Config.ARGB_8888,
					true);

			Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			paint.setTextAlign(Align.CENTER);
			paint.setTextSize(scale * 14);
			paint.setAntiAlias(true);
			paint.setARGB(255, 255, 255, 255);

			Canvas canvas = new Canvas(bitmap);
			Rect bounds = new Rect();
			paint.getTextBounds(text, 0, text.length(), bounds);
			float x = bitmap.getWidth() / 2;
			float y = bitmap.getHeight() / 2;
			canvas.drawText(text, x, y, paint);

			return bitmap;
		}

		public static List<PuntoRaccolta> getFromGridId(String id) {
			try {
				String[] parsed = id.split(":");
				int x = Integer.parseInt(parsed[0]);
				int y = Integer.parseInt(parsed[1]);

				return grid.get(x).get(y);
			} catch (Exception e) {
				return null;
			}
		}

		private static boolean checkDistanceAndMerge(int i, int j, List<PuntoRaccolta> curr) {
			List<PuntoRaccolta> src = grid.get(i).get(j);
			if (src.size() == 0) {
				return false;
			}

			LatLng srcLatLng = getLatLngFromBasicObject(src.get(0));
			LatLng currLatLng = getLatLngFromBasicObject(curr.get(0));

			if (srcLatLng != null && currLatLng != null) {
				float[] dist = new float[3];

				Location.distanceBetween(srcLatLng.latitude, srcLatLng.longitude, currLatLng.latitude, currLatLng.longitude,
						dist);

				if (dist[0] < MAX_VISIBLE_DISTANCE) {
					src.addAll(curr);
					curr.clear();
					return true;
				}
			}
			return false;
		}

	}

	public static void fitMapWithOverlays(Collection<PuntoRaccolta> objects, GoogleMap map) {
		double[] ll = null, rr = null;
		if (objects != null) {
			for (PuntoRaccolta o : objects) {
				double[] location = o.location();
				if (ll == null) {
					ll = location.clone();
					rr = ll.clone();
				} else {
					ll[0] = Math.min(ll[0], location[0]);
					ll[1] = Math.max(ll[1], location[1]);

					rr[0] = Math.max(rr[0], location[0]);
					rr[1] = Math.min(rr[1], location[1]);
				}
			}
		}
		fit(map, ll, rr, objects != null && objects.size() > 1);
	}

	// public static PuntoRaccolta getPuntoDiRaccoltaFromMarker(Marker marker){
	// return puntiDiRaccolta.get(marker);
	// }

	private static void fit(GoogleMap map, double[] ll, double[] rr, boolean zoomIn) {
		if (ll != null && rr != null) {
			float[] dist = new float[3];
			Location.distanceBetween(ll[0], ll[1], rr[0], rr[1], dist);
			if (dist[0] > MAX_VISIBLE_DISTANCE) {
				LatLngBounds bounds = LatLngBounds.builder().include(new LatLng(rr[0], rr[1]))
						.include(new LatLng(ll[0], ll[1])).build();
				map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 64));
			} else {
				map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(ll[0], ll[1]), MAX_ZOOM));
			}
		}
	}

	// public static GeoPoint requestMyLocation(Context ctx) {
	// return DTHelper.getLocationHelper().getLocation();
	// }
}
