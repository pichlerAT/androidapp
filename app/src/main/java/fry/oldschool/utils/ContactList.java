package fry.oldschool.utils;

import java.util.ArrayList;

public class ContactList extends MySQL {

    public ArrayList<ContactGroup> groups=new ArrayList<>();

    protected ContactList(String... splitLine) {
        ContactGroup cg0=new ContactGroup("All Contacts");
        groups.add(cg0);

        for(String s : splitLine) {
            String[] r=s.split(",");
            cg0.add(new Contact(Integer.parseInt(r[0]),r[1],r[2]));
        }
    }

    protected boolean mysql_update() {
        String resp=connect("contact/get.php","");
        if(resp!=null && resp.length()>0) {
            updateContacts(resp.substring(1).split(";"));
            return true;
        }
        return false;
    }

    protected void updateContacts(String... splitLine) {
        ContactGroup cg0=groups.get(0);
        for(String s : splitLine) {
            String[] r=s.split(",");
            int id=Integer.parseInt(r[0]);
            Contact cont=getContact(id);
            if(cont == null) {
                cont=new Contact(id,r[1],r[2]);
                cg0.add(cont);
            }else {
                cont.name = r[2];
            }
        }
    }

    protected Contact getContact(int id) {
        for(Contact cont : groups.get(0).contacts) {
            if(cont.id == id) {
                return cont;
            }
        }
        return null;
    }

    public ContactGroup getContactGroup(int index) {
        return groups.get(index);
    }

    public void deleteContact(Contact cont) {
        for(ContactGroup grp : groups) {
            grp.contacts.remove(cont);
        }
        App.conMan.add(cont);
    }

    public void sendRequest(String email) {
        App.conMan.add(new ContactRequest(email));
    }
}