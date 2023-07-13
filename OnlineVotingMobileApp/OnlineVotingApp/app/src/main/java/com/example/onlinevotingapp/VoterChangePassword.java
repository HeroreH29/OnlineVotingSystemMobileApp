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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

public class VoterChangePassword extends AppCompatActivity {

    TextInputLayout newPassword, confirmPassword;
    Button changePasswordBtn;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ListenerRegistration electionEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voter_change_password);

        //Initialization for the Back Button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Input Inits
        newPassword = findViewById(R.id.changePassword);
        confirmPassword = findViewById(R.id.changeConfirmPassword);

        //Button Inits
        changePasswordBtn = findViewById(R.id.changePasswordBtn);

        //Checking if election has ended
        checkEnd();

        //Change Password Button Tap
        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword();
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

    private void changePassword() {

        String newPass, confirmPass;

        newPass = newPassword.getEditText().getText().toString();
        confirmPass = confirmPassword.getEditText().getText().toString();

        if (newPass.isEmpty()) {
            newPassword.setError("Field cannot be empty");
        } else if (newPass.length() > 25) {
            newPassword.setError("Limit exceeded");
        } else if (newPass.length() <= 6) {
            newPassword.setError("Password is too short");
        } else {
            newPassword.setError(null);
        }

        if (confirmPass.isEmpty()) {
            confirmPassword.setError("Field cannot be empty");
        } else {
            confirmPassword.setError(null);
        }

        if (!newPass.equals(confirmPass)) {
            confirmPassword.setError("Passwords do not match");
        } else {
            confirmPassword.setError(null);
        }

        if (!newPass.isEmpty() && !confirmPass.isEmpty() && newPass.equals(confirmPass) && newPass.length() > 6 && newPass.length() <= 25) {

            user.updatePassword(newPass)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), VoterHomepage.class)
                                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            } else {
                                Toast.makeText(getApplicationContext(), "Password Change Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("ChangePassError", e.toString());
                        }
                    });
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