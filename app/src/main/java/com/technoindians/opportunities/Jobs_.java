package com.technoindians.opportunities;

/**
 * @author
 * Girish Mane <girishmane8692@gmail.com>
 * Created on 26/07/2016
 * Last modified 15/08/2016
 *
 */
public class Jobs_ {

    String id;
    String title;
    String position;
    String description;
    String organisation;
    String user_id;
    String name;
    String type;
    String profile_pic;
    String primary_skill;
    String secondary_skill;
    String date_of_post;
    String is_apply;
    int status;

        /* set methods */

    public void setId(String id) {
        this.id = id;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public void setUserId(String user_id) {
        this.user_id = user_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public void setSkillPrimary(String primary_skill) {
        this.primary_skill = primary_skill;
    }

    public void setSkillSecondary(String secondary_skill) {
        this.secondary_skill = secondary_skill;
    }

    public void setDate(String date_of_post) {
        this.date_of_post = date_of_post;
    }

    public void setApply(String is_apply){
        this.is_apply = is_apply;
    }

    /* get methods */

    public int getStatus() {
        return this.status;
    }

    public String getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getPosition() {
        return this.position;
    }

    public String getDescription() {
        return this.description;
    }

    public String getOrganisation() {
        return this.organisation;
    }

    public String getUserId() {
        return this.user_id;
    }

    public String getName() {
        return this.name;
    }

    public String getType() {
        return this.type;
    }

    public String getPic() {
        return this.profile_pic;
    }

    public String getSkillPrimary() {
        return this.primary_skill;
    }

    public String getSkillSecondary() {
        return this.secondary_skill;
    }

    public String getDate() {
        return this.date_of_post;
    }

    public String getApply(){
        return this.is_apply;
    }
}
