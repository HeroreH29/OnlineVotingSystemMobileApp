package com.example.onlinevotingapp;

import static android.service.controls.ControlsProviderService.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    TextInputLayout logInEmail, logInPassword;
    TextView forgotPassword;
    Button logIn, register;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth;

    String emailAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            //TextInputs
            logInEmail = findViewById(R.id.logInEmailAddress);
            logInPassword = findViewById(R.id.logInPassword);

            //TextViews
            forgotPassword = findViewById(R.id.forgotPassword);

            //Buttons
            logIn = findViewById(R.id.logInButton);
            register = findViewById(R.id.registerButton);

            //Firebase Authentication
            mAuth = FirebaseAuth.getInstance();

            //Log In Button Tap
            logIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    logInEmail.setError(null);
                    logInPassword.setError(null);
                    String email = logInEmail.getEditText().getText().toString().trim();
                    String password = logInPassword.getEditText().getText().toString().trim();

                    if (password.length() > 25) {
                        logInPassword.setError("Password Exceeds Limit");
                    }
                    if (password.length() > 0 && password.length() <= 6) {
                        logInPassword.setError("Password Is Too Short");
                    }
                    if (password.isEmpty()) {
                        logInPassword.setError("Field Cannot Be Empty");
                    }
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        logInEmail.setError("Invalid Email Address");
                    }
                    if (!email.isEmpty() && !password.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length() > 6) {
                        logInEmail.setError(null);
                        logInPassword.setError(null);
                        mAuth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            checkLogIn(email);
                                        } else {
                                            Toast.makeText(MainActivity.this, "Email/Password Do Not Match", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }
            });

            //Register Button Tap
            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    db.collection("Admin").whereEqualTo("hasEnded", true).get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    if (queryDocumentSnapshots.isEmpty()) {
                                        startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(), "Elections Has Ended!\nUsers Cannot Register Anymore", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
            });
        } catch (Exception e) {
            Log.d("Main Activity Log", e.toString());
        }

        //Forgot Password Button Tap
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ForgotPassword.class));
            }
        });
    }

    private void checkLogIn(String email) {
        db.collection("Voters").document(email).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String userType = documentSnapshot.getString("userType");

                            if (userType.equals("Voter")) {
                                db.collection("Admin").whereEqualTo("hasEnded", true).get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                if (queryDocumentSnapshots.isEmpty()) {
                                                    Log.d(TAG, "onSuccess: Election is still ongoing");
                                                    Toast.makeText(MainActivity.this, "Logged-In Successfully", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(MainActivity.this, VoterHomepage.class));
                                                    finish();
                                                }
                                                else {
                                                    startActivity(new Intent(getApplicationContext(), VoteResults.class));
                                                    Toast.makeText(getApplicationContext(), "Election Has Already Ended.\nCongratulate The Winners!", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                }
                                            }
                                        });
                            }
                        } else {
                            db.collection("Candidates").document(email).get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if (documentSnapshot.exists()) {
                                                String userType = documentSnapshot.getString("userType");

                                                if (userType.equals("Candidate")) {
                                                    db.collection("Admin").whereEqualTo("hasEnded", true).get()
                                                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                    if (queryDocumentSnapshots.isEmpty()) {
                                                                        Log.d(TAG, "onSuccess: Election is still ongoing");
                                                                        Toast.makeText(MainActivity.this, "Logged-In Successfully", Toast.LENGTH_SHORT).show();
                                                                        startActivity(new Intent(MainActivity.this, CandidateHomepage.class));
                                                                        finish();
                                                                    }
                                                                    else {
                                                                        startActivity(new Intent(getApplicationContext(), VoteResults.class));
                                                                        Toast.makeText(getApplicationContext(), "Election Has Already Ended.\nCongratulate The Winners!", Toast.LENGTH_SHORT).show();
                                                                        finish();
                                                                    }
                                                                }
                                                            });
                                                }
                                            } else {
                                                db.collection("Admin").document(email).get()
                                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                if (documentSnapshot.exists()) {
                                                                    String userType = documentSnapshot.getString("userType");

                                                                    if (userType.equals("Admin")) {
                                                                        Toast.makeText(getApplicationContext(), "Admin Logged-In", Toast.LENGTH_SHORT).show();
                                                                        startActivity(new Intent(getApplicationContext(), AdminHomepage.class));
                                                                        finish();
                                                                    }
                                                                }
                                                            }
                                                        });
                                            }
                                        }

                                    });
                        }
                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();

        //Check if election end date and time is the same with current date and time
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
                            long time;
                            int compareValue;
                            Timestamp timestamp = Timestamp.now();
                            for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                                /*time = documentSnapshot.getTimestamp("electionEnd").getSeconds();
                                localDateTime = LocalDateTime.ofEpochSecond(time, 0, ZoneId.systemDefault().getRules().getOffset(localDateTime));
                                Toast.makeText(getApplicationContext(), dateTimeFormatter.format(localDateTime), Toast.LENGTH_LONG).show();*/
                                compareValue = timestamp.compareTo(documentSnapshot.getTimestamp("electionEnd"));

                                if (compareValue < 0) {
                                    Log.d(TAG, "onSuccess: It's too early to end the elections");
                                }
                                else if (compareValue >= 0) {
                                    String admin = documentSnapshot.getId();
                                    db.collection("Admin").document(admin).update("hasEnded", true);
                                }
                            }
                        }
                    }
                });

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            emailAddress = user.getEmail();
            db.collection("Voters").document(emailAddress).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                String userType = documentSnapshot.getString("userType");

                                if (userType.equals("Voter")) {
                                    db.collection("Admin").whereEqualTo("hasEnded", true).get()
                                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    if (queryDocumentSnapshots.isEmpty()) {
                                                        Log.d(TAG, "onSuccess: Election is still ongoing");
                                                        Toast.makeText(MainActivity.this, "Voter Logged-In", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(MainActivity.this, VoterHomepage.class));
                                                        finish();
                                                    }
                                                    else {
                                                        startActivity(new Intent(getApplicationContext(), ElectionResults.class));
                                                        Toast.makeText(getApplicationContext(), "Election Has Already Ended.\nCongratulate The Winners!", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    }
                                                }
                                            });
                                }
                            } else {
                                db.collection("Candidates").document(emailAddress).get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                if (documentSnapshot.exists()) {
                                                    String userType = documentSnapshot.getString("userType");

                                                    if (userType.equals("Candidate")) {
                                                        db.collection("Admin").whereEqualTo("hasEnded", true).get()
                                                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                        if (queryDocumentSnapshots.isEmpty()) {
                                                                            Log.d(TAG, "onSuccess: Election is still ongoing");
                                                                            Toast.makeText(MainActivity.this, "Logged-In Successfully", Toast.LENGTH_SHORT).show();
                                                                            startActivity(new Intent(MainActivity.this, CandidateHomepage.class));
                                                                            finish();
                                                                        }
                                                                        else {
                                                                            startActivity(new Intent(getApplicationContext(), ElectionResults.class));
                                                                            Toast.makeText(getApplicationContext(), "Election Has Already Ended.\nCongratulate The Winners!", Toast.LENGTH_SHORT).show();
                                                                            finish();
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                } else {
                                                    db.collection("Admin").document(emailAddress).get()
                                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                    if (documentSnapshot.exists()) {
                                                                        String userType = documentSnapshot.getString("userType");

                                                                        if (userType.equals("Admin")) {
                                                                            Toast.makeText(getApplicationContext(), "Admin Logged-In", Toast.LENGTH_SHORT).show();
                                                                            startActivity(new Intent(getApplicationContext(), AdminHomepage.class));
                                                                            finish();
                                                                        }
                                                                    }
                                                                }
                                                            });
                                                }
                                            }

                                        });
                            }
                        }
                    });
        }
    }
}