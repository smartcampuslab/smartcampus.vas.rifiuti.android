package it.smartcampuslab.riciclo;

import it.smartcampuslab.riciclo.model.PuntoRaccolta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

public class PuntoDiRaccoltaAdapter extends BaseExpandableListAdapter {
	private Activity activity;
	private int layoutItemId;
	private int layoutGroupId;
	private SparseArray<List<PuntoRaccolta>> data;

	public PuntoDiRaccoltaAdapter(Activity activity, int layoutGroupId, int layoutItemId, List<PuntoRaccolta> objects) {
		this.activity = activity;
		this.layoutGroupId = layoutGroupId;
		this.layoutItemId = layoutItemId;
		data = new SparseArray<List<PuntoRaccolta>>();
		Map<String,Integer> map = new HashMap<String, Integer>();
		Collections.sort(objects, new Comparator<PuntoRaccolta>() {
			public int compare(PuntoRaccolta lhs, PuntoRaccolta rhs) {
				return lhs.getTipologiaPuntiRaccolta().compareTo(rhs.getTipologiaPuntiRaccolta());
			}
		});
		for (PuntoRaccolta pr : objects) {
			Integer key = map.get(pr.getTipologiaPuntiRaccolta());
			if (key == null) {
				key = map.size();
				map.put(pr.getTipologiaPuntiRaccolta(), key);
				data.append(key, new ArrayList<PuntoRaccolta>());
			}
			data.get(key).add(pr);
		}
	}

	@Override
	public PuntoRaccolta getChild(int groupPosition, int childPosition) {
		return data.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		TextView row;
		if (convertView == null) {
			row = (TextView) activity.getLayoutInflater().inflate(layoutItemId, parent, false);
		} else {
			row = (TextView) convertView;
		}
		PuntoRaccolta pr = getChild(groupPosition, childPosition);
		row.setText(pr.dettaglio());
		return row;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return data.get(groupPosition).size();
	}

	@Override
	public List<PuntoRaccolta> getGroup(int groupPosition) {
		return data.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return data.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = activity.getLayoutInflater().inflate(layoutGroupId, null);
		}
		CheckedTextView tv = (CheckedTextView) convertView.findViewById(R.id.title_tv);
		tv.setText(data.get(groupPosition).get(0).getTipologiaPuntiRaccolta());
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
}
