package fry.oldschool.data;

import java.util.ArrayList;

import fry.oldschool.utils.FryFile;
import fry.oldschool.utils.Fryable;
import fry.oldschool.utils.Searchable;
import fry.oldschool.utils.SearchableList;

public class ContactGroup extends MySQL implements Fryable {

    protected String name;

    protected SearchableList<Contact> contacts = new SearchableList<>();

    protected ContactGroup() {
        super(TYPE_CONTACT_GROUP, 0, USER_ID);
    }

    protected ContactGroup(int id, String name) {
        super(TYPE_CONTACT_GROUP, id, USER_ID);
        this.name = name;
    }

    protected ContactGroup(String name) {
        this(0, name);
    }

    @Override
    public boolean equals(Object o) {
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
        ContactGroup grp = new ContactGroup(id, name);
        grp.type = type;
        grp.user_id = user_id;
        grp.contacts = contacts.clone();
        return grp;
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
    protected void synchronize(MySQL mysql) {
        ContactGroup g = (ContactGroup) mysql;
        name = g.name;
        contacts = g.contacts;
    }

    @Override
    public boolean canEdit() {
        return true;
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

    public void setName(String name) {
        this.name = name;
        if(id != 0) {
            OfflineEntry.update(this);
        }
    }

    public void addContacts(ArrayList<Contact> contacts) {
        for(Contact c : contacts) {
            if(getContactByUserId(c.user_id) == null) {
                this.contacts.add(c);
            }
        }
        if(id != 0) {
            OfflineEntry.update(this);
        }
    }

    public void removeContact(Contact contact) {
        contacts.remove(contact);
        if(id != 0) {
            OfflineEntry.update(this);
        }
    }

    public void removeContacts(ArrayList<Contact> contacts) {
        for(Contact c : contacts) {
            this.contacts.remove(c);
        }
        if(id != 0) {
            OfflineEntry.update(this);
        }
    }

    public void delete() {
        ContactList.groups.remove(this);
        if(id == 0) {
            ConnectionManager.remove(this);
        }else {
            ConnectionManager.remove(TYPE_CONTACT_GROUP,id);
            OfflineEntry.delete(this);
        }
    }

    public String getName() {
        return name;
    }

    public int getNoContacts() {
        return contacts.size();
    }

    public Contact getContact(int index) {
        return contacts.get(index);
    }

}
