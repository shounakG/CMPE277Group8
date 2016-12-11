package edu.sjsu.ireportgrp8;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.google.android.gms.location.LocationListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by dmodh on 12/4/16.
 */

public class ReportDetailActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ActivityCompat.OnRequestPermissionsResultCallback,LocationListener {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseUser user;

    private String currentLat, currentLong, distance;
    private String reportid = null;
    private TextView tv_title_value;
    private TextView tv_location_value;
    private TextView tv_address_value;
    private TextView tv_datetime_value;
    private TextView tv_size_severity_value;
    private TextView tv_status_value;
    private Spinner spinnerStatus;
    private Button btn_chnage_status;

    private GoogleApiClient mGoogleApiClient;
    private View baseLayout;
    private final int REQUEST_LOCATION = 0;
    LocationRequest mLocationRequest;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view_report);

        baseLayout = findViewById(R.id.detailReportScrollView);
        reportid = this.getIntent().getExtras().getString("reportid");

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            //finish();
            //startActivity(new Intent(this, FirebaseAuthDemoLogin.class));
        }
        user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        tv_title_value = (TextView) findViewById(R.id.tv_title_value);
        tv_location_value = (TextView) findViewById(R.id.tv_location_value);
        tv_address_value = (TextView) findViewById(R.id.tv_address_value);
        tv_datetime_value = (TextView) findViewById(R.id.tv_datetime_value);
        tv_size_severity_value = (TextView) findViewById(R.id.tv_size_severity_value);
        tv_status_value = (TextView) findViewById(R.id.tv_status_value);
        btn_chnage_status = (Button) findViewById(R.id.btn_chnage_status);
        spinnerStatus = (Spinner) findViewById(R.id.spinnerStatus);
        buildGoogleApiClient();
        createLocationRequest();
        calculateDistance();
        fetchReportDetails(reportid);
    }

    private void calculateDistance() {

    }


    private void fetchReportDetails(String reportid) {
        final DatabaseReference ref = databaseReference.child("userreports").child(user.getUid()).child(reportid);
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        ArrayList<String> imageList = new ArrayList<String>();
                        final String reportid = dataSnapshot.child("reportId").getValue().toString();
                        String title = dataSnapshot.child("title").getValue().toString();
                        String location = dataSnapshot.child("latitude").getValue() + ", " + dataSnapshot.child("longitude").getValue();
                        String address = dataSnapshot.child("address").getValue().toString();
                        String datetime = dataSnapshot.child("datetime").getValue().toString();
                        String size_severity = dataSnapshot.child("size").getValue() + " & " + dataSnapshot.child("severity").getValue();
                        final String status = dataSnapshot.child("status").getValue().toString();
                        btn_chnage_status.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                databaseReference = FirebaseDatabase.getInstance().getReference();
                                final DatabaseReference ref = databaseReference.child("userreports").child(user.getUid()).child(reportid);
                                String selectedItem = spinnerStatus.getSelectedItem().toString();
                                switch (selectedItem) {
                                    case "Still There":
                                        ref.child("status").setValue("Still There");
                                        tv_status_value.setText("Still There");
                                        Toast.makeText(getApplicationContext(), "Status Changed...", Toast.LENGTH_SHORT).show();
                                        break;
                                    case "Removal Confirmed":
                                        ref.child("status").setValue("Removal Confirmed");
                                        tv_status_value.setText("Removal Confirmed");
                                        Toast.makeText(getApplicationContext(), "Status Changed...", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }
                        });
                        for(DataSnapshot image : dataSnapshot.child("images").getChildren()) {
                            imageList.add(image.getValue().toString());
                        }
                        tv_title_value.setText(title);
                        tv_location_value.setText(location);
                        tv_address_value.setText(address);
                        tv_datetime_value.setText(datetime);
                        tv_size_severity_value.setText(size_severity);
                        tv_status_value.setText(status);
                        LinearLayout layout = (LinearLayout)findViewById(R.id.imageLayout);
                        for(String image : imageList)
                        {
                            final ImageView iv = new ImageView(getApplicationContext());
                            iv.setLayoutParams(new android.view.ViewGroup.LayoutParams(500,400));
                            iv.setMaxHeight(20);
                            iv.setMaxWidth(20);
                            layout.addView(iv);
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageRef = storage.getReferenceFromUrl("gs://ireport-16f3e.appspot.com");
                            StorageReference imageRef = storageRef.child("reports").child(image);
                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Picasso.with(getApplicationContext()).load(uri).into(iv);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLat = location.getLatitude()+"";
        currentLong = location.getLongitude()+"";
        final DatabaseReference ref = databaseReference.child("userreports").child(user.getUid()).child(reportid);
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        try {
                            DistanceAsyncTask distanceAsyncTask = new DistanceAsyncTask();
                            distanceAsyncTask.execute("https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + currentLat +
                                    "," + currentLong +
                                    "&destinations=" + dataSnapshot.child("latitude").getValue() +
                                    "," + dataSnapshot.child("longitude").getValue() +
                                    "&mode=walking&units=imperial&key=AIzaSyDm7GmsBE4SIJojzIizbZ7sSEn_yqA0LyU").get();
                        } catch (InterruptedException ie) {
                            ie.printStackTrace();
                        } catch (ExecutionException ee) {
                            ee.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
            return;
        } else {
        }
        startLocationUpdates();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(10000);
//        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
            return;
        }
        else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(this, "App need needs to access location for navigation", Toast.LENGTH_SHORT).show();
            Snackbar.make(baseLayout, "App need needs to access location for navigation",
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(ReportDetailActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_LOCATION);
                        }
                    })
                    .show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    public class DistanceAsyncTask extends AsyncTask<String,Void,String> implements DialogInterface.OnCancelListener {
        private Dialog dialog = null;
        @Override
        protected void onPreExecute() {
            dialog = new Dialog(ReportDetailActivity.this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try{
                URL newURL = new URL(strings[0]);
                System.out.println("URL = " +newURL);
                HttpURLConnection connection = (HttpURLConnection)newURL.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoOutput(true);
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuffer json = new StringBuffer(1024);
                String tmp="";
                while((tmp=reader.readLine())!=null)
                    json.append(tmp).append("\n");
                reader.close();
                dialog.dismiss();
                return json.toString();
            } catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                if(s!=null) {
                    JSONObject distanceObject = new JSONObject(s);
                    JSONObject jsonObj = (JSONObject) distanceObject.getJSONArray("rows").get(0);
                    JSONObject jsonElements = (JSONObject) jsonObj.getJSONArray("elements").get(0);
                    distance = jsonElements.getJSONObject("distance").getString("text");
                    String[] splitArray = distance.split("\\s+");
                    if(splitArray[1].equals("mi")){
                        double distanceMiles = Double.parseDouble(splitArray[0]);
                        if(distanceMiles > 0.00568182) {
                            btn_chnage_status.setVisibility(View.GONE);
                            Toast.makeText(ReportDetailActivity.this, "Out of range...", Toast.LENGTH_SHORT).show();
                        }
                    } else if(splitArray[1].equals("ft")) {
                        double distanceFeet = Double.parseDouble(splitArray[0]);
                        if(distanceFeet > 30) {
                            btn_chnage_status.setVisibility(View.GONE);
                            Toast.makeText(ReportDetailActivity.this, "Out of range...", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(),"Unable to contact Google Api", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            cancel(true);
        }
    }
}
