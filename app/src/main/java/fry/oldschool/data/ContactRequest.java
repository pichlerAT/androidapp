package fry.oldschool.data;

public class ContactRequest {

    protected int id;

    protected int user_id;

    protected String email;

    protected String name;

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

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    protected static class Send extends MySQL {

        protected String email;

        protected Send(String email) {
            super(TYPE_CONTACT_REQUEST, 0, USER_ID);
            this.email = email;
        }

        @Override
        public boolean equals(Object o) {
            return false;
        }

        @Override
        public Object backup() {
            return null;
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

        @Override
        protected void synchronize(MySQL mysql) { }

        @Override
        public boolean canEdit() {
            return false;
        }
    }

    protected static class Accept extends MySQL {

        protected int id;

        protected Accept(int id) {
            super(TYPE_CONTACT_REQUEST, id, 0);
        }

        @Override
        public boolean equals(Object o) {
            return false;
        }

        @Override
        public Object backup() {
            return null;
        }

        @Override
        protected boolean mysql() {
            String resp = getLine(DIR_CONTACT_REQUEST + "accept.php", "&id=" + id);
            if(resp != null) {
                ContactList.removeContactRequestById(id);
                ConnectionManager.sync_contact = true;
                ConnectionManager.sync();
                return true;
            }
            return false;
        }

        @Override
        protected void synchronize(MySQL mysql) { }

        @Override
        public boolean canEdit() {
            return false;
        }
    }

}
