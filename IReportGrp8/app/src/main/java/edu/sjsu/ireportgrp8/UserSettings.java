package edu.sjsu.ireportgrp8;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserSettings extends AppCompatActivity {

    public final String TAG = getClass().getName();

    private DatabaseReference mUserSettingsReference;
    SwitchCompat reportConf,statusConf,anonymousSetting;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (mAuth.getCurrentUser() == null) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                else{
                    //get UUID of the authenticated user
                    String userUUID = mAuth.getCurrentUser().getUid();

                    mUserSettingsReference = FirebaseDatabase.getInstance().getReference()
                            .child(getString(R.string.key_public_user_settings)).child(userUUID);
                    setupScreen();
                }
            }};

        reportConf = (SwitchCompat) findViewById(R.id.switch_rpt_conf);
        statusConf = (SwitchCompat) findViewById(R.id.switch_status_conf);
        anonymousSetting = (SwitchCompat) findViewById(R.id.switch_anonymous);
        anonymousSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(anonymousSetting.isChecked()){
                    statusConf.setChecked(false);
                    reportConf.setChecked(false);
                }
            }
        });
        


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

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }

}
