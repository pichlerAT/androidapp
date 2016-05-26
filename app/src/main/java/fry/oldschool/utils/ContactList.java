package fry.oldschool.utils;

import java.util.ArrayList;
import java.util.Iterator;

public class ContactList extends MySQL {

    public ArrayList<ContactGroup> groups=new ArrayList<>();

    public ArrayList<Contact> contactRequests=new ArrayList<>();

    protected ContactList(String... splitLine) {
        ContactGroup all=new ContactGroup("All Contacts");
        groups.add(all);

        for(int i=2;i<splitLine.length;i+=3) {
            all.contacts.add(new Contact(Integer.parseInt(splitLine[i-2]),splitLine[i-1],splitLine[i]));
        }
    }

    @Override
    protected boolean mysql_update() {
        boolean get_contacts = false;
        boolean get_requests = false;

        ArrayList<String> respList=connect_list("contact/get.php","");
        Iterator<String> it = respList.iterator();
        String resp = it.next();
        if(resp.substring(0,3).equals("suc")) {
            get_contacts = true;
            if(resp.length()>4) {
                updateContacts(resp.substring(3).split(";"));
            }
            while(it.hasNext()) {
                ContactGroup g = new ContactGroup(it.next().split(";"));
                if(!hasContactGroup(g.id)) {
                    groups.add(groups.size() - 1, g);
                }
            }
        }

        resp=connect("contact/request/get.php","");
        if(resp.substring(0,3).equals("suc")) {
            get_requests = true;
            if(resp.length()>4) {
                setContactRequests(resp.substring(4).split(";"));
            }
        }

        return (get_contacts && get_requests);
    }

    protected void setContactRequests(String... r) {
        contactRequests=new ArrayList<>();
        for(int i=2;i<r.length;i+=3) {
            contactRequests.add(new Contact(Integer.parseInt(r[i-2]),r[i-1],r[i]));
        }
    }

    protected void updateContacts(String... r) {
        ContactGroup cg0=groups.get(groups.size()-1);
        for(int i=2;i<r.length;i+=3) {
            int id = Integer.parseInt(r[i-2]);
            Contact cont = findContactById(id);
            if(cont == null) {
                cont = new Contact(id,r[i-1],r[i]);
                cg0.contacts.add(cont);
            }else {
                cont.email = r[i-1];
                cont.name = r[i];
            }
        }
    }

    protected boolean hasContactGroup(int group_id) {
        for(ContactGroup g : groups) {
            if(g.id == group_id) {
                return true;
            }
        }
        return false;
    }

    protected Contact findContactById(int contact_id) {
        return groups.get(groups.size()-1).findContactById(contact_id);
    }

    public void deleteContact(Contact cont) {
        for(ContactGroup grp : groups) {
            grp.contacts.remove(cont);
        }
        App.conMan.add(new Contact.Delete(cont.id));
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
