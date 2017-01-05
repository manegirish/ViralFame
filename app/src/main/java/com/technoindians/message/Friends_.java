package com.technoindians.message;

/**
 * @author
 * Girish Mane <girishmane8692@gmail.com>
 * Created on 20/07/2016
 * Last modified 30/07/2016
 *
 */

public class Friends_ {

        int status;
        String user_id;
        String name;
        String type;
        String skill;
        String images;

        /* set methods */
        public void setStatus(int status) {
            this.status = status;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setRole(String skill) {
            this.skill = skill;
        }

        public void setImages(String images) {
            this.images = images;
        }


        /* get methods */
        public int getStatus() {
            return this.status;
        }

        public String getUser_id() {
            return this.user_id;
        }

        public String getName() {
            return this.name;
        }

        public String getType() {
            return this.type;
        }

        public String getSkill() {
            return this.skill;
        }

        public String getImages() {
            return this.images;
        }

}


