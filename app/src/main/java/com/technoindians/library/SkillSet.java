package com.technoindians.library;

import com.technoindians.constants.Constants;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author
 * Girish Mane <girishmane8692@gmail.com>
 * Created on 14/07/2016
 * Last modified 15/07/2016
 *
 */

public class SkillSet {

    public static ArrayList<String> set(ArrayList<HashMap<String,String>> skillArray){
        ArrayList<String> skills = new ArrayList<>();
        for (int i = 0;i<skillArray.size();i++){
            skills.add(skillArray.get(i).get(Constants.SKILL));
        }
        return skills;
    }
}
