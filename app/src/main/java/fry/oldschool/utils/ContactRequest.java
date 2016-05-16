package fry.oldschool.utils;

/**
 * Created by Stefan on 16.05.2016.
 */
public class ContactRequest extends MySQL {

    protected String email;

    protected ContactRequest(String email) {
        this.email = email;
    }

    @Override
    protected byte getType() {
        return 0;
    }

    @Override
    protected boolean mysql_update() {
        String resp = connect("contact/request.php","contact_email="+email);

        if(resp == null) {
            return true;
        }

        App.conMan.remove(this);
        return false;
    }
}
