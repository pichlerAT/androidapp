package fry.oldschool.data;

import java.util.ArrayList;

public class ShareGroup {

    public String name;

    public ArrayList<Share> contacts = new ArrayList<>();

    public ShareGroup(String name) {
        this.name = name;
    }

    public Share findShareByUserId(int id) {
        for(Share s : contacts) {
            if(s.id == id) {
                return s;
            }
        }
        return null;
    }

}
