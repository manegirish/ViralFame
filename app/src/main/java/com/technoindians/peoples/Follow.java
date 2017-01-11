package com.technoindians.peoples;

/**
 * @author
 * Girish M(girishmane8692@gmail.com)
 * Created on 12/08/16.
 * Last modified 04/08/2016
 */

public class Follow {

    private int status;
    private String id;
    private String user_id;
    private String name;
    private String user_type;
    private String profile_pic;
    private String last_updated;
    private String is_follow;
    private String skill;

    public void setStatus(int status) {
        this.status = status;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String user_id) {
        this.user_id = user_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String user_type) {
        this.user_type = user_type;
    }

    public void setPhoto(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public void setUpdated(String last_updated) {
        this.last_updated = last_updated;
    }

    public void setIsFollow(String is_follow) {
        this.is_follow = is_follow;
    }

    public void setSkill(String skill) {
        this.skill = skill;
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

    public String getType() {
        return this.user_type;
    }

    public String getPhoto() {
        return this.profile_pic;
    }

    public String getUpdated() {
        return this.last_updated;
    }

    public String getIsFollow() {
        return this.is_follow;
    }

    public String getSkill() {
        return this.skill;
    }

    public int getStatus(){
        return this.status;
    }

}
