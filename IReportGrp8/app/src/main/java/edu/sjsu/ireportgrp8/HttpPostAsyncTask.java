package edu.sjsu.ireportgrp8;

import android.os.AsyncTask;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by SrIHaaR on 12/15/2016.
 */

public class HttpPostAsyncTask extends AsyncTask<String, Void, Void> {
    @Override
    protected Void doInBackground(String... data) {
        URL url = null;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL("https://fcm.googleapis.com/fcm/send");

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setChunkedStreamingMode(0);
            urlConnection.setRequestProperty ("Authorization", "key=AAAAuolaSVw:APA91bEoDORPR5kvulMKxgxUXY2lRlM82l2SzLv1CJMNvEnBfRWmdc_uTiRi-PhQGz4SAu-KfO-0yQ5mRPSfc97yiwCEfLb3wQvExFhBmM7WAqR7UV5qZgiR5CkUh9XoCduiOzEMKhJBbfNePhn0RMg28IU8nNZygw");
            urlConnection.setRequestProperty("Content-Type", "application/json");

            OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
            out.write(data[0].getBytes());
            out.flush();
            out.close();

            int responseCode = urlConnection.getResponseCode();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //print result
            System.out.println(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }

        return null;
    }
}
