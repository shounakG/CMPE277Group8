package edu.sjsu.ireportgrp8;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button reg = (Button) findViewById(R.id.registerbutton);
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent regActivity = new Intent(getApplicationContext(),UserRegistration.class);
                startActivity(regActivity);
            }
        });
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void launchSettings(){
        Intent regActivity = new Intent(getApplicationContext(),UserSettings.class);
        startActivity(regActivity);

    }
}
