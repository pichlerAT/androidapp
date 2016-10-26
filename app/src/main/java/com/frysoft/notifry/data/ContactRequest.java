package com.frysoft.notifry.data;

import com.frysoft.notifry.utils.FryFile;
import com.frysoft.notifry.utils.Logger;

public class ContactRequest extends Contact {

    protected ContactRequest(FryFile fry) {
        super(fry);
    }

    protected ContactRequest(int id, int user_id, String email, String name) {
        super(TYPE_CONTACT_REQUEST, id, user_id, email, name);
    }

    @Override
    protected void addData(MySQL mysql) {
    }

    @Override
    protected boolean mysql() {
        if(isOffline()) {
            MySQL mysql = new MySQL(getPath(), PHP_CREATE);
            mysql.add("contact_email", email);
            FryFile fry = mysql.execute();

            if(fry == null) {
                return false;
            }

            update_time = fry.readLong();
            return true;

        }else {
            MySQL mysql = new MySQL(getPath(), PHP_UPDATE);
            mysql.addId("id", id);
            FryFile fry = mysql.execute();

            if(fry == null) {
                return false;
            }

            user_id = User.getId();

            id = fry.readId();
            update_time = fry.readLong();
            ContactList.getAllContactsGroup().addContact(this);
            return true;
        }
    }

    @Override
    protected char getType() {
        return TYPE_CONTACT_REQUEST;
    }

    public void accept() {
        Logger.Log("Contact", "accept()");
        update();
        ContactList.contactRequests.remove(this);
    }

    public void decline() {
        Logger.Log("Contact", "decline()");
        delete();
        ContactList.contactRequests.remove(this);
    }

}
