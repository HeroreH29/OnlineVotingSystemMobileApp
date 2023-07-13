package com.example.onlinevotingapp;

import static android.service.controls.ControlsProviderService.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class VoterChangeDetails extends AppCompatActivity {

    TextInputLayout voterChangeFirstName, voterChangeLastName, voterChangeCollege, voterChangeYearSection;
    Button changeDetails;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String email, firstName, lastName, college, yearSection;

    ListenerRegistration electionEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_voter_details);

        //Initialization for the Back Button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Input Layouts
        voterChangeFirstName = findViewById(R.id.voterChangeFirstName);
        voterChangeLastName = findViewById(R.id.voterChangeLastName);
        voterChangeCollege = findViewById(R.id.voterChangeCollege);
        voterChangeYearSection = findViewById(R.id.voterChangeYearSection);

        voterChangeYearSection.getEditText().setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        //Buttons
        changeDetails = findViewById(R.id.voterChangeDetailsButton);

        //Checking if election has ended
        checkEnd();

        //Display current voter details
        showCurrentDetails();

        //Change Details Button Tap
        changeDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detailChange();
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

    private void showCurrentDetails() {
        FirebaseUser user = mAuth.getCurrentUser();
        email = user.getEmail();
        db.collection("Voters").document(email).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            firstName = documentSnapshot.getString("voterFirstName");
                            voterChangeFirstName.getEditText().setText(firstName);
                            lastName = documentSnapshot.getString("voterLastName");
                            voterChangeLastName.getEditText().setText(lastName);
                            college = documentSnapshot.getString("voterCollege");
                            voterChangeCollege.getEditText().setText(college);
                            yearSection = documentSnapshot.getString("voterYearSection");
                            voterChangeYearSection.getEditText().setText(yearSection);
                        }
                    }
                });
    }

    private void detailChange() {
        firstName = voterChangeFirstName.getEditText().getText().toString().trim();
        lastName = voterChangeLastName.getEditText().getText().toString().trim();
        college = voterChangeCollege.getEditText().getText().toString().trim();
        yearSection = voterChangeYearSection.getEditText().getText().toString().trim();

        try {
            //First Name, Last Name, College, and Year & Section Conditions
            if (TextUtils.isEmpty(firstName)) {
                voterChangeFirstName.setError("Field Cannot Be Empty");
            } else {
                voterChangeFirstName.setError(null);
            }

            if (TextUtils.isEmpty(lastName)) {
                voterChangeLastName.setError("Field Cannot Be Empty");
            } else {
                voterChangeLastName.setError(null);
            }

            if (TextUtils.isEmpty(college)) {
                voterChangeCollege.setError("Field Cannot Be Empty");
            } else {
                voterChangeCollege.setError(null);
            }

            if (TextUtils.isEmpty(yearSection)) {
                voterChangeYearSection.setError("Field Cannot Be Empty");
            } else {
                voterChangeYearSection.setError(null);
            }

            if (!firstName.isEmpty() && !lastName.isEmpty() && !college.isEmpty() && !yearSection.isEmpty()) {

                finalizeDetailChange(firstName, lastName, college, yearSection);
            }

        } catch (
                Exception e) {
            Log.d("Voter Log", e.toString());
        }

    }

    private void finalizeDetailChange(String firstName, String lastName, String college, String yearSection) {
        try {
            FirebaseUser user = mAuth.getCurrentUser();
            String email = user.getEmail();

            Map<String, Object> voter = new HashMap<>();
            voter.put("voterFirstName", firstName);
            voter.put("voterLastName", lastName);
            voter.put("voterCollege", college);
            voter.put("voterYearSection", yearSection);

            db.collection("Voters").document(email).set(voter, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(), "Voter Detail Changed Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), VoterHomepage.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Something Went Wrong...", Toast.LENGTH_SHORT).show();
                            Log.d("Voter Register Log", e.toString());
                        }
                    });

        } catch (
                Exception e) {
            Log.d("Voter", e.toString());
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
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