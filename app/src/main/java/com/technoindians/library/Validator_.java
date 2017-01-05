package com.technoindians.library;

/**
 * @author
 * Girish Mane <girishmane8692@gmail.com>
 * Created on 10/07/2016
 * Last modified 10/07/2016
 *
 */
public class Validator_ {

    public static int comment(String message){
        if (message==null){
            return 0;
        }
        if (message.length()<=2||message.length()>500){
            return 0;
        }
        return 1;
    }
}
