package fry.oldschool.data;

import fry.oldschool.utils.App;
import fry.oldschool.utils.FryFile;
import fry.oldschool.utils.Fryable;
import fry.oldschool.utils.Logger;
import fry.oldschool.utils.Searchable;

public class Contact extends MySQL implements Fryable, Searchable {

    protected String email;

    protected String name;

    protected static Contact createRequest(String email) {
        Logger.Log("Contact#createRequest(String)");
        if(!App.hasInternetConnection) {
            return null;
        }
        Contact cont = new Contact(TYPE_CONTACT_REQUEST, 0, 0, email, null);
        cont.create();
        return cont;
    }

    protected Contact(FryFile fry) {
        super(fry);
        Logger.Log("Contact#Contact(FryFile)");
        email = fry.getString();
        name = fry.getString();
    }

    protected Contact(char type, int id, int user_id, String email, String name) {
        super(type, id, user_id);
        Logger.Log("Contact#Contact(char,int,int,String,String)");
        this.email = email;
        this.name = name;
    }

    protected Contact(int id,int user_id,String email,String name) {
        this(TYPE_CONTACT, id, user_id, email, name);
        Logger.Log("Contact#Contact(int,int,String,String)");
    }

    @Override
    public void writeTo(FryFile fry) {
        Logger.Log("Contact#writeTo(FryFile)");
        super.writeTo(fry);
        fry.write(email);
        fry.write(name);
    }

    @Override
    public boolean search(String... keyWords) {
        Logger.Log("Contact#search(String...)");
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
        Logger.Log("Contact#mysql_create()");
        return (getLine(DIR_CONTACT_REQUEST+"send.php", "&email="+email) != null);
    }

    @Override
    protected boolean mysql_update() { // contact request accept
        Logger.Log("Contact#mysql_update()");
        String resp = getLine(DIR_CONTACT_REQUEST+"accept.php", "&id="+id);
        if(resp != null) {
            // TODO get data from resp (id, user_id, name)
            return true;
        }
        return false;
    }

    @Override
    protected boolean mysql_delete() { // contact request decline
        Logger.Log("Contact#mysql_delete()");
        return (getLine(DIR_CONTACT_REQUEST+"decline.php", "&id="+id) != null);
    }

    public void accept() {
        Logger.Log("Contact#accept()");
        create();
        ContactList.contactRequests.remove(this);
    }

    public void decline() {
        Logger.Log("Contact#decline()");
        delete();
        ContactList.contactRequests.remove(this);
    }

    public String getEmail() {
        Logger.Log("Contact#getEmail()");
        return email;
    }

    public String getName() {
        Logger.Log("Contact#getName()");
        return name;
    }

}
