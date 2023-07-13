package com.example.onlinevotingapp;

import static android.service.controls.ControlsProviderService.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.w3c.dom.DocumentType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoterRegistration extends AppCompatActivity {

    TextInputLayout voterEmail, voterPassword, voterConfirmPassword, voterFirstName, voterLastName, voterCollege, voterYearSection;
    Button voterRegisterButton;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String[] positions = {"Chairperson", "Vice Chairperson", "Secretary", "Treasurer", "Auditor", "4th Year Representative"
            , "3rd Year Representative", "2nd Year Representative", "1st Year Representative"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voter_registration);

        //Initialization for the Back Button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Input Layouts
        voterEmail = findViewById(R.id.voterViewEmail);
        voterPassword = findViewById(R.id.voterPassword);
        voterConfirmPassword = findViewById(R.id.voterConfirmPassword);
        voterFirstName = findViewById(R.id.voterViewFirstName);
        voterLastName = findViewById(R.id.voterViewLastName);
        voterCollege = findViewById(R.id.voterViewCollege);
        voterYearSection = findViewById(R.id.voterViewYearSection);

        voterYearSection.getEditText().setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        //Button
        voterRegisterButton = findViewById(R.id.voterRegisterButton);

        //Register Button Tap
        voterRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerVoter();
            }
        });
    }

    public void registerVoter() {
        try {
            String email = voterEmail.getEditText().getText().toString().trim();
            String password = voterPassword.getEditText().getText().toString().trim();
            String confirmPassword = voterConfirmPassword.getEditText().getText().toString().trim();
            String firstName = voterFirstName.getEditText().getText().toString().trim();
            String lastName = voterLastName.getEditText().getText().toString().trim();
            String college = voterCollege.getEditText().getText().toString().trim();
            String yearSection = voterYearSection.getEditText().getText().toString().trim();

            //Email Conditions
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                voterEmail.setError("Invalid Email Address");
            } else {
                voterEmail.setError(null);
            }

            //Password Conditions
            if (password.isEmpty()) {
                voterPassword.setError("Field Cannot Be Empty");
            } else {
                voterPassword.setError(null);
            }
            if (confirmPassword.isEmpty()) {
                voterConfirmPassword.setError("Field Cannot Be Empty");
            } else {
                voterConfirmPassword.setError(null);
            }

            if (password.isEmpty() && confirmPassword.isEmpty()) {
                voterPassword.setError("Field Cannot Be Empty");
                voterConfirmPassword.setError("Field Cannot Be Empty");
            } else if (!password.equals(confirmPassword)) {
                voterPassword.setError("Passwords Do Not Match");
                voterConfirmPassword.setError("Passwords Do Not Match");
            } else {
                voterPassword.setError(null);
                voterConfirmPassword.setError(null);
            }

            if (password.length() > 25) {
                voterPassword.setError("Password Exceeds Limit");
            } else {
                voterPassword.setError(null);
            }
            if (password.length() < 6) {
                voterPassword.setError("Password Is Too Short");
            } else {
                voterPassword.setError(null);
            }


            //First Name, Last Name, College, and Year & Section Conditions
            if (TextUtils.isEmpty(firstName)) {
                voterFirstName.setError("Field Cannot Be Empty");
            } else {
                voterFirstName.setError(null);
            }

            if (TextUtils.isEmpty(lastName)) {
                voterLastName.setError("Field Cannot Be Empty");
            } else {
                voterLastName.setError(null);
            }

            if (TextUtils.isEmpty(college)) {
                voterCollege.setError("Field Cannot Be Empty");
            } else {
                voterCollege.setError(null);
            }

            if (TextUtils.isEmpty(yearSection)) {
                voterYearSection.setError("Field Cannot Be Empty");
            } else {
                voterYearSection.setError(null);
            }

            if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches() && !password.isEmpty() && password.equals(confirmPassword) && password.length() <= 25 && password.length() > 6 && confirmPassword.length() <= 25 && confirmPassword.length() > 6 && !firstName.isEmpty() && !lastName.isEmpty() && !college.isEmpty() && !yearSection.isEmpty()) {

                finalizeRegistration(email, password, firstName, lastName, college, yearSection);
            }

        } catch (Exception e) {
            Log.d("Voter Log", e.toString());
        }
    }

    private void finalizeRegistration(String email, String password, String firstName, String lastName, String college, String yearSection) {
        try {
            Map<String, Object> voter = new HashMap<>();
            voter.put("userType", "Voter");
            voter.put("voterFirstName", firstName);
            voter.put("voterLastName", lastName);
            voter.put("voterCollege", college);
            voter.put("voterYearSection", yearSection);

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                db.collection("Voters").document(email).set(voter, SetOptions.merge())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Map<String, Boolean> pos = new HashMap<>();

                                                for (int a = 0; a < positions.length; a++) {
                                                    pos.put(positions[a], false);
                                                }

                                                Map<String, Object> votes = new HashMap<>();
                                                votes.put("Votes", pos);

                                                Map<String, Boolean> hasVoted = new HashMap<>();
                                                hasVoted.put("hasVoted", false);

                                                db.collection("Voters").document(email).set(hasVoted, SetOptions.merge());
                                                db.collection("Voters").document(email).set(votes, SetOptions.merge())
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                insertUIDToCandidates();
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                                                        Log.d(TAG, "onFailure: " + e.toString());
                                                    }
                                                });
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(VoterRegistration.this, "Something Went Wrong...", Toast.LENGTH_SHORT).show();
                                                Log.d("Voter Register Log", e.toString());
                                            }
                                        });
                            } else {
                                Toast.makeText(getApplicationContext(), "Voter Already Registered", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } catch (Exception e) {
            Log.d("Voter", e.toString());
        }
    }

    private void insertUIDToCandidates() {
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();
        //Toast.makeText(getApplicationContext(), uid, Toast.LENGTH_SHORT).show();

       db.collection("Candidates").whereEqualTo("registrationStatus", "Accepted").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Map<String, Integer> voter = new HashMap<>();
                        voter.put(uid, 0);
                        List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                            String docuID = documentSnapshot.getId();
                            db.collection("Candidates").document(docuID).set(voter, SetOptions.merge());
                        }
                        Map<String, Boolean> hasVoted = new HashMap<>();
                        hasVoted.put("hasVoted", false);
                        Toast.makeText(VoterRegistration.this, "Voter Registered Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(VoterRegistration.this, VoterHomepage.class)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    }
                });
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
}