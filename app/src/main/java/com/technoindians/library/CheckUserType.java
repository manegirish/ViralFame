/*
 * Copyright (c) 2016
 * Girish D Mane (girishmane8692@gmail.com)
 * Gurujot Singh Pandher (gsp11111992@gmail.com)
 * All rights reserved.
 * This application code can not be used directly without prior permission of owners.
 *
 */

package com.technoindians.library;

import com.technoindians.constants.Constants;
import com.technoindians.preferences.Preferences;

/**
 * @author Girish D M(girishmane8692@gmail.com)
 *         Created on 27/10/16.
 *         Last Modified on 27/10/16.
 */

public class CheckUserType {

    public static boolean isGuest(){
        if (Preferences.get(Constants.USER_TYPE).equalsIgnoreCase("3")){
            return true;
        }
        return false;
    }
}
