package com.technoindians.directory;

import java.io.File;
import java.util.ArrayList;

/**
 * @author
 * Girish Mane <girishmane8692@gmail.com>
 * Created on 25/04/2016
 * Last modified 27/04/2016
 *
 */

public class Make_ {
    private static ArrayList<String> makeDirList(){
        ArrayList<String> dirList = new ArrayList<>();

        dirList.add(DirectoryList.DIR_MAIN);
        dirList.add(DirectoryList.DIR_SHARED_FILES);
        dirList.add(DirectoryList.IMAGE);
        dirList.add(DirectoryList.VIDEO);
        dirList.add(DirectoryList.AUDIO);
        dirList.add(DirectoryList.PROFILE);

        return dirList;
    }

    public static void makeDirectories(){
        for (int i = 0; i < makeDirList().size(); i++) {
            File folder = new File(makeDirList().get(i));
            if (!folder.exists()) {
                @SuppressWarnings("unused")
                boolean success = folder.mkdir();
            }
        }
    }
}
