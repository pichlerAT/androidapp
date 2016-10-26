package com.frysoft.notifry.data;

import com.frysoft.notifry.data.value.ValueString;
import com.frysoft.notifry.utils.FryFile;
import com.frysoft.notifry.utils.Logger;
import com.frysoft.notifry.utils.SearchableList;
import com.frysoft.notifry.utils.Date;

import java.util.ArrayList;

public class ContactGroup extends MySQLEntry {

    protected ValueString name = new ValueString();

    protected boolean contacts_changed = false;

    protected SearchableList<Contact> contacts = new SearchableList<>();

    protected ContactGroup(FryFile fry) {
        super(fry);
        Logger.Log("ContactGroup", "ContactGroup(FryFile)");

        name.readFrom(fry);

        ContactGroup all = ContactList.groups.get(ContactList.groups.size() - 1);
        int NoContacts = fry.readArrayLength();
        for(int i=0; i<NoContacts; ++i) {
            Contact cont = all.getContactByUserId(fry.readId());
            if(cont != null) {
                contacts.add(cont);
            }
        }

        if(fry instanceof FryFile.Compact) {
            contacts_changed = (fry.readChar() == 1);
        }

        if(name.isChanged() || contacts_changed) {
            update();
        }
    }

    protected ContactGroup(int id, int user_id, String name) {
        super(id, user_id, Date.getMillis());
        Logger.Log("ContactGroup", "ContactGroup(int,String)");
        this.name.setValue(name);
    }

    protected ContactGroup(String name) {
        this(0, 0, name);
        Logger.Log("ContactGroup", "ContactGroup(String)");
    }

    @Override
    public boolean equals(Object o) {
        Logger.Log("ContactGroup", "equals(Object)");
        if(o instanceof ContactGroup) {
            ContactGroup g = (ContactGroup) o;
            if(g.id != id || !g.name.equals(name) || g.contacts.baseLength() != contacts.baseLength()) {
                for (int i = 0; i < contacts.baseLength(); ++i) {
                    if (!g.contacts.getBase(i).equals(contacts.getBase(i))) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    protected void addData(MySQL mysql) {
        mysql.add("name", name);
        mysql.addString("contacts", getContactsString());
    }

    @Override
    public boolean canEdit() {
        Logger.Log("ContactGroup", "canEdit()");
        return true;
    }

    @Override
    public int getShareId() {
        return 0;
    }

    @Override
    protected void sync(MySQLEntry entry) {
        ContactGroup cg = (ContactGroup) entry;
        boolean update = false;

        if(name.doUpdate(cg.name)) {
            update = true;
        }

        contacts = cg.contacts;

        if(update) {
            update();
        }
    }

    @Override
    protected char getType() {
        return TYPE_CONTACT_GROUP;
    }

    @Override
    public void writeTo(FryFile fry) {
        Logger.Log("ContactGroup", "writeTo(FryFile)");
        super.writeTo(fry);
        name.writeTo(fry);

        int[] uids = new int[contacts.size()];
        for(int i=0; i<uids.length; ++i) {
            uids[i] = contacts.get(i).user_id;
        }
        fry.writeIntArray(uids);

        fry.writeChar((char)(contacts_changed ? 1 : 0));
    }

    @Override
    protected void remove() {
        ContactList.groups.remove(this);
    }

    public String getContactsString() {
        Logger.Log("ContactGroup", "getContactsString()");
        FryFile fry = new FryFile.Split((char)0);
        fry.writeArrayLength(contacts.size());
        for(Contact c : contacts) {
            fry.writeId(c.user_id);
        }
        return fry.getWrittenString();
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
        this.name.setValue(name);
        update();
    }

    public void addContact(Contact contact) {
        if(getContactByUserId(contact.user_id) == null) {
            contacts.add(contact);
            contacts_changed = true;
            update();
        }
    }

    public void addContacts(ArrayList<Contact> contacts) {
        for(Contact contact : contacts) {
            if(getContactByUserId(contact.user_id) == null) {
                this.contacts.add(contact);
            }
        }
        contacts_changed = true;
        update();
    }

    public void removeContact(Contact contact) {
        contacts.remove(contact);
        contacts_changed = true;
        update();
    }

    public void removeContacts(ArrayList<Contact> contacts) {
        for(Contact c : contacts) {
            this.contacts.remove(c);
        }
        contacts_changed = true;
        update();
    }

    public String getName() {
        Logger.Log("ContactGroup", "getName()");
        return name.getValue();
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
