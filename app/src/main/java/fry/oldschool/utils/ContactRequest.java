package fry.oldschool.utils;

public class ContactRequest extends Entry {

    protected String email;

    protected ContactRequest(String email) {
        this.email = email;
    }

    @Override
    protected byte getType() {
        return type_contactrequest;
    }

    @Override
    protected String getConnectionManagerString() {
        return ( super.getConnectionManagerString() + SEP_1 + email );
    }

    @Override
    protected boolean mysql_update() {
        String resp = connect("contact/request.php","&contact_email="+email);

        if(resp == null) {
            return true;
        }

        App.conMan.remove(this);
        return false;
    }
}
