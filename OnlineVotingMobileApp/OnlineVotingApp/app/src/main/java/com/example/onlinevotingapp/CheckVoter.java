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
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class CheckVoter extends AppCompatActivity {

    String uid;
    TextView voterName, voterEmail, voterNameTitle, chairpersonName, viceChairpersonName, secretaryName, treasurerName, auditorName, fourthYearName, thirdYearName, secondYearName, firstYearName;
    CollectionReference voters = FirebaseFirestore.getInstance().collection("Voters");
    CollectionReference candidates = FirebaseFirestore.getInstance().collection("Candidates");
    ListenerRegistration chair, vice, sec, tre, aud, four, third, seco, first;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_voter);

        uid = getIntent().getStringExtra("uid");

        //TextViews
        voterName = findViewById(R.id.voterName);
        voterEmail = findViewById(R.id.voterEmail);
        voterNameTitle = findViewById(R.id.voterNameTitle);
        chairpersonName = findViewById(R.id.chairpersonName);
        viceChairpersonName = findViewById(R.id.viceChairpersonName);
        secretaryName = findViewById(R.id.secretaryName);
        treasurerName = findViewById(R.id.treasurerName);
        auditorName = findViewById(R.id.auditorName);
        fourthYearName = findViewById(R.id.fourthYearName);
        thirdYearName = findViewById(R.id.thirdYearName);
        secondYearName = findViewById(R.id.secondYearName);
        firstYearName = findViewById(R.id.firstYearName);

        //Initialization for the Back Button
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        displayVoter();
        getChairperson();
        getViceChairperson();
        getSecretary();
        getTreasurer();
        getAuditor();
        getFourth();
        getThird();
        getSecond();
        getFirst();
    }

    private void getFirst() {
        first = candidates.whereEqualTo("posOrder", 9).whereEqualTo(uid, 2)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value.isEmpty()) {
                            Log.d(TAG, "onEvent: No vote for first");
                        } else {
                            List<DocumentSnapshot> documentSnapshots = value.getDocuments();
                            for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                                firstYearName.setText(documentSnapshot.getString("candidateFullName"));
                            }
                        }
                    }
                });
    }

    private void getSecond() {
        seco = candidates.whereEqualTo("posOrder", 8).whereEqualTo(uid, 2)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value.isEmpty()) {
                            Log.d(TAG, "onEvent: No vote for second");
                        } else {
                            List<DocumentSnapshot> documentSnapshots = value.getDocuments();
                            for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                                secondYearName.setText(documentSnapshot.getString("candidateFullName"));
                            }
                        }
                    }
                });
    }

    private void getThird() {
        third = candidates.whereEqualTo("posOrder", 7).whereEqualTo(uid, 2)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value.isEmpty()) {
                            Log.d(TAG, "onEvent: No vote for third");
                        } else {
                            List<DocumentSnapshot> documentSnapshots = value.getDocuments();
                            for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                                thirdYearName.setText(documentSnapshot.getString("candidateFullName"));
                            }
                        }
                    }
                });
    }

    private void getFourth() {
        four = candidates.whereEqualTo("posOrder", 6).whereEqualTo(uid, 2)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value.isEmpty()) {
                            Log.d(TAG, "onEvent: No vote for fourth");
                        } else {
                            List<DocumentSnapshot> documentSnapshots = value.getDocuments();
                            for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                                fourthYearName.setText(documentSnapshot.getString("candidateFullName"));
                            }
                        }
                    }
                });
    }

    private void getAuditor() {
        aud = candidates.whereEqualTo("posOrder", 5).whereEqualTo(uid, 2)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value.isEmpty()) {
                            Log.d(TAG, "onEvent: No vote for auditor");
                        } else {
                            List<DocumentSnapshot> documentSnapshots = value.getDocuments();
                            for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                                auditorName.setText(documentSnapshot.getString("candidateFullName"));
                            }
                        }
                    }
                });
    }

    private void getTreasurer() {
        tre = candidates.whereEqualTo("posOrder", 4).whereEqualTo(uid, 2)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value.isEmpty()) {
                            Log.d(TAG, "onEvent: No vote for treasurer");
                        } else {
                            List<DocumentSnapshot> documentSnapshots = value.getDocuments();
                            for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                                treasurerName.setText(documentSnapshot.getString("candidateFullName"));
                            }
                        }
                    }
                });
    }

    private void getSecretary() {
        sec = candidates.whereEqualTo("posOrder", 3).whereEqualTo(uid, 2)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value.isEmpty()) {
                            Log.d(TAG, "onEvent: No vote for secretary");
                        } else {
                            List<DocumentSnapshot> documentSnapshots = value.getDocuments();
                            for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                                secretaryName.setText(documentSnapshot.getString("candidateFullName"));
                            }
                        }
                    }
                });
    }

    private void getViceChairperson() {
        vice = candidates.whereEqualTo("posOrder", 2).whereEqualTo(uid, 2)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value.isEmpty()) {
                            Log.d(TAG, "onEvent: No vote for vice chairperson");
                        } else {
                            List<DocumentSnapshot> documentSnapshots = value.getDocuments();
                            for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                                viceChairpersonName.setText(documentSnapshot.getString("candidateFullName"));
                            }
                        }
                    }
                });
    }

    private void getChairperson() {
        chair = candidates.whereEqualTo("posOrder", 1).whereEqualTo(uid, 2)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value.isEmpty()) {
                            Log.d(TAG, "onEvent: No vote for chairperson");
                        } else {
                            List<DocumentSnapshot> documentSnapshots = value.getDocuments();
                            for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                                chairpersonName.setText(documentSnapshot.getString("candidateFullName"));
                            }
                        }
                    }
                });
    }

    private void displayVoter() {
        voters.whereEqualTo("voterUID", uid)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        List<DocumentSnapshot> documentSnapshotList = value.getDocuments();
                        for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                            AdminVoterItem voter = documentSnapshot.toObject(AdminVoterItem.class);
                            voter.setDocumentID(documentSnapshot.getId());

                            voterName.setText(voter.getVoterFirstName() + " " + voter.getVoterLastName());
                            voterEmail.setText(voter.getDocumentID());
                            voterNameTitle.setText(voter.getVoterFirstName() + "'s Votes");
                        }
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(getApplicationContext(), AdminEvaluationLog.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();

        chair.remove();
        vice.remove();
        sec.remove();
        tre.remove();
        aud.remove();
        four.remove();
        third.remove();
        seco.remove();
        first.remove();
    }
}