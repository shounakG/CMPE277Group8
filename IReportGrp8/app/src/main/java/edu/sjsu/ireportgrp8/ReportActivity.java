package edu.sjsu.ireportgrp8;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import edu.sjsu.ireportgrp8.Interfaces.IimageElementClicked;
import edu.sjsu.ireportgrp8.fragments.ImageElementFragment;

public class ReportActivity extends AppCompatActivity implements IimageElementClicked{
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    ImageView mImageView;
    String mCurrentPhotoPath;
    Uri photoURI;
    File photoFile;
    private Button mCameraButton;
    private Button mReportButton;
    private FrameLayout  mImageStrip;
    /*FragmentManager fm;
    FragmentTransaction ft;*/
    int i = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        i =1;
        if (ReportFormActivity.photoFileList!=null) {
            ReportFormActivity.photoFileList = null;
        }


        mCameraButton = (Button)findViewById(R.id.pic_button);
        mCameraButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                dispatchTakePictureIntent();
            }
        });

        mReportButton = (Button)findViewById(R.id.report_button);
        mReportButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(ReportActivity.this, ReportFormActivity.class);
                startActivity(i);
            }
        });
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.mydomain.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //mImageStrip = (FrameLayout)findViewById(R.id.tl_img_strip);
        TableRow row=new TableRow(this.getApplicationContext());
        FrameLayout f = new FrameLayout(this);
        int tempID;
        tempID=f.generateViewId();
        //row.addView(f);
        //mImageStrip.addView(row);


        FragmentManager fm;
        FragmentTransaction ft;
        fm = getSupportFragmentManager();
        Fragment fragmentimg = fm.findFragmentById(R.id.Fragment_Container_ImageStrip);




        if (fragmentimg == null) {
            fragmentimg = new ImageElementFragment();
            Bundle bundle=new Bundle();
            bundle.putString("id",Integer.toString(i));
            bundle.putParcelable("img",photoURI);
            fragmentimg.setArguments(bundle);
            int id = i;
            switch(id){
                case 1 : id = R.id.image1;
                        break;
                case 2 :id = R.id.image12;
                        break;
                case 3 :id = R.id.image13;
                        break;
                default : id = R.id.image13;
                            break;
            }
            if (ReportFormActivity.photoFileList==null){
            //||ReportFormActivity.photoFileList.get(i).equals(null)){
                ReportFormActivity.photoFileList = new File[3];
                ReportFormActivity.photoFileList[0]=(photoFile);
            }else if(i<3 && ReportFormActivity.photoFileList[i-1]==null){
                ReportFormActivity.photoFileList[i-1]=photoFile;
            }else{
                ReportFormActivity.photoFileList[2]=photoFile;
                i=3;
            }


            ft = fm.beginTransaction().replace(id, fragmentimg, Integer.toString(i));
            i++;
            ft.commitAllowingStateLoss();
        }




        /*if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(imageBitmap);
        }*/
    }

    @Override
    public void closeButtonClicked(String id) {
        FragmentManager fm;
        FragmentTransaction ft;
        fm = getSupportFragmentManager();
        Fragment fragmentimg = fm.findFragmentById(R.id.Fragment_Container_ImageStrip);
        if (fragmentimg == null) {
            ReportFormActivity.photoFileList[Integer.valueOf(id)-1]=null;
            //i++;
            ft = fm.beginTransaction();
            ft.remove(fm.findFragmentByTag(id));
            ft.commitAllowingStateLoss();
            i= Integer.valueOf(id);
        }
    }
}
