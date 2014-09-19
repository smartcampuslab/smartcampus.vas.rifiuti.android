package eu.trentorise.smartcampus.rifiuti;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import eu.trentorise.smartcampus.rifiuti.utils.ArgUtils;
import eu.trentorise.smartcampus.rifiuti.utils.onBackListener;

public class ContactsFragment extends Fragment implements onBackListener {

	private ActionBarActivity abActivity;
	private ArrayList<HashMap<String, String>> data;
	private int mPosition;

	public static ContactsFragment newInstance(ArrayList<HashMap<String, String>> data, int position) {
		ContactsFragment rf = new ContactsFragment();
		Bundle b = new Bundle();
		b.putSerializable(ArgUtils.ARGUMENT_CONTACTS, data);
		b.putSerializable(ArgUtils.ARGUMENT_CONTACT_POSITION, position);
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
		mPosition = bundle.getInt(ArgUtils.ARGUMENT_CONTACT_POSITION);

		((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(data.get(mPosition).get("name"));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBack();
			return true;
		} else if (item.getItemId() == R.id.action_goto) {
			callAppForDirectionsGmaps(data.get(mPosition).get("address"));
		}
		return false;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.contact_detail_menu, menu);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		return inflater.inflate(R.layout.fragment_contacts, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		abActivity = (ActionBarActivity) getActivity();

		createOfficeView(getView().findViewById(R.id.contacts_details), data.get(mPosition));

	}

	@Override
	public void onStart() {
		super.onStart();
		if (abActivity != null && abActivity instanceof MainActivity) {
			((MainActivity) abActivity).hideDrawerIndicator();
			((MainActivity) abActivity).lockDrawer();
			abActivity.getSupportActionBar().setHomeButtonEnabled(true);
			abActivity.supportInvalidateOptionsMenu();
		}
	}

	@Override
	public void onBack() {
		getFragmentManager().popBackStack();
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
		prepareField(view, data.get("phone") + "\n" + getString(R.string.contacts_fax_ph, data.get("fax")),
				R.id.contacts_tel_container, R.id.contacts_tel, R.string.contacts_tel_ph);
		prepareField(view, data.get("pec"), R.id.contacts_pec_container, R.id.contacts_pec, R.string.contacts_pec_ph);

		final String web = data.get("web");
		View webImg = view.findViewById(R.id.contacts_web_container);
		webImg.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (web == null)
					return;
				Uri uri = web.startsWith("http") ? Uri.parse(web) : Uri.parse("http://" + web);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				getActivity().startActivity(intent);
			}
		});

		final String mail = data.get("email");
		View emailImg = view.findViewById(R.id.contacts_email_container);
		emailImg.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", mail, null));
				getActivity().startActivity(Intent.createChooser(intent, getString(R.string.feedback_mail)));
			}
		});

		final String pec = data.get("pec");
		View pecImg = view.findViewById(R.id.contacts_pec_container);
		pecImg.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", pec, null));
				startActivity(Intent.createChooser(intent, getString(R.string.feedback_mail)));
			}
		});

		final String phone = data.get("phone");
		View telImg = view.findViewById(R.id.contacts_tel_container);
		telImg.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				callPhoneIntent(phone);
			}

		});
	}

	private void callPhoneIntent(final String phone) {
		String p = phone.replace(" ", "");
		p = p.replace("/", "");
		p = p.replace(".", "");
		Intent callIntent = new Intent(Intent.ACTION_DIAL);
		callIntent.setData(Uri.parse("tel:" + p));
		startActivity(callIntent);
	}

	private void callAppForDirectionsGmaps(String addr) {

		String url = "http://maps.google.com/maps?daddr=" + addr;

		Intent navigation = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		getActivity().startActivity(navigation);
	}

}
