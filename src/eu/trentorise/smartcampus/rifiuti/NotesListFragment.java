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
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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

public class NotesListFragment extends ListFragment implements OnAddListener {

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
		goToDetail(position);

	}

	private void goToDetail(int position) {
		Log.d("not implemented", "yet");
	}

	@Override
	public void onAdd(String s) {
		NotesHelper.addNote(s);
		setListAdapter(new ArrayAdapter<Note>(getActivity(),
				android.R.layout.simple_list_item_1, NotesHelper.getNotes()));
	}

}
