package com.example.onlinevotingapp;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CandidateRegistration extends AppCompatActivity {

    ImageView candidateImage;
    TextView clickableText;
    TextInputLayout candidateEmail, candidatePassword, candidateConfirmPassword, candidateFullName, candidateYearSection, candidatePosition, candidateAchievements, candidatePersonalQualities, candidateBackground;
    AutoCompleteTextView candidatePositionList;
    ArrayList<String> positions;
    ArrayAdapter<String> positionsAdapter;
    CheckBox candidateCheckBox;
    Button candidateRegistrationButton;
    ProgressBar progressBar;
    Uri imageUri;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    StorageReference reference = FirebaseStorage.getInstance().getReference();
    DatabaseReference root = FirebaseDatabase.getInstance().getReference("candidateImages/");
    String imageURL, selectedPosition;
    double progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidate_registration);

        //Initialization for the Back Button
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        //ImageView
        candidateImage = findViewById(R.id.candidateImage);

        //TextView
        clickableText = findViewById(R.id.clickableText);

        //Input Layouts
        candidateEmail = findViewById(R.id.candidateEmail);
        candidatePassword = findViewById(R.id.candidatePassword);
        candidateConfirmPassword = findViewById(R.id.candidateConfirmPassword);
        candidateFullName = findViewById(R.id.candidateFullName);
        candidateYearSection = findViewById(R.id.candidateYearSection);
        candidatePosition = findViewById(R.id.candidatePosition);
        candidateAchievements = findViewById(R.id.candidateAchievements);
        candidatePersonalQualities = findViewById(R.id.candidatePersonalQualities);
        candidateBackground = findViewById(R.id.candidateBackground);

        candidateYearSection.getEditText().setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        //Progress Bar
        progressBar = findViewById(R.id.progressBar);

        //Checkbox
        candidateCheckBox = findViewById(R.id.candidateCheckBox);

        //Button
        candidateRegistrationButton = findViewById(R.id.candidateRegistrationButton);

        //Click Listener for Clickable Text
        clickableText.setOnClickListener(view -> choosePictures());

        //Click Listener for Register Button
        candidateRegistrationButton.setOnClickListener(view -> registerCandidate());

        //Position Dropdown Init
        candidatePosition.getEditText().setText("None");
        positions = new ArrayList<>();
        positions.add("Chairperson");
        positions.add("Vice Chairperson");
        positions.add("Secretary");
        positions.add("Treasurer");
        positions.add("Auditor");
        positions.add("4th Year Representative");
        positions.add("3rd Year Representative");
        positions.add("2nd Year Representative");
        positions.add("1st Year Representative");

        positionsAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.candidate_position_item, positions);

        candidatePositionList = findViewById(R.id.positionDropDownMenu);
        candidatePositionList.setAdapter(positionsAdapter);
        candidatePositionList.setThreshold(1);



    }

    private void registerCandidate() {
        try {
            String email = candidateEmail.getEditText().getText().toString().trim();
            String password = candidatePassword.getEditText().getText().toString();
            String confirmPassword = candidateConfirmPassword.getEditText().getText().toString();
            String fullName = candidateFullName.getEditText().getText().toString().trim();
            String yearSection = candidateYearSection.getEditText().getText().toString().trim();
            String position = candidatePosition.getEditText().getText().toString().trim();
            String achievements = candidateAchievements.getEditText().getText().toString().trim();
            String personalQualities = candidatePersonalQualities.getEditText().getText().toString().trim();
            String background = candidateBackground.getEditText().getText().toString().trim();


            //Email Conditions
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                candidateEmail.setError("Invalid Email Address");
            } else {
                candidateEmail.setError(null);
            }

            //Password Conditions
            if (!password.equals(confirmPassword)) {
                candidatePassword.setError("Passwords Do Not Match");
                candidateConfirmPassword.setError("Passwords Do Not Match");
            } else {
                candidatePassword.setError(null);
                candidateConfirmPassword.setError(null);
            }

            if (password.length() > 25) {
                candidatePassword.setError("Password Exceeds Limit");
            } else {
                candidatePassword.setError(null);
            }
            if (password.length() < 6) {
                candidatePassword.setError("Password Is Too Short");
            } else {
                candidatePassword.setError(null);
            }

            //Candidate Details Conditions
            if (fullName.isEmpty()) {
                candidateFullName.setError("Field Cannot Be Empty");
            }
            if (yearSection.isEmpty()) {
                candidateYearSection.setError("Field Cannot Be Empty");
            }
            if (position.isEmpty()) {
                candidatePosition.setError("Field Cannot Be Empty");
            }
            else if (position.equals("None")) {
                candidatePosition.setError("Running Position Cannot Be None");
            }
            if (achievements.isEmpty()) {
                candidateAchievements.setError("Field Cannot Be Empty");
            }
            if (personalQualities.isEmpty()) {
                candidatePersonalQualities.setError("Field Cannot Be Empty");
            }
            if (background.isEmpty()) {
                candidateBackground.setError("Field Cannot Be Empty");
            } else {
                candidateFullName.setError(null);
                candidateYearSection.setError(null);
                candidatePosition.setError(null);
                candidateAchievements.setError(null);
                candidatePersonalQualities.setError(null);
                candidateBackground.setError(null);
            }

            //To check if all fields are filled with the required data
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches() && !password.isEmpty() && password.length() > 6 && password.length() <= 25 && confirmPassword.length() > 6 && confirmPassword.length() <= 25 && password.equals(confirmPassword) && !fullName.isEmpty() && !yearSection.isEmpty() && !position.isEmpty() && !position.equals("None") && !achievements.isEmpty() && !personalQualities.isEmpty() && !background.isEmpty()) {
                if (!candidateCheckBox.isChecked()) {
                    candidateCheckBox.setError("Tick The Checkbox To Proceed");
                    Toast.makeText(getApplicationContext(), "Tick The Checkbox To Proceed", Toast.LENGTH_SHORT).show();
                } else {
                    if (imageUri == null) {
                        candidateCheckBox.setError(null);
                        Toast.makeText(getApplicationContext(), "Please Attach An Image To Proceed", Toast.LENGTH_SHORT).show();
                    } else {
                        candidateCheckBox.setError(null);
                        uploadPicture(email, password, fullName, yearSection, position, achievements, personalQualities, background, imageUri);
                    }
                }

            }

        } catch (Exception e) {
            Log.d("Candidate log", e.toString());
        }

    }

    private void finalizeRegistration(String email, String password, String fullName, String yearSection, String position, String achievements, String personalQualities, String background, String imageURL) {

        try {
            Map<String, Object> candidate = new HashMap<>();
            candidate.put("userType", "Candidate");
            candidate.put("registrationStatus", "Pending");
            candidate.put("candidateFullName", fullName);
            candidate.put("candidateYearSection", yearSection);
            candidate.put("candidatePosition", position);
            candidate.put("candidateAchievements", achievements);
            candidate.put("candidatePersonalQualities", personalQualities);
            candidate.put("candidateBackground", background);
            candidate.put("imageURL", imageURL);

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            db.collection("Candidates").document(email).set(candidate)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(getApplicationContext(), "Candidate Registration Sent To Admin", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(), CandidateHomepage.class)
                                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getApplicationContext(), "Something Went Wrong...", Toast.LENGTH_SHORT).show();
                                        Log.d("Candidate Register Log", e.toString());
                                    });
                        } else {
                            Toast.makeText(getApplicationContext(), "Candidate Already Registered", Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (Exception e) {
            Log.d("Candidate Regis Log", e.toString());
        }

    }

    private void choosePictures() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(candidateImage);

        }
    }

    private void uploadPicture(String email, String password, String fullName, String yearSection, String position, String achievements, String personalQualities, String background, Uri uri) {
        StorageReference fileRef = reference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        candidateRegistrationButton.setEnabled(false);
        candidateRegistrationButton.setFocusable(false);
        candidateRegistrationButton.setFocusableInTouchMode(false);
        Toast.makeText(getApplicationContext(), "Image Uploading\nPlease Wait...", Toast.LENGTH_SHORT).show();
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        imageURL = uri.toString();
                        if ((int) progress == 100) {
                            Toast.makeText(getApplicationContext(), "Image Uploaded", Toast.LENGTH_SHORT).show();
                            finalizeRegistration(email, password, fullName, yearSection, position, achievements, personalQualities, background, imageURL);
                        }

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Image Upload Failed", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                progressBar.setProgress((int) progress);
            }
        });
    }


    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
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