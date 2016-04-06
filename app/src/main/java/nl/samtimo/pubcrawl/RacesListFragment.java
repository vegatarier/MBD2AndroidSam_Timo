package nl.samtimo.pubcrawl;

import android.content.Context;
import android.net.Uri;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RacesListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class RacesListFragment extends Fragment implements AdapterView.OnItemClickListener{

    private OnFragmentInteractionListener mListener;

    private ArrayList<Race> races;
    private View rootView;
    private RaceListAdapter adapter;

    public RacesListFragment() {
        races = new ArrayList<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Request request = new Request(RequestMethod.GET, "races", null, null);
        new RequestTask(this).execute(request);
    }

    public void loadRaces(String json){
        try{
            JSONArray racesArr = new JSONArray(json);
            for (int i=0; i<racesArr.length(); i++) {
                JSONObject race = racesArr.getJSONObject(i);
                races.add(new Race(race.getString("_id"), race.getString("name"), null, null, null));
            }
            adapter.notifyDataSetChanged();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_races_list, container, false);

        // get the ListView from fragment_list
        ListView listView = (ListView) rootView.findViewById(R.id.list_races);
        // register ListView so I can use it with the context menu
        registerForContextMenu(listView);
        // create adapter, parameters: activity, layout of individual items, array of values
        adapter = new RaceListAdapter(getActivity(), races);
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
        Race item = (Race)((ListView)parent).getItemAtPosition(position);
        //TextView o = (TextView)view.findViewById(R.id.list_fruits);
        //o.setBackgroundColor(0xFF00FF00);
        //ListView listView = (ListView) rootView.findViewById(R.id.list_fruits);
        /*listView.setItemChecked(position, true);
        adapter.notifyDataSetChanged();*/
        if(item!=null) mListener.onListFragmentInteraction(item);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Race race);
    }
}
