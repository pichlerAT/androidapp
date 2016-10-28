package com.frysoft.notifry.data;

import java.util.ArrayList;

public class ShareGroup extends ArrayList<Share> {

    protected String name;

    public ShareGroup(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Share getShareByContact(Contact contact) {
        for(int i=0; i<size(); ++i) {
            Share share = get(i);
            if(share.contact.getUserId() == contact.getUserId()) {
                return share;
            }
        }
        return null;
    }

}
