package com.technoindians.portfolio;

/**
 * Created by Girish on 7/8/16.
 */
public class Images_ {

    private String wall_id;
    private String path;
    private String like_count;
    private String user_id;

    public void setUserId(String user_id) {
        this.user_id = user_id;
    }

    public void setWallId(String wall_id) {
        this.wall_id = wall_id;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setLikeCount(String like_count) {
        this.like_count = like_count;
    }

    public String getUserId() {
        return this.user_id;
    }

    public String getWallId() {
        return this.wall_id;
    }

    public String getPath() {
        return this.path;
    }

    public String getLikeCount() {
        return this.like_count;
    }
}
