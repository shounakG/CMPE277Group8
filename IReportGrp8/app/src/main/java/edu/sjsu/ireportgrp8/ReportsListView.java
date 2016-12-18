package edu.sjsu.ireportgrp8;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dmodh on 12/1/16.
 */

public class ReportsListView extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private ArrayList<Report> reportList = new ArrayList<Report>();
    private ListView reportListView;
    private SearchView searchView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view_reports);
        reportListView = (ListView) findViewById(R.id.report_list_view);

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            //finish();
            //startActivity(new Intent(this, FirebaseAuthDemoLogin.class));
        }

        this.readDataFromFirebase();
    }

    private void getUpdates(DataSnapshot data) {
        for(DataSnapshot ds : data.getChildren()) {
            List<String> imageList = new ArrayList<String>();
            Report report = new Report();
            report.setReportId(ds.child("reportId").getValue().toString());
            if(ds.child("address").getValue()!=null){
                report.setAddress(ds.child("address").getValue().toString());
            }


            report.setDescription(ds.child("description").getValue().toString());
            report.setStatus(ds.child("status").getValue().toString());
            report.setSize(ds.child("size").getValue().toString());
            report.setSeverity(ds.child("severity").getValue().toString());
            report.setDatetime(ds.child("datetime").getValue().toString());
            if(ds.child("longitude").getValue()!=null){
                report.setLongitude(ds.child("longitude").getValue().toString());
            }
            if(ds.child("latitude").getValue()!=null){
                report.setLatitude(ds.child("latitude").getValue().toString());
            }

            report.setEmail(ds.child("email").getValue().toString());
            report.setScreenname(ds.child("screenname").getValue().toString());
            for(DataSnapshot image : ds.child("images").getChildren()) {
                imageList.add(image.getValue().toString());
            }
            report.setImages(imageList);
            reportList.add(report);
        }
        ReportAdapter adapter = new ReportAdapter(this, reportList);
        reportListView.setAdapter(adapter);
        final Context context = this;
        reportListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Report selectedReport = reportList.get(position);
                Intent detailIntent = new Intent(context, ReportDetailActivity.class);
                detailIntent.putExtra("reportid", selectedReport.getReportId());
                startActivity(detailIntent);
            }
        });
    }

    private void readDataFromFirebase() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference ref = databaseReference.child("userreports").child(user.getUid());
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        getUpdates(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.main_menu, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return super.onCreateOptionsMenu(menu);
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mapView_menu: {
                Bundle bundle = new Bundle();
                bundle.putSerializable("reports", reportList);
                Intent intent = new Intent(ReportsListView.this, UserReportsMapActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
            return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
