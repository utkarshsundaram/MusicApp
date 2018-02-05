/*
 * Created by Mohamed Ibrahim N
 * Created on : 5/2/18 5:51 PM
 * File name : Utility.java
 * Last modified by : Mohamed Ibrahim N
 * Last modified on : 5/2/18 5:35 PM
 * Project : MusicApp
 * Organization : FreeLancer trinhvanbien
 * Copyright (c) 2018. All rights reserved.
 */

package in.tr.musicapp.utility;

import android.net.Uri;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Utility {
    public static String getLength(long milliseconds) {
        return String.format(Locale.ENGLISH, "%02d:%02d sec",
                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
    }

    //Get the redirect link for the url
    public static String getRedirect(String urlString) throws ClientProtocolException, IOException {

        urlString = urlString.replace(" ", "%20");

        HttpParams httpParameters = new BasicHttpParams();
        HttpClientParams.setRedirecting(httpParameters, false);

        HttpClient httpClient = new DefaultHttpClient(httpParameters);
        HttpGet httpget = new HttpGet(urlString);
        HttpContext context = new BasicHttpContext();

        HttpResponse response = httpClient.execute(httpget, context);

        // If we didn't get a '302 Found' we aren't being redirected.
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_MOVED_TEMPORARILY && response.getStatusLine().getStatusCode() != 301)
            throw new IOException(response.getStatusLine().toString());

        Header loc[] = response.getHeaders("Location");
        return loc.length > 0 ? loc[loc.length - 1].getValue() : null;
    }
}
