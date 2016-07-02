package fry.oldschool.data;

import java.util.ArrayList;

import fry.oldschool.utils.FryFile;
import fry.oldschool.utils.Fryable;
import fry.oldschool.utils.Searchable;
import fry.oldschool.utils.SearchableList;

public class ContactGroup extends MySQL implements Fryable {

    public String name;

    public SearchableList<Contact> contacts = new SearchableList<>();

    protected ContactGroup() { }

    /**
     * Used for the "All Contacts" ContactGroup
     * @param name Name of the ContactGroup
     */
    public ContactGroup(String name) {
        this.name = name;
    }

    public ContactGroup(int id, String name) {
        this.type = TYPE_CONTACT_GROUP;
        this.id = id;
        this.name = name;
    }

    @Override
    protected boolean mysql() {
        String resp = getLine(DIR_CONTACT_GROUP + "create.php","&group_name="+name+"&contacts="+getContactsString());
        if(resp != null) {
            id = Integer.parseInt(resp);
            return true;
        }
        return false;
    }

    @Override
    public void writeTo(FryFile fry) {
        fry.write(id);
        fry.write(name);

        int[] uids = new int[contacts.size()];
        for(int i=0; i<uids.length; ++i) {
            uids[i] = contacts.get(i).user_id;
        }
        fry.write(uids);
    }

    @Override
    public void readFrom(FryFile fry) {
        id = fry.getInt();
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

    protected String getUpdateString() {
        return ("&group_id="+id+"&group_name="+name+"&contacts="+getContactsString());
    }

    public boolean equals(ContactGroup grp) {
        if(grp.id != id || grp.contacts.size()!=contacts.size() || !grp.name.equals(name)) {
            return false;
        }
        for(int i=0; i<grp.contacts.size(); ++i) {
            if(grp.contacts.get(i).user_id != contacts.get(i).user_id) {
                return false;
            }
        }
        return true;
    }

    public String getContactsString() {
        String s = "" + contacts.size();
        for(Contact c : contacts) {
            s += S + c.user_id ;
        }
        return s;
    }

    public int getContactIndexByUserId(int user_id) {
        for(int i=0; i<contacts.size(); ++i) {
            if(contacts.get(i).user_id == user_id) {
                return i;
            }
        }
        return -1;
    }

    public Contact getContactByUserId(int user_id) {
        for(Contact c : contacts) {
            if(c.user_id == user_id) {
                return c;
            }
        }
        return null;
    }

    public void setContacts(SearchableList<Contact> contacts){
        this.contacts = contacts;
    }

    public void rename(String name) {
        this.name = name;
        if(id != 0) {
            OfflineEntry.update(TYPE_CONTACT_GROUP, id);
        }
    }

    public void addContacts(ArrayList<Contact> contacts) {
        for(Contact c : contacts) {
            if(getContactByUserId(c.user_id) == null) {
                this.contacts.add(c);
            }
        }
        if(id != 0) {
            OfflineEntry.update(TYPE_CONTACT_GROUP, id);
        }
    }

    public void removeContact(Contact contact) {
        contacts.remove(contact);
        if(id != 0) {
            OfflineEntry.update(TYPE_CONTACT_GROUP, id);
        }
    }

    public void removeContacts(ArrayList<Contact> contacts) {
        for(Contact c : contacts) {
            this.contacts.remove(c);
        }
        if(id != 0) {
            OfflineEntry.update(TYPE_CONTACT_GROUP, id);
        }
    }

    public void delete() {
        ContactList.groups.remove(this);
        if(id == 0) {
            ConnectionManager.remove(this);
        }else {
            ConnectionManager.remove(TYPE_CONTACT_GROUP,id);
            OfflineEntry.delete(TYPE_CONTACT_GROUP, id);
        }
    }

}
