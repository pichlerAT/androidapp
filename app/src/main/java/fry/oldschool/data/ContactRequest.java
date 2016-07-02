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
            OfflineEntry.delete(MySQL.TYPE_CONTACT_REQUEST, id);
        }
    }

    protected static class Send extends MySQL {

        protected String email;

        protected Send(String email) {
            this.type = TYPE_CONTACT_REQUEST;
            this.email = email;
        }

        @Override
        protected boolean mysql() {
            String resp = getLine(DIR_CONTACT_REQUEST + "send.php", "&contact_email=" + email);
            if(resp != null) {
                if(resp.equals("err_crs1")) {
                    // TODO Edwin: user not found
                }else if(resp.equals("err_crs2")) {
                    // TODO Edwin: contact already exists
                }else if(resp.equals("err_crs3")) {
                    // TODO Edwin: contact request already exists
                }else {
                    return true;
                }
            }
            return false;
        }
    }

    protected static class Accept extends MySQL {

        protected int id;

        protected Accept(int id) {
            this.type = TYPE_CONTACT_REQUEST;
            this.id = id;
        }

        @Override
        protected boolean mysql() {
            String resp = getLine(DIR_CONTACT_REQUEST + "accept.php", "&id=" + id);
            if(resp != null) {
                ContactList.removeContactRequest(id);
                ConnectionManager.sync_contact = true;
                ConnectionManager.sync();
                return true;
            }
            return false;
        }
    }

}
