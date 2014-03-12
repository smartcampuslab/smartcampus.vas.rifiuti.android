package eu.trentorise.smartcampus.rifiuti;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import eu.trentorise.smartcampus.rifiuti.utils.ArgUtils;

public class ContactsFragment extends Fragment {

	private ActionBarActivity abActivity;
	private ArrayList<HashMap<String, String>> data;

	public static ContactsFragment newInstance(ArrayList<HashMap<String, String>> data) {
		ContactsFragment rf = new ContactsFragment();
		Bundle b = new Bundle();
		b.putSerializable(ArgUtils.ARGUMENT_CONTACTS, data);
		rf.setArguments(b);
		return rf;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);

		Bundle bundle = getArguments();
		data = (ArrayList<HashMap<String, String>>) bundle.get(ArgUtils.ARGUMENT_CONTACTS);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		abActivity = (ActionBarActivity) getActivity();
		abActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		abActivity.getSupportActionBar().setHomeButtonEnabled(true);
		
		TextView nameTv = (TextView) getView().findViewById(R.id.contacts_name);
		nameTv.setText(data.get(0).get("name"));
		
		if (data.size() > 1) {
			ExpandableListView lv = (ExpandableListView) getView().findViewById(R.id.listView);
			lv.setVisibility(View.VISIBLE);
			SparseArray<Map<String,String>> array = new SparseArray<Map<String,String>>();
			for (int i = 0; i < data.size(); i++) {
				array.put(i, data.get(i));
			}
			OfficeListAdapter adapter = new OfficeListAdapter(getActivity(), array);
			lv.setAdapter(adapter);
		} else if (data.size() == 1) {
//			View child = getActivity().getLayoutInflater().inflate(R.layout.contacts_details, (ViewGroup)getView());
			createOfficeView(getView().findViewById(R.id.contacts_details), data.get(0));
//			((ViewGroup)getView()).addView(child);
		}
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (data.size() > 1) {
			return inflater.inflate(R.layout.fragment_contacts_el, container, false);
		} else {
			return inflater.inflate(R.layout.fragment_contacts, container, false);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
//		TextView nameTv = (TextView) getView().findViewById(R.id.contacts_name);
//		nameTv.setText(data.get("name"));
//
//		prepareField("description", R.id.contacts_descr_layout, R.id.contacts_descr, null);
//		prepareField("address", R.id.contacts_address_layout, R.id.contacts_address, null);
//		prepareField("opening", R.id.contacts_opening_layout, R.id.contacts_opening, null);
//		prepareField("web", R.id.contacts_web, R.id.contacts_web, R.string.contacts_web_ph);
//		prepareField("email", R.id.contacts_email, R.id.contacts_email, R.string.contacts_email_ph);
//		prepareField("phone", R.id.contacts_tel, R.id.contacts_tel, R.string.contacts_tel_ph);
//		prepareField("pec", R.id.contacts_pec, R.id.contacts_pec, R.string.contacts_pec_ph);
//		prepareField("fax", R.id.contacts_fax, R.id.contacts_fax, R.string.contacts_fax_ph);
//
//		final String mail = data.get("email");
//		ImageView emailImg = (ImageView) getView().findViewById(
//				R.id.contacts_email_img);
//		emailImg.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
//						"mailto", mail, null));
//				getActivity().startActivity(
//						Intent.createChooser(intent,
//								getString(R.string.feedback_mail)));
//			}
//		});
//
//		final String pec = data.get("pec");
//		ImageView pecImg = (ImageView) getView().findViewById(
//				R.id.contacts_pec_img);
//		pecImg.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
//						"mailto", pec, null));
//				startActivity(Intent.createChooser(intent,
//						getString(R.string.feedback_mail)));
//			}
//		});
//
//		final String phone = data.get("phone");
//		ImageView telImg = (ImageView) getView().findViewById(
//				R.id.contacts_tel_img);
//		telImg.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				String p = phone.replace(" ", "");
//				p = p.replace("/", "");
//				p = p.replace(".", "");
//				Intent callIntent = new Intent(Intent.ACTION_DIAL);
//				callIntent.setData(Uri.parse("tel:" + p));
//				startActivity(callIntent);
//			}
//		});
//
	}

