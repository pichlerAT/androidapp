package com.frysoft.notifry.data;

import java.util.ArrayList;

import com.frysoft.notifry.utils.Logger;

public class ShareList {

    /**
     * MySQL TYPE_...
     */
    protected char type;

    protected int share_id;

    protected ArrayList<ShareStorage> storages = new ArrayList<>();

    public ShareList(char type, int share_id) {
        Logger.Log("ShareList", "ShareList(char,int)");
        this.type = type;
        this.share_id = share_id;
    }

    protected void add(Contact cont) {
        Logger.Log("ShareList", "add(Contact)");
        add(cont, Share.PERMISSION_VIEW);
    }

    protected void add(Contact cont, byte permission) {
        Logger.Log("ShareList", "add(Contact,byte)");
        Share share = new Share(type, 0, cont.user_id, permission, share_id, cont.email, cont.name);
        share.create();
    }

    protected void addStorage(byte permission, int id, int user_id) {
        Logger.Log("ShareList", "addStorage(byte,int,int)");
        storages.add(new ShareStorage(permission, id, user_id));
    }

    protected boolean remove(Share share) {
        Logger.Log("ShareList", "remove(Share)");
        for(int i = 0; i< storages.size(); ++i) {
            if(storages.get(i).id == share.id) {
                storages.remove(i);
                return true;
            }
        }
        return false;
    }

    public ArrayList<ContactGroup> getList() {
        Logger.Log("ShareList", "getList()");
        ArrayList<ContactGroup> groupList = new ArrayList<>();
        ContactGroup allContacts = ContactList.getAllContactsGroup();
        ContactGroup allShares = new ContactGroup(allContacts.name);

        for(Contact cont : allContacts.contacts) {
            allShares.contacts.add(new Share(type, Share.PERMISSION_NONE, share_id, cont));
        }

        for(ShareStorage storage : storages) {
            Share share = (Share) allShares.getContactByUserId(storage.user_id);
            share.type = type;
            share.id = storage.id;
            share.permission = storage.permission;
            share.share_id = share_id;
        }

        for(ContactGroup grp : ContactList.getGroups()) {
            ContactGroup grpShare = new ContactGroup(grp.id, grp.name);
            for(Contact cont : grp.contacts) {
                grpShare.contacts.add(allShares.getContactByUserId(cont.user_id));
            }
            groupList.add(grpShare);
        }

        groupList.add(allShares);

        return groupList;
    }

    public boolean isSharedWithUserId(int id) {
        Logger.Log("ShareList", "isSharedWithUserId(int)");
        for(ShareStorage s : storages) {
            if(s.user_id == id) {
                return true;
            }
        }
        return false;
    }

    protected static class ShareStorage {

        protected byte permission;

        protected int id;

        protected int user_id;

        protected ShareStorage(byte permission,int id, int user_id) {
            Logger.Log("ShareList$ShareStorage", "ShareStorage(byte,int,int)");
            this.permission = permission;
            this.id = id;
            this.user_id = user_id;
        }

    }

}