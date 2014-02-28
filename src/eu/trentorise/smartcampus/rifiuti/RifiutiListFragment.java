package eu.trentorise.smartcampus.rifiuti;

import java.util.List;

import eu.trentorise.smartcampus.rifiuti.data.RifiutiHelper;
import eu.trentorise.smartcampus.rifiuti.utils.ArgUtils;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class RifiutiListFragment extends ListFragment {

	private String tipologiaRaccolta = null;
	private String tipologiaRifiuto = null;

	public static RifiutiListFragment newIstanceTipologiaRaccolta(String raccolta) {
		RifiutiListFragment rf = new RifiutiListFragment();
		Bundle b = new Bundle();
		b.putString(ArgUtils.ARGUMENT_TIPOLOGIA_RACCOLTA, raccolta);
		rf.setArguments(b);
		return rf;
	}

	public static RifiutiListFragment newIstanceTipologiaRifiuto(String rifiuto) {
		RifiutiListFragment rf = new RifiutiListFragment();
		Bundle b = new Bundle();
		b.putString(ArgUtils.ARGUMENT_TIPOLOGIA_RIFIUTO, rifiuto);
		rf.setArguments(b);
		return rf;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_rifiuti_list, container, false);
		return viewGroup;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Bundle bundle = getArguments();
		if (bundle.containsKey(ArgUtils.ARGUMENT_TIPOLOGIA_RACCOLTA))
			tipologiaRaccolta = bundle.getString(ArgUtils.ARGUMENT_TIPOLOGIA_RACCOLTA);
		if (bundle.containsKey(ArgUtils.ARGUMENT_TIPOLOGIA_RIFIUTO))
			tipologiaRifiuto = bundle.getString(ArgUtils.ARGUMENT_TIPOLOGIA_RIFIUTO);

		List<String> list = null;
		try {
			if (tipologiaRaccolta != null) {
				list=RifiutiHelper.getRifiutoPerTipoRaccolta(tipologiaRaccolta);
			} else 		if (tipologiaRifiuto != null) {
				list=RifiutiHelper.getRifiutoPerTipoRifiuti(tipologiaRifiuto);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_column, R.id.text,list);
		setListAdapter(adapter);
	}

	@Override
	public void onStart() {
		super.onStart();

	}
}
