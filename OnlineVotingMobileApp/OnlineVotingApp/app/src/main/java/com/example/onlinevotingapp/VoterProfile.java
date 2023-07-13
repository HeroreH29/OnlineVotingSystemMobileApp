package com.example.onlinevotingapp;

import static android.service.controls.ControlsProviderService.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class VoterProfile extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProfileSettingAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ListenerRegistration electionEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voter_profile);

        //Checking if Election has ended
        checkEnd();

        //Initialization for the Back Button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        ArrayList<ProfileSettingItem> settingList = new ArrayList<>();
        settingList.add(new ProfileSettingItem("View Voter Profile"));
        settingList.add(new ProfileSettingItem("Change Voter Details"));
        settingList.add(new ProfileSettingItem("Change Account Password"));

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        adapter = new ProfileSettingAdapter(settingList);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new ProfileSettingAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                if (position == 0) {
                    startActivity(new Intent(getApplicationContext(), ViewVoterProfile.class));
                }
                if (position == 1) {
                    startActivity(new Intent(getApplicationContext(), VoterChangeDetails.class));
                }
                if (position == 2) {
                    startActivity(new Intent(getApplicationContext(), VoterChangePassword.class));
                }
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