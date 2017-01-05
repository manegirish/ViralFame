package com.technoindians.validation;

/**
 * Created by girish on 26/7/16.
 */
public class JobValidation_ {

    public static int isTitle(String title, int min,int max){
        if (title==null){
            return 2;
        }
        if (!title.matches("[a-zA-Z.? ]*")){
            return 2;
        }
        if (title.length()<=min||title.length()>max){
            return 2;
        }
        return 1;
    }

    public static int isDescription(String description, int min,int max){
        if (description==null){
            return 2;
        }
        if (description.length()<=min||description.length()>max){
            return 2;
        }
        return 1;
    }
}
