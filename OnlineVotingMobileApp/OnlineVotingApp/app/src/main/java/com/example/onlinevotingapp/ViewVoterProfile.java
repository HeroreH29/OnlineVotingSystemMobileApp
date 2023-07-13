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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

public class ViewVoterProfile extends AppCompatActivity {

    CollectionReference voterCollection = FirebaseFirestore.getInstance().collection("Voters");
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    TextInputLayout voterViewEmail, voterViewFirstName, voterViewLastName, voterViewCollege, voterViewYearSection;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ListenerRegistration electionEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        //Initialization for the Back Button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Init for InputLayouts
        voterViewEmail = findViewById(R.id.voterViewEmail);
        voterViewFirstName = findViewById(R.id.voterViewFirstName);
        voterViewLastName = findViewById(R.id.voterViewLastName);
        voterViewCollege = findViewById(R.id.voterViewCollege);
        voterViewYearSection = findViewById(R.id.voterViewYearSection);

        //Checking if election has ended
        checkEnd();

        viewVoterDetails();
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

    private void viewVoterDetails() {
        String email = user.getEmail();

        voterCollection.document(email).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String firstName = documentSnapshot.getString("voterFirstName");
                            String lastName = documentSnapshot.getString("voterLastName");
                            String college = documentSnapshot.getString("voterCollege");
                            String yearSection = documentSnapshot.getString("voterYearSection");

                            voterViewEmail.getEditText().setText(email);
                            voterViewFirstName.getEditText().setText(firstName);
                            voterViewLastName.getEditText().setText(lastName);
                            voterViewCollege.getEditText().setText(college);
                            voterViewYearSection.getEditText().setText(yearSection);
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Document Not Found!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                        Log.d("View Voter Error", e.toString());
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

    @Override
    protected void onStop() {
        super.onStop();
        electionEnd.remove();
    }
}