	/**
	 * 
	 */
	public void prepareField(View view, String value, int idLayout, int idTv, Integer idPh) {
		String descr = value;
		if (descr != null && descr.trim().length() > 0) {
			view.findViewById(idLayout).setVisibility(View.VISIBLE);
			TextView descrTv = (TextView) view.findViewById(idTv);
			if (idPh != null) {
				descrTv.setText(getString(idPh, descr));
			} else { 
				descrTv.setText(descr);
			}
		} else {
			view.findViewById(idLayout).setVisibility(View.GONE);
		}
	}

	/**
	 * @param view
	 * @param data
	 */
	public void createOfficeView(View view, Map<String, String> data) {
		prepareField(view, data.get("description"), R.id.contacts_descr, R.id.contacts_descr, null);
		prepareField(view, data.get("address"), R.id.contacts_address_layout, R.id.contacts_address, null);
		prepareField(view, data.get("opening"), R.id.contacts_opening_layout, R.id.contacts_opening, null);
		prepareField(view, data.get("web"), R.id.contacts_web, R.id.contacts_web, R.string.contacts_web_ph);
		prepareField(view, data.get("email"), R.id.contacts_email_container, R.id.contacts_email, R.string.contacts_email_ph);
		prepareField(view, data.get("phone"), R.id.contacts_tel_container, R.id.contacts_tel, R.string.contacts_tel_ph);
		prepareField(view, data.get("pec"), R.id.contacts_pec_container, R.id.contacts_pec, R.string.contacts_pec_ph);
		prepareField(view, data.get("fax"), R.id.contacts_fax, R.id.contacts_fax, R.string.contacts_fax_ph);

		final String mail = data.get("email");
		ImageView emailImg = (ImageView) view.findViewById(
				R.id.contacts_email_img);
		emailImg.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
						"mailto", mail, null));
				getActivity().startActivity(
						Intent.createChooser(intent,
								getString(R.string.feedback_mail)));
			}
		});

		final String pec = data.get("pec");
		ImageView pecImg = (ImageView) view.findViewById(
				R.id.contacts_pec_img);
		pecImg.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
						"mailto", pec, null));
				startActivity(Intent.createChooser(intent,
						getString(R.string.feedback_mail)));
			}
		});

		final String phone = data.get("phone");
		ImageView telImg = (ImageView) view.findViewById(
				R.id.contacts_tel_img);
		telImg.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String p = phone.replace(" ", "");
				p = p.replace("/", "");
				p = p.replace(".", "");
				Intent callIntent = new Intent(Intent.ACTION_DIAL);
				callIntent.setData(Uri.parse("tel:" + p));
				startActivity(callIntent);
			}
		});
	}

	private class OfficeListAdapter extends BaseExpandableListAdapter {

		private SparseArray<Map<String,String>> offices;
		public LayoutInflater inflater;
		
		public OfficeListAdapter(Activity act, SparseArray<Map<String,String>> offices) {
		    this.offices = offices;
		    inflater = act.getLayoutInflater();
		  }
		
		@Override
		public Map<String,String> getChild(int groupPosition, int childPosition) {
		    return offices.get(groupPosition);
		}
		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return 0;
		}
		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.contacts_details, null);
		    }
			Map<String,String> data = getChild(groupPosition, childPosition);
			createOfficeView(convertView, data);
//			convertView.findViewById(R.id.contacts_descr_label).setVisibility(View.GONE);
			return convertView;
		}
		@Override
		public int getChildrenCount(int groupPosition) {
			return 1;
		}
		@Override
		public Map<String, String> getGroup(int groupPosition) {
			return offices.get(groupPosition);
		}
		@Override
		public int getGroupCount() {
			return offices.size();
		}
		@Override
		public long getGroupId(int groupPosition) {
			return 0;
		}
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			if (convertView == null) {
		      convertView = inflater.inflate(R.layout.contacts_group, null);
		    }
			CheckedTextView tv = (CheckedTextView) convertView.findViewById(R.id.title_tv);
		    Map<String, String> data = getGroup(groupPosition);
		    tv.setText(data.get("office"));
		    tv.setChecked(isExpanded);
		    return convertView;
		}
		@Override
		public boolean hasStableIds() {
			return false;
		}
		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return false;
		}
		
	}
}
