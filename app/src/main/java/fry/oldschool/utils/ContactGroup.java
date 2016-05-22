package fry.oldschool.utils;

import java.util.ArrayList;

public class ContactGroup extends Entry {

    protected int id;

    public String name;

    public ArrayList<Contact> contacts=new ArrayList<>();

    protected ContactGroup(String... r) {
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
            this.contacts.add(App.conLis.findContactById(Integer.parseInt(r[i])));
        }
    }

    public void setContacts(ArrayList<Contact> contacts){
        this.contacts = contacts;
    }

    public void rename(String name) {
        this.name = name;
        App.conMan.add(this);
    }

    public void addContacts(ArrayList<Contact> contacts) {
        for(Contact c : contacts) {
            if(findContactById(c.id) == null) {
                this.contacts.add(c);
            }
        }
        App.conMan.add(this);
    }

    public void removeContacts(ArrayList<Contact> contacts) {
        for(Contact c : contacts) {
            this.contacts.remove(c);
        }
        App.conMan.add(this);
    }

    public void delete() {
        App.conLis.groups.remove(this);
        App.conMan.add(new Delete(id));
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
        if(id == 0) {
            String resp = connect("contact/group/create.php","&group_name="+name+"&contacts="+getContactsString());
            if(resp.substring(0,3).equals("suc")) {
                id = Integer.parseInt(resp.substring(3));
                return true;
            }
        }else {
            String resp = connect("contact/group/update.php","&group_id="+id+"&group_name="+name+"&contacts="+getContactsString());
            if(resp.equals("suc")) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected String getString() {
        return TYPE_CONTACTGROUP + "" + id + ";" + name + ";" + getContactsString();
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
        protected String getString() {
            return TYPE_CONTACTGROUP_DELETE + "" + group_id;
        }
    }

}
