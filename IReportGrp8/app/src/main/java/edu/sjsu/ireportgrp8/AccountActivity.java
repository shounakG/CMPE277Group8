package edu.sjsu.ireportgrp8;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountActivity extends AppCompatActivity {
    private final String TAG = getClass().getName();
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mUserReference;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        mAuth = FirebaseAuth.getInstance();
        mUserReference = FirebaseDatabase.getInstance().getReference()
                .child(getString(R.string.key_public_users));
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(mAuth.getCurrentUser() == null){
                    Intent intent =new Intent(AccountActivity.this,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                else{
                    mUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(mAuth.getCurrentUser().getUid())){
                                Log.d(TAG,"User exists");
                            }
                            else{
                                Log.d(TAG,"User does not exists... Creating basic profile");
                                PublicUser newPublicUser = new PublicUser(mAuth.getCurrentUser().getEmail(),mAuth.getCurrentUser().getEmail(),null,null,null,null,null,null);
                                mDatabase.child(getString(R.string.key_public_users)).child(mAuth.getCurrentUser().getUid()).setValue(newPublicUser);

                                UserSettingsData newUserSettings = new UserSettingsData(true,true,false);
                                //replace screen name with User UUID
                                mDatabase.child(getString(R.string.key_public_user_settings)).child(mAuth.getCurrentUser().getUid()).setValue(newUserSettings);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d(TAG,"Error Occurred"+databaseError.getMessage());

                        }
                    });

                    if(mAuth.getCurrentUser().getEmail().contains("@gmail.com")){
                        Log.d(TAG,"GMAIL User... so he must be Admin");
                        //add code for Admin User HERE.
                    }
                    else {
                        Log.d(TAG,"Normal User...");
                    }
                }
            }

        };

        Button submitNewReport = (Button) findViewById(R.id.submit_new_report);
        submitNewReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startSubmitReport = new Intent(AccountActivity.this,ReportActivity.class);
                startActivity(startSubmitReport);
            }
        });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appmenu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.settings_page:
                launchSettings();
                return true;
            case R.id.profile_page:
                Intent regActivity = new Intent(getApplicationContext(),UserRegistration.class);
                startActivity(regActivity);
                return true;
            case R.id.logoutMenu:
                LoginManager.getInstance().logOut();
                mAuth.getInstance().signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void launchSettings(){
        Intent regActivity = new Intent(getApplicationContext(),UserSettings.class);
        startActivity(regActivity);

    }
}
