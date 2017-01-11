package com.technoindians.network;

import android.util.Log;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * @author
 * Girish Mane <girishmane8692@gmail.com>
 * Created on 25/04/2016
 * Last modified 11/01/2017
 *
 */
public class MakeCall {
    private static OkHttpClient client = new OkHttpClient();

    public static String post(String url,RequestBody formBody,String TAG) throws Exception {
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        Response response = client.newCall(request).execute();
        String res = response.body().string();
        Log.e(TAG,"Url -> "+url+"?"+bodyToString(request)+"\nResponse -> "+res);
        if (response.isSuccessful()){
            return res;
        }else {
            return null;
        }
    }
    public static String dummyPost(String url,RequestBody formBody,String TAG) throws Exception {
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        Log.e(TAG,"dummyPost() Url -> "+url+"?"+bodyToString(request));
        return null;
    }
    private static String bodyToString(final Request request){
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }
}
