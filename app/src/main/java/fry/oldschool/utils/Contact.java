package fry.oldschool.utils;

public class Contact extends Entry {

    protected int id;

    public String email;

    public String name;

    protected Contact(int id,String email,String name) {
        this.id = id;
        this.email = email;
        this.name = name;
    }

    @Override
    protected byte getType() {
        return type_contact;
    }

    @Override
    protected boolean mysql_update() {
        String resp = connect("contact/delete.php","&contact_id="+id);

        if(resp == null) {
            return true;
        }

        App.conMan.remove(this);
        return false;
    }

    @Override
    protected String getConnectionManagerString() {
        return ( super.getConnectionManagerString() + SEP_1 + id + SEP_1 + email + SEP_1 + name );
    }

}
