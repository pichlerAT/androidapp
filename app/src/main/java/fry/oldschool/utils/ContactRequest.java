package fry.oldschool.utils;

public class ContactRequest {

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

        protected Contact contact;

        protected Accept(Contact contact) {
            this.contact = contact;
        }

        protected Accept(String line) {
            String[] r = line.split(";");
            contact = new Contact(Integer.parseInt(r[0]),r[1],r[2]);
        }

        @Override
        protected String getConManString() {
            return TYPE_CONTACTREQUEST_ACCEPT + "" + contact.id + ";" + contact.email + ";" + contact.name;
        }

        @Override
        protected boolean mysql_update() {
            String resp = connect("contact/request/accept.php", "&contact_id=" + contact.id);

            if (resp.equals("suc")) {
                App.conLis.groups.get(App.conLis.groups.size()-1).contacts.add(contact);
                return true;
            }

            return false;
        }
    }

    protected static class Decline extends Entry {

        protected int contact_id;

        protected Decline(int contact_id) {
            this.contact_id = contact_id;
        }

        protected Decline(String line) {
            contact_id = Integer.parseInt(line);
        }

        @Override
        protected String getConManString() {
            return TYPE_CONTACTREQUEST_DECLINE + "" + contact_id;
        }

        @Override
        protected boolean mysql_update() {
            String resp = connect("contact/request/decline.php", "&contact_id=" + contact_id);

            return resp.equals("suc");
        }
    }
}
