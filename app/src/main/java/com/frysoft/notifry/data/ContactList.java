package com.frysoft.notifry.data;

import com.frysoft.notifry.utils.FryFile;
import com.frysoft.notifry.utils.Logger;
import com.frysoft.notifry.utils.SearchableList;

import java.util.ArrayList;
import java.util.Collection;

public class ContactList {

    protected static ArrayList<ContactGroup> groups=new ArrayList<>();

    protected static ArrayList<Contact> contactRequests=new ArrayList<>();

    protected static final String ALL_CONTACTS = "All Contacts";

    static {

        groups.add(new ContactGroup(ALL_CONTACTS));

    }

    protected static void writeTo(FryFile file) {
        Logger.Log("ContactList", "writeTo(FryFile)");
        file.writeObjects(getAllContacts());
        file.writeObjects(groups.subList(0, groups.size()-1).toArray());
    }

    protected static void resetData() {
        groups = new ArrayList<>();
        groups.add(new ContactGroup(ALL_CONTACTS));
    }

    protected static void readFrom(FryFile fry) {
        Logger.Log("ContactList", "readFrom(FryFile)");
        ContactGroup all = groups.get(0);

        int NoContacts = fry.readArrayLength();
        for(int i=0; i<NoContacts; ++i) {
            all.contacts.add(new Contact(fry));
        }

        int NoContactGroups = fry.readArrayLength();
        for(int i=0; i<NoContactGroups; ++i) {
            groups.add(groups.size()-1, new ContactGroup(fry));
        }
    }

    //protected static void synchronizeContactsFromMySQL(FryFile fry) {
    protected static void synchronizeFromMySQL(FryFile fry) {
        ContactGroup all = groups.get(groups.size() - 1);
        boolean[] isOnline = new boolean[all.contacts.size()];
        int NoContacts = fry.readArrayLength();

        for(int i=0; i<NoContacts; ++i) {
            Contact onlineEntry = new Contact(fry);
            int index = all.getContactIndexByUserId(onlineEntry.user_id);

            if(index < 0) {
                if(!ConnectionManager.hasEntry(onlineEntry, MySQLEntry.BASETYPE_DELETE)) {
                    all.contacts.add(onlineEntry);
                }

            }else {
                isOnline[index] = true;
                all.contacts.get(index).synchronize(onlineEntry);
            }
        }
        for(int i=isOnline.length-1; i>=0; --i) {
            if(!isOnline[i]) {
                all.contacts.remove(i);
            }
        }

        isOnline = new boolean[groups.size() - 1];
        int NoContactGroups = fry.readArrayLength();

        for(int i=0; i<NoContactGroups; ++i) {
            ContactGroup onlineEntry = new ContactGroup(fry);
            int index = getContactGroupIndexById(onlineEntry.id);

            if(index < 0) {
                if(!ConnectionManager.hasEntry(onlineEntry, MySQLEntry.BASETYPE_DELETE)) {
                    groups.add(groups.size() - 1, onlineEntry);
                }

            }else {
                isOnline[index] = true;
                groups.get(index).synchronize(onlineEntry);
            }
        }
        for(int i=isOnline.length-1; i>=0; --i) {
            if(!isOnline[i]) {
                groups.remove(i);
            }
        }

        contactRequests=new ArrayList<>();
        int NoContactRequests = fry.readArrayLength();

        for(int i=0; i<NoContactRequests; ++i) {
            contactRequests.add(new ContactRequest(fry));
        }
    }
/*
    protected static void synchronizeContactGroupsFromMySQL(FryFile fry) { // TODO Stefan: recheck whole method
        Logger.Log("ContactList", "synchronizeContactGroupsFromMySQL(String[])");

        int lastIndex = 0;
        boolean[] offIsOn = new boolean[groups.size()-1];
        int[] onIndex = new int[groups.size()-1];
        ContactGroup all = groups.get(groups.size() - 1);
        ContactGroup[] onBackup = new ContactGroup[groups.size()-1];

        int NoContactGroups = fry.readArrayLength();
        for(int i=0; i<NoContactGroups; ++i) {
            ContactGroup on = new ContactGroup(fry);

            int off_index = getContactGroupIndexById(on.id);
            if(off_index >= 0) {
                offIsOn[off_index] = true;
                ContactGroup off = groups.get(off_index);
                if(!off.equals(on)) {
                    onBackup[lastIndex] = on;
                    onIndex[lastIndex] = off_index;
                    ++lastIndex;
                }
            }else if(!ConnectionManager.hasEntry(on, (char)(MySQLEntry.TYPE_CONTACT_GROUP | MySQLEntry.BASETYPE_DELETE))) {
                groups.add(groups.size() - 1, on);
            }
        }
        // TODO accept online or offline?
        /*
        if(acceptOnline) ->
        for(int i=0; i<lastIndex; ++i) {
            groups.set(onIndex[i],onBackup[i]);
        }
        *//*
        for(int i=offIsOn.length-1; i>=0; --i) {
            if(!offIsOn[i] && groups.get(i).isOnline()) {
                groups.remove(i);
            }
        }
    }
*//*
    protected static void synchronizeContactRequestsFromMySQL(FryFile fry) {
        Logger.Log("ContactList", "synchronizeContactRequestsFromMySQL(String[])");

        contactRequests=new ArrayList<>();

        int NoContactRequests = fry.readArrayLength();
        for(int i=0; i<NoContactRequests; ++i) {
            contactRequests.add(new ContactRequest(fry));
        }
    }
*/
    public static boolean isEmpty() {
        Logger.Log("ContactList", "isEmpty()");
        return (groups.get(groups.size() - 1).contacts.size() == 0);
    }

