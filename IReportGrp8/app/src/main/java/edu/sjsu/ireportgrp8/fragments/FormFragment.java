package edu.sjsu.ireportgrp8.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.sjsu.ireportgrp8.DetailedReportActivity;
import edu.sjsu.ireportgrp8.MapActivity;
import edu.sjsu.ireportgrp8.R;
import edu.sjsu.ireportgrp8.ResidentReport;

public class FormFragment extends Fragment {
    private volatile ResidentReport residentReport;

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.info_window_form_fragment, container, false);

        TextView titleTextView = (TextView) view.findViewById(R.id.mapCustomDialogTitle);
        titleTextView.setText(residentReport.getTitle());

        TextView descTextView = (TextView) view.findViewById(R.id.mapCustomDialogDesc);
        descTextView.setText(residentReport.getDescription());

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.mapCustomDialogbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.me, DetailedReportActivity.class);

                Bundle bundle = new Bundle();
                bundle.putSerializable("reportObj", residentReport);
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });
    }

    public void setResidentReport(ResidentReport residentReport) {
        this.residentReport = residentReport;
    }
}
