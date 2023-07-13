package com.example.onlinevotingapp;

import static android.service.controls.ControlsProviderService.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ElectionResults extends AppCompatActivity {

    ImageView chairWinner, viceChairWinner, secretaryWinner, treasurerWinner, auditorWinner, fourthYearWinner, thirdYearWinner, secondYearWinner, firstYearWinner;
    TextView chairWinnerName, viceChairWinnerName, secretaryWinnerName, treasurerWinnerName, auditorWinnerName, fourthYearWinnerName, thirdYearWinnerName, secondYearWinnerName, firstYearWinnerName;

    CollectionReference candidates = FirebaseFirestore.getInstance().collection("Candidates");
    ArrayList<String> chairNames = new ArrayList<>();
    ArrayList<String> chairImageURL = new ArrayList<>();

    ArrayList<String> viceChairNames = new ArrayList<>();
    ArrayList<String> viceChairImageURL = new ArrayList<>();

    ArrayList<String> secretaryNames = new ArrayList<>();
    ArrayList<String> secretaryImageURL = new ArrayList<>();

    ArrayList<String> treasurerNames = new ArrayList<>();
    ArrayList<String> treasurerImageURL = new ArrayList<>();

    ArrayList<String> auditorNames = new ArrayList<>();
    ArrayList<String> auditorImageURL = new ArrayList<>();

    ArrayList<String> fourthNames = new ArrayList<>();
    ArrayList<String> fourthImageURL = new ArrayList<>();

    ArrayList<String> thirdNames = new ArrayList<>();
    ArrayList<String> thirdImageURL = new ArrayList<>();

    ArrayList<String> secondNames = new ArrayList<>();
    ArrayList<String> secondImageURL = new ArrayList<>();

    ArrayList<String> firstNames = new ArrayList<>();
    ArrayList<String> firstImageURL = new ArrayList<>();

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_election_results);

        //Initialization for the Back Button
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Election Results");
        actionBar.setDisplayHomeAsUpEnabled(true);

        //ImageViews
        chairWinner = findViewById(R.id.chairWinner);
        viceChairWinner = findViewById(R.id.viceChairWinner);
        secretaryWinner = findViewById(R.id.secretaryWinner);
        treasurerWinner = findViewById(R.id.treasurerWinner);
        auditorWinner = findViewById(R.id.auditorWinner);
        fourthYearWinner = findViewById(R.id.fourthYearWinner);
        thirdYearWinner = findViewById(R.id.thirdYearWinner);
        secondYearWinner = findViewById(R.id.secondYearWinner);
        firstYearWinner = findViewById(R.id.firstYearWinner);

        //TextViews
        chairWinnerName = findViewById(R.id.chairWinnerName);
        viceChairWinnerName = findViewById(R.id.viceChairWinnerName);
        secretaryWinnerName = findViewById(R.id.secretaryWinnerName);
        treasurerWinnerName = findViewById(R.id.treasurerWinnerName);
        auditorWinnerName = findViewById(R.id.auditorWinnerName);
        fourthYearWinnerName = findViewById(R.id.fourthYearWinnerName);
        thirdYearWinnerName = findViewById(R.id.thirdYearWinnerName);
        secondYearWinnerName = findViewById(R.id.secondYearWinnerName);
        firstYearWinnerName = findViewById(R.id.firstYearWinnerName);

        displayWinners();
    }

    private void displayWinners() {
        candidates.orderBy("voteCount", Query.Direction.DESCENDING).whereEqualTo("posOrder", 1).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            Log.d(TAG, "Chair is Empty");
                        } else {
                            List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                                chairNames.add(documentSnapshot.getString("candidateFullName"));
                                chairImageURL.add(documentSnapshot.getString("imageURL"));
                            }
                            chairWinnerName.setText("Chairperson:\n" + chairNames.get(0));
                            Picasso.get().load(chairImageURL.get(0)).into(chairWinner);
                        }

                    }
                });

        candidates.orderBy("voteCount", Query.Direction.DESCENDING).whereEqualTo("posOrder", 2).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            Log.d(TAG, "Vice Chair is Empty");
                        } else {
                            List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                                viceChairNames.add(documentSnapshot.getString("candidateFullName"));
                                viceChairImageURL.add(documentSnapshot.getString("imageURL"));
                            }
                            viceChairWinnerName.setText("Vice Chairperson:\n" + viceChairNames.get(0));
                            Picasso.get().load(viceChairImageURL.get(0)).into(viceChairWinner);
                        }
                    }
                });

        candidates.orderBy("voteCount", Query.Direction.DESCENDING).whereEqualTo("posOrder", 3).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            Log.d(TAG, "Secretary is Empty");
                        } else {
                            List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                                secretaryNames.add(documentSnapshot.getString("candidateFullName"));
                                secretaryImageURL.add(documentSnapshot.getString("imageURL"));
                            }
                            secretaryWinnerName.setText("Secretary:\n" + secretaryNames.get(0));
                            Picasso.get().load(secretaryImageURL.get(0)).into(secretaryWinner);
                        }
                    }
                });

        candidates.orderBy("voteCount", Query.Direction.DESCENDING).whereEqualTo("posOrder", 4).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            Log.d(TAG, "Treasurer is Empty");
                        } else {
                            List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                                treasurerNames.add(documentSnapshot.getString("candidateFullName"));
                                treasurerImageURL.add(documentSnapshot.getString("imageURL"));
                            }
                            treasurerWinnerName.setText("Treasurer:\n" + treasurerNames.get(0));
                            Picasso.get().load(treasurerImageURL.get(0)).into(treasurerWinner);
                        }
                    }
                });

        candidates.orderBy("voteCount", Query.Direction.DESCENDING).whereEqualTo("posOrder", 5).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            Log.d(TAG, "Auditor is Empty");
                        } else {
                            List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                                auditorNames.add(documentSnapshot.getString("candidateFullName"));
                                auditorImageURL.add(documentSnapshot.getString("imageURL"));
                            }
                            auditorWinnerName.setText("Auditor:\n" + auditorNames.get(0));
                            Picasso.get().load(auditorImageURL.get(0)).into(auditorWinner);
                        }
                    }
                });

        candidates.orderBy("voteCount", Query.Direction.DESCENDING).whereEqualTo("posOrder", 6).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            Log.d(TAG, "4th Year is Empty");
                        } else {
                            List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                                fourthNames.add(documentSnapshot.getString("candidateFullName"));
                                fourthImageURL.add(documentSnapshot.getString("imageURL"));
                            }
                            fourthYearWinnerName.setText("4th Year Representative:\n" + fourthNames.get(0));
                            Picasso.get().load(fourthImageURL.get(0)).into(fourthYearWinner);
                        }
                    }
                });

        candidates.orderBy("voteCount", Query.Direction.DESCENDING).whereEqualTo("posOrder", 7).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            Log.d(TAG, "3rd Year is Empty");
                        } else {
                            List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                                thirdNames.add(documentSnapshot.getString("candidateFullName"));
                                thirdImageURL.add(documentSnapshot.getString("imageURL"));
                            }
                            thirdYearWinnerName.setText("3rd Year Representative:\n" + thirdNames.get(0));
                            Picasso.get().load(thirdImageURL.get(0)).into(thirdYearWinner);
                        }
                    }
                });

        candidates.orderBy("voteCount", Query.Direction.DESCENDING).whereEqualTo("posOrder", 8).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            Log.d(TAG, "2nd Year is Empty");
                        } else {
                            List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                                secondNames.add(documentSnapshot.getString("candidateFullName"));
                                secondImageURL.add(documentSnapshot.getString("imageURL"));
                            }
                            secondYearWinnerName.setText("2nd Year Representative:\n" + secondNames.get(0));
                            Picasso.get().load(secondImageURL.get(0)).into(secondYearWinner);
                        }
                    }
                });

        candidates.orderBy("voteCount", Query.Direction.DESCENDING).whereEqualTo("posOrder", 9).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            Log.d(TAG, "1st Year is Empty");
                        } else {
                            List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                                firstNames.add(documentSnapshot.getString("candidateFullName"));
                                firstImageURL.add(documentSnapshot.getString("imageURL"));
                            }
                            firstYearWinnerName.setText("1st Year Representative:\n" + firstNames.get(0));
                            Picasso.get().load(firstImageURL.get(0)).into(firstYearWinner);
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
}