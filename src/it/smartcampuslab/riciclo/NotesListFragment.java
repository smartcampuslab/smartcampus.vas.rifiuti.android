package it.smartcampuslab.riciclo;

import it.smartcampuslab.riciclo.AddNoteFragment.OnAddListener;
import it.smartcampuslab.riciclo.data.NotesHelper;
import it.smartcampuslab.riciclo.data.RifiutiHelper;
import it.smartcampuslab.riciclo.model.Note;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

public class NotesListFragment extends ListFragment implements OnAddListener, ActionMode.Callback {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_noteslist, container, false);
		ImageView addIcon = (ImageView) view.findViewById(R.id.notes_empty_addicon);
		addIcon.setColorFilter(getResources().getColor(R.color.rifiuti_middle), Mode.MULTIPLY);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListAdapter(new NotesAdapter(getActivity(), R.layout.note_row, NotesHelper.getNotes()));

		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		Drawable colord = new ColorDrawable(getResources().getColor(R.color.rifiuti_middle));
		getListView().setDivider(colord);
		getListView().setDividerHeight(1);
	}

	@Override
	public void onStop() {
		super.onStop();
		if (NotesHelper.notesActionMode != null) {
			NotesHelper.notesActionMode.finish();
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.notes_list, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_add) {
			AddNoteFragment addNoteFragment = AddNoteFragment.newInstance(this, null);
			addNoteFragment.show(getFragmentManager(), "");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		if (NotesHelper.notesActionMode != null) {
			SparseBooleanArray pos = getListView().getCheckedItemPositions();
			for (int i = 0; i < getListAdapter().getCount(); i++) {
				if (pos.get(i)) {
					return;
				}
			}
			// no element's checked
			NotesHelper.notesActionMode.finish();
		} else if (getActivity() instanceof ActionBarActivity) {
			NotesHelper.notesActionMode = ((ActionBarActivity) getActivity()).startSupportActionMode(NotesListFragment.this);
		}

		// v.setSelected(!v.isSelected());
		// toggleBackground(l, position, v);

		if (NotesHelper.notesActionMode != null) {
			NotesHelper.notesActionMode.invalidate();
		}
	}

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		MenuInflater inflater = mode.getMenuInflater();
		inflater.inflate(R.menu.context_notes, menu);
		return true;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		if (mode != null) {
			if (checkAndGetSelectedItem(getListView().getCheckedItemPositions()) >= 0) {
				menu.getItem(0).setVisible(true);
			} else {
				menu.getItem(0).setVisible(false);
			}
		}
		return true;
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		SparseBooleanArray pos;
		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			pos = RifiutiHelper.copySparseBooleanArray(getListView().getCheckedItemPositions());
		} else {
			pos = getListView().getCheckedItemPositions().clone();
		}

		if (item.getItemId() == R.id.action_delete) {
			showConfirmAndDelete(pos);
			// new DeleteNotesTask().execute(pos);
			// getActivity().setProgressBarIndeterminateVisibility(true);
		} else if (item.getItemId() == R.id.action_edit) {
			int index = checkAndGetSelectedItem(pos);
			AddNoteFragment anf = AddNoteFragment.newInstance(this, NotesHelper.getNotes().get(index));
			anf.show(getFragmentManager(), "");
			getListView().setItemChecked(index, false);
		}
		mode.finish();
		return false;
	}

	private void showConfirmAndDelete(final SparseBooleanArray pos) {
		int message = R.string.note_dialog_delete_msg;
		if (pos.size() > 1) {
			message = R.string.note_dialog_delete_msg_multi;
		}
		new AlertDialog.Builder(getActivity()).setTitle(R.string.profile_dialog_title).setMessage(message)
				.setPositiveButton(R.string.confirm, new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						getActivity().setProgressBarIndeterminateVisibility(true);
						new DeleteNotesTask().execute(pos);
					}
				}).setNegativeButton(android.R.string.cancel, null).create().show();
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
		NotesHelper.notesActionMode = null;

		/*
		 * clean selected
		 */
		if (getListView() != null) {
			SparseBooleanArray pos = getListView().getCheckedItemPositions();
			for (int i = 0; i < getListAdapter().getCount(); i++) {
				if (pos.get(i)) {
					getListView().setItemChecked(i, false);
				}
			}
		}
	}

	@Override
	public void onAdd(String s) {
		if (s.trim().length() > 0) {
			NotesHelper.addNote(s);
			setListAdapter(new ArrayAdapter<Note>(getActivity(), R.layout.note_row, NotesHelper.getNotes()));
		}
	}

	@Override
	public void onEdit(Note n) {
		if (n.getText().trim().length() > 0) {
			NotesHelper.editNote(n);

		} else {
			NotesHelper.deleteNotes(n);
		}
		setListAdapter(new ArrayAdapter<Note>(getActivity(), R.layout.note_row, NotesHelper.getNotes()));
	}

	private int checkAndGetSelectedItem(SparseBooleanArray pos) {
		int cnt = 0;
		int index = 0;
		for (int i = 0; i < pos.size(); i++) {
			if (pos.valueAt(i)) {
				cnt++;
				index = pos.keyAt(i);
			}
		}
		return (cnt == 1) ? index : -1;
	}

	private class NotesAdapter extends ArrayAdapter<Note> {

		public NotesAdapter(Context context, int resource, List<Note> list) {
			super(context, resource, list);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rootview = super.getView(position, convertView, parent);
			rootview.setTag(getItem(position));
			return rootview;
		}
	}

	private class DeleteNotesTask extends AsyncTask<SparseBooleanArray, Void, Void> {
		@Override
		protected Void doInBackground(SparseBooleanArray... params) {
			SparseBooleanArray pos = params[0];
			for (int i = 0; i < pos.size(); i++) {
				if (pos.valueAt(i)) {
					NotesHelper.deleteNotes(((Note) getListView().getItemAtPosition(pos.keyAt(i))));
				}
			}
			publishProgress();
			return null;
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
			if (getListView() != null) {
				setListAdapter(new ArrayAdapter<Note>(getActivity(), R.layout.note_row, NotesHelper.getNotes()));
			}
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			getActivity().setProgressBarIndeterminateVisibility(false);
		}

	}

}
