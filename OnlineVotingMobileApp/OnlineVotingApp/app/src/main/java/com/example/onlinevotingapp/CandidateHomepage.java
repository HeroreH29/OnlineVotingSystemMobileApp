package com.example.onlinevotingapp;

import static android.service.controls.ControlsProviderService.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CandidateHomepage extends AppCompatActivity {

    Button logOut;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    EditText candName, candYearSection, candAchievements, candPersonalQualities, candBackgrond, registrationStatus;
    TextView candPosition;
    ImageView candImage;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference notebookRef = db.collection("Candidates");
    String status;
    ListenerRegistration registration;
    ListenerRegistration electionEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidate_homepage);

        //ImageView
        candImage = findViewById(R.id.candImage);

        //EditText
        candName = findViewById(R.id.candName);
        candYearSection = findViewById(R.id.candYearSection);
        candAchievements = findViewById(R.id.candAchievements);
        candPersonalQualities = findViewById(R.id.candPersonalQualities);
        candBackgrond = findViewById(R.id.candBackground);
        registrationStatus = findViewById(R.id.candRegistrationStatus);

        //TextView
        candPosition = findViewById(R.id.candPosition);

        //Buttons
        logOut = findViewById(R.id.candidateLogout);

        //Log Out Button Tap
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Toast.makeText(getApplicationContext(), "Logged Out Successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CandidateHomepage.this, MainActivity.class));
                finish();
            }
        });

        //Checking if elections has ended
        checkEnd();

        //For Displaying Candidate Details
        getCandidateDetails();

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

    private void getCandidateDetails() {
        FirebaseUser user = mAuth.getCurrentUser();
        String emailAddress = user.getEmail();

        if (user != null) {
            registration = notebookRef.document(emailAddress)
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (value.exists()) {
                                if (error != null) {
                                    Log.e("Candidate Homepage 1", "onEvent: ", error);
                                    return;
                                }
                                if (value != null) {
                                    Log.d("Candidate Document", "onEvent: -----------------------------");
                                    Log.d("Candidate Document", "onEvent: " + value.getData());

                                    String name = value.getString("candidateFullName");
                                    String yearSection = value.getString("candidateYearSection");
                                    String position = value.getString("candidatePosition");
                                    String achievement = value.getString("candidateAchievements");
                                    String personalQualities = value.getString("candidatePersonalQualities");
                                    String background = value.getString("candidateBackground");
                                    status = value.getString("registrationStatus");
                                    String imageURL = value.getString("imageURL");

                                    candName.setText(name);
                                    candYearSection.setText(yearSection);
                                    candPosition.setText(position);
                                    candAchievements.setText(achievement);
                                    candPersonalQualities.setText(personalQualities);
                                    candBackgrond.setText(background);
                                    Picasso.get().load(imageURL)
                                            .resize(450, 0).into(candImage);
                                    registrationStatus.setText(status);

                                    if (status.isEmpty()) {

                                    }

                                    if (status.equals("Pending")) {
                                        registrationStatus.setTextColor(Color.BLUE);
                                        registrationStatus.setText(status);
                                        Toast.makeText(CandidateHomepage.this, "Wait For Your Registration To Be Approved", Toast.LENGTH_LONG).show();
                                    } else if (status.equals("Accepted")) {
                                        Toast.makeText(CandidateHomepage.this, "Congratulations!\nYour Registration Has Been Approved!", Toast.LENGTH_LONG).show();
                                        registrationStatus.setTextColor(Color.GREEN);
                                        registrationStatus.setText(status);
                                    } else if (status.equals("Rejected")) {
                                        new AlertDialog.Builder(CandidateHomepage.this)
                                                .setTitle("Notice")
                                                .setMessage("Your registration is rejected. Your account and registration will be removed from the system\n\nSee you on the next elections!")
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageURL);
                                                        photoRef.delete()
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        notebookRef.document(emailAddress).delete()
                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void unused) {
                                                                                        accountDelete();
                                                                                    }
                                                                                })
                                                                                .addOnFailureListener(new OnFailureListener() {
                                                                                    @Override
                                                                                    public void onFailure(@NonNull Exception e) {
                                                                                        Log.d(TAG, "Error Deleting Document");
                                                                                    }
                                                                                });
                                                                    }
                                                                });

                                                    }
                                                })
                                                .show()
                                                .setCancelable(false);
                                        registrationStatus.setTextColor(Color.RED);
                                        registrationStatus.setText(status);
                                    }


                                } else {
                                    Log.e("Candidate Homepage 2", "onEvent: NULL");
                                }
                            } else {
                                Log.d(TAG, "Document doesn't exist");
                            }
                        }
                    });
        }
    }

    private void accountDelete() {
        FirebaseUser user = mAuth.getCurrentUser();
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Account Deleted!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e);
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
        registration.remove();
        electionEnd.remove();
    }
}