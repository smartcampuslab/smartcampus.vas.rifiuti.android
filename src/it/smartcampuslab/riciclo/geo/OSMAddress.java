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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Address;

/**
 * Address object built out of OSM address data.
 * 
 * @author raman
 *
 */
public class OSMAddress implements Serializable {
	private static final long serialVersionUID = -7767384643140415919L;

	private Locale locale;
	
	private String id;
	private String name;
	private double location[];
	private String osm_id;
	private String osm_key;
	private String osm_value;
	private String street;
	private String housenumber;
	private String postcode;
	private String[] places;
	private Map<String, String> city;
	private Map<String, String> country;
	
	
	public OSMAddress() {
		super();
		this.locale = Locale.getDefault();
	}
	
	/**
	 * @param locale
	 */
	public OSMAddress(Locale locale) {
		super();
		this.locale = locale;
	}

	/**
	 * Create instance out of the JSON representation
	 * @param locale
	 * @param a {@link JSONObject} representation
	 * @throws JSONException 
	 */
	public OSMAddress(Locale locale, JSONObject a) throws JSONException {
		this.locale = locale;
		this.id = a.optString("id", null);
		this.name = a.optString("name", null);
		this.osm_id = a.optString("osm_id" , null);
		this.osm_key = a.optString("osm_key" , null);
		this.osm_value = a.optString("osm_value" , null);
		this.housenumber = a.optString("housenumber", null);
		this.postcode = a.optString("postcode", null);
		this.street = a.optString("street", null);
		if (a.has("places")) {
			this.places = a.getString("places").split(",");
			for (int i = 0; i < this.places.length; i++) {
				this.places[i] = this.places[i].trim();
			}
		}
		JSONArray names = a.names();
		this.city = new HashMap<String, String>();
		this.country = new HashMap<String, String>();
		String name = null, key = null;
		for (int i = 0; i < names.length(); i++) {
			name = names.getString(i);
			if (name.startsWith("city")) {
				key = name.indexOf('_') > 0 ? name.substring(name.indexOf('_')+1) : "";
				city.put(key, a.optString(name, null));
			}
			if (name.startsWith("country")) {
				key = name.indexOf('_') > 0 ? name.substring(name.indexOf('_')+1) : "";
				country.put(key, a.optString(name, null));
			}
		}
		String[] loc = a.getString("coordinate").split(",");
		this.location = new double[]{Double.parseDouble(loc[0].trim()), Double.parseDouble(loc[1].trim())};
	}
	public Locale getLocale() {
		return locale;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double[] getLocation() {
		return location;
	}
	public void setLocation(double[] location) {
		this.location = location;
	}
	public String getOsm_id() {
		return osm_id;
	}
	public void setOsm_id(String osm_id) {
		this.osm_id = osm_id;
	}
	public String getOsm_key() {
		return osm_key;
	}
	public void setOsm_key(String osm_key) {
		this.osm_key = osm_key;
	}
	public String getOsm_value() {
		return osm_value;
	}
	public void setOsm_value(String osm_value) {
		this.osm_value = osm_value;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getHousenumber() {
		return housenumber;
	}
	public void setHousenumber(String housenumber) {
		this.housenumber = housenumber;
	}
	public String getPostcode() {
		return postcode;
	}
	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}
	public String[] getPlaces() {
		return places;
	}
	public void setPlaces(String[] places) {
		this.places = places;
	}
	public Map<String, String> getCity() {
		return city;
	}
	public void setCity(Map<String, String> city) {
		this.city = city;
	}
	public Map<String, String> getCountry() {
		return country;
	}
	public void setCountry(Map<String, String> country) {
		this.country = country;
	}
	
	/**
	 * @return city name in the locale used
	 */
	public String city() {
		String res = city.get(locale.getLanguage());
		if (res == null) return city.get("");
		return res;
	}
	
	/**
	 * @return country name in the locale used
	 */
	public String country() {
		String res = country.get(locale.getLanguage());
		if (res == null) return country.get("");
		return res;
	}
	
	/**
	 * Convert the object to {@link Address} data structure
	 * @return converted {@link Address} instance with
	 * address line corresponding to the formatted address of the object
	 * with lat/lon, country name, and locality filled. 
	 */
	public Address toAddress() {
		Address a = new Address(locale);
		a.setAddressLine(0, formattedAddress());
		a.setCountryName(country());
		a.setLatitude(location[0]);
		a.setLongitude(location[1]);
		a.setLocality(city());
		return a;
	}

	/**
	 * @return formatted representation of the address containing the street, number, 
	 * city, postal code, and country
	 */
	public String formattedAddress() {
		StringBuilder sb = new StringBuilder();
		if (street != null) sb.append(street);
		if (housenumber != null) {
			if (sb.length() > 0) sb.append(", ");
			sb.append(housenumber);
		}
		if (city != null && !city.isEmpty()) {
			if (sb.length() > 0) sb.append(", ");
			sb.append(city());
		}
		if (postcode != null) {
			if (sb.length() > 0) sb.append(", ");
			sb.append(postcode);
		}
		if (sb.length() > 0) sb.append(", ");
		sb.append(country());
		String res = sb.toString();
		if (name != null && name.length() > 0 && !res.contains(name)) return name +", "+res;
		return res;
	}
	
}
