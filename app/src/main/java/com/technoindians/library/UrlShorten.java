/*
 * Copyright (c) 2016
 * Girish D Mane (girishmane8692@gmail.com)
 * Gurujot Singh Pandher (gsp11111992@gmail.com)
 * All rights reserved.
 * This application code can not be used directly without prior permission of owners.
 *
 */

package com.technoindians.library;

import android.os.AsyncTask;
import android.util.Log;

import com.andreabaccega.googlshortenerlib.GooglShortenerRequestBuilder;
import com.andreabaccega.googlshortenerlib.GooglShortenerResult;
import com.andreabaccega.googlshortenerlib.GoogleShortenerPerformer;
import com.squareup.okhttp.OkHttpClient;
import com.technoindians.constants.Keys;

import java.util.concurrent.ExecutionException;

/**
 * Created by girish on 25/10/16.
 */

public class UrlShorten {

    public static String get(String url){
        try {
            return new Shorten().execute(url).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class Shorten extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            if (params[0] != null && params[0].length() > 0) {
                return  makeShort(params[0]);
            }
            return null;
        }
    }

    private static String makeShort(String url) {
        String short_url = null;
        GoogleShortenerPerformer shortenerPerformer = new GoogleShortenerPerformer(new OkHttpClient());
        // String longUrl = "https://github.com/vekexasia/android-googl-shortener-lib";
        GooglShortenerResult result = shortenerPerformer.shortenUrl(
                new GooglShortenerRequestBuilder()
                        .buildRequest(url, Keys.API_KEY)
        );

        if (GooglShortenerResult.Status.SUCCESS.equals(result.getStatus())) {
            Log.e("shortanUrl()", "Success =>" + result.getShortenedUrl());
            short_url = result.getShortenedUrl();
            // all ok result.getShortenedUrl() contains the shortened url!
        } else if (GooglShortenerResult.Status.IO_EXCEPTION.equals(result.getStatus())) {
            Log.e("shortanUrl()", "IO_EXCEPTION =>" + result.getException());
            // connectivity error. result.getException() returns the thrown exception while performing
            // the request to google servers!
        } else {
            Log.e("shortanUrl()", "Status.RESPONSE_ERROR =>" + GooglShortenerResult.Status.RESPONSE_ERROR + "exception -> " + result.getException());
            // Status.RESPONSE_ERROR
            // this happens if google replies with an unexpected response or if there are some other issues processing
            // the result.

            // result.getException() contains a GooglShortenerException containing a message that can help resolve the issue!
        }
        return short_url;
    }
}
