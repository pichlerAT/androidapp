package fry.oldschool.utils;

import java.util.ArrayList;

public class ContactGroup extends OnlineEntry {

    protected int id;

    public String name;

    public ArrayList<Contact> contacts=new ArrayList<>();

    protected ContactGroup(String line) {
        String[] r = line.split(S);
        if(r.length == 1) {
            name = r[0];
            return;
        }
        id = Integer.parseInt(r[0]);
        name = r[1];
        if(r[2].equals("n")) {
            return;
        }
        for(int i=2;i<r.length;++i) {
            Contact c = App.conLis.findContactByUserId(Integer.parseInt(r[i]));
            if(c != null) {
                this.contacts.add(c);
            }
        }
        if(id == 0) {
            App.conMan.add(this);
        }
    }

    public void setContacts(ArrayList<Contact> contacts){
        this.contacts = contacts;
    }

    public void rename(String name) {
        this.name = name;
        if(id != 0) {
            App.conMan.add(new Update(id));
        }
    }

    public void addContacts(ArrayList<Contact> contacts) {
        for(Contact c : contacts) {
            if(findContactById(c.id) == null) {
                this.contacts.add(c);
            }
        }
        if(id != 0) {
            App.conMan.add(new Update(id));
        }
    }

    public void removeContact(Contact contact) {
        contacts.remove(contact);
        if(id != 0) {
            App.conMan.add(new Update(id));
        }
    }

    public void removeContacts(ArrayList<Contact> contacts) {
        for(Contact c : contacts) {
            this.contacts.remove(c);
        }
        if(id != 0) {
            App.conMan.add(new Update(id));
        }
    }

    public void delete() {
        App.conLis.groups.remove(this);
        if(id == 0) {
            App.conMan.remove(this);
        }else {
            App.conMan.add(new Delete(id));
        }
    }

    protected Contact findContactById(int id) {
        for(Contact c : contacts) {
            if(c.id == id) {
                return c;
            }
        }
        return null;
    }
    protected Contact findContactByUserId(int user_id) {
        for(Contact c : contacts) {
            if(c.user_id == user_id) {
                return c;
            }
        }
        return null;
    }

    @Override
    protected boolean mysql_update() {
        String resp = connect("contact/group/create.php","&group_name="+name+"&contacts="+getContactsString());
        if(resp.substring(0,3).equals("suc")) {
            id = Integer.parseInt(resp.substring(3));
            return true;
        }
        return false;
    }

    protected String getContactsString() {
        if(contacts.size() == 0) {
            return "n";
        }
        String s = "";
        for(Contact c : contacts) {
            s += c.user_id + S ;
        }
        return s;
    }

    protected static class Delete extends Entry {

        protected int group_id;

        protected Delete(int group_id) {
            this.group_id = group_id;
        }

        protected Delete(String line) {
            group_id = Integer.parseInt(line);
        }

        @Override
        protected boolean mysql_update() {
            String resp = connect("contact/group/delete.php","&group_id="+group_id);

            return resp.equals("suc");
        }

        @Override
        protected String getConManString() {
            return TYPE_CONTACTGROUP_DELETE + "" + group_id;
        }
    }

    protected static class Update extends Entry {

        protected int id;

        protected Update(int id) {
            this.id = id;
        }

        protected Update(String line) {
            id = Integer.parseInt(line);
        }

        @Override
        protected boolean mysql_update() {
            ContactGroup grp = App.conLis.findContactGroupById(id);
            if(grp == null) {
                return true;
            }
            String resp = connect("contact/group/update.php","&group_id="+id+"&group_name="+grp.name+"&contacts="+grp.getContactsString());

            return resp.equals("suc");
        }

        @Override
        protected String getConManString() {
            return TYPE_CONTACTGROUP_UPDATE + "" + id;
        }
    }

}
