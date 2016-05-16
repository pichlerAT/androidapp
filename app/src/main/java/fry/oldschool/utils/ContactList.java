package fry.oldschool.utils;

import java.util.ArrayList;

public class ContactList extends MySQL {

    protected ArrayList<ContactGroup> groups=new ArrayList<>();

    protected ContactList(String... splitLine) {
        ContactGroup cg0=new ContactGroup("All Contacts");
        groups.add(cg0);

        for(String s : splitLine) {
            String[] r=s.split(",");
            cg0.add(new Contact(Integer.parseInt(r[0]),r[1],r[2]));
        }

        System.out.println("*****| ContactList#ContactList(String...):");
        for(Contact cont : cg0.contacts) {
            System.out.println("*****| "+cont.getString());
        }
    }

    @Override
    protected byte getType() {
        return 0;
    }

    @Override
    protected boolean mysql_update() {
        String resp=connect("get_contacts.php","");
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
                cont=new Contact(r[1],r[2]);
                cg0.add(cont);
            }else {
                cont.name = r[2];
            }
        }
        System.out.println("*****| ContactList#updateContacts(String...):");
        for(Contact cont : cg0.contacts) {
            System.out.println("*****| "+cont.getString());
        }
    }

    protected Contact getContact(int id) {
        for(Contact cont : groups.get(0).contacts) {
            if(cont.id==id) {
                return cont;
            }
        }
        return null;
    }
}
