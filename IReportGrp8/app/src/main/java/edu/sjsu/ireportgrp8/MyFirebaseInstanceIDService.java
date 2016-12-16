package edu.sjsu.ireportgrp8;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by SrIHaaR on 12/12/2016.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "FIREBASE_INSTANCE";
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        try {
            registerToken(token);
        } catch (Exception e) {

        }

    }

    public void registerToken(String token) {
        System.out.println("got the firebase token   " + token);
        String email;
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth == null) {
            return;
        }

        if (auth.getCurrentUser() == null) {
            return;
        }

        email = auth.getCurrentUser().getEmail();
        if (email == null) {
            return;
        }

        Map<String, Object> userTokenMap = new HashMap<String, Object>();
        email = email.replaceAll("\\W", "");
        userTokenMap.put(email, token);
        mDatabase.child("usertokens").updateChildren(userTokenMap);
    }
}
