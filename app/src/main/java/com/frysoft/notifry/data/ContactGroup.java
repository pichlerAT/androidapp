package com.frysoft.notifry.data;

import com.frysoft.notifry.utils.FryFile;
import com.frysoft.notifry.utils.Logger;
import com.frysoft.notifry.utils.SearchableList;

import java.util.ArrayList;

public class ContactGroup extends MySQLEntry {

    protected String name;

    protected SearchableList<Contact> contacts = new SearchableList<>();

    protected ContactGroup(FryFile fry) {
        super(fry);
        Logger.Log("ContactGroup", "ContactGroup(FryFile)");

        name = fry.getString();

        ContactGroup all = ContactList.groups.get(ContactList.groups.size() - 1);
        int NoContacts = fry.getChar();
        for(int i=0; i<NoContacts; ++i) {
            Contact cont = all.getContactByUserId(fry.getInt());
            if(cont != null) {
                contacts.add(cont);
            }
        }
    }

    protected ContactGroup(int id, String name) {
        super(TYPE_CONTACT_GROUP, id, 0);
        Logger.Log("ContactGroup", "ContactGroup(int,String)");
        this.name = name;
    }

    protected ContactGroup(String name) {
        this(0, name);
        Logger.Log("ContactGroup", "ContactGroup(String)");
    }

    @Override
    public boolean equals(Object o) {
        Logger.Log("ContactGroup", "equals(Object)");
        if(o instanceof ContactGroup) {
            ContactGroup g = (ContactGroup) o;
            if(g.id != id || !g.name.equals(name) || g.contacts.size() != contacts.size()) {
                return false;
            }
            for(int i=0; i<contacts.size(); ++i) {
                if(!g.contacts.get(i).equals(contacts.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public ContactGroup backup() {
        Logger.Log("ContactGroup", "backup()");
        ContactGroup grp = new ContactGroup(id, name);
        grp.type = type;
        grp.user_id = user_id;
        grp.contacts = contacts.clone();
        return grp;
    }

    @Override
    protected boolean mysql_create() {
        Logger.Log("ContactGroup", "mysql_create()");
        String resp = executeMySQL(DIR_CONTACT_GROUP+"create.php","&name="+name+"&contacts="+getContactsString());
        if(resp != null) {
            id = Integer.parseInt(resp);
            return true;
        }
        return false;
    }

    @Override
    protected boolean mysql_update() {
        Logger.Log("ContactGroup", "mysql_update()");
        return (executeMySQL(DIR_CONTACT_GROUP+"update.php", "&id="+signed(id)+"&name="+name+"&contacts="+getContactsString()) != null);
    }

    @Override
    protected boolean mysql_delete() {
        Logger.Log("ContactGroup", "mysql_delete()");
        return (executeMySQL(DIR_CONTACT_GROUP+"delete.php", "&id="+signed(id)) != null);
    }

    @Override
    protected void synchronize(MySQL mysql) {
        Logger.Log("ContactGroup", "synchronize(MySQL)");
        ContactGroup g = (ContactGroup) mysql;
        name = g.name;
        contacts = g.contacts;
    }

    @Override
    public boolean canEdit() {
        Logger.Log("ContactGroup", "canEdit()");
        return true;
    }

    @Override
    public void writeTo(FryFile fry) {
        Logger.Log("ContactGroup", "writeTo(FryFile)");
        super.writeTo(fry);
        fry.writeString(name);

        int[] uids = new int[contacts.size()];
        for(int i=0; i<uids.length; ++i) {
            uids[i] = contacts.get(i).user_id;
        }
        fry.writeIntArray(uids);
    }

    public String getContactsString() {
        Logger.Log("ContactGroup", "getContactsString()");
        String s = "" + contacts.size();
        for(Contact c : contacts) {
            s += S + signed(c.user_id) ;
        }
        return s;
    }

    public int getContactIndexByUserId(int user_id) {
        Logger.Log("ContactGroup", "getContactIndexByUserId(int)");
        for(int i=0; i<contacts.size(); ++i) {
            if(contacts.get(i).user_id == user_id) {
                return i;
            }
        }
        return -1;
    }

    public Contact getContactByUserId(int user_id) {
        Logger.Log("ContactGroup", "getContactByUserId(int)");
        for(Contact c : contacts) {
            if(c.user_id == user_id) {
                return c;
            }
        }
        return null;
    }

    public void setContacts(SearchableList<Contact> contacts){
        Logger.Log("ContactGroup", "setContacts(SearchableList<Contact>)");
        this.contacts = contacts;
    }

    public void setName(String name) {
        Logger.Log("ContactGroup", "setName(String)");
        this.name = name;
        update();
    }

    public void addContacts(ArrayList<Contact> contacts) {
        Logger.Log("ContactGroup", "addContacts(ArrayList<Contact>)");
        for(Contact c : contacts) {
            if(getContactByUserId(c.user_id) == null) {
                this.contacts.add(c);
            }
        }
        update();
    }

    public void removeContact(Contact contact) {
        Logger.Log("ContactGroup", "removeContact(Contact)");
        contacts.remove(contact);
        update();
    }

    public void removeContacts(ArrayList<Contact> contacts) {
        Logger.Log("ContactGroup", "removeContacts(ArrayList<Contact>)");
        for(Contact c : contacts) {
            this.contacts.remove(c);
        }
        update();
    }

    @Override
    public void delete() {
        Logger.Log("ContactGroup", "delete()");
        super.delete();
        ContactList.groups.remove(this);
    }

    public String getName() {
        Logger.Log("ContactGroup", "getName()");
        return name;
    }

    public int getNoContacts() {
        Logger.Log("ContactGroup", "getNoContacts()");
        return contacts.size();
    }

    public Contact getContact(int index) {
        Logger.Log("ContactGroup", "getContact(int)");
        return contacts.get(index);
    }

    public SearchableList<Contact> getContacts() {
        Logger.Log("ContactGroup", "getContacts()");
        return contacts;
    }

}
