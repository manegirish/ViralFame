package com.technoindians.wall;

/**
 * Created by girish on 10/7/16.
 */

public class Feed_ {

    String id;
    String user_id;
    String name;
    String type;
    String post_text;
    String media_type;
    String media_file;
    String total_comments;
    String total_likes;
    String is_like;
    String date_of_post;
    String profile_pic;
    String last_updated;
    String skill;
    String media_size;
    String is_follow;

    /* set methods */
    public void setId(String id){
        this.id=id;
    }
    public void setUser_id(String user_id){
        this.user_id=user_id;
    }
    public void setName(String name){
        this.name=name;
    }
    public void setType(String type){
        this.type=type;
    }
    public void setPost_text(String post_text){
        this.post_text=post_text;
    }
    public void setMedia_type(String media_type){
        this.media_type=media_type;
    }
    public void setMedia_file(String media_file){
        this.media_file=media_file;
    }
    public void setTotal_comments(String total_comments){
        this.total_comments=total_comments;
    }
    public void setTotal_likes(String total_likes){
        this.total_likes=total_likes;
    }
    public void setIs_like(String is_like){
        this.is_like=is_like;
    }
    public void setDate_of_post(String date_of_post){
        this.date_of_post=date_of_post;
    }
    public void setLast_updated(String last_updated){
        this.last_updated=last_updated;
    }
    public void setProfile_pic(String profile_pic){
        this.profile_pic=profile_pic;
    }
    public void setSkill(String skill){
        this.skill=skill;
    }
    public void setSize(String media_size){
        this.media_size=media_size;
    }
    public void setFollow(String is_follow){
        this.is_follow=is_follow;
    }


    /* get methods */

    public String getId(){
        return this.id;
    }
    public String getUser_id(){
        return this.user_id;
    }
    public String getName(){
        return this.name;
    }
    public String getType(){
        return this.type;
    }
    public String getPost_text(){
        return this.post_text;
    }
    public String getMedia_file(){
        return this.media_file;
    }
    public String getMedia_type(){
        return this.media_type;
    }
    public String getTotal_comments(){
        return this.total_comments;
    }
    public String getTotal_likes(){
        return this.total_likes;
    }
    public String getIs_like(){
        return this.is_like;
    }
    public String getDate_of_post(){
        return this.date_of_post;
    }
    public String getLast_updated(){
        return this.last_updated;
    }
    public String getProfile_pic(){
        return this.profile_pic;
    }
    public String getSkill(){
        return this.skill;
    }
    public String getSize(){
        return this.media_size;
    }
    public String getFollow(){
        return this.is_follow;
    }
}
