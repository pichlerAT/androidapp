package com.frysoft.notifry.data;

import com.frysoft.notifry.data.value.ValueString;
import com.frysoft.notifry.utils.App;
import com.frysoft.notifry.utils.FryFile;
import com.frysoft.notifry.utils.Fryable;
import com.frysoft.notifry.utils.Logger;
import com.frysoft.notifry.utils.Searchable;

public class Contact extends MySQLEntry implements Fryable, Searchable {

    protected ValueString email = new ValueString();

    protected ValueString name = new ValueString();

    protected static Contact createRequest(String email) {
        Logger.Log("Contact", "createRequest(String)");
        if(!App.hasInternetConnection) {
            return null;
        }
        Contact cont = new ContactRequest(0, 0, email, null);
        cont.create();
        return cont;
    }

    protected Contact(FryFile fry) {
        super(fry);
        email.readFrom(fry);
        name.readFrom(fry);
    }

    protected Contact(char type, int id, int user_id, String email, String name) {
        super(id, user_id, 0);
        Logger.Log("Contact", "Contact(char,int,int,String,String)");
        this.email.setValue(email);
        this.name.setValue(name);
    }

    @Override
    public void writeTo(FryFile fry) {
        Logger.Log("Contact", "writeTo(FryFile)");
        super.writeTo(fry);
        email.writeTo(fry);
        name.writeTo(fry);
    }

    @Override
    protected void remove() {
        ContactList.removeContact(this);
    }

    @Override
    public boolean search(String... keyWords) {
        Logger.Log("Contact", "search(String...)");
        String name = this.name.getValue().toLowerCase();
        for(String keyWord : keyWords) {
            if(name.contains(keyWord)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void addData(MySQL mysql) {
    }

    @Override
    public boolean canEdit() {
        return false;
    }

    @Override
    public int getShareId() {
        return 0;
    }

    @Override
    protected void sync(MySQLEntry entry) {
    }

    @Override
    protected char getType() {
        return TYPE_CONTACT;
    }

    @Override
    protected void synchronize(MySQLEntry entry) {
        Contact contact = (Contact) entry;
        email = contact.email;
        name = contact.name;
    }

    public String getEmail() {
        Logger.Log("Contact", "getEmail()");
        return email.getValue();
    }

    public String getName() {
        Logger.Log("Contact", "getName()");
        return name.getValue();
    }

}
