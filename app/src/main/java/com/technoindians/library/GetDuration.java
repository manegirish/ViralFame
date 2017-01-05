package com.technoindians.library;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import com.technoindians.constants.Constants;
import com.technoindians.database.UpdateOperations;
import com.technoindians.pops.ShowToast;

import java.io.IOException;

/**
 * @author
 * Girish Mane <girishmane8692@gmail.com>
 * Created on 24/08/2016
 * Last modified 25/08/2016
 *
 */

public class GetDuration {

    private static UpdateOperations updateOperations;

    public static String get(String wall_id,String url, Context context){
        updateOperations = new UpdateOperations(context);
        Log.e("get","url => "+url);
        long milliseconds = 0;
        try {
            MediaPlayer mPlayer = new MediaPlayer();
            mPlayer.setDataSource(url);
            mPlayer.prepare();
            milliseconds = mPlayer.getDuration();
            mPlayer.stop();
            mPlayer.release();
        } catch (IllegalArgumentException e) {
            ShowToast.toast(context, "You might not set the URI correctly! IllegalArgumentException");
        } catch (SecurityException e) {
            ShowToast.toast(context, "You might not set the URI correctly! SecurityException");
        } catch (IllegalStateException e) {
            ShowToast.toast(context, "You might not set the URI correctly! IllegalStateException");
        } catch (IOException e) {
            e.printStackTrace();
        }
        long duration = milliseconds / 1000;
        long hours = duration / 3600;
        long minutes = (duration - hours * 3600) / 60;
        long seconds = duration - (hours * 3600 + minutes * 60);

        StringBuilder time = new StringBuilder();
        if (hours>0){
            time.append(hours+"h ");
        }
        if (minutes>0){
            time.append(minutes+"m ");
        }
        if (seconds>0){
            time.append(seconds+"s ");
        }
      /*  Log.e("GetDuration","milliseconds =>"+milliseconds+" seconds => "+seconds+"\n duration => "
                +duration+" hours => "+hours+"\n minutes => "+minutes+" time => "+time.toString());
*/

            updateOperations.updateFeed(wall_id, Constants.MEDIA_DURATION,time.toString());
        return time.toString();
    }
}
