package com.technoindians.wall;

/**
 * Created by trojan on 15/7/16.
 */
public class Liked_ {
/*
*{

    "id":"2",
    "user_id":"7",
    "name":"Girish Mane",
    "skills":"Singing",
    "profile_pic":"uploads\/profiles\/7.jpg",
    "type":"1",
    "is_follow":1,
    "status":1

}
* */
    String id;
    String user_id;
    String name;
    String skill;
    String profile_pic;
    String user_type;
    String is_follow;

    /* set methods */
    public void setId(String id){
        this.id = id;
    }
    public void setUserId(String user_id){
        this.user_id = user_id;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setUserType(String user_type){
        this.user_type = user_type;
    }
    public void setIsFollow(String is_follow){
        this.is_follow = is_follow;
    }
    public void setProfile_pic(String profile_pic){
        this.profile_pic = profile_pic;
    }
    public void setSkill(String skill){
        this.skill = skill;
    }

    /* get methods */

    public String getId(){
        return this.id;
    }
    public String getUserId(){
        return this.user_id;
    }
    public String getName(){
        return this.name;
    }
    public String getUserType(){
        return this.user_type;
    }
    public String getIsFollow(){
        return this.is_follow;
    }
    public String getProfile_pic(){
        return this.profile_pic;
    }
    public String getSkill(){
        return this.skill;
    }
}

