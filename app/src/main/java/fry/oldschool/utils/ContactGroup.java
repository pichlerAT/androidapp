package fry.oldschool.utils;

import java.util.ArrayList;

public class ContactGroup extends Entry {

    protected int id;

    public String name;

    public ArrayList<Contact> contacts=new ArrayList<>();

    protected ContactGroup(String line) {
        String[] r = line.split(";");
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
            Contact c = App.conLis.findContactById(Integer.parseInt(r[i]));
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
            App.conMan.add(new Update(this));
        }
    }

    public void addContacts(ArrayList<Contact> contacts) {
        for(Contact c : contacts) {
            if(findContactById(c.id) == null) {
                this.contacts.add(c);
            }
        }
        if(id != 0) {
            App.conMan.add(new Update(this));
        }
    }

    public void removeContact(Contact contact) {
        contacts.remove(contact);
        if(id != 0) {
            App.conMan.add(new Update(this));
        }
    }

    public void removeContacts(ArrayList<Contact> contacts) {
        for(Contact c : contacts) {
            this.contacts.remove(c);
        }
        if(id != 0) {
            App.conMan.add(new Update(this));
        }
    }

    public void delete() {
        App.conLis.groups.remove(this);
        if(id != 0) {
            App.conMan.add(new Delete(id));
        }
    }

    protected Contact findContactById(int contact_id) {
        for(Contact c : contacts) {
            if(c.id == contact_id) {
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

    @Override
    protected String getConManString() {
        return null;
    }

    protected String getContactsString() {
        if(contacts.size() == 0) {
            return "n";
        }
        String s = "";
        for(Contact c : contacts) {
            s += c.id + ";" ;
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

        protected int group_id;

        protected String name;

        protected String contacts;

        protected Update(int group_id,String name,String contacts) {
            this.group_id = group_id;
            this.name = name;
            this.contacts = contacts;
        }

        protected Update(ContactGroup g) {
            group_id = g.id;
            name = g.name;
            contacts = g.getContactsString();
        }

        protected Update(String line) {
            String[] r = line.split(";");
            group_id = Integer.parseInt(r[0]);
            name = r[1];
            contacts = line.substring( r[0].length() + r[1].length() + 2 );
        }

        @Override
        protected boolean mysql_update() {
            String resp = connect("contact/group/update.php","&group_id="+group_id+"&group_name="+name+"&contacts="+contacts);

            return resp.equals("suc");
        }

        @Override
        protected String getConManString() {
            return TYPE_CONTACTGROUP_UPDATE + "" + group_id + ";" + name + ";" + contacts;
        }
    }

}
