package com.technoindians.directory;

/**
 * @author
 * Girish Mane <girishmane8692@gmail.com>
 * Created on 25/4/16.
 * Last modified 27/08/2016
 *
 */

public class DirectoryList {

    public static final String ROOT_DIR = android.os.Environment.getExternalStorageDirectory().
            getAbsolutePath().toString();

    public static final String DIR_MAIN=ROOT_DIR+"/Viral Fame/";
    public static final String DIR_SHARED_FILES="Files/";
    public static final String IMAGE=DIR_MAIN+"Images/";
    public static final String VIDEO=DIR_MAIN+"Videos/";
    public static final String AUDIO=DIR_MAIN+"Audio/";
    public static final String PROFILE=DIR_MAIN+"Profile/";

}
