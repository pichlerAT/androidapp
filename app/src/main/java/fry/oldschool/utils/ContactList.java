package fry.oldschool.utils;

import java.util.ArrayList;
import java.util.Iterator;

public class ContactList extends MySQL {

    public void print() {
        System.out.println("---------- ContactList#print ----------");
        System.out.println("------------ ContactGroups ------------");
        for(ContactGroup g : groups) {
            System.out.println("|" + g.id + "|" + g.name + "|" + g.getContactsString() + "|");
        }
        System.out.println("-------------- Contacts ---------------");
        for(Contact c : groups.get(groups.size()-1).contacts) {
            System.out.println("|"+c.id+"|"+c.email+"|"+c.name+"|");
        }
        System.out.println("------------ ContactRequests ----------");
        for(Contact c : contactRequests) {
            System.out.println("|"+c.id+"|"+c.email+"|"+c.name+"|");
        }
        System.out.println("---------------------------------------");
    }

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

        System.out.println("---------- ContactList#mysql_update ----------");

        ArrayList<String> respList=connect_list("contact/get.php","");

        Iterator<String> it = respList.iterator();
        String resp = it.next();
        if(resp.substring(0,3).equals("suc")) {
            get_contacts = true;
            if(resp.length()>4) {
                updateContacts(resp.substring(3).split(";"));
            }
            while(it.hasNext()) {
                ContactGroup g = new ContactGroup(it.next());
                if(!hasContactGroup(g.id) && !hasContactGroupDeleted(g.id)) {
                    groups.add(groups.size() - 1, g);
                }
            }
        }

        resp=connect("contact/request/get.php","");
        if(resp.substring(0,3).equals("suc")) {
            get_requests = true;
            if(resp.length()>3) {
                setContactRequests(resp.substring(3).split(";"));
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
        boolean[] online = new boolean[cg0.contacts.size()];
        for(int i=2;i<r.length;i+=3) {
            int id = Integer.parseInt(r[i-2]);
            Contact cont = cg0.findContactById(id);
            if(cont == null) {
                if(!hasContactDeleted(id)) {
                    cont = new Contact(id, r[i-1], r[i]);
                    cg0.contacts.add(cont);
                }
            }else {
                cont.email = r[i-1];
                cont.name = r[i];
                online[(i-2)/3] = true;
            }
        }
        for(int i=online.length-1;i>=0;--i) {
            if(!online[i]) {
                cg0.contacts.remove(i);
            }
        }
    }

    protected boolean hasContactDeleted(int contact_id) {
        for(Entry e : App.conMan.entry) {
            if(e instanceof Contact.Delete) {
                Contact.Delete c = (Contact.Delete)e;
                if(c.contact_id == contact_id) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean hasContactGroupDeleted(int group_id) {
        for(Entry e : App.conMan.entry) {
            if(e instanceof ContactGroup.Delete) {
                ContactGroup.Delete g = (ContactGroup.Delete)e;
                if(g.group_id == group_id) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean hasContactGroup(int group_id) {
        for(ContactGroup g : groups) {
            if(g.id == group_id) {
                return true;
            }
        }
        return false;
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

    public void deleteContact(Contact cont) {
        for(ContactGroup grp : groups) {
            grp.removeContact(cont);
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
