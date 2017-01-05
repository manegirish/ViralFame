package com.technoindians.library;

/**
 * @author
 * Girish Mane <girishmane8692@gmail.com>
 * Created on 18/07/2016
 * Last modified 25/07/2016
 *
 */

import android.util.Log;
import com.technoindians.constants.Constants;
import com.technoindians.network.Urls;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class UploadFile {

    static int serverResponseCode = 0;

    public static HashMap<String, String> uploadFile(String sourceFileUri,String user_id) {

        String upLoadServerUri = Urls.DOMAIN + Urls.UPLOAD_IMAGE;
        String fileName = sourceFileUri;
        HashMap<String, String> responseMap = new HashMap<>();
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);
        if (!sourceFile.isFile()) {
            responseMap.put(Constants.STATUS, "0");
            responseMap.put(Constants.RESPONSE, "File not found");
            return responseMap;
        }
        try {
            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            URL url = new URL(upLoadServerUri);
            conn = (HttpURLConnection) url.openConnection(); // Open a HTTP
            conn.setDoInput(true); // Allow Inputs
            conn.setDoOutput(true); // Allow Outputs
            conn.setUseCaches(false); // Don't use a Cached Copy
            conn.setRequestMethod("POST");

            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("uploaded_file", fileName);
            conn.setRequestProperty("action", "profile");
            conn.setRequestProperty("user_id", user_id);

            dos = new DataOutputStream(conn.getOutputStream());

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"action\""
                    + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes("profile");
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"user_id\""
                    + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(user_id);
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);

            dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                    + fileName + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available(); // create a buffer of
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            serverResponseCode = conn.getResponseCode();
            String serverResponseMessage = conn.getResponseMessage();
            InputStream in = new BufferedInputStream(conn.getInputStream());
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(in));
            String line;
            StringBuilder result = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            Log.e("uploadFile", "HTTP Response is " + serverResponseMessage +
                    ": " + serverResponseCode+" string --> "+result.toString());
            if (serverResponseCode == 200) {
                responseMap.put(Constants.STATUS, "1");
                responseMap.put(Constants.RESPONSE,result.toString());
            }
            fileInputStream.close();
            dos.flush();
            dos.close();

        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        conn.disconnect();
        return responseMap;
    }
}
