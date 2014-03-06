package eu.trentorise.smartcampus.rifiuti;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;

import eu.trentorise.smartcampus.rifiuti.AddNoteFragment.OnAddListener;
import eu.trentorise.smartcampus.rifiuti.data.NotesHelper;
import eu.trentorise.smartcampus.rifiuti.model.Note;
import eu.trentorise.smartcampus.rifiuti.model.Profile;
import eu.trentorise.smartcampus.rifiuti.utils.PreferenceUtils;
import android.R.anim;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.internal.view.menu.ActionMenuView.ActionMenuChildView;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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
			setListAdapter(new ArrayAdapter<Note>(getActivity(),
					android.R.layout.simple_list_item_1, NotesHelper.getNotes()));
			setEmptyText(getString(R.string.no_notes));

			getListView().setOnItemLongClickListener(
					new AdapterView.OnItemLongClickListener() {

						@Override
						public boolean onItemLongClick(AdapterView<?> arg0,
								View view, int position, long arg3) {
							if (NotesHelper.notesActionMode != null) {
								return false;
							}

							// Start the CAB using the ActionMode.Callback
							// defined above
							if (getActivity() instanceof ActionBarActivity)
								NotesHelper.notesActionMode = ((ActionBarActivity) getActivity())
										.startSupportActionMode(NotesListFragment.this);
							
							getListView().setItemChecked(position, true);
							toggleBackground(getListView(), position, view);
							return true;

						}
					});
			getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
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
			AddNoteFragment addNoteFragment = AddNoteFragment.newInstance(this);
			addNoteFragment.show(getFragmentManager(), "");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		if (NotesHelper.notesActionMode != null) {
			boolean check = getListView().isItemChecked(position);
			//hack because when in actionmode check is always true
			getListView().setItemChecked(position, check);
			toggleBackground(l, position, v);
			getListView().post(new Runnable() {
				
				@Override
				public void run() {
					SparseBooleanArray pos = getListView().getCheckedItemPositions();
					for(int i=0;i<pos.size();i++){
						if(pos.get(i))
							return;
					}
					//no element's checked
					NotesHelper.notesActionMode.finish();
				}
			}); 
		} else
			goToDetail(position);

	}

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		MenuInflater inflater = mode.getMenuInflater();
		inflater.inflate(R.menu.context_notes, menu);
		return true;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode arg0, Menu arg1) {
		return false;
	}

	@Override
	public boolean onActionItemClicked(ActionMode arg0, MenuItem arg1) {
		if (arg1.getItemId() == R.id.action_delete) {
			SparseBooleanArray pos = getListView().getCheckedItemPositions();
			arg0.finish();
		}
		return false;
	}

	@Override
	public void onDestroyActionMode(ActionMode arg0) {
		NotesHelper.notesActionMode = null;
		
		//clean background
		getListView().post(new Runnable() {
			
			@Override
			public void run() {
				SparseBooleanArray pos = getListView().getCheckedItemPositions();
				for(int i=0;i<pos.size();i++){
					if(pos.get(i)){
						getListView().setItemChecked(i, false);
						toggleBackground(getListView(), i, getListView().getChildAt(i));
					}
				}
			}
		}); 
	}

	@Override
	public void onAdd(String s) {
		NotesHelper.addNote(s);
		setListAdapter(new ArrayAdapter<Note>(getActivity(),
				android.R.layout.simple_list_item_1, NotesHelper.getNotes()));
	}

	private void goToDetail(int position) {
		Log.d("not implemented", "yet");
	}
	
	private void toggleBackground(ListView l,int pos,View v){
		if(l.isItemChecked(pos))
			v.setBackgroundColor(Color.CYAN);
		else
			v.setBackgroundColor(Color.TRANSPARENT);
		
	}

}
