package com.technoindians.directory;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * @author
 * Girish Mane <girishmane8692@gmail.com>
 * Created on 22/6/16.
 * Last modified 27/08/2016
 *
 */

public class DirectoryOperations {

    public static boolean copy(File sourceFile, File destFile) {
        if (!destFile.exists()) {
            try {
            destFile.createNewFile();
            FileChannel source = null;
            FileChannel destination = null;
            try {
                source = new FileInputStream(sourceFile).getChannel();
                destination = new FileOutputStream(destFile).getChannel();
                destination.transferFrom(source, 0, source.size());
            } finally {
                if (source != null)
                    source.close();
                if (destination != null)
                    destination.close();
            }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }
}
