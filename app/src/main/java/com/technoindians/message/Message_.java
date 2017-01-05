package com.technoindians.message;

/**
 * @author
 * Girish Mane <girishmane8692@gmail.com>
 * Created on 20/07/2016
 * Last modified 30/07/2016
 *
 */

public class Message_ {

    int status;
    String id;
    String message;
    String user_id;
    String name;
    String profile_pic;
    String type;
    String skills;
    String is_read;
    String total_unread;
    String send_date;
    String self;
    String message_type;

    /* set methods */

    public void setStatus(int status) {
        this.status = status;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String user_id) {
        this.user_id = user_id;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSkill(String skills) {
        this.skills = skills;
    }

    public void setRead(String is_read) {
        this.is_read = is_read;
    }

    public void setCount(String total_unread) {
        this.total_unread = total_unread;
    }

    public void setDate(String send_date) {
        this.send_date = send_date;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    public void setMessageType(String message_type) {
        this.message_type = message_type;
    }


    /* get methods */

    public int getStatus() {
        return this.status;
    }

    public String getId() {
        return this.id;
    }

    public String getUserId() {
        return this.user_id;
    }

    public String getName() {
        return this.name;
    }

    public String getProfile_pic(){
        return  this.profile_pic;
    }

    public String getMessage() {
        return this.message;
    }

    public String getType() {
        return this.type;
    }

    public String getSkill(){
        return this.skills;
    }

    public String getRead() {
        return this.is_read;
    }

    public String getCount() {
        return this.total_unread;
    }

    public String getDate() {
        return this.send_date;
    }

    public String getSelf() {
        return this.self;
    }

    public String getMessageType(){
        return this.message_type;
    }

}


