package fry.oldschool.data;

import java.util.ArrayList;

import fry.oldschool.utils.FryFile;
import fry.oldschool.utils.Fryable;

public class ContactList {

    public static ArrayList<ContactGroup> groups=new ArrayList<>();

    public static ArrayList<ContactRequest> contactRequests=new ArrayList<>();

    public static void writeTo(FryFile file) {
        file.write(groups.get(groups.size()-1).contacts.toArray());
        file.write(groups.subList(0,groups.size()-1).toArray());
    }

    public static void readFrom(FryFile file) {
        ContactGroup all = groups.get(groups.size() - 1);

        int NoContacts = file.getChar();
        for(int i=0; i<NoContacts; ++i) {
            all.contacts.add(new Contact(file.getInt(),file.getInt(),file.getString(),file.getString()));
        }

        int NoContactGroups = file.getChar();
        for(int i=0; i<NoContactGroups; ++i) {
            ContactGroup grp = new ContactGroup(file.getInt(),file.getString());

            NoContacts = file.getChar();
            for(int j=0; j<NoContacts; ++j) {
                Contact cont = findContactById(file.getInt());
                if(cont != null) {
                    grp.contacts.add(cont);
                }
            }
        }
    }

    public static void synchronizeContactsFromMySQL(String... r) {
        ContactGroup all = groups.get(groups.size() - 1);
        int index = 0;

        int NoContacts = Integer.parseInt(r[index++]);
        for(int i=0; i<NoContacts; ++i) {
            Contact on = new Contact(Integer.parseInt(r[index++]),Integer.parseInt(r[index++]),r[index++],r[index++]);
            Contact off = all.findContactByUserId(on.user_id);

            if(off != null) {
                off.email = on.email;
                off.name = on.name;
            }else if(!ConnectionManager.hasEntry(OnlineEntry.TYPE_CONTACT | OnlineEntry.BASETYPE_DELETE, on.id)) {
                all.contacts.add(on);
            }
        }

        int NoContactGroups = Integer.parseInt(r[index++]);
        for(int i=0; i<NoContactGroups; ++i) {
            ContactGroup on = new ContactGroup(Integer.parseInt(r[index++]),r[index++]);

            NoContacts = Integer.parseInt(r[index++]);
            for(int j=0; j<NoContacts; ++j) {
                Contact cont = all.findContactByUserId(Integer.parseInt(r[index++]));
                if(cont != null) {
                    on.contacts.add(cont);
                }
            }

            ContactGroup off = ContactList.findContactGroupById(on.id);
            if(off != null) {
                // TODO ContactGroup: choose OFF or ON
            }else if(!ConnectionManager.hasEntry(OnlineEntry.TYPE_CONTACT_GROUP | OnlineEntry.BASETYPE_DELETE, on.id)) {
                groups.add(groups.size() - 1, on);
            }
        }
    }

    public static void synchronizeContactRequestsFromMySQL(String... r) {
        contactRequests=new ArrayList<>();
        for(int i=3; i<r.length; i+=4) {
            contactRequests.add(new ContactRequest(Integer.parseInt(r[i-3]),Integer.parseInt(r[i-2]),r[i-1],r[i]));
        }
    }
/*
    protected static void setContactRequests(String... r) {
        contactRequests=new ArrayList<>();
        for(int i=3;i<r.length;i+=4) {
            contactRequests.add(new ContactRequest(Integer.parseInt(r[i-3]),Integer.parseInt(r[i-2]),r[i-1],r[i]));
        }
    }
/*
    protected static void updateContacts(String... r) {
        ContactGroup cg0=groups.get(groups.size()-1);
        boolean[] online = new boolean[cg0.contacts.size()];
        int onlineIndex = 0;
        for(int i=3; i<r.length; i+=4) {
            int id = Integer.parseInt(r[i-3]);
            Contact cont = cg0.findContactById(id);
            if(cont == null) {
                if(!ConnectionManager.hasEntry(OnlineEntry.TYPE_CONTACT | OnlineEntry.BASETYPE_DELETE,id)) {
                    cont = new Contact(id, Integer.parseInt(r[i-2]), r[i-1], r[i]);
                    cg0.contacts.add(cont);
                }
            }else {
                cont.email = r[i-1];
                cont.name = r[i];
                online[onlineIndex++] = true;
            }
        }
        for(int i=online.length-1;i>=0;--i) {
            if(!online[i]) {
                cg0.contacts.remove(i);
            }
        }
    }

    protected static void updateContactGroup(String... r) {
        int id = Integer.parseInt(r[0]);
        if(!ConnectionManager.hasEntry(OnlineEntry.TYPE_CONTACT_GROUP | OnlineEntry.BASETYPE_DELETE,id)) {
            ContactGroup grp = new ContactGroup(id,r[1],new ArrayList<Contact>());
            if(!r[2].equals("n")) {
                for (int i = 2; i < r.length; ++i) {
                    Contact cont = findContactByUserId(Integer.parseInt(r[i]));
                    if (cont != null) {
                        grp.contacts.add(cont);
                    }
                }
            }
            groups.add(groups.size()-1, grp);
        }
    }
*/
    public static boolean isEmpty() {
        return (groups.get(groups.size() - 1).contacts.size() == 0);
    }

    public static void removeContactRequest(int id) {
        for(int i=0; i<contactRequests.size(); ++i) {
            if(contactRequests.get(i).id == id) {
                contactRequests.remove(i);
            }
        }
    }

    protected static Contact findContactById(int contact_id) {
        return groups.get(groups.size()-1).findContactById(contact_id);
    }

    protected static Contact findContactByUserId(int user_id) {
        return groups.get(groups.size()-1).findContactByUserId(user_id);
    }

    protected static ContactGroup findContactGroupById(int group_id) {
        for(ContactGroup g : groups) {
            if(g.id == group_id) {
                return g;
            }
        }
        return null;
    }

    public static void deleteContact(Contact cont) {
        for(ContactGroup grp : groups) {
            grp.removeContact(cont);
        }
        ConnectionManager.add(new Delete(OfflineEntry.TYPE_CONTACT,cont.id));
    }

    public static void sendRequest(String email) {
        ConnectionManager.add(new ContactRequest.Send(email));
    }

    public static void createContactGroup(String name) {
        ContactGroup grp=new ContactGroup(0,name,new ArrayList<Contact>());
        groups.add(groups.size()-1,grp);
    }
}
