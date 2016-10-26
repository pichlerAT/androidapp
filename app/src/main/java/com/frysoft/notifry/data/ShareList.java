package com.frysoft.notifry.data;

import com.frysoft.notifry.utils.FryFile;
import com.frysoft.notifry.utils.Fryable;
import com.frysoft.notifry.utils.Logger;

import java.util.ArrayList;

public class ShareList implements Fryable {

    /**
     * MySQL TYPE_...
     */
    protected MySQLEntry sharedEntry;

    public ArrayList<Share> shares = new ArrayList<>();

    public ShareList(MySQLEntry sharedEntry) {
        Logger.Log("ShareList", "ShareList(char,int)");
        this.sharedEntry = sharedEntry;
    }

    @Override
    public void writeTo(FryFile fry) {
        fry.writeObjects(shares);
    }

    public int size() {
        return shares.size();
    }

    public Share get(int index) {
        return shares.get(index);
    }

    public int getId(int index) {
        return shares.get(index).id;
    }

    public Share getByUserId(int user_id) {
        for (Share share : shares) {
            if (share.user_id == user_id) {
                return share;
            }
        }
        return null;
    }

    public Share getById(int id) {
        for(Share share : shares) {
            if(share.id == id) {
                return share;
            }
        }
        return null;
    }

    public byte getPermission(int index) {
        return shares.get(index).permission.getValue();
    }

    public void readFrom(FryFile fry) {
        int NoShares = fry.readArrayLength();
        for(int i=0; i<NoShares; ++i) {
            Share share = new Share(fry);
            share.sharedEntry = sharedEntry;
            shares.add(share);
        }
    }

    public void add(Contact cont) {
        Logger.Log("ShareList", "add(Contact)");
        add(cont, Share.PERMISSION_VIEW);
    }

    public void add(Contact contact, byte permission) {
        Logger.Log("ShareList", "add(Contact,byte)");
        if(getByUserId(contact.user_id) == null) {
            Share share = new Share(permission, contact, sharedEntry);
            shares.add(share);
            share.create();

        }else {
            // TODO message: entry is already shared with this user
        }
    }

    public boolean remove(Share share) {
        Logger.Log("ShareList", "remove(Share)");
        return shares.remove(share);
    }

    public ArrayList<ContactGroup> getList() {
        Logger.Log("ShareList", "getList()");
        ArrayList<ContactGroup> groupList = new ArrayList<>();
        ContactGroup allContacts = ContactList.getAllContactsGroup();
        ContactGroup allShares = new ContactGroup(allContacts.name.getValue());
/*
        for(Contact contact : allContacts.contacts) {
            Share share = getByUserId(contact.user_id);
            if(share == null) {
                allShares.contacts.add()
            }else {
                allShares.contacts.add(share);
            }
        }
        /*
        for(Contact cont : allContacts.contacts) {
            allShares.contacts.add(new Share(0, cont.user_id, Share.PERMISSION_NONE, cont.email.getValue(), cont.name.getValue(), sharedEntry));
        }
/*
        for(ShareStorage storage : storages) {
            Share share = (Share) allShares.getContactByUserId(storage.user_id);
            if(share == null) {
                continue;
            }
            //share.type = type;
            share.id = storage.id;
            share.permission.setValue(storage.permission);
            //share.share_id = share_id;
        }
*/
        for(ContactGroup grp : ContactList.getGroups()) {
            ContactGroup grpShare = new ContactGroup(grp.id, grp.user_id, grp.name.getValue());
            for(Contact cont : grp.contacts) {
                grpShare.contacts.add(allShares.getContactByUserId(cont.user_id));
            }
            groupList.add(grpShare);
        }

        groupList.add(allShares);

        return groupList;
    }

    public boolean isSharedWithUserId(int user_id) {
        Logger.Log("ShareList", "isSharedWithUserId(int)");
        for(Share share : shares) {
            if(share.user_id == user_id) {
                return true;
            }
        }
        /*
        for(ShareStorage s : storages) {
            if(s.user_id == id) {
                return true;
            }
        }
        */
        return false;
    }

}