package com.example.onlinevotingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class VoteResults extends AppCompatActivity {

    PieChart chairperson, viceChairperson, secretary, treasurer, auditor, fourthYearRepresentative, thirdYearRepresentative, secondYearRepresentative, firstYearRepresentative;

    CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("Candidates");

    TextView electionResults;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_results);

        //TextView init
        electionResults = findViewById(R.id.electionResultsButton);

        //PieCharts init
        chairperson = findViewById(R.id.chairperson);
        viceChairperson = findViewById(R.id.viceChairperson);
        secretary = findViewById(R.id.secretary);
        treasurer = findViewById(R.id.treasurer);
        auditor = findViewById(R.id.auditor);
        fourthYearRepresentative = findViewById(R.id.fourthYear);
        thirdYearRepresentative = findViewById(R.id.thirdYear);
        secondYearRepresentative = findViewById(R.id.secondYear);
        firstYearRepresentative = findViewById(R.id.firstYear);

        //Initialization for the Back Button
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Vote Results");
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_power_settings_new_24);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Getting Candidate Data per Position and insert it to the Pie Chart
        getDataChairperson();
        getDataViceChairperson();
        getDataSecretary();
        getDataTreasurer();
        getDataAuditor();
        getDataFourth();
        getDataThird();
        getDataSecond();
        getDataFirst();

        //Election Results Button on Tap
        electionResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ElectionResults.class));
            }
        });
    }

    private void getDataFirst() {
        collectionReference.orderBy("candidateFullName", Query.Direction.ASCENDING).whereEqualTo("posOrder", 9)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        List<DocumentSnapshot> documentSnapshots = value.getDocuments();
                        ArrayList<PieEntry> votes = new ArrayList<>();
                        for(DocumentSnapshot documentSnapshot : documentSnapshots) {
                            String name = documentSnapshot.getString("candidateFullName");
                            int voteCount = documentSnapshot.getDouble("voteCount").intValue();
                            votes.add(new PieEntry(voteCount, name));
                        }
                        firstPie(votes);
                    }
                });
    }

    private void getDataSecond() {
        collectionReference.orderBy("candidateFullName", Query.Direction.ASCENDING).whereEqualTo("posOrder", 8)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        List<DocumentSnapshot> documentSnapshots = value.getDocuments();
                        ArrayList<PieEntry> votes = new ArrayList<>();
                        for(DocumentSnapshot documentSnapshot : documentSnapshots) {
                            String name = documentSnapshot.getString("candidateFullName");
                            int voteCount = documentSnapshot.getDouble("voteCount").intValue();
                            votes.add(new PieEntry(voteCount, name));
                        }
                        secondPie(votes);
                    }
                });
    }

    private void getDataThird() {
        collectionReference.orderBy("candidateFullName", Query.Direction.ASCENDING).whereEqualTo("posOrder", 7)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        List<DocumentSnapshot> documentSnapshots = value.getDocuments();
                        ArrayList<PieEntry> votes = new ArrayList<>();
                        for(DocumentSnapshot documentSnapshot : documentSnapshots) {
                            String name = documentSnapshot.getString("candidateFullName");
                            int voteCount = documentSnapshot.getDouble("voteCount").intValue();
                            votes.add(new PieEntry(voteCount, name));
                        }
                        thirdPie(votes);
                    }
                });
    }

    private void getDataFourth() {
        collectionReference.orderBy("candidateFullName", Query.Direction.ASCENDING).whereEqualTo("posOrder", 6)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        List<DocumentSnapshot> documentSnapshots = value.getDocuments();
                        ArrayList<PieEntry> votes = new ArrayList<>();
                        for(DocumentSnapshot documentSnapshot : documentSnapshots) {
                            String name = documentSnapshot.getString("candidateFullName");
                            int voteCount = documentSnapshot.getDouble("voteCount").intValue();
                            votes.add(new PieEntry(voteCount, name));
                        }
                        fourthPie(votes);
                    }
                });
    }

    private void getDataAuditor() {
        collectionReference.orderBy("candidateFullName", Query.Direction.ASCENDING).whereEqualTo("posOrder", 5)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        List<DocumentSnapshot> documentSnapshots = value.getDocuments();
                        ArrayList<PieEntry> votes = new ArrayList<>();
                        for(DocumentSnapshot documentSnapshot : documentSnapshots) {
                            String name = documentSnapshot.getString("candidateFullName");
                            int voteCount = documentSnapshot.getDouble("voteCount").intValue();
                            votes.add(new PieEntry(voteCount, name));
                        }
                        auditorPie(votes);
                    }
                });
    }

    private void getDataTreasurer() {
        collectionReference.orderBy("candidateFullName", Query.Direction.ASCENDING).whereEqualTo("posOrder", 4)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        List<DocumentSnapshot> documentSnapshots = value.getDocuments();
                        ArrayList<PieEntry> votes = new ArrayList<>();
                        for(DocumentSnapshot documentSnapshot : documentSnapshots) {
                            String name = documentSnapshot.getString("candidateFullName");
                            int voteCount = documentSnapshot.getDouble("voteCount").intValue();
                            votes.add(new PieEntry(voteCount, name));
                        }
                        treasurerPie(votes);
                    }
                });
    }

    private void getDataSecretary() {
        collectionReference.orderBy("candidateFullName", Query.Direction.ASCENDING).whereEqualTo("posOrder", 3)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        List<DocumentSnapshot> documentSnapshots = value.getDocuments();
                        ArrayList<PieEntry> votes = new ArrayList<>();
                        for(DocumentSnapshot documentSnapshot : documentSnapshots) {
                            String name = documentSnapshot.getString("candidateFullName");
                            int voteCount = documentSnapshot.getDouble("voteCount").intValue();
                            votes.add(new PieEntry(voteCount, name));
                        }
                        secretaryPie(votes);
                    }
                });
    }

    private void getDataViceChairperson() {
        collectionReference.orderBy("candidateFullName", Query.Direction.ASCENDING).whereEqualTo("posOrder", 2)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        List<DocumentSnapshot> documentSnapshots = value.getDocuments();
                        ArrayList<PieEntry> votes = new ArrayList<>();
                        for(DocumentSnapshot documentSnapshot : documentSnapshots) {
                            String name = documentSnapshot.getString("candidateFullName");
                            int voteCount = documentSnapshot.getDouble("voteCount").intValue();
                            votes.add(new PieEntry(voteCount, name));
                        }
                        viceChairpersonPie(votes);
                    }
                });
    }

    private void getDataChairperson() {
        collectionReference.orderBy("candidateFullName", Query.Direction.ASCENDING).whereEqualTo("posOrder", 1)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        List<DocumentSnapshot> documentSnapshots = value.getDocuments();
                        ArrayList<PieEntry> votes = new ArrayList<>();
                        for(DocumentSnapshot documentSnapshot : documentSnapshots) {
                            String name = documentSnapshot.getString("candidateFullName");
                            int voteCount = documentSnapshot.getDouble("voteCount").intValue();
                            votes.add(new PieEntry(voteCount, name));
                        }
                        chairpersonPie(votes);
                    }
                });
    }

    private void chairpersonPie(ArrayList<PieEntry> votes) {

        PieDataSet pieDataSet = new PieDataSet(votes, "Candidates");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter(chairperson));

        chairperson.setData(pieData);
        chairperson.setUsePercentValues(true);
        chairperson.getDescription().setEnabled(true);
        chairperson.setCenterText("Chairperson");
        chairperson.animate();
        chairperson.invalidate();
        chairperson.setEntryLabelColor(Color.BLACK);
        chairperson.getDescription().setEnabled(false);
        chairperson.setRotationAngle(0);
        chairperson.setHighlightPerTapEnabled(true);

        Legend legend = chairperson.getLegend();
        legend.setEnabled(true);
        legend.setTextColor(Color.WHITE);

        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setWordWrapEnabled(true);
        legend.setDrawInside(false);
    }

    private void viceChairpersonPie(ArrayList<PieEntry> votes) {

        PieDataSet pieDataSet = new PieDataSet(votes, "Candidates");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter(viceChairperson));

        viceChairperson.setData(pieData);
        viceChairperson.setUsePercentValues(true);
        viceChairperson.getDescription().setEnabled(true);
        viceChairperson.setCenterText("Vice\nChairperson");
        viceChairperson.animate();
        viceChairperson.invalidate();
        viceChairperson.setEntryLabelColor(Color.BLACK);
        viceChairperson.getDescription().setEnabled(false);
        viceChairperson.setRotationAngle(0);
        viceChairperson.setHighlightPerTapEnabled(true);

        Legend legend = viceChairperson.getLegend();
        legend.setEnabled(true);
        legend.setTextColor(Color.WHITE);

        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setWordWrapEnabled(true);
        legend.setDrawInside(false);
    }

    private void secretaryPie(ArrayList<PieEntry> votes) {

        PieDataSet pieDataSet = new PieDataSet(votes, "Candidates");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter(secretary));

        secretary.setData(pieData);
        secretary.setUsePercentValues(true);
        secretary.getDescription().setEnabled(true);
        secretary.setCenterText("Secretary");
        secretary.animate();
        secretary.invalidate();
        secretary.setEntryLabelColor(Color.BLACK);
        secretary.getDescription().setEnabled(false);
        secretary.setRotationAngle(0);
        secretary.setHighlightPerTapEnabled(true);

        Legend legend = secretary.getLegend();
        legend.setEnabled(true);
        legend.setTextColor(Color.WHITE);

        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setWordWrapEnabled(true);
        legend.setDrawInside(false);
    }

    private void treasurerPie(ArrayList<PieEntry> votes) {

        PieDataSet pieDataSet = new PieDataSet(votes, "Candidates");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter(treasurer));

        treasurer.setData(pieData);
        treasurer.setUsePercentValues(true);
        treasurer.getDescription().setEnabled(true);
        treasurer.setCenterText("Treasurer");
        treasurer.animate();
        treasurer.invalidate();
        treasurer.setEntryLabelColor(Color.BLACK);
        treasurer.getDescription().setEnabled(false);
        treasurer.setRotationAngle(0);
        treasurer.setHighlightPerTapEnabled(true);

        Legend legend = treasurer.getLegend();
        legend.setEnabled(true);
        legend.setTextColor(Color.WHITE);

        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setWordWrapEnabled(true);
        legend.setDrawInside(false);
    }

    private void auditorPie(ArrayList<PieEntry> votes) {

        PieDataSet pieDataSet = new PieDataSet(votes, "Candidates");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter(auditor));

        auditor.setData(pieData);
        auditor.setUsePercentValues(true);
        auditor.getDescription().setEnabled(true);
        auditor.setCenterText("Auditor");
        auditor.animate();
        auditor.invalidate();
        auditor.setEntryLabelColor(Color.BLACK);
        auditor.getDescription().setEnabled(false);
        auditor.setRotationAngle(0);
        auditor.setHighlightPerTapEnabled(true);

        Legend legend = auditor.getLegend();
        legend.setEnabled(true);
        legend.setTextColor(Color.WHITE);

        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setWordWrapEnabled(true);
        legend.setDrawInside(false);
    }

    private void fourthPie(ArrayList<PieEntry> votes) {

        PieDataSet pieDataSet = new PieDataSet(votes, "Candidates");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter(fourthYearRepresentative));

        fourthYearRepresentative.setData(pieData);
        fourthYearRepresentative.setUsePercentValues(true);
        fourthYearRepresentative.getDescription().setEnabled(true);
        fourthYearRepresentative.setCenterText("4th Year\nRepresentative");
        fourthYearRepresentative.animate();
        fourthYearRepresentative.invalidate();
        fourthYearRepresentative.setEntryLabelColor(Color.BLACK);
        fourthYearRepresentative.getDescription().setEnabled(false);
        fourthYearRepresentative.setRotationAngle(0);
        fourthYearRepresentative.setHighlightPerTapEnabled(true);

        Legend legend = fourthYearRepresentative.getLegend();
        legend.setEnabled(true);
        legend.setTextColor(Color.WHITE);

        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setWordWrapEnabled(true);
        legend.setDrawInside(false);
    }

    private void thirdPie(ArrayList<PieEntry> votes) {

        PieDataSet pieDataSet = new PieDataSet(votes, "Candidates");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter(thirdYearRepresentative));

        thirdYearRepresentative.setData(pieData);
        thirdYearRepresentative.setUsePercentValues(true);
        thirdYearRepresentative.getDescription().setEnabled(true);
        thirdYearRepresentative.setCenterText("3rd Year\nRepresentative");
        thirdYearRepresentative.animate();
        thirdYearRepresentative.invalidate();
        thirdYearRepresentative.setEntryLabelColor(Color.BLACK);
        thirdYearRepresentative.getDescription().setEnabled(false);
        thirdYearRepresentative.setRotationAngle(0);
        thirdYearRepresentative.setHighlightPerTapEnabled(true);

        Legend legend = thirdYearRepresentative.getLegend();
        legend.setEnabled(true);
        legend.setTextColor(Color.WHITE);

        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setWordWrapEnabled(true);
        legend.setDrawInside(false);
    }

    private void secondPie(ArrayList<PieEntry> votes) {

        PieDataSet pieDataSet = new PieDataSet(votes, "Candidates");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter(secondYearRepresentative));

        secondYearRepresentative.setData(pieData);
        secondYearRepresentative.setUsePercentValues(true);
        secondYearRepresentative.getDescription().setEnabled(true);
        secondYearRepresentative.setCenterText("2nd Year Representative");
        secondYearRepresentative.animate();
        secondYearRepresentative.invalidate();
        secondYearRepresentative.setEntryLabelColor(Color.BLACK);
        secondYearRepresentative.getDescription().setEnabled(false);
        secondYearRepresentative.setRotationAngle(0);
        secondYearRepresentative.setHighlightPerTapEnabled(true);

        Legend legend = secondYearRepresentative.getLegend();
        legend.setEnabled(true);
        legend.setTextColor(Color.WHITE);

        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setWordWrapEnabled(true);
        legend.setDrawInside(false);
    }

    private void firstPie(ArrayList<PieEntry> votes) {

        PieDataSet pieDataSet = new PieDataSet(votes, "Candidates");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter(firstYearRepresentative));

        firstYearRepresentative.setData(pieData);
        firstYearRepresentative.setUsePercentValues(true);
        firstYearRepresentative.getDescription().setEnabled(true);
        firstYearRepresentative.setCenterText("1st Year Representative");
        firstYearRepresentative.animate();
        firstYearRepresentative.invalidate();
        firstYearRepresentative.setEntryLabelColor(Color.BLACK);
        firstYearRepresentative.getDescription().setEnabled(false);
        firstYearRepresentative.setRotationAngle(0);
        firstYearRepresentative.setHighlightPerTapEnabled(true);

        Legend legend = firstYearRepresentative.getLegend();
        legend.setEnabled(true);
        legend.setTextColor(Color.WHITE);

        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setWordWrapEnabled(true);
        legend.setDrawInside(false);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mAuth.signOut();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            Toast.makeText(getApplicationContext(), "Logged Out", Toast.LENGTH_SHORT).show();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}