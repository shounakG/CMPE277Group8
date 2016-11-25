package edu.sjsu.ireportgrp8;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserRegistration extends AppCompatActivity {

    private DatabaseReference mDatabase;

    EditText tv_ScreenName,tv_FirstName,tv_LastName,tv_StreetName,tv_AptNo,tv_CityName,tv_ZipCode;
    Button registrationSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        registrationSubmit = (Button) findViewById(R.id.registration_submit);
        registrationSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitData();
            }
        });
        tv_AptNo = (EditText) findViewById(R.id.apt_number);
        tv_FirstName = (EditText) findViewById(R.id.first_name);
        tv_LastName = (EditText) findViewById(R.id.last_name);
        tv_StreetName = (EditText) findViewById(R.id.street_name);
        tv_ScreenName = (EditText) findViewById(R.id.screen_name);
        tv_CityName = (EditText) findViewById(R.id.city_name);
        tv_ZipCode = (EditText) findViewById(R.id.zipcode);



    }

    public void submitData(){
        String screenName = tv_ScreenName.getText().toString();
        String firstName = tv_FirstName.getText().toString();
        String lastName = tv_LastName.getText().toString();
        String streetName = tv_StreetName.getText().toString();
        String aptNo = tv_AptNo.getText().toString();
        String cityName = tv_CityName.getText().toString();
        String zipcode = tv_ZipCode.getText().toString();

        //Replace hard coded email address with user's email address
        PublicUser newPublicUser = new PublicUser("abc@xyz.com",screenName,firstName,lastName,streetName,aptNo,cityName,zipcode);

        //replace screen name with User UUID
        mDatabase.child("publicusers").child(screenName).setValue(newPublicUser);
    }
}
