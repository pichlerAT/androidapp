package com.frysoft.notifry.data;

import java.util.ArrayList;

import com.frysoft.notifry.utils.Logger;

public class ShareList {

    /**
     * MySQL TYPE_...
     */
    protected char type;

    protected int id;

    protected ArrayList<ShareStorage> sharedList = new ArrayList<>();

    public ShareList(char type, int id) {
        Logger.Log("ShareList", "ShareList(char,int)");
        this.type = type;
        this.id = id;
    }

    protected void add(byte permission, int user_id, int share_id) {
        Logger.Log("ShareList", "add(byte,int,int)");
        sharedList.add(new ShareStorage(permission, user_id, share_id));
    }

    protected void addShare(Share share) {
        Logger.Log("ShareList", "addShare(Share)");
        sharedList.add(new ShareStorage(share.permission, share.user_id, share.share_id));
    }

    protected boolean remove(Share share) {
        Logger.Log("ShareList", "remove(Share)");
        for(int i=0; i<sharedList.size(); ++i) {
            if(sharedList.get(i).share_id == share.id) {
                sharedList.remove(i);
                return true;
            }
        }
        return false;
    }

    public ArrayList<ContactGroup> getShareList() {
        Logger.Log("ShareList", "getShareList()");
        ArrayList<ContactGroup> groupList = new ArrayList<>();
        ContactGroup allContacts = ContactList.getAllContactsGroup();
        ContactGroup allShares = new ContactGroup(allContacts.name);

        for(Contact cont : allContacts.contacts) {
            allShares.contacts.add(new Share(type, Share.PERMISSION_NONE, id, cont));
        }

        for(ShareStorage storage : sharedList) {
            Share share = (Share) allShares.getContactByUserId(storage.user_id);
            share.permission = storage.permission;
            share.share_id = storage.share_id;
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
        for(ShareStorage s : sharedList) {
            if(s.user_id == id) {
                return true;
            }
        }
        return false;
    }

    public void addShare(Contact contact) {
        Logger.Log("ShareList", "addShare(Contact)");
        Share share = new Share(type, Share.PERMISSION_VIEW, id, contact);
        addShare(share);
        share.create();
    }

    public void addShare(ArrayList<Contact> contacts) {
        Logger.Log("ShareList", "addShare(ArrayList<Contact>)");
        for(Contact contact : contacts) {
            addShare(contact);
        }
    }

    public void addShare(Contact contact,byte permission) {
        Logger.Log("ShareList", "addShare(Contact,byte)");
        Share share = new Share(type, permission, id, contact);
        addShare(share);
        share.create();
    }

    public void addShare(ArrayList<Contact> contacts,byte permission) {
        Logger.Log("ShareList", "addShare(ArrayList<Contact>,byte)");
        for(Contact contact : contacts) {
            addShare(contact,permission);
        }
    }

    public void addShare(ArrayList<Contact> contacts,byte[] permissions) {
        Logger.Log("ShareList", "addShare(ArrayList<Contact>,byte[])");
        for(int i=0;i<contacts.size();++i) {
            addShare(contacts.get(i),permissions[i]);
        }
    }

    public void removeShare(Share share) {
        Logger.Log("ShareList", "removeShare(Share)");
        for(int i=0; i<sharedList.size(); ++i) {
            if(sharedList.get(i).share_id == share.id) {
                sharedList.remove(i);
                share.delete();
            }
        }
    }

    public void removeShare(ArrayList<Share> shares) {
        Logger.Log("ShareList", "removeShare(ArrayList<Share>)");
        for(Share share : shares) {
            removeShare(share);
        }
    }

    protected static class ShareStorage {

        protected byte permission;

        protected int user_id;

        protected int share_id;

        protected ShareStorage(byte permission,int user_id, int share_id) {
            Logger.Log("ShareList$ShareStorage", "ShareStorage(byte,int,int)");
            this.permission = permission;
            this.user_id = user_id;
            this.share_id = share_id;
        }

    }

}