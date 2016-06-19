package fry.oldschool.data;

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
        if(ContactList.contactRequests.remove(this)) {
            ConnectionManager.add(new Accept(id));
        }
    }

    public void decline() {
        if(ContactList.contactRequests.remove(this)) {
            ConnectionManager.add(new Delete(OfflineEntry.TYPE_CONTACT_REQUEST,id));
        }
    }

    protected static class Send extends OnlineEntry {

        protected String email;

        protected Send(String email) {
            this.type = TYPE_CONTACT_REQUEST;
            this.email = email;
        }

        @Override
        protected boolean mysql() {
            String resp = getLine(DIR_CONTACT_REQUEST + "send.php", "&contact_email=" + email);

            if(resp.equals("suc")) {
                return true;
            }

            if(resp.equals("err_crs2") || resp.equals("err_crs3") || resp.equals("err_crs4")) {
                return true;
            }

            return false;
        }
    }

    protected static class Accept extends OnlineEntry {

        protected int id;

        protected Accept(int id) {
            this.type = TYPE_CONTACT_REQUEST;
            this.id = id;
        }

        @Override
        protected boolean mysql() {
            String resp = getLine(DIR_CONTACT_REQUEST + "accept.php", "&id=" + id);
            if(resp.equals("suc")) {
                ContactList.removeContactRequest(id);
                ConnectionManager.sync_contact = true;
                return true;
            }
            return false;
        }
    }

}
