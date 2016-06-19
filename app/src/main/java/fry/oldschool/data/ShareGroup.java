package fry.oldschool.data;

import java.util.ArrayList;

public class ShareGroup {

    public String name;

    public ArrayList<Share> contacts;

    public ShareGroup(String name) {
        this.name = name;
    }

    public void setPermission(byte permission) {
        for(Share s : contacts) {
            s.setPermission(permission);
        }
    }

}
