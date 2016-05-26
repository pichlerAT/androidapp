package fry.oldschool.utils;

public class Contact {

    protected int id;

    public String email;

    public String name;

    protected Contact(int id,String email,String name) {
        this.id = id;
        this.email = email;
        this.name = name;
    }

    protected static class Delete extends Entry {

        protected int contact_id;

        protected Delete(int contact_id) {
            this.contact_id = contact_id;
        }

        protected Delete(String line) {
            contact_id = Integer.parseInt(line);
        }

        @Override
        protected boolean mysql_update() {
            String resp = connect("contact/delete.php","&contact_id="+contact_id);

            return resp.equals("suc");
        }

        @Override
        protected String getConManString() {
            return TYPE_CONTACT_DELETE + "" + contact_id;
        }
    }

}
