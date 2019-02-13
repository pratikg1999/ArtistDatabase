package com.example.android.no_issues;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddTrackActivity extends AppCompatActivity {
    Button addTrackInActivity;
    EditText tvTrackName;
    TextView tvArtistName;
    SeekBar sbTrackRating;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_track);
        tvTrackName = (EditText) findViewById(R.id.trackName);
        addTrackInActivity = (Button) findViewById(R.id.addTrackInTrackActivity);
        tvArtistName = (TextView) findViewById(R.id.tv_Artist_Name);
        sbTrackRating = (SeekBar) findViewById(R.id.sb_Track_Rating);
        Intent intent = getIntent();

        String id = intent.getStringExtra("artistId");
        String artistName = intent.getStringExtra("artistName");
        tvArtistName.setText(artistName);

        final DatabaseReference trackArtist = FirebaseDatabase.getInstance().getReference("tracks").child(id);

        addTrackInActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String trackName = tvTrackName.getText().toString().trim();
                int rating = sbTrackRating.getProgress();
                if(trackName!=""){
                    String id = trackArtist.push().getKey();
                    Track track = new Track(id, trackName, rating);
                    trackArtist.child(id).setValue(track);
                    Toast.makeText(AddTrackActivity.this, "track saved successfully", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}
