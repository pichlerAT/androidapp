package fry.oldschool.utils;

public class ContactRequest {

    protected int id;

    protected int user_id;

    public String email;

    public String name;

    protected ContactRequest(int id,int user_id,String email,String name) {
        this.id = id;
        this.user_id = user_id;
        this.email = email;
        this.name = name;
    }

    public void accept() {
        if(App.conLis.contactRequests.remove(this)) {
            App.conMan.add(new Accept(id,user_id,email,name));
        }
    }

    public void decline() {
        if(App.conLis.contactRequests.remove(this)) {
            App.conMan.add(new Decline(id));
        }
    }

    protected static class Send extends Entry {

        protected String email;

        protected Send(String email) {
            this.email = email;
        }

        @Override
        protected String getConManString() {
            return TYPE_CONTACTREQUEST_SEND + "" +email;
        }

        @Override
        protected boolean mysql_update() {
            String resp = connect("contact/request/send.php", "&contact_email=" + email);

            if(resp.equals("suc")) {
                return true;
            }

            if(resp.equals("err_crs2") || resp.equals("err_crs3") || resp.equals("err_crs4")) {
                return true;
            }

            return false;
        }
    }

    protected static class Accept extends Entry {

        protected int id;

        protected int user_id;

        protected String email;

        protected String name;

        protected Accept(int id,int user_id,String email,String name) {
            this.id = id;
            this.user_id = user_id;
            this.email = email;
            this.name = name;
        }

        protected Accept(String line) {
            String[] r = line.split(S);
            id = Integer.parseInt(r[0]);
            user_id = Integer.parseInt(r[1]);
            email = r[2];
            name = r[3];
        }

        @Override
        protected String getConManString() {
            return TYPE_CONTACTREQUEST_ACCEPT + "" + id + S + user_id + S + email + S + name;
        }

        @Override
        protected boolean mysql_update() {
            String resp = connect("contact/request/accept.php", "&contact_id=" + id);

            if (resp.equals("suc")) {
                int contact_id = Integer.parseInt(resp.substring(3));
                App.conLis.groups.get(App.conLis.groups.size() - 1).contacts.add(new Contact(contact_id,user_id,email,name));
                return true;
            }

            return false;
        }
    }

    protected static class Decline extends Entry {

        protected int id;

        protected Decline(int id) {
            this.id = id;
        }

        protected Decline(String line) {
            id = Integer.parseInt(line);
        }

        @Override
        protected String getConManString() {
            return TYPE_CONTACTREQUEST_DECLINE + "" + id;
        }

        @Override
        protected boolean mysql_update() {
            String resp = connect("contact/request/decline.php", "&request_id=" + id);

            return resp.equals("suc");
        }
    }
}
