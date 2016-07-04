package fry.oldschool.data;

import java.util.ArrayList;
import java.util.Collection;

import fry.oldschool.utils.FryFile;
import fry.oldschool.utils.Logger;
import fry.oldschool.utils.SearchableList;

public class ContactList {

    protected static ArrayList<ContactGroup> groups=new ArrayList<>();

    protected static ArrayList<Contact> contactRequests=new ArrayList<>();

    static {

        groups.add(new ContactGroup("All Contacts"));

    }

    public static void writeTo(FryFile file) {
        Logger.Log("ContactList#writeTo(FryFile)");
        file.write(getAllContacts());
        file.write(groups.subList(0,groups.size()-1).toArray());
    }

    public static void readFrom(FryFile fry) {
        Logger.Log("ContactList#readFrom(FryFile)");
        ContactGroup all = groups.get(groups.size() - 1);

        int NoContacts = fry.getChar();
        for(int i=0; i<NoContacts; ++i) {
            all.contacts.add(new Contact(fry));
        }

        int NoContactGroups = fry.getChar();
        for(int i=0; i<NoContactGroups; ++i) {
            groups.add(groups.size()-1, new ContactGroup(fry));
        }
    }

    public static void synchronizeContactsFromMySQL(String... r) {
        Logger.Log("ContactList#synchronizeContactsFromMySQL(String...)");
        ContactGroup all = groups.get(groups.size() - 1);
        boolean[] offIsOn = new boolean[all.contacts.size()];
        int index = 0;

        int NoContacts = Integer.parseInt(r[index++]);
        for(int i=0; i<NoContacts; ++i) {
            Contact on = new Contact(Integer.parseInt(r[index++]),Integer.parseInt(r[index++]),r[index++],r[index++]);
            int off_index = all.getContactIndexByUserId(on.user_id);

            if(off_index >= 0) {
                offIsOn[off_index] = true;
                Contact off = all.contacts.get(off_index);
                off.email = on.email;
                off.name = on.name;
            }else if(!ConnectionManager.hasEntry((char)(MySQL.TYPE_CONTACT | MySQL.BASETYPE_DELETE), on.id)) {
                all.contacts.add(on);
            }
        }
        for(int i=offIsOn.length-1; i>=0; --i) {
            if(!offIsOn[i]) {
                all.contacts.remove(i);
            }
        }

        offIsOn = new boolean[groups.size()-1];
        ContactGroup[] onBackup = new ContactGroup[groups.size()-1];
        int[] onIndex = new int[groups.size()-1];
        int lastIndex = 0;

        int NoContactGroups = Integer.parseInt(r[index++]);
        for(int i=0; i<NoContactGroups; ++i) {
            ContactGroup on = new ContactGroup(Integer.parseInt(r[index++]),r[index++]);

            NoContacts = Integer.parseInt(r[index++]);
            for(int j=0; j<NoContacts; ++j) {
                Contact cont = all.getContactByUserId(Integer.parseInt(r[index++]));
                if(cont != null) {
                    on.contacts.add(cont);
                }
            }

            int off_index = getContactGroupIndexById(on.id);
            if(off_index >= 0) {
                offIsOn[off_index] = true;
                ContactGroup off = groups.get(off_index);
                if(!off.equals(on)) {
                    onBackup[lastIndex] = on;
                    onIndex[lastIndex] = off_index;
                    ++lastIndex;
                }
            }else if(!ConnectionManager.hasEntry((char)(MySQL.TYPE_CONTACT_GROUP | MySQL.BASETYPE_DELETE), on.id)) {
                groups.add(groups.size() - 1, on);
            }
        }
        // TODO accept online or offline?
        /*
        if(acceptOnline) ->
        for(int i=0; i<lastIndex; ++i) {
            groups.set(onIndex[i],onBackup[i]);
        }
        */
        for(int i=offIsOn.length-1; i>=0; --i) {
            if(!offIsOn[i] && groups.get(i).id != 0) {
                groups.remove(i);
            }
        }
    }

    public static void synchronizeContactRequestsFromMySQL(String... r) {
        Logger.Log("ContactList#synchronizeContactRequestsFromMySQL(String...)");
        contactRequests=new ArrayList<>();
        for(int i=3; i<r.length; i+=4) {
            contactRequests.add(new Contact(Integer.parseInt(r[i-3]),Integer.parseInt(r[i-2]),r[i-1],r[i]));
        }
    }

    public static boolean isEmpty() {
        Logger.Log("ContactList#isEmpty()");
        return (groups.get(groups.size() - 1).contacts.size() == 0);
    }

    protected static void removeContactRequestById(int id) {
        Logger.Log("ContactList#removeContactRequestById(int)");
        for(int i=0; i<contactRequests.size(); ++i) {
            if(contactRequests.get(i).id == id) {
                contactRequests.remove(i);
            }
        }
    }

    public static int getContactGroupIndexById(int group_id) {
        Logger.Log("ContactList#getContactGroupIndexById(int)");
        for(int i=0; i<groups.size(); ++i) {
            if(groups.get(i).id == group_id) {
                return i;
            }
        }
        return -1;
    }

    protected static Contact getContactByUserId(int user_id) {
        Logger.Log("ContactList#getContactByUserId(int)");
        return groups.get(groups.size()-1).getContactByUserId(user_id);
    }

    protected static ContactGroup getContactGroupById(int group_id) {
        Logger.Log("ContactList#getContactGroupById(int)");
        for(ContactGroup g : groups) {
            if(g.id == group_id) {
                return g;
            }
        }
        return null;
    }

    public static void deleteContact(Contact cont) {
        Logger.Log("ContactList#deleteContact(Contact)");
        for(ContactGroup grp : groups) {
            grp.removeContact(cont);
        }
        cont.delete();
    }

    public static void sendRequest(String email) {
        Logger.Log("ContactList#sendRequest(String)");
        Contact.createRequest(email);
    }

    public static void createContactGroup(String name) {
        Logger.Log("ContactList#createContactGroup(String)");
        ContactGroup grp=new ContactGroup(name);
        grp.create();
        groups.add(groups.size()-1, grp);
    }

    public static void search(String... keyWords) {
        Logger.Log("ContactList#search(String...)");
        for(ContactGroup grp : groups) {
            grp.contacts.search(keyWords);
        }
    }

    public static ContactGroup getAllContactsGroup() {
        Logger.Log("ContactList#getAllContactsGroup()");
        return groups.get(groups.size() - 1);
    }

    public static SearchableList<Contact> getAllContacts() {
        Logger.Log("ContactList#getAllContacts()");
        return getAllContactsGroup().contacts;
    }

    public static ArrayList<ContactGroup> getAllGroups() {
        Logger.Log("ContactList#getAllGroups()");
        return groups;
    }

    public static Collection<ContactGroup> getGroups() {
        Logger.Log("ContactList#getGroups()");
        return groups.subList(0, groups.size() - 1);
    }

    public static int getNoContacts() {
        Logger.Log("ContactList#getNoContacts()");
        return getAllContacts().size();
    }

    public static int getNoGroups() {
        Logger.Log("ContactList#getNoGroups()");
        return groups.size();
    }

    public static ContactGroup getGroup(int index) {
        Logger.Log("ContactList#getGroup()");
        return groups.get(index);
    }

    public static int getNoRequests() {
        Logger.Log("ContactList#getNoRequests()");
        return contactRequests.size();
    }

    public static ArrayList<Contact> getRequests() {
        Logger.Log("ContactList#getRequests()");
        return contactRequests;
    }

}
