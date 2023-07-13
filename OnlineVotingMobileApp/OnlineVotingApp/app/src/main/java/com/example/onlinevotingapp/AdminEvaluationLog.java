package com.example.onlinevotingapp;

import static android.service.controls.ControlsProviderService.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class AdminEvaluationLog extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference voters = db.collection("Voters");
    RecyclerView hasVotedView, hasNotVotedView;
    HasVotedAdapter hasVotedAdapter;
    HasNotVotedAdapter hasNotVotedAdapter;

    TextView chairText, viceChairText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_evaluation_log);

        //Initialization for the Back Button
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        //TextViews
        chairText = findViewById(R.id.chairText);
        viceChairText = findViewById(R.id.viceChairText);

        //Recycler View Things
        hasVotedView();
        hasNotVotedView();

    }

    private void hasVotedView() {
        Query query = voters.whereEqualTo("hasVoted", true);

        FirestoreRecyclerOptions<AdminVoterItem> options = new FirestoreRecyclerOptions.Builder<AdminVoterItem>()
                .setQuery(query, AdminVoterItem.class)
                .build();

        Log.d(TAG, "hasVotedView: " + query);

        hasVotedAdapter = new HasVotedAdapter(options);

        hasVotedView = findViewById(R.id.hasVotedList);
        hasVotedView.setHasFixedSize(true);
        hasVotedView.setLayoutManager(new LinearLayoutManager(this));
        hasVotedView.setAdapter(hasVotedAdapter);
        hasVotedView.setItemAnimator(null);
        hasVotedAdapter.startListening();

        hasVotedAdapter.setOnItemClickListener(new HasVotedAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                AdminVoterItem item = documentSnapshot.toObject(AdminVoterItem.class);

                String uid = item.getVoterUID();
                startActivity(new Intent(getApplicationContext(), CheckVoter.class).putExtra("uid", uid));
                finish();
            }
        });
    }

    private void hasNotVotedView() {
        Query query = voters.whereEqualTo("hasVoted", false);

        FirestoreRecyclerOptions<AdminVoterItem> options = new FirestoreRecyclerOptions.Builder<AdminVoterItem>()
                .setQuery(query, AdminVoterItem.class)
                .build();

        hasNotVotedAdapter = new HasNotVotedAdapter(options);

        hasNotVotedView = findViewById(R.id.hasNotVotedList);
        hasNotVotedView.setHasFixedSize(true);
        hasNotVotedView.setLayoutManager(new LinearLayoutManager(this));
        hasNotVotedView.setAdapter(hasNotVotedAdapter);
        hasNotVotedView.setItemAnimator(null);
        hasNotVotedAdapter.startListening();

        hasNotVotedAdapter.setOnItemClickListener(new HasNotVotedAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        hasVotedAdapter.stopListening();;
        hasNotVotedAdapter.stopListening();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}