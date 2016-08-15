package com.frysoft.notifry.data;

import com.frysoft.notifry.utils.App;
import com.frysoft.notifry.utils.FryFile;
import com.frysoft.notifry.utils.Fryable;
import com.frysoft.notifry.utils.Logger;
import com.frysoft.notifry.utils.Searchable;

public class Contact extends MySQL implements Fryable, Searchable {

    protected String email;

    protected String name;

    protected static Contact createRequest(String email) {
        Logger.Log("Contact", "createRequest(String)");
        if(!App.hasInternetConnection) {
            return null;
        }
        Contact cont = new Contact(TYPE_CONTACT_REQUEST, 0, 0, email, null);
        cont.create();
        return cont;
    }

    protected Contact(FryFile fry) {
        super(fry);
        Logger.Log("Contact", "Contact(FryFile)");
        email = fry.getString();
        name = fry.getString();
    }

    protected Contact(char type, int id, int user_id, String email, String name) {
        super(type, id, user_id);
        Logger.Log("Contact", "Contact(char,int,int,String,String)");
        this.email = email;
        this.name = name;
    }

    @Override
    public void writeTo(FryFile fry) {
        Logger.Log("Contact", "writeTo(FryFile)");
        super.writeTo(fry);
        fry.writeString(email);
        fry.writeString(name);
    }

    @Override
    public boolean search(String... keyWords) {
        Logger.Log("Contact", "search(String...)");
        String name = this.name.toLowerCase();
        for(String keyWord : keyWords) {
            if(name.contains(keyWord)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean mysql_create() { // contact request send
        Logger.Log("Contact", "mysql_create()");
        return (executeMySQL(DIR_CONTACT_REQUEST+"create.php", "&email="+email) != null);
    }

    @Override
    protected boolean mysql_update() { // contact request accept
        Logger.Log("Contact", "mysql_update()");
        FryFile fry = executeMySQL(DIR_CONTACT_REQUEST+"update.php", "&share_id="+id);
        if(fry == null) {
            return false;
        }
        id = fry.getUnsignedInt();
        user_id = User.getId();
        ContactList.getAllContactsGroup().contacts.add(this);
        return true;
    }

    @Override
    protected boolean mysql_delete() { // contact request decline
        Logger.Log("Contact", "mysql_delete()");
        if(type(TYPE_CONTACT_REQUEST)) {
            return (executeMySQL(DIR_CONTACT_REQUEST+"delete.php", "&share_id="+id) != null);

        }else {
            return (executeMySQL(DIR_CONTACT+"delete.php", "&share_id="+id) != null);
        }
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

    public String getEmail() {
        Logger.Log("Contact", "getEmail()");
        return email;
    }

    public String getName() {
        Logger.Log("Contact", "getName()");
        return name;
    }

}
