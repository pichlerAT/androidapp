package fry.oldschool.utils;

public class ContactRequest {

    protected static class Send extends Entry {

        protected String email;

        protected Send(String email) {
            this.email = email;
        }

        @Override
        protected byte getType() {
            return type_contactrequest_send;
        }

        @Override
        protected String getConnectionManagerString() {
            return (super.getConnectionManagerString() + SEP_1 + email);
        }

        @Override
        protected boolean mysql_update() {
            String resp = connect("contact/request/send.php", "&contact_email=" + email);

            if (resp != null && !resp.equals("err_cr0") && !resp.equals("err_cr1") && !resp.equals("err_cr5")) {
                if (!resp.equals("suc")) {
                    error(resp);
                }
                App.conMan.remove(this);
                return false;
            }
            return true;
        }
    }

    protected static class Accept extends Entry {

        protected Contact cont;

        protected Accept(Contact cont) {
            this.cont = cont;
        }

        @Override
        protected byte getType() {
            return type_contactrequest_accept;
        }

        @Override
        protected String getConnectionManagerString() {
            return (super.getConnectionManagerString() + SEP_1 + cont.id + SEP_1 + cont.email + SEP_1 + cont.name);
        }

        @Override
        protected boolean mysql_update() {
            String resp = connect("contact/request/accept.php", "&contact_id=" + cont.id);

            if (resp != null) {
                if (!resp.equals("suc")) {
                    error(resp);
                }
                App.conMan.remove(this);
                App.conLis.addContact(cont);
                return false;
            }
            return true;
        }
    }
}
