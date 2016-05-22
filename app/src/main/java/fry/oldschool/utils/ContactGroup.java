package fry.oldschool.utils;

import java.util.ArrayList;
import java.util.Iterator;

public class ContactGroup extends Entry {

    protected int id;

    public String name;

    public ArrayList<Contact> contacts=new ArrayList<>();

    protected ContactGroup(String name) {
        this.id = 0;
        this.name = name;
    }

    protected ContactGroup(int id,String[] contacts) {
        this.id = id;
        update(contacts);
    }

    public void add(Contact contact) {
        contacts.add(contact);
    }

    public void setContacts(ArrayList<Contact> contacts){
        this.contacts = contacts;
    }

    public void rename(String name) {
        this.name = name;
        App.conMan.add(this);
    }

    public void addContacts(ArrayList<Contact> contacts) {
        for(Contact c : contacts) {
            if(findContactById(c.id) == null) {
                this.contacts.add(c);
            }
        }
        App.conMan.add(this);
    }

    public void removeContacts(ArrayList<Contact> contacts) {
        for(Contact c : contacts) {
            this.contacts.remove(c);
        }
        App.conMan.add(this);
    }

    protected void update(String[] contacts) {
        name = contacts[1];
        for(int i=2;i<contacts.length;++i) {
            int contact_id = Integer.parseInt(contacts[i]);
            Contact c = findContactById(contact_id);
            if(c == null) {
                c = App.conLis.findContactById(contact_id);
                if(c == null) {
                    System.err.println("SCHEISSE!!");
                    continue;
                }
                this.contacts.add(i-2,c);
            }
        }
    }

    public void delete() {
        name = null;
        contacts = null;
        App.conLis.groups.remove(this);
        App.conMan.add(this);
    }

    protected Contact findContactById(int contact_id) {
        for(Contact c : contacts) {
            if(c.id == contact_id) {
                return c;
            }
        }
        return null;
    }

    @Override
    protected byte getType() {
        return TYPE_CONTACTGROUP;
    }

    @Override
    protected boolean mysql_update() {
        String resp;
        if(id == 0) {
            resp = connect("contact/group/create.php","&group_name="+name+"&contacts="+getContactsString());
        }else if(name == null) {
            resp = connect("contact/group/delete.php","&group_id="+id);
        }else {
            resp = connect("contact/group/update.php","&group_id="+id+"&group_name="+name+"&contacts="+getContactsString());
        }
        if(resp.substring(0,3).equals("suc")) {
            if(id == 0) {
                id = Integer.parseInt(resp.substring(3));
            }
            return true;
        }
        return false;
    }

    @Override
    protected String getConnectionManagerString() {
        return ( super.getConnectionManagerString() + SEP_1 + id + SEP_1 + name + SEP_1 + getContactsString() );
    }

    protected String getContactsString() {
        String s = "";
        Iterator<Contact> it = contacts.iterator();
        if(it.hasNext()) {
            s += it.next().id;
        }
        while(it.hasNext()) {
            s += SEP_1 + it.next().id;
        }
        return s;
    }
}
