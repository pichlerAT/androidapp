package fry.oldschool.data;

import java.util.ArrayList;

public class ShareList {

    /**
     * MySQL TYPE_...
     */
    protected char type;

    protected int id;

    protected ArrayList<ShareStorage> sharedList = new ArrayList<>();

    public ShareList(char type, int id) {
        this.type = type;
        this.id = id;
    }

    public ArrayList<ContactGroup> getShareList() {
        ArrayList<ContactGroup> groupList = new ArrayList<>();
        ContactGroup allContacts = ContactList.getAllContactsGroup();
        ContactGroup allShares = new ContactGroup(allContacts.name);

        for(Contact cont : allContacts.contacts) {
            allShares.contacts.add(new Share(type, id, cont));
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

        return groupList;
    }

    protected void add(byte permission, int user_id, int share_id) {
        sharedList.add(new ShareStorage(permission, user_id, share_id));
    }

    protected void addShare(Share share) {
        sharedList.add(new ShareStorage(share.permission, share.user_id, share.share_id));
    }

    protected boolean remove(Share share) {
        for(int i=0; i<sharedList.size(); ++i) {
            if(sharedList.get(i).share_id == share.id) {
                sharedList.remove(i);
                return true;
            }
        }
        return false;
    }

    protected boolean hasUserId(int id) {
        for(ShareStorage s : sharedList) {
            if(s.user_id == id) {
                return true;
            }
        }
        return false;
    }

    protected void removeShare(int share_id) {
        for(int i=0; i<sharedList.size(); ++i) {
            if(sharedList.get(i).share_id == share_id) {
                sharedList.remove(i);
                return;
            }
        }
    }

    protected static class ShareStorage {

        protected byte permission;

        protected int user_id;

        protected int share_id;

        protected ShareStorage(byte permission,int user_id, int share_id) {
            this.permission = permission;
            this.user_id = user_id;
            this.share_id = share_id;
        }

    }

}