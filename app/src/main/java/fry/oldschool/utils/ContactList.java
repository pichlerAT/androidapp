package fry.oldschool.utils;

import java.util.ArrayList;

public class ContactList extends MySQL {

    public ArrayList<ContactGroup> groups=new ArrayList<>();

    public ArrayList<Contact> contactRequests=new ArrayList<>();

    protected ContactList(String... splitLine) {
        ContactGroup cg0=new ContactGroup("All Contacts");
        groups.add(cg0);

        for(String s : splitLine) {
            String[] r=s.split(",");
            cg0.add(new Contact(Integer.parseInt(r[0]),r[1],r[2]));
        }
    }

    protected boolean mysql_update() {
        boolean get_contacts = false;
        boolean get_requests = false;

        String resp=connect("contact/get.php","");
        if(resp!=null && resp.substring(0,3).equals("suc")) {
            if(resp.length()>4) {
                updateContacts(resp.substring(4).split(";"));
            }
            get_contacts = true;
        }

        resp=connect("contact/request/get.php","");
        if(resp!=null && resp.substring(0,3).equals("suc")) {
            if(resp.length()>4) {
                setContactRequests(resp.substring(4).split(";"));
            }
            get_requests = true;
        }

        return (get_contacts && get_requests);
    }

    protected void setContactRequests(String... splitLine) {
        for(String s : splitLine) {
            String[] r = s.split(",");
            int contact_id = Integer.parseInt(r[0]);
            if(!requestLoaded(contact_id)) {
                contactRequests.add(new Contact(Integer.parseInt(r[0]),r[1],r[2]));
            }
        }
    }

    protected void updateContacts(String... splitLine) {
        int contactCount = Integer.parseInt(splitLine[0]);
        ContactGroup cg0=groups.get(groups.size()-1);

        for(int i=1;i<contactCount+1;++i) {
            String[] r = splitLine[i].split(",");
            int id = Integer.parseInt(r[0]);
            Contact cont = findContactById(id);
            if(cont == null) {
                cont = new Contact(id,r[1],r[2]);
                cg0.add(cont);
            }else {
                cont.name = r[2];
            }
        }
        for(int i=contactCount+1;i<splitLine.length;++i) {
            String[] r = splitLine[i].split(",");
            int id = Integer.parseInt(r[0]);
            ContactGroup grp = findContactGroupById(id);
            if(grp == null) {
                grp = new ContactGroup(id, r);
                groups.add(groups.size()-1,grp);
            }else {
                grp.name = r[1];
                grp.update(r);

            }
        }
    }

    protected ContactGroup findContactGroupById(int group_id) {
        for(ContactGroup g : groups) {
            if(g.id == group_id) {
                return g;
            }
        }
        return null;
    }

    protected Contact findContactById(int contact_id) {
        return groups.get(groups.size()-1).findContactById(contact_id);
    }

    protected void addContact(Contact cont) {
        groups.get(groups.size()-1).add(cont);
    }

    protected Contact getContact(int index) {
        return groups.get(groups.size()-1).contacts.get(index);
    }

    protected boolean requestLoaded(int contact_id) {
        for(Contact c : contactRequests) {
            if(c.id == contact_id) {
                return true;
            }
        }
        return false;
    }

    public ContactGroup getContactGroup(int index) {
        return groups.get(index);
    }

    public void deleteContact(Contact cont) {
        for(ContactGroup grp : groups) {
            grp.contacts.remove(cont);
        }
        App.conMan.add(cont);
    }

    public void sendRequest(String email) {
        App.conMan.add(new ContactRequest.Send(email));
    }

    public void acceptRequest(Contact cont) {
        if(contactRequests.remove(cont)) {
            App.conMan.add(new ContactRequest.Accept(cont));
        }
    }

    public void declineRequest(Contact cont) {
        if(contactRequests.remove(cont)) {
            App.conMan.add(new ContactRequest.Decline(cont.id));
        }
    }

    public void createContactGroup(String name) {
        ContactGroup grp=new ContactGroup(name);
        App.conMan.add(grp);
        groups.add(groups.size()-1,grp);
    }
}
