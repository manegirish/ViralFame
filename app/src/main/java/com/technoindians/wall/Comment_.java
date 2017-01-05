package com.technoindians.wall;

/**
 * Created by trojan on 14/7/16.
 */
public class Comment_ {

    String id;
    String user_id;
    String name;
    String comment;
    String user_type;
    String last_updated;
    String profile_pic;

    /* set methods */
    public void setId(String id){
        this.id=id;
    }
    public void setUserId(String user_id){
        this.user_id=user_id;
    }
    public void setName(String name){
        this.name=name;
    }
    public void setComment(String comment){
        this.comment=comment;
    }
    public void setUserType(String user_type){
        this.user_type=user_type;
    }
    public void setLast_updated(String last_updated){
        this.last_updated=last_updated;
    }
    public void setProfile_pic(String profile_pic){
        this.profile_pic=profile_pic;
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
    public String getComment(){
        return this.comment;
    }
    public String getUserType(){
        return this.user_type;
    }
    public String getLast_updated(){
        return this.last_updated;
    }
    public String getProfile_pic(){
        return this.profile_pic;
    }

}

