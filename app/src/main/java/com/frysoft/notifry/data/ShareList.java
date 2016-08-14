package com.frysoft.notifry.data;

import com.frysoft.notifry.utils.FryFile;
import com.frysoft.notifry.utils.Fryable;
import com.frysoft.notifry.utils.Logger;

import java.util.ArrayList;

public class ShareList implements Fryable {

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

    @Override
    public void writeTo(FryFile fry) {
        fry.writeObjects(storages);
    }

    public int size() {
        return storages.size();
    }

    public int getId(int index) {
        return storages.get(index).id;
    }

    public byte getPermission(int index) {
        return storages.get(index).permission;
    }

    public void readFrom(FryFile fry) {
        int NoStorages = fry.getArrayLength();
        for(int i=0; i<NoStorages; ++i) {
            storages.add(new ShareStorage(fry));
        }
    }

    public void add(Contact cont) {
        Logger.Log("ShareList", "add(Contact)");
        add(cont, Share.PERMISSION_VIEW);
    }

    public void add(Contact cont, byte permission) {
        Logger.Log("ShareList", "add(Contact,byte)");
        Share share = new Share(type, 0, cont.user_id, share_id, permission, cont.email, cont.name);
        share.create();
    }

    protected void addStorage(byte permission, int id, int user_id) {
        Logger.Log("ShareList", "addStorage(byte,int,int)");
        storages.add(new ShareStorage(id, user_id, permission));
    }

    public boolean remove(Share share) {
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
            allShares.contacts.add(new Share(type, share_id, Share.PERMISSION_NONE, cont));
        }

        for(ShareStorage storage : storages) {
            Share share = (Share) allShares.getContactByUserId(storage.user_id);
            if(share == null) {
                continue;
            }
            //share.type = type;
            share.id = storage.id;
            share.permission = storage.permission;
            //share.share_id = share_id;
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

    protected static class ShareStorage implements Fryable {

        protected byte permission;

        protected int id;

        protected int user_id;

        protected ShareStorage(FryFile fry) {
            id = fry.getUnsignedInt();
            user_id = fry.getUnsignedInt();
            permission = fry.getUnsignedByte();
        }

        protected ShareStorage(int id, int user_id, byte permission) {
            Logger.Log("ShareList$ShareStorage", "ShareStorage(byte,int,int)");
            this.permission = permission;
            this.id = id;
            this.user_id = user_id;
        }

        @Override
        public void writeTo(FryFile fry) {
            fry.writeUnsignedInt(id);
            fry.writeUnsignedInt(user_id);
            fry.writeUnsignedByte(permission);
        }
    }

}