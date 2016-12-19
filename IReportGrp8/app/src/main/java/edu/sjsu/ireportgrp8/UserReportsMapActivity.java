package edu.sjsu.ireportgrp8;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.PersistableBundle;

import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

public class UserReportsMapActivity
        extends AppCompatActivity
        implements OnMapReadyCallback {

    private static final String RECYCLER_VIEW = "RECYCLER_VIEW_MARKER";
    private static final String FORM_VIEW = "FORM_VIEW_MARKER";
    public static UserReportsMapActivity me;

    private MapView mapView;
    private List<Report> reports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_map_view);

        reports = (ArrayList<Report>) getIntent().getExtras().getSerializable("reports");

        me = this;

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        for(Report report : reports) {
            String latitude = report.getLatitude();
            String longitude = report.getLongitude();
            LatLng loc = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
            googleMap.addMarker(new MarkerOptions()
                    .position(loc)
                    .snippet(report.getReportId())
                    .title(report.getTitle())
                    .icon(BitmapDescriptorFactory .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            ).setTitle(report.getTitle());
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 14));
        }

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                String reportid = marker.getSnippet();
                Intent detailIntent = new Intent(UserReportsMapActivity.this, ReportDetailActivity.class);
                detailIntent.putExtra("reportid", reportid);
                startActivity(detailIntent);

                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, ReportsListView.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}