package eu.trentorise.smartcampus.rifiuti;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class AddNoteFragment extends DialogFragment{

	public interface OnAddListener {
		public void onAdd(String s);
	}

	private static final int REQ_CODE = 0;
	private static final String PASSED_STRING="passed_string";


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().setTitle(getString(R.string.note_add));
		final View view = inflater.inflate(R.layout.fragment_add_note,
				container, false);
		
		if(getArguments()!=null && getArguments().containsKey(PASSED_STRING) )
			((EditText)view.findViewById(R.id.fragment_add_et)).setText(getArguments().getString(PASSED_STRING));
		
		Button btn = (Button) view.findViewById(R.id.fragment_add_ok_btn);
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				((OnAddListener) getTargetFragment()).onAdd(((EditText) view
						.findViewById(R.id.fragment_add_et)).getText()
						.toString());
				getDialog().dismiss();
			}
		});
		return view;
	}


	public static AddNoteFragment newInstance(OnAddListener listener,String old) {
		AddNoteFragment out = new AddNoteFragment();
		out.setTargetFragment(((Fragment) listener), REQ_CODE);
		if(old!=null){
			Bundle b = new Bundle();
			b.putString(PASSED_STRING, old);
			out.setArguments(b);
		}
		
		return out;
	}

}
