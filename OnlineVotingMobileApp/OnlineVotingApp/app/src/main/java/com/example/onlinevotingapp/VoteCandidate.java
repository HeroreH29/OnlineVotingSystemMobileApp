package com.example.onlinevotingapp;

import static android.service.controls.ControlsProviderService.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoteCandidate extends AppCompatActivity {

    EditText voteName, voteYearSection, voteAchievements, votePersonalQualities, voteBackgrond;
    TextView votePosition;
    Button voteCandidate;
    ImageView voteImage;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference notebookRef = db.collection("Candidates");

    String candidateName, ID, pos, email, uid;
    Integer voteCount;

    ListenerRegistration electionEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_candidate);

        //ImageView
        voteImage = findViewById(R.id.voteImage);

        //EditText
        voteName = findViewById(R.id.voteName);
        voteYearSection = findViewById(R.id.voteYearSection);
        voteAchievements = findViewById(R.id.voteAchievements);
        votePersonalQualities = findViewById(R.id.votePersonalQualities);
        voteBackgrond = findViewById(R.id.voteBackground);

        //TextView
        votePosition = findViewById(R.id.votePosition);

        //Button
        voteCandidate = findViewById(R.id.voteCandidate);

        //Initialization for the Back Button
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Checking if election has ended
        checkEnd();

        candidateName = getIntent().getStringExtra("name");

        //Retrieving selected candidate's document ID for later use
        getDocumentID(candidateName);

        //Getting current user's email and UID
        email = user.getEmail();
        uid = user.getUid();

        //Vote Candidate Button Tap
        voteCandidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkVotes();
            }
        });

    }

    private void checkEnd() {
        electionEnd = db.collection("Admin").whereEqualTo("hasEnded", true)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value.isEmpty()) {
                            Log.d(TAG, "onEvent: Election has not yet ended");
                        }
                        else {
                            startActivity(new Intent(getApplicationContext(), VoteResults.class));
                            Toast.makeText(getApplicationContext(), "Election Has Ended", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
    }

    private void insertUIDForCandidate(String documentID) {
        Log.d(TAG, "insertUIDForCandidate: " + documentID);
        Map<String, Integer> votes = new HashMap<>();
        votes.put(uid, 1);

        notebookRef.document(documentID).set(votes, SetOptions.merge());
    }

    private void recordVote() {
        notebookRef.whereEqualTo("candidateFullName", candidateName).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                            ID = documentSnapshot.getId();
                            pos = documentSnapshot.getString("candidatePosition");
                            voteCount = documentSnapshot.getDouble("voteCount").intValue();
                            voteCount = voteCount + 1;

                            notebookRef.document(ID).update(uid, 2)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                            notebookRef.document(ID).update("voteCount", voteCount)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {

                                                            db.collection("Voters").document(email).update("Votes." + pos, true).
                                                                    addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            Map<String, Boolean> hasVoted = new HashMap<>();
                                                                            hasVoted.put("hasVoted", true);
                                                                            db.collection("Voters").document(email).set(hasVoted, SetOptions.merge());
                                                                            Toast.makeText(getApplicationContext(), "Candidate Voted Successfully", Toast.LENGTH_SHORT).show();
                                                                            startActivity(new Intent(getApplicationContext(), VoterHomepage.class)
                                                                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION));
                                                                            overridePendingTransition(0, 0);
                                                                            finish();
                                                                        }
                                                                    });
                                                        }
                                                    });
                                        }
                                    });
                        }
                    }
                });
    }

    private void checkVotes() {
        notebookRef.whereEqualTo("candidateFullName", candidateName).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                            pos = documentSnapshot.getString("candidatePosition");
                        }
                        db.collection("Voters").document(email).get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        Map<String, Boolean> map = (Map<String, Boolean>) documentSnapshot.get("Votes");


                                        boolean chairperson = map.get(pos);
                                        boolean viceChairperson = map.get(pos);
                                        boolean treasurer = map.get(pos);
                                        boolean secretary = map.get(pos);
                                        boolean auditor = map.get(pos);
                                        boolean fourthYearRep = map.get(pos);
                                        boolean thirdYearRep = map.get(pos);
                                        boolean secondYearRep = map.get(pos);
                                        boolean firstYearRep = map.get(pos);

                                        if (chairperson == false) {
                                            recordVote();
                                        } else if (viceChairperson == false) {
                                            recordVote();
                                        } else if (treasurer == false) {
                                            recordVote();
                                        } else if (secretary == false) {
                                            recordVote();
                                        } else if (auditor == false) {
                                            recordVote();
                                        } else if (fourthYearRep == false) {
                                            recordVote();
                                        } else if (thirdYearRep == false) {
                                            recordVote();
                                        } else if (secondYearRep == false) {
                                            recordVote();
                                        } else if (firstYearRep == false) {
                                            recordVote();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "You Have Already Voted\nFor This Position", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                });
    }

    private void getDocumentID(String candidateName) {

        notebookRef.whereEqualTo("candidateFullName", candidateName).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                            String documentID = documentSnapshot.getId();
                            String name = documentSnapshot.getString("candidateFullName");
                            String yearSection = documentSnapshot.getString("candidateYearSection");
                            String position = documentSnapshot.getString("candidatePosition");
                            String achievement = documentSnapshot.getString("candidateAchievements");
                            String personalQualities = documentSnapshot.getString("candidatePersonalQualities");
                            String background = documentSnapshot.getString("candidateBackground");
                            String imageURL = documentSnapshot.getString("imageURL");

                            if (name.equals(candidateName)) {
                                voteName.setText(name);
                                voteYearSection.setText(yearSection);
                                votePosition.setText(position);
                                voteAchievements.setText(achievement);
                                votePersonalQualities.setText(personalQualities);
                                voteBackgrond.setText(background);
                                Picasso.get().load(imageURL)
                                        .resize(450, 0).into(voteImage);
                            }
                            insertUIDForCandidate(documentID);

                        }

                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        electionEnd.remove();
    }
}