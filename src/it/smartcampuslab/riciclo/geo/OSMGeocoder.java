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
package it.smartcampuslab.riciclo.geo;

import java.io.IOException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import eu.trentorise.smartcampus.network.RemoteConnector;
import eu.trentorise.smartcampus.network.RemoteException;

/**
 * Geocoder based on the SC geocoder Web service.
 * Performs the operations of direct/reverse geocoding.
 * @author raman
 *
 */
public class OSMGeocoder {

	/**
	 * 
	 */
	private static final double GEOCODE_DISTANCE = 0.5d;
	private Context mContext;
	private Locale mLocale = Locale.getDefault();

	private static final String LOC_PATH = "/core.geocoder/spring/location";
	private static final String ADDR_PATH = "/core.geocoder/spring/address";
	private String serverUrl;
	
	/**
	 * Constructor based on the specified server address
	 * @param context
	 * @param serverUrl
	 */
	public OSMGeocoder(Context context) {
		this(context, "https://vas.smartcampuslab.it", Locale.getDefault());
	}

	/**
	 * Constructor based on the specified server address and locale
	 * @param context
	 * @param serverUrl
	 * @param locale
	 */
	public OSMGeocoder(Context context, String serverUrl, Locale locale) {
		mContext = context;
		mLocale = locale;
		this.serverUrl = serverUrl;
	}

	private boolean isConnected() {
		NetworkInfo info = ((ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo();
		return info != null && info.isConnected();
	}



	private List<OSMAddress> queryLocations(String q, double[] referenceLocation, Double radius, String token) throws RemoteException, Exception, ProtocolException {
		
		List<OSMAddress> addrs = null;
		if (!isConnected())
			throw new Exception("No connection");

		try {
			StringBuilder sb = new StringBuilder();
			if (q != null) {
				sb.append(ADDR_PATH);
			} else {
				sb.append(LOC_PATH);
			}
			Map<String,Object> params = new HashMap<String, Object>();
			params.put("latlng", referenceLocation[0]+","+referenceLocation[1]);
			params.put("distance", radius);
			if (q != null) {
				params.put("address", q);
			}
			
			JSONObject jsonObject = execute(serverUrl, sb.toString(), params, token);

			addrs = jsonObject2addressList(jsonObject);
			return addrs;
		} catch (IOException e) {
			throw new ProtocolException(e.getMessage());
		} catch (JSONException e) {
			throw new ProtocolException(e.getMessage());
		}

	}

	/**
	 * Reverse geocoding operation. Cannot be called from UI thread.
	 * @param lat
	 * @param lng
	 * @param token access token
	 * @return list of {@link OSMAddress} instances around the 
	 * specified point ordered by the increasing distance to the point
	 */
	public List<OSMAddress> getFromLocation(double lat, double lng, String token) throws ProtocolException, RemoteException, Exception {
		if (!isConnected())
			throw new Exception("No connection");

		return queryLocations(null, new double[]{lat,lng}, GEOCODE_DISTANCE, token);
	}

	private List<OSMAddress> jsonObject2addressList(JSONObject in) throws IOException, JSONException {
		List<OSMAddress> addrs = new ArrayList<OSMAddress>();

		JSONObject jsonObject = in.getJSONObject("response");
		
		JSONArray results = jsonObject.getJSONArray("docs");
		if (results == null || results.length() < 1) {
			return addrs;
		}

		for (int i = 0; i < results.length(); i++) {
			JSONObject a = results.getJSONObject(i);
			OSMAddress address = new OSMAddress(mLocale, a);
			addrs.add(address);
		}

		return addrs;
	}

	private JSONObject execute(String server, String query, Map<String,Object> params, String token) throws SecurityException, RemoteException {
		String result = RemoteConnector.getJSON(server, query, token, params);
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject = new JSONObject(result);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;

	}
}
