package fry.oldschool.utils;

import java.util.ArrayList;

public class ContactGroup {

    protected String name;

    protected ArrayList<Contact> contacts=new ArrayList<>();

    protected ContactGroup(String name) {
        this.name = name;
    }

    protected void add(Contact contact) {
        contacts.add(contact);
    }

}
