package fry.oldschool.utils;

import java.util.ArrayList;

public class ContactGroup {

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

    protected Contact findContactById(int contact_id) {
        for(Contact c : contacts) {
            if(c.id == contact_id) {
                return c;
            }
        }
        return null;
    }

}
