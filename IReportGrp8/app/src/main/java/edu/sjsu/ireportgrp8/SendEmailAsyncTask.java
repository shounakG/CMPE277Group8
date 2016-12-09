package edu.sjsu.ireportgrp8;

import android.os.AsyncTask;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import javax.ws.rs.core.MediaType;

/**
 * Created by pnedunuri on 12/8/16.
 */

public class SendEmailAsyncTask extends AsyncTask<String,Void,Void>
{
    @Override
    protected Void doInBackground(String... params) {
        Client client = Client.create();
        client.addFilter(new HTTPBasicAuthFilter("api",
                "key-48171d3fd8035e9e3a21881aaade014a"));
        WebResource webResource =
                client.resource("https://api.mailgun.net/v3/mail.pruthvi-nadunooru.name" +
                        "/messages");
        MultivaluedMapImpl formData = new MultivaluedMapImpl();
        formData.add("from", "CMPE 277 Grp 8 <akshay@mail.pruthvi-nadunooru.name>");
        formData.add("to", params[0]);
        formData.add("subject", params[1]);
        formData.add("text", params[2]);
        webResource.type(MediaType.APPLICATION_FORM_URLENCODED).
                post(ClientResponse.class, formData);

        return null;
    }
}
