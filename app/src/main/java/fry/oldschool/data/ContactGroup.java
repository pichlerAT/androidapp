package fry.oldschool.data;

import java.util.ArrayList;

public class ContactGroup extends OnlineEntry {

    public String name;

    public ArrayList<Contact> contacts;

    public ContactGroup(String name) {
        this.name = name;
        type = TYPE_CONTACT_GROUP;
        id = 0;
        contacts = new ArrayList<>();
    }

    public ContactGroup(int id,String name,ArrayList<Contact> contacts) {
        this.type = TYPE_CONTACT_GROUP;
        this.id = id;
        this.name = name;
        this.contacts = contacts;
        if(id == 0) {
            ConnectionManager.add(this);
        }
    }

    @Override
    protected boolean mysql() {
        String resp = getLine(DIR_CONTACT_GROUP + "create.php","&group_name="+name+"&contacts="+getContactsString());
        if(resp.substring(0,3).equals("suc")) {
            id = Integer.parseInt(resp.substring(3));
            return true;
        }
        return false;
    }

    protected String getUpdateString() {
        return ("&group_id="+id+"&group_name="+name+"&contacts="+getContactsString());
    }

    public String getContactsString() { // TODO convert int to char
        if(contacts.size() == 0) {
            return "n";
        }
        String s = "";
        for(Contact c : contacts) {
            s += c.user_id + S ;
        }
        return s;
    }

    public Contact findContactById(int id) {
        for(Contact c : contacts) {
            if(c.id == id) {
                return c;
            }
        }
        return null;
    }

    public Contact findContactByUserId(int user_id) {
        for(Contact c : contacts) {
            if(c.user_id == user_id) {
                return c;
            }
        }
        return null;
    }

    public void setContacts(ArrayList<Contact> contacts){
        this.contacts = contacts;
    }

    public void rename(String name) {
        this.name = name;
        if(id != 0) {
            ConnectionManager.add(new Update(TYPE_CONTACT_GROUP,id));
        }
    }

    public void addContacts(ArrayList<Contact> contacts) {
        for(Contact c : contacts) {
            if(findContactById(c.id) == null) {
                this.contacts.add(c);
            }
        }
        if(id != 0) {
            ConnectionManager.add(new Update(TYPE_CONTACT_GROUP,id));
        }
    }

    public void removeContact(Contact contact) {
        contacts.remove(contact);
        if(id != 0) {
            ConnectionManager.add(new Update(TYPE_CONTACT_GROUP,id));
        }
    }

    public void removeContacts(ArrayList<Contact> contacts) {
        for(Contact c : contacts) {
            this.contacts.remove(c);
        }
        if(id != 0) {
            ConnectionManager.add(new Update(TYPE_CONTACT_GROUP,id));
        }
    }

    public void delete() {
        ContactList.groups.remove(this);
        if(id == 0) {
            ConnectionManager.remove(this);
        }else {
            ConnectionManager.remove(TYPE_CONTACT_GROUP,id);
            ConnectionManager.add(new Delete(TYPE_CONTACT_GROUP,id));
        }
    }

}
