package edu.sjsu.ireportgrp8;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class DetailedReportActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public static DetailedReportActivity me;

    private ResidentReport residentReport;

    private ImageView complaintPictureImgView;
    private Spinner statusSpinner;
    private TextView sizeOfGarbageTextView;
    private TextView severityTextView;
    private TextView descTextView;
    private Button submitButton;

    private DatabaseReference mDatabase;

    private GPSTracker gpsTracker;

    private boolean isInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_report);

        me = this;

        gpsTracker = new GPSTracker(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        residentReport = (ResidentReport) bundle.getSerializable("reportObj");

        complaintPictureImgView = (ImageView) findViewById(R.id.complaintPicture);

        statusSpinner = (Spinner) findViewById(R.id.statusSpinner);
        statusSpinner.setOnItemSelectedListener(this);

        Glide.with(this)
                .load(residentReport.getImage().get(0))
                .into(complaintPictureImgView);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        sizeOfGarbageTextView = (TextView) findViewById(R.id.sizeOfGarbage);
        sizeOfGarbageTextView.setText(sizeOfGarbageTextView.getText() + residentReport.getSize());

        severityTextView = (TextView) findViewById(R.id.severityLevelTextView);
        severityTextView.setText(severityTextView.getText() + residentReport.getSeverity_Level());

        descTextView = (TextView) findViewById(R.id.descTextView);
        descTextView.setText(residentReport.getDescription());

        submitButton = (Button) findViewById(R.id.submitButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child("userreports").child(residentReport.getUserId()).child(residentReport.getReportId()).child("status").setValue(statusSpinner.getSelectedItem());
                Toast.makeText(DetailedReportActivity.this, "Status Updated!", Toast.LENGTH_SHORT).show();
            }
        });

        if (residentReport.getStatus().contains("Removal Confirmed")) {
            List<String> list = new ArrayList<String>();
            list.add("Removal Confirmed");
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, list);

            // attaching data adapter to spinner
            statusSpinner.setAdapter(adapter);
            statusSpinner.setEnabled(false);

            submitButton.setEnabled(false);
        } else {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.official_status_changes, android.R.layout.simple_spinner_dropdown_item);

            // attaching data adapter to spinner
            statusSpinner.setAdapter(adapter);

            String[] statusArray = getResources().getStringArray(R.array.official_status_changes);
            for (int index = 0; index < statusArray.length; index++) {
                if (residentReport.getStatus().equalsIgnoreCase(statusArray[index])) {
                    statusSpinner.setSelection(index, false);
                }
            }
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (!isInitialized) {
            isInitialized = true;

            return;
        }
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_SHORT).show();
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.report_navigation_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Location location = gpsTracker.getLocation();

        String currLocation = location.getLatitude() + "," + location.getLongitude();

        switch (item.getItemId()) {
            case R.id.start_navigation:
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr=" + currLocation + "&daddr=" + residentReport.getLocation()));

                startActivity(intent);
                return true;
            case android.R.id.home:
                finish();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}