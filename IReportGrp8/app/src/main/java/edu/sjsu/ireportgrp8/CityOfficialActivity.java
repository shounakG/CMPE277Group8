package edu.sjsu.ireportgrp8;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.appindexing.Action;
import com.google.firebase.appindexing.FirebaseAppIndex;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.Indexable;
import com.google.firebase.appindexing.builders.Indexables;
import com.google.firebase.appindexing.builders.PersonBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class CityOfficialActivity extends AppCompatActivity {

    private static final String TAG = "iReport";
    public static CityOfficialActivity me;
    public static volatile HashMap<String, String> userTokensMap = null;

    private SearchView searchView;

    public static final String REPORTS_CHILD = "reports";
    public static final String REPORTS_URL = "reports_url";
    private static final int REQUEST_INVITE = 1;
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 10;
    public static final String ANONYMOUS = "anonymous";
    private static final String MESSAGE_SENT_EVENT = "message_sent";
    private String mUsername;
    private String mPhotoUrl;

    private static volatile HashMap <String, ReportsViewHolder> viewHolderHashMap = null;

    private ProgressBar mProgressBar;

    // Firebase instance variables
    private DatabaseReference mFirebaseDatabaseReference;
    private static volatile RecyclerView mlistRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;


    public static volatile FirebaseStorage storage = FirebaseStorage.getInstance();
    public static volatile StorageReference storageRef = storage.getReferenceFromUrl("gs://ireport-16f3e.appspot.com");

    public static volatile DatabaseReference userTokenRef = FirebaseDatabase.getInstance().getReference().child("usertokens");

    private static volatile ArrayList<ResidentReport> residentReports = null;
    private static volatile boolean misEmailFilterEnabled = false;
    private static volatile String mreportStatusFilter = null;
    private static volatile String mseverityFilter = null;

    public static class ReportsViewHolder extends RecyclerView.ViewHolder {
        public TextView reportTitleView;
        public TextView addressTextView;
        public TextView currentStatusTextView;
        public TextView datePostedTextView;
        public TextView emailTextView;

        public ImageView stripeThumbnailImgView;

        public ImageView minorOffImgView;
        public ImageView minorOnImgView;

        public ImageView mediumOffImgView;
        public ImageView mediumOnImgView;

        public ImageView urgentOffImgView;
        public ImageView urgentOnImgView;

        public RelativeLayout reportStripeView;

        private ResidentReport residentReportRef;
        private View view;

        public ReportsViewHolder(View v) {
            super(v);

            reportStripeView = (RelativeLayout) itemView.findViewById(R.id.reportStripeView);

            reportTitleView = (TextView) itemView.findViewById(R.id.reportTitleView);
            addressTextView = (TextView) itemView.findViewById(R.id.addressTextView);
            currentStatusTextView = (TextView) itemView.findViewById(R.id.currentStatusTextView);
            datePostedTextView = (TextView) itemView.findViewById(R.id.datePostedTextView);
            emailTextView = (TextView) itemView.findViewById(R.id.emailTextView);

            stripeThumbnailImgView = (ImageView) itemView.findViewById(R.id.stripeThumbnailImgView);

            minorOffImgView = (ImageView) itemView.findViewById(R.id.minorOffImgView);
            minorOnImgView = (ImageView) itemView.findViewById(R.id.minorOnImgView);

            mediumOffImgView = (ImageView) itemView.findViewById(R.id.mediumOffImgView);
            mediumOnImgView = (ImageView) itemView.findViewById(R.id.mediumOnImgView);

            urgentOffImgView = (ImageView) itemView.findViewById(R.id.urgentOffImgView);
            urgentOnImgView = (ImageView) itemView.findViewById(R.id.urgentOnImgView);

            view = v;
        }

        public void setVisibility(boolean isVisible){
            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams)itemView.getLayoutParams();
            if (isVisible){
                param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                param.width = LinearLayout.LayoutParams.MATCH_PARENT;
                itemView.setVisibility(View.VISIBLE);
            }else{
                itemView.setVisibility(View.GONE);
                param.height = 0;
                param.width = 0;
            }
            itemView.setLayoutParams(param);
        }

        public ResidentReport getResidentReportRef() {
            return residentReportRef;
        }

        public void setResidentReportRef(ResidentReport residentReportRef) {
            this.residentReportRef = residentReportRef;
        }

        public View getView() {
            return view;
        }
    }

    public abstract class MyFirebaseRecyclerAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

        private static final int UPDATE_THRESHOLD = 1000;
        public Class<T> mModelClass;
        public int mModelLayout;
        public Class<VH> mViewHolderClass;
        public List<T> mSnapshots;
        public List<T> mSnapshotsBackup;
        public volatile int updateCounter = 0;
        private int positionStart = 0;

        public MyFirebaseRecyclerAdapter(Class<T> modelClass, int modelLayout, Class<VH> viewHolderClass, Query ref) {
            mModelClass = modelClass;
            mModelLayout = modelLayout;
            mViewHolderClass = viewHolderClass;
            mSnapshots = new ArrayList<T>();

            CityOfficialActivity.firebaseRetreiveData();
            setTimer(0);
        }

        private void setTimer(int timer) {
            timer = (timer == -1) ? UPDATE_THRESHOLD : timer;

            final View view = (ViewGroup) ((ViewGroup) CityOfficialActivity.this
                    .findViewById(android.R.id.content)).getChildAt(0);
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (updateCounter > 0) {
                                notifyItemRangeInserted(positionStart, updateCounter);
                                positionStart = updateCounter;
                                updateCounter = 0;

                                setTimer(10);
                            } else {
                                setTimer(-1);
                            }
                        }
                    });
                }
            }, timer);
        }


        public MyFirebaseRecyclerAdapter(Class<T> modelClass, int modelLayout, Class<VH> viewHolderClass, DatabaseReference ref) {
            this(modelClass, modelLayout, viewHolderClass, (Query) ref);
        }

        public void addData(T data) {
            mSnapshots.add(data);

            residentReports.add((ResidentReport) data);
            mFirebaseAdapter.mSnapshotsBackup = mFirebaseAdapter.mSnapshots;
            updateCounter++;
        }


        @Override
        public int getItemCount() {
            return mSnapshots.size();
        }

        public T getItem(int position) {
            return mSnapshots.get(position);
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            ViewGroup view = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(mModelLayout, parent, false);
            try {
                Constructor<VH> constructor = mViewHolderClass.getConstructor(View.class);
                return constructor.newInstance(view);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        @Override
        public void onBindViewHolder(VH viewHolder, int position) {
            T model = getItem(position);
            populateViewHolder(viewHolder, model, position);
        }

        abstract protected void populateViewHolder(VH viewHolder, T model, int position);
    }

    private static void firebaseRetreiveData() {
        // Get a reference to our posts
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("userreports");

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                for (DataSnapshot dataUser : dataSnapshot.getChildren()) {
                    String userId = dataSnapshot.getKey();
                    Map<String, Object> map = (Map<String, Object>) dataUser.getValue();

                    ResidentReport report = new ResidentReport(map.get("title"), map.get("email"), map.get("images"), map.get("screenname"), map.get("description"), map.get("datetime"), map.get("latitude") + "," + map.get("longitude"), map.get("severity"), map.get("size"), map.get("status"), map.get("address"), map.get("annonymous"));
                    report.setReportId(dataUser.getKey());
                    report.setUserId(userId);

                    mFirebaseAdapter.addData(report);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                ResidentReport report = dataSnapshot.getValue(ResidentReport.class);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                ResidentReport report = dataSnapshot.getValue(ResidentReport.class);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
                ResidentReport report = dataSnapshot.getValue(ResidentReport.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });


        // send push notification to user
        userTokenRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot dataUser : dataSnapshot.getChildren()) {
                    Map<String, String> map = (Map<String, String>) dataSnapshot.getValue();

                    for (Map.Entry<String, String> mapEntry : map.entrySet()) {
                        userTokensMap.put(mapEntry.getKey(), mapEntry.getValue());
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    protected static volatile MyFirebaseRecyclerAdapter<ResidentReport, ReportsViewHolder> mFirebaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_official);
        mAuth = FirebaseAuth.getInstance();

        userTokensMap = new HashMap<String, String>();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(CityOfficialActivity.this, "Connection failed",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        me = this;

        residentReports = new ArrayList<ResidentReport>();
        viewHolderHashMap = new HashMap<String, ReportsViewHolder>();

        // Set default username is anonymous.
        mUsername = ANONYMOUS;

        // Initialize ProgressBar and RecyclerView.
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        // New child entries
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        repopulateReports();

//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in.
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            {
                finish();
            }
                return true;
            case R.id.refreshButton:
            {
                searchView.setQuery("", true);
                misEmailFilterEnabled = false;
                mseverityFilter = mreportStatusFilter = null;

                updateReports();
            }
            return true;
            case R.id.filter_menu:
            {
                LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

                View customView = inflater.inflate(R.layout.filter_popup_window, null);
                final PopupWindow popupWindow = new PopupWindow(customView, MATCH_PARENT, MATCH_PARENT);

                if(Build.VERSION.SDK_INT>=21){
                    popupWindow.setElevation(5.0f);
                }

                // Get a reference for the custom view close button
                ImageButton closeButton = (ImageButton) customView.findViewById(R.id.ib_close);


                CheckBox emailFilterCB = (CheckBox) customView.findViewById(R.id.emailCheckBox);
                emailFilterCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                         @Override
                         public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                             misEmailFilterEnabled = isChecked;
                         }
                     }
                );

                AppCompatSpinner reportFilterSpinner = (AppCompatSpinner) customView.findViewById(R.id.reportFilterSpinner);
                reportFilterSpinner.setSelection(0);
                reportFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mreportStatusFilter = parent.getItemAtPosition(position).toString();

                        mreportStatusFilter = (mreportStatusFilter.equals("Report Status")) ? null : mreportStatusFilter;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                AppCompatSpinner severityFilterSpinner = (AppCompatSpinner) customView.findViewById(R.id.severitySpinner);
                severityFilterSpinner.setSelection(0);
                severityFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mseverityFilter = parent.getItemAtPosition(position).toString();

                        mseverityFilter = (mseverityFilter.equals("Priority Level")) ? null : mseverityFilter;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


                AppCompatButton cancelButton = (AppCompatButton) customView.findViewById(R.id.filterCancelButton);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Dismiss the popup window
                        popupWindow.dismiss();
                    }
                });

                AppCompatButton applyFilterButton = (AppCompatButton) customView.findViewById(R.id.filterApplyButton);
                applyFilterButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Dismiss the popup window
                        popupWindow.dismiss();

                        updateReports();
                    }
                });

                // Set a click listener for the popup window close button
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Dismiss the popup window
                        popupWindow.dismiss();
                    }
                });

                popupWindow.showAtLocation(this.findViewById(R.id.activity_city_official), Gravity.CENTER, 0, 0);
            }
                return true;
            case R.id.mapView_menu:
            {
                Intent intent = new Intent(this, MapActivity.class);

                Bundle bundle = new Bundle();
                bundle.putSerializable("residentReports", residentReports);

                intent.putExtras(bundle);

                startActivity(intent);
            }
            return true;
            case R.id.logout_menu:
                LoginManager.getInstance().logOut();
                mAuth.getInstance().signOut();
                // Google sign out
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                if(status.isSuccess()) {
                                    Intent intent = new Intent(CityOfficialActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }else{
                                    Toast.makeText(CityOfficialActivity.this, "Something went wrong.Please try again",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                return true;
            case R.id.heatMap_menu:
            {
                Intent intent = new Intent(this, HeatMapActivity.class);

                Bundle bundle = new Bundle();
                bundle.putSerializable("residentReports", residentReports);

                intent.putExtras(bundle);

                startActivity(intent);
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateReports() {
        // set the original copy
        mFirebaseAdapter.mSnapshots = mFirebaseAdapter.mSnapshotsBackup;

        List<ResidentReport> searchResults = new ArrayList<ResidentReport>();

        String searchQuery = searchView.getQuery().toString();
        if (searchQuery != null && searchQuery.length() > 0) {
            // hide keyboard
            searchView.clearFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

            searchQuery = searchQuery.toUpperCase();
            for (ResidentReport report : mFirebaseAdapter.mSnapshots) {
                if (report.getTitle().toUpperCase().contains(searchQuery) || report.getStatus().toUpperCase().contains(searchQuery) || report.getSeverity_Level().toUpperCase().contains(searchQuery) || report.getUser_Email().toUpperCase().contains(searchQuery)) {
                    searchResults.add(report);
                }
            }
        } else {
            searchResults.addAll(mFirebaseAdapter.mSnapshots);
        }

        if (misEmailFilterEnabled) {
            for (int index = 0; index < searchResults.size(); index++) {
                ResidentReport report = searchResults.get(index);
                String userEmail = report.getUser_Email().toUpperCase();
                for (int innerIndex = index + 1; innerIndex < searchResults.size(); innerIndex++) {
                    if (userEmail.contains(searchResults.get(innerIndex).getUser_Email().toUpperCase())) {
                        if (index + 1 == innerIndex) {
                            break;
                        } else {
                            ResidentReport temp = searchResults.get(index + 1);
                            searchResults.set(index + 1, searchResults.get(innerIndex));
                            searchResults.set(innerIndex, temp);
                        }
                    }
                }
            }
        }

        if (mreportStatusFilter != null) {
            for (int index = 0; index < searchResults.size(); index++) {
                ResidentReport report = searchResults.get(index);
                if (!report.getStatus().equalsIgnoreCase(mreportStatusFilter)) {
                    searchResults.remove(index);

                    index--;
                }
            }
        }

        if (mseverityFilter != null) {
            for (int index = 0; index < searchResults.size(); index++) {
                ResidentReport report = searchResults.get(index);
                if (!report.getSeverity_Level().equalsIgnoreCase(mseverityFilter)) {
                    searchResults.remove(index);

                    index--;
                }
            }
        }

        mFirebaseAdapter.mSnapshotsBackup = mFirebaseAdapter.mSnapshots;
        mFirebaseAdapter.mSnapshots = searchResults;

        mlistRecyclerView.removeAllViews();
        mlistRecyclerView.setAdapter(mFirebaseAdapter);
    }

    public class WrapContentLinearLayoutManager extends LinearLayoutManager {

        public WrapContentLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        //... constructor
        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                Log.e("probe", "meet a IOOBE in RecyclerView");
            }
        }
    }

    private void repopulateReports() {
        if (mlistRecyclerView != null) {
            mlistRecyclerView.removeAllViews();
        }

        mlistRecyclerView = (RecyclerView) findViewById(R.id.listRecyclerView);
        mLinearLayoutManager = new WrapContentLinearLayoutManager(CityOfficialActivity.this, LinearLayoutManager.VERTICAL, false);
        mlistRecyclerView.setLayoutManager(mLinearLayoutManager);

        mFirebaseAdapter = new MyFirebaseRecyclerAdapter<ResidentReport,
                ReportsViewHolder>(
                ResidentReport.class,
                R.layout.fragment_report_stripe,
                ReportsViewHolder.class,
                mFirebaseDatabaseReference.child(REPORTS_CHILD)) {

            @Override
            protected void populateViewHolder(ReportsViewHolder viewHolder, ResidentReport residentReport, int position) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);

                final ReportsViewHolder viewHolderFinal = (ReportsViewHolder) viewHolder;
                final ResidentReport residentReportFinal = (ResidentReport) residentReport;

                viewHolderHashMap.put(residentReportFinal.getTitle(), viewHolderFinal);

                viewHolderFinal.setResidentReportRef(residentReportFinal);

                viewHolderFinal.reportStripeView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(CityOfficialActivity.this, DetailedReportActivity.class);

                        Bundle bundle = new Bundle();
                        bundle.putSerializable("reportObj", residentReportFinal);
                        intent.putExtras(bundle);

                        startActivity(intent);
                    }
                });

                viewHolderFinal.reportTitleView.setText(residentReportFinal.getTitle());
                viewHolderFinal.addressTextView.setText(residentReportFinal.getAddress());
                viewHolderFinal.datePostedTextView.setText(residentReportFinal.getFullDate());
                viewHolderFinal.currentStatusTextView.setText(residentReportFinal.getStatus());
                if (residentReportFinal.getAnnonymous().equals("false"))
                {
                    viewHolderFinal.emailTextView.setText(residentReportFinal.getUser_Email());
                } else {
                    viewHolderFinal.emailTextView.setText("Anonymous");
                }

                viewHolderFinal.minorOnImgView.bringToFront();
                viewHolderFinal.mediumOnImgView.bringToFront();
                viewHolderFinal.urgentOnImgView.bringToFront();

                // show severity level
                switch (residentReportFinal.getSeverity_Level()) {
                    case "minor": {
                        viewHolderFinal.mediumOffImgView.bringToFront();
                    }
                    case "medium": {
                        viewHolderFinal.urgentOffImgView.bringToFront();
                    }
                }

                StorageReference imageRef = storageRef.child("reports").child(residentReportFinal.getImage().get(0));

                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // download uri
                        residentReportFinal.setUri(uri);

                        Glide.with(CityOfficialActivity.this)
                                .load(uri)
                                .into(viewHolderFinal.stripeThumbnailImgView);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });

                // write this message to the on-device index
                FirebaseAppIndex.getInstance().update(getReportIndexable(residentReportFinal));

                // log a view action on it
                FirebaseUserActions.getInstance().end(getMessageViewAction(residentReportFinal));
            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int reportsCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (reportsCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mlistRecyclerView.scrollToPosition(positionStart);
                }
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                int reportsCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (reportsCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mlistRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        mlistRecyclerView.setAdapter(mFirebaseAdapter);
    }

    private Indexable getReportIndexable(ResidentReport residentReport) {
        PersonBuilder sender = Indexables.personBuilder()
                .setIsSelf(mUsername == residentReport.getUser_Screen_Name())
                .setName(residentReport.getUser_Screen_Name())
                .setUrl(REPORTS_URL.concat(residentReport.getReportId() + "/sender"));

        PersonBuilder recipient = Indexables.personBuilder()
                .setName(mUsername)
                .setUrl(REPORTS_URL.concat(residentReport.getReportId() + "/recipient"));

        Indexable messageToIndex = Indexables.messageBuilder()
                .setName(residentReport.getTitle())
                .setUrl(REPORTS_URL.concat(residentReport.getReportId()))
                .setSender(sender)
                .setRecipient(recipient)
                .build();

        return messageToIndex;
    }

    private Action getMessageViewAction(ResidentReport residentReport) {
        return new Action.Builder(Action.Builder.VIEW_ACTION)
                .setObject(residentReport.getUser_Screen_Name(), REPORTS_URL.concat(residentReport.getReportId()))
                .setMetadata(new Action.Metadata.Builder().setUpload(false))
                .build();
    }
}
