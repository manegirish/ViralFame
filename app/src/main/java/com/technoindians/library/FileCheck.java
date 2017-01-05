package com.technoindians.library;

import android.media.MediaMetadataRetriever;
import android.util.Log;
import java.io.File;
import java.text.DecimalFormat;

public class FileCheck {


	public static String size(int size){
		String hrSize = null;
		double m = size/1024.0;
		DecimalFormat dec = new DecimalFormat("0.00");
		if (m > 1 && m < 25) {
			hrSize = dec.format(m).concat(" MB");
		} else {
			hrSize = dec.format(size).concat(" KB");
		}
		return hrSize;
	}

    public static String duration(String filePath){
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(filePath);
        // convert duration to minute:seconds
        String duration =
                metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long milliseconds = Long.parseLong(duration);

        int second = (int) (milliseconds / 1000) % 60 ;
        int minutes = (int) ((milliseconds / (1000*60)) % 60);
        int hours   = (int) ((milliseconds / (1000*60*60)) % 24);
        String time = "";
        if (hours>0){
            time=""+hours+" hr";
        }
        if (minutes>0){
            time = time+" "+minutes+"m";
        }
        if (second>0){
            time = time+" "+second+"s";
        }
        Log.e("duration -> ", " time -> "+time+"\n minutes -> "+minutes+" second -> "+second);
        metaRetriever.release();
        return time;
    }

	public static String getFileSize(long size) {
		if (size <= 0)
			return "0";
		final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		String file_size = new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
		Log.e("getFileSize()","size => "+size+" file_size => "+file_size);
		return file_size;
	}

	public static boolean isExist(String file_path) {
		Log.e("FileCheck"," => "+file_path);
		if (new File(file_path).exists()) {
			return true;
		}
		return false;
	}

	public static String getFileName(String url){
		String file_name = "-Unknown-";
		if(url.lastIndexOf(".") != -1) {
            file_name = url.substring(url.lastIndexOf("/")+1);
		}
        return file_name;
	}

	public static String getDestinationPath(String url,String directory){
		String file_name = null;
		if(url.lastIndexOf(".") != -1) {
			file_name = url.substring(url.lastIndexOf("/")+1);
		}
        file_name = directory+file_name;
        Log.e("file_name"," -> "+file_name);
		return file_name;
	}
}
