package com.example.android.no_issues;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TracksFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TracksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TracksFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    RecyclerView rViewTracksFragment;
    ArrayList<Track> tracks;
    ArrayList<Artist> correspondingArtists;
    DatabaseReference DB;


    private OnFragmentInteractionListener mListener;

    public TracksFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TracksFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TracksFragment newInstance(String param1, String param2) {
        TracksFragment fragment = new TracksFragment();
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
        DB = FirebaseDatabase.getInstance().getReference();
        tracks = new ArrayList<>();
        correspondingArtists = new ArrayList<>();
        View v = inflater.inflate(R.layout.fragment_tracks, container, false);
        rViewTracksFragment = (RecyclerView) v.findViewById(R.id.rViewTrackFragment);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this.getContext());
        final RViewTracksFragmentAdapter adapter = new RViewTracksFragmentAdapter(this.getContext(), DB, tracks, correspondingArtists);
        rViewTracksFragment.setLayoutManager(manager);
        rViewTracksFragment.setAdapter(adapter);
        DB.addValueEventListener(new ValueEventListener() {
            @Override

            public void onDataChange(@NonNull DataSnapshot dataBaseSnapshot) {
                tracks.clear();
                correspondingArtists.clear();
                DataSnapshot dataSnapshot = dataBaseSnapshot.child("tracks");
                for(DataSnapshot artistSnapshot: dataSnapshot.getChildren()){
                    for(DataSnapshot trackSnapshot: artistSnapshot.getChildren()){
                        tracks.add(trackSnapshot.getValue(Track.class));
                        correspondingArtists.add(dataBaseSnapshot.child("artists").child(artistSnapshot.getKey()).getValue(Artist.class));
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("MyDatabase", "Failed to read value.", databaseError.toException());
            }
        });


        return v;
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

    public class RViewTracksFragmentAdapter extends RecyclerView.Adapter<RViewTracksFragmentAdapter.RViewTracksFragmentVHolder>{
        Context context;
        DatabaseReference db;
        ArrayList<Track> list;
        ArrayList<Artist> artistList;
        public  RViewTracksFragmentAdapter(Context context, DatabaseReference db, ArrayList<Track> list, ArrayList<Artist> artistList){
            this.context = context;
            this.db = db;
            this.list = list;
            this.artistList = artistList;
        }

        @NonNull
        @Override
        public RViewTracksFragmentVHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_track, viewGroup, false);
            RViewTracksFragmentVHolder holder = new RViewTracksFragmentVHolder(v);
            return  holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RViewTracksFragmentVHolder holder, int i) {
            String trackName = list.get(i).getName();
            String artistName = artistList.get(i).getName();
            int rating = list.get(i).getRating();

            holder.tvTrackRating.setText(rating+"");
            holder.tvTrackName.setText(trackName);
            holder.tvArtistName.setText(artistName);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class RViewTracksFragmentVHolder extends RecyclerView.ViewHolder{
        TextView tvTrackName;
        TextView tvTrackRating;
        TextView tvArtistName;
            public RViewTracksFragmentVHolder(@NonNull View itemView) {
                super(itemView);
                tvArtistName = (TextView) itemView.findViewById(R.id.tv_artist_name);
                tvTrackName = (TextView) itemView.findViewById(R.id.tv_track_name);
                tvTrackRating  = (TextView) itemView.findViewById(R.id.tv_track_rating);
            }
        }
    }
}
