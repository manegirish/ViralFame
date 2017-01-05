package com.technoindians.message;

/**
 * @author
 * Girish Mane <girishmane8692@gmail.com>
 * Created on 20/07/2016
 * Last modified 30/07/2016
 *
 */

public class Details_ {

    String id;
    String description;
    String send_date;
    String respond_by;
    String user_id;
    String self;

    /* set methods */

    public void setId(String id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(String send_date) {
        this.send_date = send_date;
    }

    public void setName(String respond_by) {
        this.respond_by = respond_by;
    }

    public void setUserId(String user_id) {
        this.user_id = user_id;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    /* get methods */

    public String getId() {
        return this.id;
    }

    public String getDescription() {
        return this.description;
    }

    public String getDate() {
        return this.send_date;
    }

    public String getName() {
        return this.respond_by;
    }

    public String getUserId() {
        return this.user_id;
    }

    public String getSelf() {
        return this.self;
    }
}


