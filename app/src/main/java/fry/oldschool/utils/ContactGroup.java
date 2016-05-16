package fry.oldschool.utils;

import java.util.ArrayList;

public class ContactGroup {

    public String name;

    public ArrayList<Contact> contacts=new ArrayList<>();

    protected ContactGroup(String name) {
        this.name = name;
    }

    public void add(Contact contact) {
        contacts.add(contact);
    }

    public void setContacts(ArrayList<Contact> contacts){
        this.contacts = contacts;
    }

}
