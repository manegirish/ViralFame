package com.technoindians.library;

import android.content.Context;
import android.os.AsyncTask;

import com.technoindians.constants.Constants;
import com.technoindians.database.UpdateOperations;
import com.technoindians.directory.DirectoryList;
import com.technoindians.peoples.UserPortfolioActivity;
import com.technoindians.preferences.Preferences;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author
 * Girish Mane <girishmane8692@gmail.com>
 * Created on 25/04/2016
 * Last modified 25/04/2016
 *
 */

public class ImageDownloader {

    private static UpdateOperations updateOperations;

    public static void downloadImage(String url,String file_name,boolean save,Context context,int type) throws IOException {
        //Log.e("url"," -> "+url+" file_name -> "+file_name);
        updateOperations = new UpdateOperations(context);
        if (type==1){
            new DownloadProfile(url,file_name,save,context).execute();
        }
        if (type == 2){
           new DownloadFriend(url,file_name).execute();
        }

    }


    private static class DownloadProfile extends AsyncTask<Void,Void,Void>{
        String url=null;
        Context context;
        String file_name;
        boolean save;

        public DownloadProfile(String url,String file_name,boolean save,Context context){
            this.url = url;
            this.file_name = file_name;
            this.context = context;
            this.save=save;
        }
        @Override
        protected Void doInBackground(Void... params) {
            URL wallpaperURL = null;
            File root = new File(DirectoryList.DIR_MAIN);
            try {
                if (!root.exists()){
                    root.mkdir();
                }
                wallpaperURL = new URL(url);
                URLConnection connection = wallpaperURL.openConnection();
                InputStream inputStream = new BufferedInputStream(wallpaperURL.openStream(), 10240);
                File cacheFile = new File(new File(DirectoryList.DIR_MAIN), file_name);
                FileOutputStream outputStream = new FileOutputStream(cacheFile);
                byte buffer[] = new byte[1024];
                int dataSize;
                while ((dataSize = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, dataSize);
                }
                outputStream.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (save==true){
                updateOperations.updateProfile(Preferences.get(Constants.USER_ID),
                        Constants.LOCAL_PATH,DirectoryList.DIR_MAIN + file_name);
            }
        }
    }


    private static class DownloadFriend extends AsyncTask<Void,Void,Void>{

        String url=null;
        String file_name;

        public DownloadFriend(String url,String file_name){
            this.url = url;
            this.file_name = file_name;
        }

        @Override
        protected Void doInBackground(Void... params) {
            URL wallpaperURL = null;
            File root = new File(DirectoryList.PROFILE);
            try {
                if (!root.exists()){
                    root.mkdir();
                }
                wallpaperURL = new URL(url);
                URLConnection connection = wallpaperURL.openConnection();
                InputStream inputStream = new BufferedInputStream(wallpaperURL.openStream(), 10240);
                File cacheFile = new File(new File(DirectoryList.PROFILE), file_name);
                FileOutputStream outputStream = new FileOutputStream(cacheFile);
                byte buffer[] = new byte[1024];
                int dataSize;
                while ((dataSize = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, dataSize);
                }
                outputStream.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            UserPortfolioActivity.userPortfolioActivity.updateImage();
        }
    }
}
