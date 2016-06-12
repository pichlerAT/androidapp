package fry.oldschool.utils;

public class Contact {

    protected int id;

    protected int user_id;

    public String email;

    public String name;

    protected Contact(int id,int user_id,String email,String name) {
        this.id = id;
        this.user_id = user_id;
        this.email = email;
        this.name = name;
    }

    public boolean equals(Share share) {
        return ( share.user_id == user_id );
    }

    protected static class Delete extends Entry {

        protected int id;

        protected Delete(int id) {
            this.id = id;
        }

        protected Delete(String line) {
            id = Integer.parseInt(line);
        }

        @Override
        protected boolean mysql() {
            String resp = getLine("contact/delete.php","&contact_id="+id);

            return resp.equals("suc");
        }

        @Override
        protected String getConManString() {
            return TYPE_CONTACT_DELETE + "" + id;
        }
    }

}
