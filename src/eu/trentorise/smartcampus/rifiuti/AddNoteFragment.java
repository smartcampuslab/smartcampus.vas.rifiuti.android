package eu.trentorise.smartcampus.rifiuti;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import eu.trentorise.smartcampus.rifiuti.model.Note;

public class AddNoteFragment extends DialogFragment implements View.OnClickListener {

	public interface OnAddListener {
		public void onAdd(String s);

		public void onEdit(Note n);
	}

	private static final int REQ_CODE = 0;
	private static final String PASSED_NOTE = "passed_note";

	private EditText noteEditText;

	private boolean isEdit = false;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.note_add);

		View contentView = getActivity().getLayoutInflater().inflate(R.layout.fragment_note_add,
				new LinearLayout(getActivity()), false);
		builder.setView(contentView);

		noteEditText = (EditText) contentView.findViewById(R.id.fragment_add_et);

		if (getArguments() != null && getArguments().containsKey(PASSED_NOTE)) {
			Note n = (Note) getArguments().get(PASSED_NOTE);
			noteEditText.setText(n.getText());
			isEdit = true;
		}

		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				String text = noteEditText.getText().toString();
				if (isEdit) {
					Note n = (Note) getArguments().get(PASSED_NOTE);
					n.setText(text);
					((OnAddListener) getTargetFragment()).onEdit(n);
				} else {
					((OnAddListener) getTargetFragment()).onAdd(text);
				}
				getDialog().dismiss();
			}
		});

		builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				getDialog().cancel();
			}
		});

		return builder.create();
	}

	public static AddNoteFragment newInstance(OnAddListener listener, Note n) {
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
		String text = ((EditText) getView().findViewById(R.id.fragment_add_et)).getText().toString();
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
