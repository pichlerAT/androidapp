package fry.oldschool.utils;

public class Contact extends MySQL {

    public String email;

    public String name;

    protected int id;

    protected Contact(String email,String name) {
        this.email = email;
        this.name = name;
    }

    protected Contact(int id,String email,String name) {
        this(email,name);
        this.id = id;
    }

    @Override
    protected byte getType() {
        return -1;
    }

    @Override
    protected boolean mysql_update() {
        String resp = connect("contact/delete.php","contact_id="+id);

        if(resp == null) {
            return true;
        }

        App.conMan.remove(this);
        return false;
    }

    @Override
    protected String getString() {
        return ( id + "," + email + "," + name );
    }

}
