package eu.trentorise.smartcampus.rifiuti;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import eu.trentorise.smartcampus.rifiuti.data.RifiutiHelper;
import eu.trentorise.smartcampus.rifiuti.model.Calendario;
import eu.trentorise.smartcampus.rifiuti.model.DatiTipologiaRaccolta;
import eu.trentorise.smartcampus.rifiuti.model.PuntoRaccolta;
import eu.trentorise.smartcampus.rifiuti.utils.ArgUtils;

public class PuntoDiRaccoltaDetailFragment extends Fragment {

	private PuntoRaccolta puntoDiRaccolta=null;
	private List<Calendario> calendario = null;
	private List<DatiTipologiaRaccolta> tipologie = null;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_puntodiraccolta_details, container, false);
		return viewGroup;
	}
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Bundle bundle = getArguments();
		if (bundle.containsKey(ArgUtils.ARGUMENT_PUNTO_DI_RACCOLTA))
			puntoDiRaccolta = (PuntoRaccolta) bundle.getSerializable(ArgUtils.ARGUMENT_PUNTO_DI_RACCOLTA);
		try {
			//get lista orari per punto di raccolta
			calendario = RifiutiHelper.getCalendars(puntoDiRaccolta);
			//get lista tipologie per punto di raccolta
			tipologie = RifiutiHelper.readTipologiaRaccoltaPerTipologiaPuntoRaccolta(puntoDiRaccolta.getTipologiaPuntiRaccolta());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TextView mDettagli = (TextView) getActivity().findViewById(R.id.puntodiraccolta_dettagli);
		mDettagli.setText(puntoDiRaccolta.getIndirizzo());
		ImageView mappa = (ImageView) getActivity().findViewById(R.id.map_dettagli);
		mappa.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
				MapFragment fragment = new MapFragment();
				Bundle args = new Bundle();
				ArrayList<PuntoRaccolta> list = new ArrayList<PuntoRaccolta>();
				list.add(puntoDiRaccolta);
				args.putSerializable(ArgUtils.ARGUMENT_PUNTO_DI_RACCOLTA, list);
				fragment.setArguments(args);
				fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				fragmentTransaction.replace(R.id.content_frame, fragment, "map");
				fragmentTransaction.addToBackStack(fragment.getTag());
				fragmentTransaction.commit();
			}
		});
		ListView mOrari= (ListView) getActivity().findViewById(R.id.puntodiraccolta_listaorari);
		CalendarioAdapter calendarAdapter = new CalendarioAdapter(getActivity(), R.layout.calendario_adapter,calendario);
		mOrari.setAdapter(calendarAdapter);
				
				
		ListView mTipologieRaccolta= (ListView) getActivity().findViewById(R.id.puntodiraccolta_listatipologie);
		TipologieAdapter tipologieAdapter = new TipologieAdapter(getActivity(), R.layout.tipologiaraccolta_adapter,tipologie);
		mTipologieRaccolta.setAdapter(tipologieAdapter);		
		
		
//		listPuntiRaccolta.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//				FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
//				PuntoDiRaccoltaDetailFragment fragment = new PuntoDiRaccoltaDetailFragment();
//
//				Bundle args = new Bundle();
//				args.putParcelable(ArgUtils.ARGUMENT_PUNTO_DI_RACCOLTA, puntiDiRaccolta.get(arg2));
//				fragment.setArguments(args);
//				fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//				// fragmentTransaction.detach(this);
//				fragmentTransaction.replace(R.id.content_frame, fragment, "puntodiraccolta");
//				fragmentTransaction.addToBackStack(fragment.getTag());
//				fragmentTransaction.commit();
//			}
//		});
	}
}
