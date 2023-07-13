package com.example.onlinevotingapp;

import static android.service.controls.ControlsProviderService.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.O)
public class DateTimeChoose extends AppCompatActivity {

    Button setElectionDate, endElections, proceedButton;
    TextView electionsText;

    LocalDateTime localDateTime = LocalDateTime.now();
    int yr = localDateTime.getYear();
    int mos = localDateTime.getMonthValue();
    int dy = localDateTime.getDayOfMonth();
    int hr = localDateTime.getHour();
    int min = localDateTime.getMinute();

    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_time_choose);


        //Initialization for the Back Button
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Buttons
        setElectionDate = findViewById(R.id.setElectionDateTime);
        endElections = findViewById(R.id.endRegistrationsNow);
        proceedButton = findViewById(R.id.proceedButton);

        //TextViews
        electionsText = findViewById(R.id.electionsText);

        //Set Election Date Button Tap
        setElectionDate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                electionDate();
            }
        });

        //End Elections Tap
        endElections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endElectionsNow();
            }
        });

        //Proceed Button Tap
        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendDateTimeToDB();
            }
        });
    }

    private void sendDateTimeToDB() {
        CollectionReference admin = FirebaseFirestore.getInstance().collection("Admin");

        Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

        Map<String, Object> electionEnd = new HashMap<>();
        electionEnd.put("electionEnd", date);

        Map<String, Boolean> hasSet = new HashMap<>();
        hasSet.put("hasSet", true);

        Map<String,Boolean> hasEnded = new HashMap<>();
        hasEnded.put("hasEnded", false);

        admin.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String adminEmail = "";
                        List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                            adminEmail = documentSnapshot.getId();
                        }

                        admin.document(adminEmail).set(hasEnded, SetOptions.merge());
                        admin.document(adminEmail).set(hasSet, SetOptions.merge());
                        admin.document(adminEmail).set(electionEnd, SetOptions.merge())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        startActivity(new Intent(getApplicationContext(), AdminHomepage.class));
                                        Toast.makeText(getApplicationContext(), "Date & Time Set!", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                    }
                });
    }

    private void endElectionsNow() {
        new AlertDialog.Builder(DateTimeChoose.this)
                .setTitle("End Elections Now")
                .setMessage("Are You Sure?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.collection("Admin").get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                                        for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                                            String admin = documentSnapshot.getId();
                                            db.collection("Admin").document(admin).update("hasEnded", true);
                                            db.collection("Admin").document(admin).update("hasSet", false);

                                            Map<String, Object> delete = new HashMap<>();
                                            delete.put("electionEnd", FieldValue.delete());
                                            db.collection("Admin").document(admin).update(delete);
                                        }
                                        startActivity(new Intent(getApplicationContext(), AdminHomepage.class));
                                        Toast.makeText(getApplicationContext(), "Elections Has Now Ended!", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(TAG, "onClick: End now cancelled");
                    }
                })
                .show()
                .setCancelable(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void electionDate() {

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(DateTimeChoose.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        localDateTime = LocalDateTime.of(year, month+1, day, hour, minute);
                        Log.d(TAG, "onTimeSet: " + localDateTime);
                        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("'Elections Will End On:\n'MMM dd, yyyy'\n@ 'hh:mm a");
                        electionsText.setText(dateTimeFormatter.format(localDateTime));
                    }
                }, hr, min, false);
                timePickerDialog.show();
            }
        }, yr, mos-1, dy);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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

                            db.collection("Admin").whereEqualTo("hasEnded", true).get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            if (queryDocumentSnapshots.isEmpty()) {
                                                Log.d(TAG, "onSuccess: Election has not ended yet");
                                            }
                                            else {
                                                electionsText.setText("Elections Will End On:\nElections Has Already Ended!");
                                            }
                                        }
                                    });
                        }
                        else {
                            List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                            LocalDateTime localDateTime = LocalDateTime.now();
                            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("'Elections Will End On:\n'MMM dd, yyyy'\n@ 'hh:mm a");
                            for (DocumentSnapshot documentSnapshot : documentSnapshots) {

                                if (documentSnapshot.getBoolean("hasEnded") == true) {
                                    electionsText.setText("Elections Will End On:\nElections Has Already Ended!");
                                }
                                else {
                                    long time = documentSnapshot.getTimestamp("electionEnd").getSeconds();
                                    localDateTime = LocalDateTime.ofEpochSecond(time, 0, ZoneId.systemDefault().getRules().getOffset(localDateTime));
                                    electionsText.setText(dateTimeFormatter.format(localDateTime));
                                }
                            }
                        }
                    }
                });
    }
}