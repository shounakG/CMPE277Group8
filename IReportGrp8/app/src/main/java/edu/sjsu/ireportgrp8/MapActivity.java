package edu.sjsu.ireportgrp8;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import edu.sjsu.ireportgrp8.fragments.FormFragment;
import edu.sjsu.ireportgrp8.fragments.RecyclerViewFragment;
import edu.sjsu.ireportgrp8.interactiveWindow.InfoWindow;
import edu.sjsu.ireportgrp8.interactiveWindow.InfoWindowManager;
import edu.sjsu.ireportgrp8.interactiveWindow.customview.TouchInterceptFrameLayout;

public class MapActivity
        extends FragmentActivity
        implements OnMapReadyCallback {

    private static final String RECYCLER_VIEW = "RECYCLER_VIEW_MARKER";
    private static final String FORM_VIEW = "FORM_VIEW_MARKER";
    public static MapActivity me;

    private MapView mapView;

    private InfoWindowManager infoWindowManager;
    private ArrayList<ResidentReport> residentReports = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);

        me = this;

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(this);

        infoWindowManager = new InfoWindowManager(getSupportFragmentManager());
        infoWindowManager.onParentViewCreated(
                (TouchInterceptFrameLayout) findViewById(R.id.mapViewContainer), savedInstanceState);

        residentReports = (ArrayList<ResidentReport>) getIntent().getExtras().getSerializable("residentReports");
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
        infoWindowManager.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        infoWindowManager.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        infoWindowManager.onMapReady(googleMap);

        for (ResidentReport report : residentReports) {
            String latLong = report.getLocation();
            String[] latLongStr = latLong.split(",");

            LatLng loc = new LatLng(Double.parseDouble(latLongStr[0]), Double.parseDouble(latLongStr[1]));
            googleMap.addMarker(new MarkerOptions().position(loc).snippet(FORM_VIEW)).setTitle(report.getTitle());
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 14));
        }

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                final InfoWindow.MarkerSpecification markerSpec =
                        new InfoWindow.MarkerSpecification(20, 90);

                Fragment fragment = null;

                switch (marker.getSnippet()) {
                    case RECYCLER_VIEW: {
                        fragment = new RecyclerViewFragment();
                    }
                        break;
                    case FORM_VIEW: {
                        for (ResidentReport report : residentReports) {
                            if (report.getTitle().equals(marker.getTitle())) {
                                fragment = new FormFragment();
                                ((FormFragment) fragment).setResidentReport(report);
                            }
                        }
                    }

                        break;
                }

                if (fragment != null) {
                    final InfoWindow infoWindow = new InfoWindow(marker, markerSpec, fragment);
                    infoWindowManager.toggle(infoWindow, true);
                }

                return true;
            }
        });
    }
}
