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

import java.util.Collection;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import eu.trentorise.smartcampus.rifiuti.model.PuntoRaccolta;
import eu.trentorise.smartcampus.rifiuti.utils.ArgUtils;

public class MapFragment extends Fragment implements OnCameraChangeListener, MapObjectContainer{

	static final LatLng HAMBURG = new LatLng(53.558, 9.927);
	static final LatLng KIEL = new LatLng(53.551, 9.993);
	private GoogleMap mMap;
	private Collection<PuntoRaccolta> puntiRaccolta;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_map, null, false);

		// SupportMapFragment mapFrag= ((SupportMapFragment)
		// getFragmentManager().findFragmentById(R.id.map));
		// map =mapFrag.getMap();
		//
		// Marker hamburg = map.addMarker(new MarkerOptions().position(HAMBURG)
		// .title("Hamburg"));
		// Marker kiel = map.addMarker(new MarkerOptions()
		// .position(KIEL)
		// .title("Kiel")
		// .snippet("Kiel is cool")
		// .icon(BitmapDescriptorFactory
		// .fromResource(R.drawable.ic_launcher)));
		//
		// // Move the camera instantly to hamburg with a zoom of 15.
		// map.moveCamera(CameraUpdateFactory.newLatLngZoom(HAMBURG, 15));
		//
		// // Zoom in, animating the camera.
		// map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
		//
		// //...

		return v;
	}

	@Override
	public void onStart() {
		super.onStart();
		initView();
	}
	@SuppressWarnings("unchecked")
	protected void initView() {
		if (getSupportMap() != null) {
			getSupportMap().clear();
			getSupportMap().getUiSettings().setRotateGesturesEnabled(false);
			getSupportMap().getUiSettings().setTiltGesturesEnabled(false);
		}
		if (getArguments() != null && getArguments().containsKey(ArgUtils.ARGUMENT_LISTA_PUNTO_DI_RACCOLTA)) {
			//get punto o punti di raccolta
			List<PuntoRaccolta> puntiRaccolta = (List<PuntoRaccolta>) getArguments().getSerializable(ArgUtils.ARGUMENT_LISTA_PUNTO_DI_RACCOLTA);
			new AsyncTask<List<PuntoRaccolta>, Void, List<PuntoRaccolta>>() {
				@Override
				protected List<PuntoRaccolta> doInBackground(List<PuntoRaccolta>... params) {
					return params[0];
				}

				@Override
				protected void onPostExecute(List<PuntoRaccolta> result) {
					addObjects(result);
				}
			}.execute(puntiRaccolta);
		}
	}
	
	
	
	@Override
	public void onCameraChange(CameraPosition position) {
		render(puntiRaccolta);
	}

	@Override
	public void addObjects(Collection<PuntoRaccolta> objects) {
		if (getSupportMap() != null) {
			this.puntiRaccolta = objects;
			render(objects);
			MapManager.fitMapWithOverlays(objects, getSupportMap());
		}
	}

	private void render(Collection<PuntoRaccolta> objects) {
		if (getSupportMap() != null) {
			getSupportMap().clear();
			if (objects != null && getActivity() != null) {
				List<MarkerOptions> cluster = MapManager.ClusteringHelper.cluster(
						getActivity().getApplicationContext(), getSupportMap(), objects);
				MapManager.ClusteringHelper.render(getSupportMap(), cluster);
			}
		}

	}
	private GoogleMap getSupportMap() {
		if (mMap == null) {
			if (getFragmentManager().findFragmentById(R.id.map) != null
					&& getFragmentManager().findFragmentById(R.id.map) instanceof SupportMapFragment)
				mMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			if (mMap != null)
				mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MapManager.DEFAULT_POINT, MapManager.ZOOM_DEFAULT));

		}
		return mMap;
	}
	
	public void onDestroyView() {
		   super.onDestroyView(); 
		   Fragment fragment = (getFragmentManager().findFragmentById(R.id.map));   
		   FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
		   ft.remove(fragment);
		   ft.commit();
		}


}
