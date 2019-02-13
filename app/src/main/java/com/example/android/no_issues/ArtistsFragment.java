package com.example.android.no_issues;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ArtistsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ArtistsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArtistsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    RecyclerView rView;
    CardView cardView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference myRef;
    ArrayList<Artist> artists;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ArtistsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ArtistsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ArtistsFragment newInstance(String param1, String param2) {
        ArtistsFragment fragment = new ArtistsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_artists, container, false);
        artists = new ArrayList<>();
        artists.add(new Artist("", "", ""));
        database = FirebaseDatabase.getInstance();
        //myRef = database.getReference("message2");
        //myRef.setValue("Hello");
        myRef =database.getReference();


        rView = (RecyclerView) v.findViewById(R.id.RView);
        cardView = (CardView) v.findViewById(R.id.cards);
        layoutManager = new LinearLayoutManager(this.getContext());
        ((LinearLayoutManager) layoutManager).setOrientation(LinearLayoutManager.VERTICAL);
        rView.setLayoutManager(layoutManager);
        final RViewAdapter adapter = new RViewAdapter(this.getContext(),database, artists);
        rView.setAdapter(adapter);



        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
/*                String value = dataSnapshot.getValue(String.class);
                Log.d("MyDatabase", "Value is: " + value);
                Toast.makeText(Main2Activity.this, value, Toast.LENGTH_SHORT).show();*/

                artists.clear();
                artists.add(new Artist("", "", ""));
                DataSnapshot artistDBSnapshot = dataSnapshot.child("artists");
                for(DataSnapshot artistSnapshot: artistDBSnapshot.getChildren()){
                    Object objArtist = artistSnapshot.getValue();
                    Artist artist  =artistSnapshot.getValue(Artist.class);

                    artists.add(artist);

                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("MyDatabase", "Failed to read value.", error.toException());
            }
        });

        return  v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void updateArtist(final String artistId, String artistName){

        LayoutInflater inflater = getLayoutInflater();
        View v  = inflater.inflate(R.layout.update_artist_layout, null);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setView(v);
        dialogBuilder.setTitle("Update artist "  + artistName);
        final AlertDialog dialog = dialogBuilder.create();
        dialog.show();
        final EditText etArtistNewName = (EditText) v.findViewById(R.id.et_artist_new_name);
        final EditText etArtistNewBand = (EditText) v.findViewById(R.id.et_artist_new_band) ;
        Button bUpdateArtistName = (Button) v.findViewById(R.id.b_update_artist_name);
        bUpdateArtistName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newName = etArtistNewName.getText().toString().trim();
                String newBand = etArtistNewBand.getText().toString().trim();
                if(newName==""){
                    etArtistNewName.setError("Enter a name");
                }
                else if(newBand==""){
                    etArtistNewBand.setError("Enter a band");
                }
                else{
                    DatabaseReference artist = myRef.child("artists").child(artistId);
                    Artist newArtist = new Artist(artistId, newName, newBand);
                    artist.setValue(newArtist);

                    //DatabaseReference trackArtist = myRef.child("tracks").child(artistId);
                    dialog.dismiss();
                }
            }
        });



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
        void onFragmentInteraction(Uri uri);
    }


    class RViewAdapter extends RecyclerView.Adapter<RViewAdapter.RViewHolder>{
        FirebaseDatabase database;
        ArrayList<Artist> list;
        Context context;
        RViewAdapter(Context context, FirebaseDatabase database, ArrayList<Artist> list){
            this.context = context;
            this.database = database;
            this.list = list;
        }
        @NonNull
        @Override
        public RViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cards,viewGroup,false);
            RViewHolder holder = new RViewHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RViewHolder rViewHolder, final int i) {
        /*rViewHolder.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/
            if(i==0){
                rViewHolder.sendButton.setVisibility(View.VISIBLE);
                rViewHolder.addTrack.setVisibility(View.GONE);
            }

            View v = rViewHolder.itemView;
            final Artist curArtist = list.get(i);
            rViewHolder.etName.setText(curArtist.getName());
            rViewHolder.etBand.setText(curArtist.getBand());
            rViewHolder.addTrack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, AddTrackActivity.class);
                    intent.putExtra("artistName", curArtist.getName());
                    intent.putExtra("artistId", curArtist.getId());
                    context.startActivity(intent);
                }
            });
            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    updateArtist(curArtist.getId(), curArtist.getName());
                    Toast.makeText(context, "updating artist", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class RViewHolder extends RecyclerView.ViewHolder{
            EditText etBand;
            EditText etName;
            Button sendButton;
            Button addTrack;
            public RViewHolder(@NonNull final View itemView) {
                super(itemView);
                etName = (EditText) itemView.findViewById(R.id.ET_name);
                etBand = (EditText) itemView.findViewById(R.id.ET_band);
                sendButton = (Button) itemView.findViewById(R.id.sendButton);
                addTrack = (Button) itemView.findViewById(R.id.addTrack);
                sendButton.setVisibility(View.GONE);

                sendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("OnClick ", "updating database");
                        Toast.makeText(itemView.getContext(), "Adding artist", Toast.LENGTH_SHORT).show();
                        String name = etName.getText().toString();
                        String band = etBand.getText().toString();
                        DatabaseReference artistRef = database.getReference("artists");
                        String id = artistRef.push().getKey();
                        Artist artist = new Artist(id, name, band);
                        artistRef.child(id).setValue(artist);
                        Toast.makeText(itemView.getContext(), id + "", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}
