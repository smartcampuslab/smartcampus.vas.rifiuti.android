package eu.trentorise.smartcampus.rifiuti;

import java.util.Date;

import eu.trentorise.smartcampus.rifiuti.model.Note;
import eu.trentorise.smartcampus.rifiuti.utils.PreferenceUtils;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class AddNoteFragment extends DialogFragment implements
		View.OnClickListener {

	public interface OnAddListener {
		public void onAdd(String s);
		public void onEdit(Note n);
	}

	private static final int REQ_CODE = 0;
	private static final String PASSED_NOTE="passed_note";

	private boolean isEdit = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().setTitle(getString(R.string.note_add));
		final View view = inflater.inflate(R.layout.fragment_add_note,
				container, false);

		if (getArguments() != null && getArguments().containsKey(PASSED_NOTE)) {
			Note n = (Note) getArguments().get(PASSED_NOTE);
			((EditText) view.findViewById(R.id.fragment_add_et))
					.setText(n.getText());
			isEdit = true;
		}

		Button btn = (Button) view.findViewById(R.id.fragment_add_ok_btn);
		btn.setOnClickListener(this);
		return view;
	}

	public static AddNoteFragment newInstance(OnAddListener listener,Note n) {
		AddNoteFragment out = new AddNoteFragment();
		out.setTargetFragment(((Fragment) listener), REQ_CODE);
		if (n != null) {
			Bundle b = new Bundle();
			b.putSerializable(PASSED_NOTE, n);
			out.setArguments(b);
		}
		return out;
	}

	@Override
	public void onClick(View v) {
		String text = ((EditText) getView()
				.findViewById(R.id.fragment_add_et)).getText().toString();
		if (isEdit) {
			Note n = (Note) getArguments().get(PASSED_NOTE);
			n.setText(text);
			((OnAddListener) getTargetFragment()).onEdit(n);
		} else {
			((OnAddListener) getTargetFragment()).onAdd(text);
		}
		getDialog().dismiss();
	}
}
