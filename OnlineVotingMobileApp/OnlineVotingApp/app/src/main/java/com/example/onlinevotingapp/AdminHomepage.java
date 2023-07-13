package com.example.onlinevotingapp;

import static android.service.controls.ControlsProviderService.TAG;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdminHomepage extends AppCompatActivity {

    Button logOut, voteResults, setDateTime, adminElectionResults;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference notebookRef = db.collection("Candidates");
    RecyclerView recyclerView;
    AdminCandidateAdapter adapter;

    ImageView electionLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_homepage);

        //ImageView
        electionLog = findViewById(R.id.electionLog);


        //Buttons
        logOut = findViewById(R.id.adminLogOut);
        voteResults = findViewById(R.id.adminVoteResults);
        setDateTime = findViewById(R.id.setDateTimeButton);
        adminElectionResults = findViewById(R.id.adminElectionResults);

        //Initialization for the Back Button
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Admin Homepage");

        //Recycler View Things
        setUpRecyclerView();

        //Log-Out Button Tap
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Toast.makeText(getApplicationContext(), "Logged Out Successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });

        //Election Log Button Tap
        electionLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AdminEvaluationLog.class));
            }
        });

        //Vote Results Button Tap
        voteResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("Admin").whereEqualTo("hasEnded", true).get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (queryDocumentSnapshots.isEmpty()) {
                                    Toast.makeText(getApplicationContext(), "Wait Until The Elections Has Ended!", Toast.LENGTH_SHORT).show();
                                } else {
                                    startActivity(new Intent(getApplicationContext(), AdminVoteResults.class));
                                }
                            }
                        });
            }
        });

        //Set Date Time Button Tap
        setDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), DateTimeChoose.class));
            }
        });

        //Election Results Button Tap
        adminElectionResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("Admin").whereEqualTo("hasEnded", true).get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (queryDocumentSnapshots.isEmpty()) {
                                    Toast.makeText(getApplicationContext(), "Wait Until The Elections Has Ended!", Toast.LENGTH_SHORT).show();
                                } else {
                                    startActivity(new Intent(getApplicationContext(), AdminElectionResults.class));
                                }
                            }
                        });
            }
        });

        //Click Listener for the RecyclerView
        adapter.setOnItemClickListener(new AdminCandidateAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                AdminCandidateItem note = documentSnapshot.toObject(AdminCandidateItem.class);

                String name = note.getCandidateFullName();
                startActivity(new Intent(getApplicationContext(), AdminEvaluateCandidate.class).putExtra("name", name));
            }
        });
    }

    private void setUpRecyclerView() {

        Query query = notebookRef.whereEqualTo("registrationStatus", "Pending").orderBy("candidateFullName", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<AdminCandidateItem> options = new FirestoreRecyclerOptions.Builder<AdminCandidateItem>()
                .setQuery(query, AdminCandidateItem.class)
                .build();

        adapter = new AdminCandidateAdapter(options);

        recyclerView = findViewById(R.id.chairpersonList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(null);

    }


    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();

        db.collection("Admin").whereEqualTo("hasSet", true).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            Log.d(TAG, "onSuccess: Admin has not yet set date and time");

                            db.collection("Admin").whereEqualTo("hasEnded", true).get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            if (queryDocumentSnapshots.isEmpty()) {
                                                Log.d(TAG, "onSuccess: Election has not ended yet");
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Elections Has Already Ended!\nCheck The Results", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                            LocalDateTime localDateTime = LocalDateTime.now();
                            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("'Elections Will End On:\n'MMM dd, yyyy'\n@ 'hh:mm a");
                            for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                                long time = documentSnapshot.getTimestamp("electionEnd").getSeconds();
                                localDateTime = LocalDateTime.ofEpochSecond(time, 0, ZoneId.systemDefault().getRules().getOffset(localDateTime));
                                Toast.makeText(getApplicationContext(), dateTimeFormatter.format(localDateTime), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}