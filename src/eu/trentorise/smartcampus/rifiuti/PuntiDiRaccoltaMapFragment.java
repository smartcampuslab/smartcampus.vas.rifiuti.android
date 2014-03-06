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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.MarkerOptions;

import eu.trentorise.smartcampus.rifiuti.data.RifiutiHelper;
import eu.trentorise.smartcampus.rifiuti.model.PuntoRaccolta;
import eu.trentorise.smartcampus.rifiuti.utils.ArgUtils;

public class PuntiDiRaccoltaMapFragment extends Fragment implements OnCameraChangeListener, MapObjectContainer {

	private GoogleMap mMap;
	private Collection<PuntoRaccolta> mPuntiRaccolta;
	private SupportMapFragment mMapFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			mPuntiRaccolta = RifiutiHelper.getPuntiRaccolta();
			for (Iterator<PuntoRaccolta> iterator = mPuntiRaccolta.iterator(); iterator.hasNext();) {
				PuntoRaccolta point = iterator.next();
				if (point.getLocalizzazione() == null || point.getLocalizzazione().trim().length() == 0) {
					iterator.remove();
				}
			}
		} catch (Exception e) {
			Log.e(getClass().getName(), "Error reading punti di raccolta: " + e.getMessage());
			mPuntiRaccolta = new ArrayList<PuntoRaccolta>();
		}
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.map_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_list:
			PuntiDiRaccoltaListFragment rf = new PuntiDiRaccoltaListFragment();
			getFragmentManager().beginTransaction().replace(R.id.content_frame, rf).commit();
			return true;
		default:
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		FragmentManager fm = getChildFragmentManager();
		mMapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map_container);
		if (mMapFragment == null) {
			mMapFragment = SupportMapFragment.newInstance();
			fm.beginTransaction().replace(R.id.map_container, mMapFragment).commit();
		}
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_map_container, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();
		initView();
	}

	public void onResume() {
		super.onResume();
		if (getSupportMap() != null) {
			getSupportMap().setMyLocationEnabled(true);
			getSupportMap().setOnCameraChangeListener(this);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (getSupportMap() != null) {
			getSupportMap().setMyLocationEnabled(false);
			getSupportMap().setOnCameraChangeListener(null);
			getSupportMap().setOnMarkerClickListener(null);
		}
	}

	@SuppressWarnings("unchecked")
	protected void initView() {
		if (getSupportMap() != null) {
			getSupportMap().clear();
			getSupportMap().getUiSettings().setRotateGesturesEnabled(false);
			getSupportMap().getUiSettings().setTiltGesturesEnabled(false);
		}

		List<PuntoRaccolta> puntiRaccolta = (List<PuntoRaccolta>) mPuntiRaccolta;
		if (puntiRaccolta != null) {
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
		render(mPuntiRaccolta);
	}

	@Override
	public void addObjects(Collection<PuntoRaccolta> objects) {
		if (getSupportMap() != null) {
			this.mPuntiRaccolta = objects;
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
			mMap = mMapFragment.getMap();
			if (mMap != null)
				mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MapManager.DEFAULT_POINT, MapManager.ZOOM_DEFAULT));

		}
		return mMap;
	}
}
