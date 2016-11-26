package edu.sjsu.ireportgrp8;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserSettings extends AppCompatActivity {

    public final String TAG = getClass().getName();

    private DatabaseReference mUserSettingsReference;
    SwitchCompat reportConf,statusConf,anonymousSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        reportConf = (SwitchCompat) findViewById(R.id.switch_rpt_conf);
        statusConf = (SwitchCompat) findViewById(R.id.switch_status_conf);
        anonymousSetting = (SwitchCompat) findViewById(R.id.switch_anonymous);

        //get UUID of the authenticated user
        String userUUID = "mathura";

        mUserSettingsReference = FirebaseDatabase.getInstance().getReference()
                .child(getString(R.string.key_public_user_settings)).child(userUUID);
        setupScreen();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNewSettings();
            }
        });
    }
    public void saveNewSettings(){

        UserSettingsData newUserSettingsData = new UserSettingsData(reportConf.isChecked(),statusConf.isChecked(),anonymousSetting.isChecked());

        mUserSettingsReference.setValue(newUserSettingsData);

        Toast.makeText(this,getString(R.string.toast_settings_updated),Toast.LENGTH_LONG).show();

    }

    public void setupScreen(){

        ValueEventListener userSettingsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get UserSettingsData object and use the values to update the UI
                UserSettingsData userSettingsData = dataSnapshot.getValue(UserSettingsData.class);

                reportConf.setChecked(userSettingsData.getReportConf());
                statusConf.setChecked(userSettingsData.getStatusConf());
                anonymousSetting.setChecked(userSettingsData.getAnonymous());


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.d(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };

        mUserSettingsReference.addListenerForSingleValueEvent(userSettingsListener);

    }

}
