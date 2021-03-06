package nl.samtimo.pubcrawl.pubs;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import nl.samtimo.pubcrawl.Pub;
import nl.samtimo.pubcrawl.R;
import nl.samtimo.pubcrawl.request.Request;
import nl.samtimo.pubcrawl.request.RequestMethod;
import nl.samtimo.pubcrawl.request.RequestTask;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PubsListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class PubsListFragment extends Fragment implements AdapterView.OnItemClickListener{
    private OnFragmentInteractionListener mListener;

    private ArrayList<Pub> pubs;
    private PubsListAdapter adapter;

    public PubsListFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pubs = new ArrayList<>();

        reloadPubs();
    }

    public void reloadPubs(){
        Request request = new Request(RequestMethod.GET, "pubs", null, null);
        new RequestTask(this).execute(request);
    }

    public void loadPubs(String json){
        try{
            JSONObject object = new JSONObject(json);
            JSONArray results = object.getJSONArray("results");
            for (int i=0; i<results.length(); i++) {
                JSONObject result = results.getJSONObject(i);
                pubs.add(new Pub(result.getString("place_id"), result.getString("name"), false, result.getString("icon")));
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pubs_list, container, false);
        // get the ListView from fragment_list
        ListView listView = (ListView) rootView.findViewById(R.id.list_pubs);
        // register ListView so I can use it with the context menu
        registerForContextMenu(listView);
        // create adapter, parameters: activity, layout of individual items, array of values
        adapter = new PubsListAdapter(getActivity(), pubs);
        // set the adapter to the ListView
        listView.setAdapter(adapter);
        // add actionlistener
        listView.setOnItemClickListener(this);

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Pub pub = (Pub) parent.getItemAtPosition(position);
        mListener.onListFragmentInteraction(pub);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Pub pub);
    }
}
