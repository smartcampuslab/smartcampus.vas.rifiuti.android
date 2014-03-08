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

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
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
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import eu.trentorise.smartcampus.rifiuti.data.RifiutiHelper;
import eu.trentorise.smartcampus.rifiuti.model.PuntoRaccolta;
import eu.trentorise.smartcampus.rifiuti.utils.ArgUtils;

public class MapFragment extends Fragment implements OnCameraChangeListener, MapObjectContainer {

	private GoogleMap mMap;
	private Collection<PuntoRaccolta> mPuntiRaccolta;
	private SupportMapFragment mMapFragment;
	private ActionBarActivity abActivity;

	private boolean showAsList() {
		return getArguments() == null || !getArguments().containsKey(ArgUtils.ARGUMENT_PUNTO_DI_RACCOLTA);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			// if has elements explicitly passed take them, otherwise use all
			if (getArguments() != null && getArguments().containsKey(ArgUtils.ARGUMENT_PUNTO_DI_RACCOLTA)) {
				mPuntiRaccolta = (List<PuntoRaccolta>)getArguments().getSerializable(ArgUtils.ARGUMENT_PUNTO_DI_RACCOLTA);
			} else {
				mPuntiRaccolta = RifiutiHelper.getPuntiRaccolta();
			}
			
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
		abActivity = (ActionBarActivity) getActivity();
		setHasOptionsMenu(true);

		if (abActivity != null) {
			abActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			abActivity.getSupportActionBar().setHomeButtonEnabled(true);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		if (showAsList()) {
			inflater.inflate(R.menu.map_menu, menu);
		}
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
			if (getArguments() != null && getArguments().containsKey(ArgUtils.ARGUMENT_LISTA_PUNTO_DI_RACCOLTA)) {
				abActivity.getSupportActionBar().setTitle(abActivity.getString(R.string.punto_di_raccolta_title));
			} else
				abActivity.getSupportActionBar().setTitle(abActivity.getString(R.string.punti_di_raccolta_title));
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
			setMarkerListener(getSupportMap());
		}
		List<PuntoRaccolta> puntiRaccolta = null;
		if (getArguments() != null && getArguments().containsKey(ArgUtils.ARGUMENT_LISTA_PUNTO_DI_RACCOLTA)) {
			// get punto o punti di raccolta
			puntiRaccolta = (List<PuntoRaccolta>) getArguments().getSerializable(
					ArgUtils.ARGUMENT_LISTA_PUNTO_DI_RACCOLTA);
		} else
			puntiRaccolta = (List<PuntoRaccolta>) mPuntiRaccolta;
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

	private void setMarkerListener(GoogleMap supportMap) {
		supportMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {
				List<PuntoRaccolta> pdr = MapManager.ClusteringHelper.getFromGridId(marker.getTitle());
				if (pdr.size() == 1) {
					Intent i = new Intent(getActivity(), PuntoRaccoltaActivity.class);
					i.putExtra(ArgUtils.ARGUMENT_PUNTO_DI_RACCOLTA, pdr.get(0));
					startActivity(i);
//					FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager()
//							.beginTransaction();
//					PuntoDiRaccoltaDetailFragment fragment = new PuntoDiRaccoltaDetailFragment();
//					Bundle args = new Bundle();
//					args.putSerializable(ArgUtils.ARGUMENT_PUNTO_DI_RACCOLTA, pdr.get(0));
//					fragment.setArguments(args);
//					fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//					// fragmentTransaction.detach(this);
//					fragmentTransaction.replace(R.id.content_frame, fragment, "puntodiraccolta");
//					fragmentTransaction.addToBackStack(fragment.getTag());
//					fragmentTransaction.commit();
				} else if (pdr.size() > 1) {
					// zoom
					MapManager.fitMapWithOverlays(pdr, getSupportMap());
				}
				return true;
			}
		});

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
