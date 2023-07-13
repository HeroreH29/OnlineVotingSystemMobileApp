package com.example.onlinevotingapp;

import static android.service.controls.ControlsProviderService.TAG;

import androidx.annotation.NonNull;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AdminEvaluateCandidate extends AppCompatActivity {

    EditText evaluateName, evaluateYearSection, evaluationAchievements, evaluationPersonalQualities, evaluationBackgrond;
    TextView evaluatePosition;
    Button approveRegistration, rejectRegistration;
    ImageView evaluationImage;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference notebookRef = db.collection("Candidates");

    String name, candidateName, documentID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluate_candidate);

        //ImageView
        evaluationImage = findViewById(R.id.evaluationImage);

        //EditText
        evaluateName = findViewById(R.id.evaluationName);
        evaluateYearSection = findViewById(R.id.evaluationYearSection);
        evaluationAchievements = findViewById(R.id.evaluationAchievements);
        evaluationPersonalQualities = findViewById(R.id.evaluationPersonalQualities);
        evaluationBackgrond = findViewById(R.id.evaluationBackground);

        //TextView
        evaluatePosition = findViewById(R.id.evaluatePosition);

        //Button
        approveRegistration = findViewById(R.id.approveRegistration);
        rejectRegistration = findViewById(R.id.rejectRegistration);

        //Initialization for the Back Button
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Evaluate Candidate");
        actionBar.setDisplayHomeAsUpEnabled(true);

        candidateName = getIntent().getStringExtra("name");
        getDocumentID();

        //Approve Button Tap
        approveRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                approveRegistration();
            }
        });

        //Reject Button Tap
        rejectRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rejectRegistration();
            }
        });
    }

    private void rejectRegistration() {
        Map<String, Object> regStat = new HashMap<>();

        regStat.put("registrationStatus", "Rejected");

        notebookRef.whereEqualTo("candidateFullName", candidateName).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();

                        for (DocumentSnapshot snapshot : snapshotList) {
                            String candidateID = snapshot.getId();

                            notebookRef.document(candidateID).set(regStat, SetOptions.merge())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(getApplicationContext(), "Registration Rejected", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getApplicationContext(), AdminHomepage.class)
                                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                        }
                                    });
                        }
                    }
                });
    }

    private void approveRegistration() {
        Map<String, Object> regStat = new HashMap<>();
        regStat.put("registrationStatus", "Accepted");


        notebookRef.whereEqualTo("candidateFullName", candidateName).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();

                        for (DocumentSnapshot snapshot : snapshotList) {
                            String candidateID = snapshot.getId();
                            String position = snapshot.getString("candidatePosition");

                            notebookRef.document(candidateID).set(regStat, SetOptions.merge())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            db.collection("Voters").get()
                                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                            List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();

                                                            Map<String, Integer> voter = new HashMap<>();

                                                            for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                                                                String voterID = documentSnapshot.getString("voterUID");
                                                                voter.put(voterID, 0);
                                                            }

                                                            notebookRef.document(candidateID).set(voter, SetOptions.merge())
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {

                                                                            Map<String, Integer> votecount = new HashMap<>();
                                                                            votecount.put("voteCount", 0);

                                                                            notebookRef.document(candidateID).set(votecount, SetOptions.merge())
                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void unused) {

                                                                                            if (position.equals("Chairperson")) {
                                                                                                notebookRef.document(candidateID).update("posOrder", 1);
                                                                                            }
                                                                                            else if (position.equals("Vice Chairperson")) {
                                                                                                notebookRef.document(candidateID).update("posOrder", 2);
                                                                                            }
                                                                                            else if (position.equals("Secretary")) {
                                                                                                notebookRef.document(candidateID).update("posOrder", 3);
                                                                                            }
                                                                                            else if (position.equals("Treasurer")) {
                                                                                                notebookRef.document(candidateID).update("posOrder", 4);
                                                                                            }
                                                                                            else if (position.equals("Auditor")) {
                                                                                                notebookRef.document(candidateID).update("posOrder", 5);
                                                                                            }
                                                                                            else if (position.equals("4th Year Representative")) {
                                                                                                notebookRef.document(candidateID).update("posOrder", 6);
                                                                                            }
                                                                                            else if (position.equals("3rd Year Representative")) {
                                                                                                notebookRef.document(candidateID).update("posOrder", 7);
                                                                                            }
                                                                                            else if (position.equals("2nd Year Representative")) {
                                                                                                notebookRef.document(candidateID).update("posOrder", 8);
                                                                                            }
                                                                                            else if (position.equals("1st Year Representative")) {
                                                                                                notebookRef.document(candidateID).update("posOrder", 9);
                                                                                            }

                                                                                            Toast.makeText(getApplicationContext(), "Registration Approved", Toast.LENGTH_SHORT).show();
                                                                                            startActivity(new Intent(getApplicationContext(), AdminHomepage.class)
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
                                    });
                        }
                    }
                });
    }

    private void getDocumentID() {
        notebookRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            AdminCandidateItem item = documentSnapshot.toObject(AdminCandidateItem.class);
                            item.setDocumentID(documentSnapshot.getId());
                            documentID = item.getDocumentID();

                            notebookRef.document(documentID).get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if (documentSnapshot.exists()) {
                                                name = documentSnapshot.getString("candidateFullName");
                                                String yearSection = documentSnapshot.getString("candidateYearSection");
                                                String position = documentSnapshot.getString("candidatePosition");
                                                String achievement = documentSnapshot.getString("candidateAchievements");
                                                String personalQualities = documentSnapshot.getString("candidatePersonalQualities");
                                                String background = documentSnapshot.getString("candidateBackground");
                                                String imageURL = documentSnapshot.getString("imageURL");

                                                if (name.equals(candidateName)) {
                                                    evaluateName.setText(name);
                                                    evaluateYearSection.setText(yearSection);
                                                    evaluatePosition.setText(position);
                                                    evaluationAchievements.setText(achievement);
                                                    evaluationPersonalQualities.setText(personalQualities);
                                                    evaluationBackgrond.setText(background);
                                                    Picasso.get().load(imageURL)
                                                            .resize(450, 0).into(evaluationImage);

                                                }
                                            }
                                        }
                                    });
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