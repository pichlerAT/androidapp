package fry.oldschool.data;

import java.util.ArrayList;

import fry.oldschool.utils.FryFile;

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
                Contact cont = all.getContactByUserId(file.getInt());
                if(cont != null) {
                    grp.contacts.add(cont);
                }
            }
        }
    }

    public static void synchronizeContactsFromMySQL(String... r) {
        ContactGroup all = groups.get(groups.size() - 1);
        boolean[] offIsOn = new boolean[all.contacts.size()];
        int index = 0;

        int NoContacts = Integer.parseInt(r[index++]);
        for(int i=0; i<NoContacts; ++i) {
            Contact on = new Contact(Integer.parseInt(r[index++]),Integer.parseInt(r[index++]),r[index++],r[index++]);
            int off_index = all.getContactIndexByUserId(on.user_id);

            if(off_index >= 0) {
                offIsOn[off_index] = true;
                Contact off = all.contacts.get(off_index);
                off.email = on.email;
                off.name = on.name;
            }else if(!ConnectionManager.hasEntry(OnlineEntry.TYPE_CONTACT | OnlineEntry.BASETYPE_DELETE, on.id)) {
                all.contacts.add(on);
            }
        }
        for(int i=offIsOn.length-1; i>=0; --i) {
            if(!offIsOn[i]) {
                all.contacts.remove(i);
            }
        }

        offIsOn = new boolean[groups.size()-1];
        ContactGroup[] onBackup = new ContactGroup[groups.size()-1];
        int[] onIndex = new int[groups.size()-1];
        int lastIndex = 0;

        int NoContactGroups = Integer.parseInt(r[index++]);
        for(int i=0; i<NoContactGroups; ++i) {
            ContactGroup on = new ContactGroup(Integer.parseInt(r[index++]),r[index++]);

            NoContacts = Integer.parseInt(r[index++]);
            for(int j=0; j<NoContacts; ++j) {
                Contact cont = all.getContactByUserId(Integer.parseInt(r[index++]));
                if(cont != null) {
                    on.contacts.add(cont);
                }
            }

            int off_index = getContactGroupIndexById(on.id);
            if(off_index >= 0) {
                offIsOn[off_index] = true;
                ContactGroup off = groups.get(off_index);
                if(!off.equals(on)) {
                    onBackup[lastIndex] = on;
                    onIndex[lastIndex] = off_index;
                    ++lastIndex;
                }
            }else if(!ConnectionManager.hasEntry(OnlineEntry.TYPE_CONTACT_GROUP | OnlineEntry.BASETYPE_DELETE, on.id)) {
                groups.add(groups.size() - 1, on);
            }
        }
        // TODO accept online or offline?
        /*
        if(acceptOnline) ->
        for(int i=0; i<lastIndex; ++i) {
            groups.set(onIndex[i],onBackup[i]);
        }
        */
        for(int i=offIsOn.length-1; i>=0; --i) {
            if(!offIsOn[i] && groups.get(i).id != 0) {
                groups.remove(i);
            }
        }
    }

    public static void synchronizeContactRequestsFromMySQL(String... r) {
        contactRequests=new ArrayList<>();
        for(int i=3; i<r.length; i+=4) {
            contactRequests.add(new ContactRequest(Integer.parseInt(r[i-3]),Integer.parseInt(r[i-2]),r[i-1],r[i]));
        }
    }

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

    public static int getContactGroupIndexById(int group_id) {
        for(int i=0; i<groups.size(); ++i) {
            if(groups.get(i).id == group_id) {
                return i;
            }
        }
        return -1;
    }

    protected static Contact getContactByUserId(int user_id) {
        return groups.get(groups.size()-1).getContactByUserId(user_id);
    }

    protected static ContactGroup getContactGroupById(int group_id) {
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
