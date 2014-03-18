package eu.trentorise.smartcampus.rifiuti;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import eu.trentorise.smartcampus.rifiuti.AddNoteFragment.OnAddListener;
import eu.trentorise.smartcampus.rifiuti.data.NotesHelper;
import eu.trentorise.smartcampus.rifiuti.model.Note;

public class NotesListFragment extends ListFragment implements OnAddListener,
		ActionMode.Callback {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		try {
			NotesHelper.init(getActivity());
			setListAdapter(new NotesAdapter(getActivity(),
					android.R.layout.simple_list_item_1, NotesHelper.getNotes()));
			setEmptyText(getString(R.string.no_notes));

			getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
			Drawable colord = new ColorDrawable(getResources().getColor(R.color.rifiuti_middle));
			getListView().setDivider(colord);
			getListView().setDividerHeight(1);
		} catch (IOException e) {
			Log.e(NotesHelper.class.getName(), e.toString());
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.notes_list, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_add) {
			AddNoteFragment addNoteFragment = AddNoteFragment.newInstance(this,
					null);
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
					SparseBooleanArray pos = getListView()
							.getCheckedItemPositions();
					for (int i = 0; i < pos.size(); i++) {
						if (pos.get(i))
							return;
					}
					// no element's checked
					NotesHelper.notesActionMode.finish();
				}
			});

		} else if (getActivity() instanceof ActionBarActivity) {
			NotesHelper.notesActionMode = ((ActionBarActivity) getActivity())
					.startSupportActionMode(NotesListFragment.this);
		}

		toggleBackground(l, position, v);

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
			if (checkAndGetSelectedItem(getListView().getCheckedItemPositions())>=0)
				menu.getItem(0).setVisible(true);
			else
				menu.getItem(0).setVisible(false);
		}
		return true;
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		if (item.getItemId() == R.id.action_delete) {
			SparseBooleanArray pos = getListView().getCheckedItemPositions()
					.clone();
			new DeleteNotesTask().execute(pos);
			getActivity().setProgressBarIndeterminateVisibility(true);
			mode.finish();
		} else if (item.getItemId() == R.id.action_edit) {
			int index = checkAndGetSelectedItem(getListView().getCheckedItemPositions());
			AddNoteFragment anf = AddNoteFragment.newInstance(this,NotesHelper.getNotes().get(index));
			anf.show(getFragmentManager(), "");
			mode.finish();
		}
		return false;
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
		NotesHelper.notesActionMode = null;

		// clean background
		getListView().post(new Runnable() {
			@Override
			public void run() {
				SparseBooleanArray pos = getListView()
						.getCheckedItemPositions();
				for (int i = 0; i < pos.size(); i++) {
					if (pos.get(i)) {
						getListView().setItemChecked(i, false);
						toggleBackground(getListView(), i, getListView()
								.getChildAt(i));
					}
				}
			}
		});
	}

	@Override
	public void onAdd(String s) {
		if (s.trim().length() > 0) {
			NotesHelper.addNote(s);
			setListAdapter(new ArrayAdapter<Note>(getActivity(),
					android.R.layout.simple_list_item_1, NotesHelper.getNotes()));
		}
	}
	
	@Override
	public void onEdit(Note n) {
		NotesHelper.editNote(n);
		setListAdapter(new ArrayAdapter<Note>(getActivity(),
				android.R.layout.simple_list_item_1, NotesHelper.getNotes()));
	}


	private int checkAndGetSelectedItem(SparseBooleanArray pos) {
		int cnt = 0;
		int index = 0;
		for (int i = 0; i < pos.size(); i++) {
			if (pos.valueAt(i)){
				cnt++;
				index=pos.keyAt(i);
			}
		}
		return (cnt==1)?index:-1;
	}

	private void toggleBackground(ListView l, int pos, View v) {
		if (l.isItemChecked(pos)) {
			v.setBackgroundColor(getResources().getColor(
					R.color.rifiuti_green_light_o50));
		} else {
			v.setBackgroundColor(Color.TRANSPARENT);
		}
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

	private class DeleteNotesTask extends
			AsyncTask<SparseBooleanArray, Void, Void> {
		@Override
		protected Void doInBackground(SparseBooleanArray... params) {
			SparseBooleanArray pos = params[0];
			for (int i = 0; i < pos.size(); i++) {
				if (pos.valueAt(i))
					NotesHelper.deleteNotes(((Note) getListView()
							.getItemAtPosition(pos.keyAt(i))));
			}
			publishProgress();
			return null;
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
			if (getListView() != null) {
				setListAdapter(new ArrayAdapter<Note>(getActivity(),
						android.R.layout.simple_list_item_1,
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
