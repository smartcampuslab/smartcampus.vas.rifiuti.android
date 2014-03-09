package eu.trentorise.smartcampus.rifiuti;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
		TextView descr = (TextView) getView().findViewById(R.id.contacts_descr);
		descr.setText(data.get("description"));
		TextView addr = (TextView) getView().findViewById(R.id.contacts_address);
		addr.setText(data.get("address"));
		TextView tel = (TextView) getView().findViewById(R.id.contacts_tel);
		tel.setText(data.get("phone"));
		TextView email = (TextView) getView().findViewById(R.id.contacts_email);
		email.setText(data.get("email"));
	}

}
