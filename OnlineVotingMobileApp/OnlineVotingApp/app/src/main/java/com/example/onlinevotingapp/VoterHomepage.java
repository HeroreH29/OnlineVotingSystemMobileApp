package com.example.onlinevotingapp;

import static android.service.controls.ControlsProviderService.TAG;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoterHomepage extends AppCompatActivity {

    Button logOut, voterProfileSettings;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference notebookRef = db.collection("Candidates");
    RecyclerView chairpersonView, viceChairpersonView, secretaryView, treasurerView, auditorView, fourthYearView, thirdYearView, secondYearView, firstYearView;
    ChairpersonAdapter chairpersonAdapter;
    ViceChairpersonAdapter viceChairpersonAdapter;
    SecretaryAdapter secretaryAdapter;
    TreasurerAdapter treasurerAdapter;
    AuditorAdapter auditorAdapter;
    FourthYearAdapter fourthYearAdapter;
    ThirdYearAdapter thirdYearAdapter;
    SecondYearAdapter secondYearAdapter;
    FirstYearAdapter firstYearAdapter;

    String voterUID;

    TextView chairText, viceChairText, secretaryText, treasurerText, auditorText, fourthYearText, thirdYearText, secondYearText, firstYearText;

    ListenerRegistration electionEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voter_homepage);

        //TextViews
        chairText = findViewById(R.id.chairText);
        viceChairText = findViewById(R.id.viceChairText);
        secretaryText = findViewById(R.id.secretaryText);
        treasurerText = findViewById(R.id.treasurerText);
        auditorText = findViewById(R.id.auditorText);
        fourthYearText = findViewById(R.id.fourthYearText);
        thirdYearText = findViewById(R.id.thirdYearText);
        secondYearText = findViewById(R.id.secondYearText);
        firstYearText = findViewById(R.id.firstYearText);

        //Buttons
        logOut = findViewById(R.id.logOutButton);
        voterProfileSettings = findViewById(R.id.voterProfileSettings);

        //Check if election has ended
        checkEnd();

        //Recycler View Things
        chairpersonRecyclerView();
        viceChairPersonView();
        secretaryView();
        treasurerView();
        auditorView();
        fourthYearView();
        thirdYearView();
        secondYearView();
        firstYearView();

        //Insert UID of current user (voter)
        insertUID();

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

        //Voter Profile Settings Button Tap
        voterProfileSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), VoterProfile.class));
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

    private void insertUID() {
        FirebaseUser user = mAuth.getCurrentUser();
        String email = user.getEmail();
        voterUID = user.getUid();

        Map<String, Object> uid = new HashMap<>();
        uid.put("voterUID", voterUID);

        db.collection("Voters").document(email).set(uid, SetOptions.merge());
    }

    private void chairpersonRecyclerView() {
        FirebaseUser user = mAuth.getCurrentUser();
        voterUID = user.getUid();
        Query query = notebookRef.whereEqualTo("posOrder", 1);

        FirestoreRecyclerOptions<VoterCandidateItem> options = new FirestoreRecyclerOptions.Builder<VoterCandidateItem>()
                .setQuery(query, VoterCandidateItem.class)
                .build();

        chairpersonAdapter = new ChairpersonAdapter(options);

        chairpersonView = findViewById(R.id.chairpersonList);
        chairpersonView.setHasFixedSize(true);
        chairpersonView.setLayoutManager(new LinearLayoutManager(this));
        chairpersonView.setAdapter(chairpersonAdapter);
        chairpersonView.setItemAnimator(null);

        checkChair();

        chairpersonAdapter.setOnItemClickListener(new ChairpersonAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                VoterCandidateItem note = documentSnapshot.toObject(VoterCandidateItem.class);

                String name = note.getCandidateFullName();
                startActivity(new Intent(getApplicationContext(), VoteCandidate.class).putExtra("name", name));
            }
        });
    }

    private void checkChair() {
        notebookRef.whereEqualTo("posOrder", 1).whereEqualTo(voterUID, 2).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            chairpersonAdapter.startListening();
                        } else {
                            chairpersonAdapter.stopListening();
                            chairText.setText("You already voted for this position");
                        }
                    }
                });
    }

    private void viceChairPersonView() {
        FirebaseUser user = mAuth.getCurrentUser();
        voterUID = user.getUid();
        Query query = notebookRef.whereEqualTo("posOrder", 2);

        FirestoreRecyclerOptions<VoterCandidateItem> options = new FirestoreRecyclerOptions.Builder<VoterCandidateItem>()
                .setQuery(query, VoterCandidateItem.class)
                .build();

        viceChairpersonAdapter = new ViceChairpersonAdapter(options);

        viceChairpersonView = findViewById(R.id.viceChairpersonList);
        viceChairpersonView.setHasFixedSize(true);
        viceChairpersonView.setLayoutManager(new LinearLayoutManager(this));
        viceChairpersonView.setAdapter(viceChairpersonAdapter);
        viceChairpersonView.setItemAnimator(null);

        checkViceChair();

        viceChairpersonAdapter.setOnItemClickListener(new ViceChairpersonAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                VoterCandidateItem note = documentSnapshot.toObject(VoterCandidateItem.class);

                String name = note.getCandidateFullName();
                startActivity(new Intent(getApplicationContext(), VoteCandidate.class).putExtra("name", name));
            }
        });
    }

    private void checkViceChair() {
        notebookRef.whereEqualTo("posOrder", 2).whereEqualTo(voterUID, 2).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            viceChairpersonAdapter.startListening();
                        } else {
                            viceChairpersonAdapter.stopListening();
                            viceChairText.setText("You already voted for this position");
                        }
                    }
                });
    }

    private void secretaryView() {
        FirebaseUser user = mAuth.getCurrentUser();
        voterUID = user.getUid();
        Query query = notebookRef.whereEqualTo("posOrder", 3);

        FirestoreRecyclerOptions<VoterCandidateItem> options = new FirestoreRecyclerOptions.Builder<VoterCandidateItem>()
                .setQuery(query, VoterCandidateItem.class)
                .build();

        secretaryAdapter = new SecretaryAdapter(options);

        secretaryView = findViewById(R.id.secretaryList);
        secretaryView.setHasFixedSize(true);
        secretaryView.setLayoutManager(new LinearLayoutManager(this));
        secretaryView.setAdapter(secretaryAdapter);
        secretaryView.setItemAnimator(null);

        checkSecretary();

        secretaryAdapter.setOnItemClickListener(new SecretaryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                VoterCandidateItem note = documentSnapshot.toObject(VoterCandidateItem.class);

                String name = note.getCandidateFullName();
                startActivity(new Intent(getApplicationContext(), VoteCandidate.class).putExtra("name", name));
            }
        });


    }

    private void checkSecretary() {
        notebookRef.whereEqualTo("posOrder", 3).whereEqualTo(voterUID, 2).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            secretaryAdapter.startListening();
                        } else {
                            secretaryAdapter.stopListening();
                            secretaryText.setText("You already voted for this position");
                        }
                    }
                });
    }

    private void treasurerView() {
        FirebaseUser user = mAuth.getCurrentUser();
        voterUID = user.getUid();
        Query query = notebookRef.whereEqualTo("posOrder", 4);

        FirestoreRecyclerOptions<VoterCandidateItem> options = new FirestoreRecyclerOptions.Builder<VoterCandidateItem>()
                .setQuery(query, VoterCandidateItem.class)
                .build();

        treasurerAdapter = new TreasurerAdapter(options);

        treasurerView = findViewById(R.id.treasurerList);
        treasurerView.setHasFixedSize(true);
        treasurerView.setLayoutManager(new LinearLayoutManager(this));
        treasurerView.setAdapter(treasurerAdapter);
        treasurerView.setItemAnimator(null);

        checkTreasurer();

        treasurerAdapter.setOnItemClickListener(new TreasurerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                VoterCandidateItem note = documentSnapshot.toObject(VoterCandidateItem.class);

                String name = note.getCandidateFullName();
                startActivity(new Intent(getApplicationContext(), VoteCandidate.class).putExtra("name", name));
            }
        });
    }

    private void checkTreasurer() {
        notebookRef.whereEqualTo("posOrder", 4).whereEqualTo(voterUID, 2).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            treasurerAdapter.startListening();
                        } else {
                            treasurerAdapter.stopListening();
                            treasurerText.setText("You already voted for this position");
                        }
                    }
                });
    }

    private void auditorView() {
        FirebaseUser user = mAuth.getCurrentUser();
        voterUID = user.getUid();
        Query query = notebookRef.whereEqualTo("posOrder", 5);

        FirestoreRecyclerOptions<VoterCandidateItem> options = new FirestoreRecyclerOptions.Builder<VoterCandidateItem>()
                .setQuery(query, VoterCandidateItem.class)
                .build();

        auditorAdapter = new AuditorAdapter(options);

        auditorView = findViewById(R.id.auditorList);
        auditorView.setHasFixedSize(true);
        auditorView.setLayoutManager(new LinearLayoutManager(this));
        auditorView.setAdapter(auditorAdapter);
        auditorView.setItemAnimator(null);

        checkAuditor();

        auditorAdapter.setOnItemClickListener(new AuditorAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                VoterCandidateItem note = documentSnapshot.toObject(VoterCandidateItem.class);

                String name = note.getCandidateFullName();
                startActivity(new Intent(getApplicationContext(), VoteCandidate.class).putExtra("name", name));
            }
        });
    }

    private void checkAuditor() {
        notebookRef.whereEqualTo("posOrder", 5).whereEqualTo(voterUID, 2).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            auditorAdapter.startListening();
                        } else {
                            auditorAdapter.stopListening();
                            auditorText.setText("You already voted for this position");
                        }
                    }
                });
    }

    private void fourthYearView() {
        FirebaseUser user = mAuth.getCurrentUser();
        voterUID = user.getUid();
        Query query = notebookRef.whereEqualTo("posOrder", 6);

        FirestoreRecyclerOptions<VoterCandidateItem> options = new FirestoreRecyclerOptions.Builder<VoterCandidateItem>()
                .setQuery(query, VoterCandidateItem.class)
                .build();

        fourthYearAdapter = new FourthYearAdapter(options);

        fourthYearView = findViewById(R.id.fourthYearList);
        fourthYearView.setHasFixedSize(true);
        fourthYearView.setLayoutManager(new LinearLayoutManager(this));
        fourthYearView.setAdapter(fourthYearAdapter);
        fourthYearView.setItemAnimator(null);

        checkFourthYear();

        fourthYearAdapter.setOnItemClickListener(new FourthYearAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                VoterCandidateItem note = documentSnapshot.toObject(VoterCandidateItem.class);

                String name = note.getCandidateFullName();
                startActivity(new Intent(getApplicationContext(), VoteCandidate.class).putExtra("name", name));
            }
        });
    }

    private void checkFourthYear() {
        notebookRef.whereEqualTo("posOrder", 6).whereEqualTo(voterUID, 2).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            fourthYearAdapter.startListening();
                        } else {
                            fourthYearAdapter.stopListening();
                            fourthYearText.setText("You already voted for this position");
                        }
                    }
                });
    }

    private void thirdYearView() {
        FirebaseUser user = mAuth.getCurrentUser();
        voterUID = user.getUid();
        Query query = notebookRef.whereEqualTo("posOrder", 7);

        FirestoreRecyclerOptions<VoterCandidateItem> options = new FirestoreRecyclerOptions.Builder<VoterCandidateItem>()
                .setQuery(query, VoterCandidateItem.class)
                .build();

        thirdYearAdapter = new ThirdYearAdapter(options);

        thirdYearView = findViewById(R.id.thirdYearList);
        thirdYearView.setHasFixedSize(true);
        thirdYearView.setLayoutManager(new LinearLayoutManager(this));
        thirdYearView.setAdapter(thirdYearAdapter);
        thirdYearView.setItemAnimator(null);

        checkThirdYear();

        thirdYearAdapter.setOnItemClickListener(new ThirdYearAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                VoterCandidateItem note = documentSnapshot.toObject(VoterCandidateItem.class);

                String name = note.getCandidateFullName();
                startActivity(new Intent(getApplicationContext(), VoteCandidate.class).putExtra("name", name));
            }
        });
    }

    private void checkThirdYear() {
        notebookRef.whereEqualTo("posOrder", 7).whereEqualTo(voterUID, 2).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            thirdYearAdapter.startListening();
                        } else {
                            thirdYearAdapter.stopListening();
                            thirdYearText.setText("You already voted for this position");
                        }
                    }
                });
    }

    private void secondYearView() {
        FirebaseUser user = mAuth.getCurrentUser();
        voterUID = user.getUid();
        Query query = notebookRef.whereEqualTo("posOrder", 8);

        FirestoreRecyclerOptions<VoterCandidateItem> options = new FirestoreRecyclerOptions.Builder<VoterCandidateItem>()
                .setQuery(query, VoterCandidateItem.class)
                .build();

        secondYearAdapter = new SecondYearAdapter(options);

        secondYearView = findViewById(R.id.secondYearList);
        secondYearView.setHasFixedSize(true);
        secondYearView.setLayoutManager(new LinearLayoutManager(this));
        secondYearView.setAdapter(secondYearAdapter);
        secondYearView.setItemAnimator(null);

        checkSecondYear();

        secondYearAdapter.setOnItemClickListener(new SecondYearAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                VoterCandidateItem note = documentSnapshot.toObject(VoterCandidateItem.class);

                String name = note.getCandidateFullName();
                startActivity(new Intent(getApplicationContext(), VoteCandidate.class).putExtra("name", name));
            }
        });
    }

    private void checkSecondYear() {
        notebookRef.whereEqualTo("posOrder", 8).whereEqualTo(voterUID, 2).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            secondYearAdapter.startListening();
                        } else {
                            secondYearAdapter.stopListening();
                            secondYearText.setText("You already voted for this position");
                        }
                    }
                });
    }

    private void firstYearView() {
        FirebaseUser user = mAuth.getCurrentUser();
        voterUID = user.getUid();
        Query query = notebookRef.whereEqualTo("posOrder", 9);

        FirestoreRecyclerOptions<VoterCandidateItem> options = new FirestoreRecyclerOptions.Builder<VoterCandidateItem>()
                .setQuery(query, VoterCandidateItem.class)
                .build();

        firstYearAdapter = new FirstYearAdapter(options);

        firstYearView = findViewById(R.id.firstYearList);
        firstYearView.setHasFixedSize(true);
        firstYearView.setLayoutManager(new LinearLayoutManager(this));
        firstYearView.setAdapter(firstYearAdapter);
        firstYearView.setItemAnimator(null);

        checkFirstYear();

        firstYearAdapter.setOnItemClickListener(new FirstYearAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                VoterCandidateItem note = documentSnapshot.toObject(VoterCandidateItem.class);

                String name = note.getCandidateFullName();
                startActivity(new Intent(getApplicationContext(), VoteCandidate.class).putExtra("name", name));
            }
        });
    }

    private void checkFirstYear() {
        notebookRef.whereEqualTo("posOrder", 9).whereEqualTo(voterUID, 2).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            firstYearAdapter.startListening();
                        } else {
                            firstYearAdapter.stopListening();
                            firstYearText.setText("You already voted for this position");
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        db.collection("Admin").whereEqualTo("hasSet", true).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            Log.d(TAG, "onSuccess: Admin has not yet set date and time");
                        }
                        else {
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
        electionEnd.remove();
    }
}