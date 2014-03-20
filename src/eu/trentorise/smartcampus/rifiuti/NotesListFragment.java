package eu.trentorise.smartcampus.rifiuti;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import eu.trentorise.smartcampus.rifiuti.AddNoteFragment.OnAddListener;
import eu.trentorise.smartcampus.rifiuti.data.NotesHelper;
import eu.trentorise.smartcampus.rifiuti.data.RifiutiHelper;
import eu.trentorise.smartcampus.rifiuti.model.Note;

public class NotesListFragment extends ListFragment implements OnAddListener, ActionMode.Callback {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListAdapter(new NotesAdapter(getActivity(), R.layout.note_row, NotesHelper.getNotes()));
		setEmptyText(getString(R.string.no_notes));

		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		Drawable colord = new ColorDrawable(getResources().getColor(R.color.rifiuti_middle));
		getListView().setDivider(colord);
		getListView().setDividerHeight(1);
		
		
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.notes_list, menu);
		super.onCreateOptionsMenu(menu, inflater);
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
			getListView().post(new Runnable() {
				@Override
				public void run() {
					SparseBooleanArray pos = getListView().getCheckedItemPositions();
					for (int i = 0; i < getListAdapter().getCount(); i++) {
						if (pos.get(i))
							return;
					}
					// no element's checked
					NotesHelper.notesActionMode.finish();
				}
			});

		} else if (getActivity() instanceof ActionBarActivity) {
			NotesHelper.notesActionMode = ((ActionBarActivity) getActivity()).startSupportActionMode(NotesListFragment.this);
		}
		//v.setSelected(!v.isSelected());
		//toggleBackground(l, position, v);

		if (NotesHelper.notesActionMode != null)
			NotesHelper.notesActionMode.invalidate();

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
			if (checkAndGetSelectedItem(getListView().getCheckedItemPositions()) >= 0)
				menu.getItem(0).setVisible(true);
			else
				menu.getItem(0).setVisible(false);
		}
		return true;
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

		SparseBooleanArray pos;
		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			pos = RifiutiHelper.copySparseBooleanArray(getListView()
					.getCheckedItemPositions());
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
		new AlertDialog.Builder(getActivity()).setTitle(R.string.dialog_confirm_title)
				.setMessage(R.string.dialog_confirm_note_msg).setPositiveButton(android.R.string.ok, new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						getActivity().setProgressBarIndeterminateVisibility(true);
						new DeleteNotesTask().execute(pos);
					}
				}).setNegativeButton(android.R.string.cancel, null).create().show();
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
		NotesHelper.notesActionMode = null;

		// clean background
		getListView().post(new Runnable() {
			@Override
			public void run() {
				SparseBooleanArray pos = getListView().getCheckedItemPositions();
				for (int i = 0; i < getListAdapter().getCount(); i++) {
					if (pos.get(i)) {
						getListView().setItemChecked(i, false);
					}
				}
			}
		});
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
		for (int i = 0; i < getListAdapter().getCount(); i++) {
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
				if (pos.valueAt(i))
					NotesHelper.deleteNotes(((Note) getListView().getItemAtPosition(pos.keyAt(i))));
			}
			publishProgress();
			return null;
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
			if (getListView() != null) {
				setListAdapter(new ArrayAdapter<Note>(getActivity(), R.layout.note_row,
						NotesHelper.getNotes()));
			}
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			getActivity().setProgressBarIndeterminateVisibility(false);
		}

	}

}
