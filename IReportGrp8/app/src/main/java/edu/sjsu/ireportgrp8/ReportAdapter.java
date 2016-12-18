package edu.sjsu.ireportgrp8;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by dmodh on 12/4/16.
 */

public class ReportAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Report> mDataSource;

    public ReportAdapter(Context context, ArrayList<Report> reports) {
        mContext = context;
        mDataSource = reports;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mDataSource.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataSource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Report report = (Report) getItem(position);
        ViewHolder holder;
        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_report, parent, false);
            holder = new ViewHolder();
            holder.report_list_status = (TextView) convertView.findViewById(R.id.report_list_status);
            holder.report_list_datetime = (TextView) convertView.findViewById(R.id.report_list_datetime);
            holder.thumbnailImageView = (ImageView) convertView.findViewById(R.id.report_list_thumbnail);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        TextView report_list_status = holder.report_list_status;
        TextView report_list_datetime = holder.report_list_datetime;
        final ImageView thumbnailImageView = holder.thumbnailImageView;
        report_list_status.setText(report.getStatus());
        report_list_datetime.setText(report.getDatetime());
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://ireport-16f3e.appspot.com");
        if(report.getImages()!=null){
            StorageReference imageRef = storageRef.child("reports").child(report.getImages().get(0));
            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.with(mContext).load(uri).placeholder(R.mipmap.ic_launcher).into(thumbnailImageView);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        }


        return convertView;
    }

    private static class ViewHolder {
        public TextView report_list_status;
        public TextView report_list_datetime;
        public ImageView thumbnailImageView;
    }
}
