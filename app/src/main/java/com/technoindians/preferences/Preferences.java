package com.technoindians.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by girish on 12/2/16.
 */
public class Preferences {

     private static SharedPreferences preferences;
     private static SharedPreferences.Editor editor;

     public static void initialize(Context con) {
     if (null == preferences) {
     preferences = PreferenceManager.getDefaultSharedPreferences(con);
     }
     if (null == editor) {
     editor = preferences.edit();
     editor.commit();
     }
     }

     public static void clear() {
     editor.clear();
     editor.commit();
     }

     public static void save(String key, String value) {
     editor.putString(key, value);
     editor.commit();
     }

     public static void save(String key, Boolean value) {
         Log.e("save String Boolean","key -> "+key+" value -> "+value);
          save(key, value);
     }

     public static void save(String key, Integer value) {
     save(key, String.valueOf(value));
     }

     public static void save(String key, Long value) {
     save(key, String.valueOf(value));
     }

     public static String get(String key) {
     return preferences.getString(key, null);
     }

     public static boolean getBoolean(String key) {
          return  preferences.getBoolean(key,false);
     }

     public static Boolean contains(String key) {
     return preferences.contains(key);
     }

     public static void removeKey(String key) {
     editor.remove(key);
     editor.commit();
     }
}