    protected static void removeContactRequestById(int id) {
        Logger.Log("ContactList", "removeContactRequestById(int)");
        for(int i=0; i<contactRequests.size(); ++i) {
            if(contactRequests.get(i).id == id) {
                contactRequests.remove(i);
            }
        }
    }

    public static int getContactGroupIndexById(int group_id) {
        Logger.Log("ContactList", "getContactGroupIndexById(int)");
        for(int i=0; i<groups.size(); ++i) {
            if(groups.get(i).id == group_id) {
                return i;
            }
        }
        return -1;
    }

    protected static Contact getContactByUserId(int user_id) {
        Logger.Log("ContactList", "getContactByUserId(int)");
        return groups.get(groups.size()-1).getContactByUserId(user_id);
    }

    protected static ContactGroup getContactGroupById(int group_id) {
        Logger.Log("ContactList", "getContactGroupById(int)");
        for(ContactGroup g : groups) {
            if(g.id == group_id) {
                return g;
            }
        }
        return null;
    }

    protected static void removeContact(Contact cont) {
        for(ContactGroup grp : groups) {
            grp.removeContact(cont);
        }
    }

    public static void deleteContact(Contact contact) {
        Logger.Log("ContactList", "deleteContact(Contact)");
        for(ContactGroup grp : groups) {
            grp.removeContact(contact);
        }
        contact.delete();
    }

    public static void sendRequest(String email) {
        Logger.Log("ContactList", "sendRequest(String)");
        Contact.createRequest(email);
    }

    public static void createContactGroup(String name) {
        Logger.Log("ContactList", "createContactGroup(String)");
        ContactGroup grp=new ContactGroup(name);
        grp.create();
        groups.add(groups.size()-1, grp);
    }

    public static void search(String... keyWords) {
        Logger.Log("ContactList", "search(String...)");
        for(ContactGroup grp : groups) {
            grp.contacts.search(keyWords);
        }
    }

    public static ContactGroup getAllContactsGroup() {
        Logger.Log("ContactList", "getAllContactsGroup()");
        return groups.get(groups.size() - 1);
    }

    public static SearchableList<Contact> getAllContacts() {
        Logger.Log("ContactList", "getAllContacts()");
        return getAllContactsGroup().contacts;
    }

    public static ArrayList<ContactGroup> getAllGroups() {
        Logger.Log("ContactList", "getAllGroups()");
        return groups;
    }

    public static Collection<ContactGroup> getGroups() {
        Logger.Log("ContactList", "getGroups()");
        return groups.subList(0, groups.size() - 1);
    }

    public static int getNoContacts() {
        Logger.Log("ContactList", "getNoContacts()");
        return getAllContacts().size();
    }

    public static int getNoGroups() {
        Logger.Log("ContactList", "getNoGroups()");
        return (groups.size() - 1);
    }

    public static int getNoRequests() {
        Logger.Log("ContactList", "getNoRequests()");
        return contactRequests.size();
    }

    public static ContactGroup getGroup(int index) {
        Logger.Log("ContactList", "getGroup(int)");
        return groups.get(index);
    }

    public static ArrayList<Contact> getRequests() {
        Logger.Log("ContactList", "getRequests()");
        return contactRequests;
    }

}
