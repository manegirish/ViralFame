package com.technoindians.opportunities;

/**
 * @author
 * Girish Mane <girishmane8692@gmail.com>
 * Created on 26/07/2016
 * Last modified 15/08/2016
 *
 */
public class Applicant_ {

    int status;
    String application_id;
    String applicant_id;
    String name;
    String type;
    String profile_pic;
    String primary_skill;
    String date_of_apply;

    /* set methods */

    public void setStatus(int status){
        this.status=status;
    }
    public void setApplicationId(String application_id) {
        this.application_id = application_id;
    }

    public void setApplicantId(String applicant_id) {
        this.applicant_id = applicant_id;
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

    public void setDate(String date_of_apply) {
        this.date_of_apply = date_of_apply;
    }

    /* get methods */

    public String getApplicationId() {
        return this.application_id;
    }

    public String getApplicantId() {
        return this.applicant_id;
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

    public String getDate() {
        return this.date_of_apply;
    }

    public int getStatus(){
        return this.status;
    }
}
