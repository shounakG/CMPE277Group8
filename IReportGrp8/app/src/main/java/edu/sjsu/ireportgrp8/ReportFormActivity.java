package edu.sjsu.ireportgrp8;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;


import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;


import javax.ws.rs.core.MediaType;

import static edu.sjsu.ireportgrp8.R.mipmap.report;

/**
 * Created by Shounak on 12/1/2016.
 */

public class ReportFormActivity extends AppCompatActivity {
    private Button mSubmitButton;
    private RadioGroup mSizeRadioGroup;
    private RadioGroup mSeverityRadioGroup;
    private EditText mdescriptionEditText;
    private EditText edt_title;
    public static List<String> currentImageList = new ArrayList<String>();
    public static File[] photoFileList;
    final ReportFormActivity ra = this;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserReference;

    private String screenName;
    private Boolean anonymous,reportconf;

    LocationManager mLocationManager;

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            //Toast.makeText(getActivity().getApplicationContext(), "Location changed, " + location.getAccuracy() + " , " + location.getLatitude()+ "," + location.getLongitude(), Toast.LENGTH_LONG).show();
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER.toString(), 1, 1, mLocationListener);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //Toast.makeText(getActivity().getApplicationContext(), status, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    private Location getLastKnownLocation() {
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        if(providers.size()!=0){
            for (String provider : providers) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    ActivityCompat.requestPermissions(ra,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    ActivityCompat.requestPermissions(ra,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                }else{

                }
                Location l = mLocationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    // Found best last known location: %s", l);
                    bestLocation = l;
                }
            }
        }else{
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                ActivityCompat.requestPermissions(ra,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                ActivityCompat.requestPermissions(ra,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                return getLastKnownLocation();
            }else{
                return null;
            }
        }

        return bestLocation;
    }




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentImageList=new ArrayList<>();
        setContentView(R.layout.activity_report_form);
        mSubmitButton = (Button) findViewById(R.id.report_button);
        mSizeRadioGroup = (RadioGroup) findViewById(R.id.radio_group_size);
        mSeverityRadioGroup = (RadioGroup) findViewById(R.id.radio_group_severity);
        mdescriptionEditText = (EditText) findViewById(R.id.litter_description_a);
        edt_title = (EditText) findViewById(R.id.litter_title);

        mAuth = FirebaseAuth.getInstance();
        mUserReference = FirebaseDatabase.getInstance().getReference();
        mUserReference.child(getString(R.string.key_public_user_settings)).child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserSettingsData userSettingsData = dataSnapshot.getValue(UserSettingsData.class);
                anonymous = userSettingsData.getAnonymous();
                reportconf = userSettingsData.getReportConf();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mUserReference.child(getString(R.string.key_public_users)).child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                PublicUser userProfileData = dataSnapshot.getValue(PublicUser.class);
                screenName = userProfileData.getScreenName();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (mAuth.getCurrentUser() == null) {
                    Intent intent = new Intent(ReportFormActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }};

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Report r = new Report();
                DateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
                dateFormatter.setLenient(false);
                Date today = new Date();
                String s = dateFormatter.format(today);
                r.setDatetime(s);
                r.setDescription(mdescriptionEditText.getText().toString());
                r.setStatus("Still There");
                r.setEmail(mAuth.getCurrentUser().getEmail());
                r.setAnnonymous(anonymous);
                r.setScreenname(screenName);
                r.setTitle(edt_title.getText().toString());

                RadioButton selectedRadioButton = (RadioButton) mSeverityRadioGroup.findViewById(mSeverityRadioGroup.getCheckedRadioButtonId());
                r.setSeverity(selectedRadioButton.getText().toString());

                selectedRadioButton = (RadioButton) mSizeRadioGroup.findViewById(mSizeRadioGroup.getCheckedRadioButtonId());
                r.setSize(selectedRadioButton.getText().toString());
                List<String> imageList = new ArrayList<String>();

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://ireport-16f3e.appspot.com/");
                //StorageReference imageRef = storageRef.child(photoURI.toString());

                if(photoFileList!=null){
                    for (int i = 0; i < photoFileList.length; i++) {
                        if (photoFileList[i]!=null){
                            final Uri file = Uri.fromFile(new File(photoFileList[i].toString()));
                            StorageReference Ref = storageRef.child("reports/"+file.getLastPathSegment());
                            UploadTask uploadTask = Ref.putFile(file);
                            String slpg = file.getLastPathSegment();
                            ReportFormActivity.currentImageList.add(slpg);

                            // Register observers to listen for when the download is done or if it fails
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle unsuccessful uploads
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.

                                    //Uri downloadUrl = taskSnapshot.getDownloadUrl();

                                    //Intent i = new Intent(ReportActivity.this, ReportFormActivity.class);
                                    // startActivity(i);
                                }
                            });

                            // Observe state change events such as progress, pause, and resume
                            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                    System.out.println("Upload is " + progress + "% done");
                                }
                            }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                                    System.out.println("Upload is paused");
                                }
                            });
                        }
                    }

                }else{
                    ReportFormActivity.currentImageList.add("no-image.jpg");
                }




                DatabaseReference mDatabase;
                mDatabase = FirebaseDatabase.getInstance().getReference();

                UUID randomReportId = UUID.randomUUID();
                //FirebaseUser user = firebaseAuth.getCurrentUser();
                //databaseReference.child("userreports").child(user.getUid()).child(randomReportId.toString()).setValue(report);

                //LocationManager mLocationManager;
                Location myLocation = getLastKnownLocation();

                if(myLocation!=null){
                    r.setLatitude(String.valueOf(myLocation.getLatitude()));
                    r.setLongitude(String.valueOf(myLocation.getLongitude()));

                    Geocoder geocoder;
                    List<Address> Addresses=null;
                    geocoder = new Geocoder(ra, Locale.getDefault());
                    try {
                        Addresses= geocoder.getFromLocation(myLocation.getLatitude(), myLocation.getLongitude(), 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (Addresses.size() > 0)
                    {
                        String yourAddress = Addresses.get(0).getAddressLine(0);
                        String yourCity = Addresses.get(0).getAddressLine(1);
                        String yourCountry = Addresses.get(0).getAddressLine(2);
                        r.setAddress(yourAddress);
                    }
                }

                UUID reportuuid = UUID.randomUUID();
                r.setReportId(reportuuid.toString());
                imageList=currentImageList;
                r.setImages(imageList);
                mDatabase.child("userreports").child(mAuth.getCurrentUser().getUid()).child(reportuuid.toString()).setValue(r);



                Toast.makeText(ra,"Information saved...", Toast.LENGTH_SHORT).show();
                if(reportconf){
                    new SendEmailAsyncTask().execute(mAuth.getCurrentUser().getEmail(),edt_title.getText().toString());
                }
                NavUtils.navigateUpFromSameTask(ReportFormActivity.this);
                return;
            }
        });
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (mAuth.getCurrentUser() == null) {
                    Intent intent = new Intent(ReportFormActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }};


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
    public class SendEmailAsyncTask extends AsyncTask<String,Void,Void>
    {
        @Override
        protected Void doInBackground(String... strings) {
            Client client = Client.create();
            client.addFilter(new HTTPBasicAuthFilter("api",
                    "key-48171d3fd8035e9e3a21881aaade014a"));
            WebResource webResource =
                    client.resource("https://api.mailgun.net/v3/mail.pruthvi-nadunooru.name" +
                            "/messages");
            MultivaluedMapImpl formData = new MultivaluedMapImpl();
            formData.add("from", "CMPE 277 Grp 8 <akshay@mail.pruthvi-nadunooru.name>");
            formData.add("to", strings[0]);
            formData.add("subject", "Report Submitted");
            formData.add("text", "Your Report "+strings[1]+" has been submitted");
            webResource.type(MediaType.APPLICATION_FORM_URLENCODED).
                    post(ClientResponse.class, formData);

            return null;
        }

    }

}
