package eu.trentorise.smartcampus.rifiuti;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import eu.trentorise.smartcampus.rifiuti.utils.ArgUtils;

public class ContactsFragment extends Fragment {

	private ActionBarActivity abActivity;
	private Map<String,String> data;

	public static ContactsFragment newInstance(HashMap<String, String> data) {
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
		abActivity = (ActionBarActivity) getActivity();
		abActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		abActivity.getSupportActionBar().setHomeButtonEnabled(true);
		
		Bundle bundle = getArguments();
		data = (Map<String,String>) bundle.get(ArgUtils.ARGUMENT_CONTACTS);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_contacts, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();
		TextView nameTv = (TextView)getView().findViewById(R.id.contacts_name);
		nameTv.setText(data.get("name"));
		
		prepareField("description", R.id.contacts_descr_layout, R.id.contacts_descr);
		prepareField("address", R.id.contacts_address_layout, R.id.contacts_address);
		prepareField("web", R.id.contacts_web_layout, R.id.contacts_web);
		prepareField("email", R.id.contacts_email_layout, R.id.contacts_email);
		prepareField("phone", R.id.contacts_tel_layout, R.id.contacts_tel);
		prepareField("pec", R.id.contacts_pec_layout, R.id.contacts_pec);
		prepareField("fax", R.id.contacts_fax_layout, R.id.contacts_fax);

		
		final String mail = data.get("email");
		ImageView emailImg = (ImageView) getView().findViewById(R.id.contacts_email_img);
		emailImg.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_SENDTO, 
						Uri.fromParts("mailto",mail, null));
				getActivity().startActivity(Intent.createChooser(intent, getString(R.string.feedback_mail)));
			}
		});

		final String pec = data.get("pec");
		ImageView pecImg = (ImageView) getView().findViewById(R.id.contacts_pec_img);
		pecImg.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_SENDTO, 
						Uri.fromParts("mailto",pec, null));
				startActivity(Intent.createChooser(intent, getString(R.string.feedback_mail)));
			}
		});

		final String phone = data.get("phone");
		ImageView telImg = (ImageView) getView().findViewById(R.id.contacts_tel_img);
		telImg.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String p = phone.replace(" ", "");
				p = p.replace("/", "");
				p = p.replace(".", "");
				Intent callIntent = new Intent(Intent.ACTION_DIAL);
		        callIntent.setData(Uri.parse("tel:"+p));
		        startActivity(callIntent);
		    }
		});
		
	}

	/**
	 * 
	 */
	public void prepareField(String key, int idLayout, int idTv) {
		String descr = data.get(key);
		if (descr != null && descr.trim().length() > 0) {
			getView().findViewById(idLayout).setVisibility(View.VISIBLE);
			TextView descrTv = (TextView) getView().findViewById(idTv);
			descrTv.setText(descr);
		} else {
			getView().findViewById(idLayout).setVisibility(View.GONE);
		}
	}

}
