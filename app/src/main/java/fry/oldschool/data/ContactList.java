package fry.oldschool.data;

import java.util.ArrayList;

public class ContactList {

    public static ArrayList<ContactGroup> groups=new ArrayList<>();

    public static ArrayList<ContactRequest> contactRequests=new ArrayList<>();

    public static String getLocalSaveString() {
        ContactGroup all = groups.get(groups.size()-1);
        if(all.contacts.size() == 0 && groups.size() == 1) {
            return ((char)0 + "" + (char)0);
        }

        String line = "";

        line += (char)all.contacts.size();
        for(Contact cont : all.contacts) {
            line += (char)(cont.id & 65535) + "" + (char)((cont.id >> 16) & 65535);
            line += (char)(cont.user_id & 65535) + "" + (char)((cont.user_id >> 16) & 65535);
            line += (char)cont.email.length() + cont.email;
            line += (char)cont.name.length() + cont.name;
        }

        line += (char)(groups.size() - 1);
        for(int i=0; i<groups.size()-1; ++i) {
            ContactGroup grp = groups.get(i);
            line += (char)(grp.id & 65535) + "" + (char)((grp.id >> 16) & 65535);
            line += (char)grp.name.length() + grp.name;
            line += (char)grp.contacts.size();
            for(Contact cont : grp.contacts) {
                line += (char)(cont.user_id & 65535) + "" + (char)((cont.user_id >> 16) & 65535);
            }
        }

        return line;
    }

    public static void recieveLocalSaveString(String line) {
        ContactGroup all = groups.get(groups.size() - 1);

        if(line == null) {
            return;
        }
        char[] charArray = line.toCharArray();

        int index = 0;
        int numberOfContacts = charArray[index++];
        for(int i=0; i<numberOfContacts; ++i) {
            int id = charArray[index] | (charArray[++index] << 16);
            int user_id = charArray[++index] | (charArray[++index] << 16);

            int endIndex = charArray[++index] + (++index);
            String email = "";
            while(index < endIndex) {
                email += charArray[index++];
            }

            endIndex = charArray[index] + (++index);
            String name = "";
            while(index < endIndex) {
                name += charArray[index++];
            }

            all.contacts.add(new Contact(id,user_id,email,name));
        }

        int numberOfContactGroups = charArray[index++];
        for(int i=0; i<numberOfContactGroups; ++i) {

            int id = charArray[index++] | (charArray[index++] << 16);
            int endIndex = charArray[index++] + index;
            String name = "";
            while(index < endIndex) {
                name += charArray[index++];
            }

            endIndex = 2*charArray[index++] + index;
            ArrayList<Contact> conts = new ArrayList<>();
            while(index < endIndex) {
                Contact cont = all.findContactByUserId(charArray[index++] | (charArray[index++] << 16));
                if(cont != null) {
                    conts.add(cont);
                }
            }

            groups.add(groups.size() - 1, new ContactGroup(id,name,conts));
        }
    }

    protected static void setContactRequests(String... r) {
        contactRequests=new ArrayList<>();
        for(int i=3;i<r.length;i+=4) {
            contactRequests.add(new ContactRequest(Integer.parseInt(r[i-3]),Integer.parseInt(r[i-2]),r[i-1],r[i]));
        }
    }

    protected static void updateContacts(String... r) {
        ContactGroup cg0=groups.get(groups.size()-1);
        boolean[] online = new boolean[cg0.contacts.size()];
        int onlineIndex = 0;
        for(int i=3; i<r.length; i+=4) {
            int id = Integer.parseInt(r[i-3]);
            Contact cont = cg0.findContactById(id);
            if(cont == null) {
                if(!hasContactDeleted(id)) {
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
        if(!hasContactGroup(id) && !hasContactGroupDeleted(id)) {
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

    protected static boolean hasContactDeleted(int id) {
        for(Entry e : ConnectionManager.entries) {
            if(e instanceof Delete) {
                Delete c = (Delete)e;
                if(c.id == id) {
                    return true;
                }
            }
        }
        return false;
    }

    protected static boolean hasContactGroupDeleted(int id) {
        for(Entry e : ConnectionManager.entries) {
            if(e instanceof Delete) {
                Delete c = (Delete)e;
                if(c.id == id) {
                    return true;
                }
            }
        }
        return false;
    }

    protected static boolean hasContactGroup(int group_id) {
        for(ContactGroup g : groups) {
            if(g.id == group_id) {
                return true;
            }
        }
        return false;
    }

    public static ContactRequest findContactRequestByUserId(int user_id) {
        for(ContactRequest cont : contactRequests) {
            if(cont.user_id == user_id) {
                return cont;
            }
        }
        return null;
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
        ConnectionManager.add(new Delete(Entry.TYPE_CONTACT,cont.id));
    }

    public static void sendRequest(String email) {
        ConnectionManager.add(new ContactRequest.Send(email));
    }

    public static void createContactGroup(String name) {
        ContactGroup grp=new ContactGroup(0,name,new ArrayList<Contact>());
        groups.add(groups.size()-1,grp);
    }
}
