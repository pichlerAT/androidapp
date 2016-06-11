package fry.oldschool.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import fry.oldschool.R;

public class TaskList extends OnlineEntry {

    protected int id;

    protected int user_id;

    protected byte state;

    public String name;

    public ArrayList<TaskListEntry> entry = new ArrayList<>();

    public ArrayList<Share> sharedContacts = new ArrayList<>();

    public static TaskList create(String name) {
        TaskList tl=new TaskList(name);
        App.conMan.add(tl);
        return tl;
    }

    public static void load() {
        try {
            File file = new File(App.appContext.getFilesDir(),App.appContext.getResources().getString(R.string.file_tasklist));
            if(!file.exists()) {
                return;
            }

            BufferedReader br=new BufferedReader(new FileReader(file));

            String line;

            while((line=br.readLine()) != null) {
                if(!line.isEmpty()) {
                    TaskList tl = new TaskList(line);
                    App.TaskLists.add(tl);
                }
            }

            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try {
            BufferedWriter bw=new BufferedWriter(new FileWriter(new File(App.appContext.getFilesDir(),App.appContext.getResources().getString(R.string.file_tasklist))));

            for(TaskList tl : App.TaskLists) {
                if(tl.isOwner()) {
                    bw.write(tl.id + S + tl.user_id + S + tl.state + S + tl.name + S + tl.getEntryStrings());
                    bw.newLine();
                }
            }

            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected TaskList(String line) {
        String[] r = line.split(S);
        if(r.length == 1) {
            user_id = USER_ID;
            state = 0;
            name = line;
            return;
        }
        id = Integer.parseInt(r[0]);
        user_id = Integer.parseInt(r[1]);
        state = Byte.parseByte(r[2]);
        name = r[3];
        for(int i=7;i<r.length;i+=4) {
            TaskListEntry e = new TaskListEntry(Integer.parseInt(r[i-3]),id,Integer.parseInt(r[i-2]),r[i-1],Byte.parseByte(r[i]));
            entry.add(e);
            if(id!=0 && e.id==0) {
                e.create(id);
            }
        }
        if(id == 0) {
            App.conMan.add(this);
        }
    }

    public boolean isOwner() {
        return ( user_id == Entry.USER_ID );
    }

    public Contact getOwner() {
        if(isOwner()) {
            return null;
        }
        return App.conLis.findContactById(user_id);
    }

    public boolean isDone() {
        return ( state == 1 );
    }

    public void rename(String name) {
        this.name = name;
        App.conMan.add(new Update(id));
    }

    public void loadShared() {
        App.conMan.add(new GetShared(id));
    }

    public void addShare(Contact contact,boolean canEdit) {
        App.conMan.add(new CreateShare(contact.user_id,id,canEdit));
    }

    public void addShare(ArrayList<Contact> contacts,boolean canEdit) {
        for(Contact contact : contacts) {
            App.conMan.add(new CreateShare(contact.user_id,id,canEdit));
        }
    }

    public void removeShare(int index) {
        Share share = sharedContacts.remove(index);
        if(share != null) {
            App.conMan.add(new DeleteShare(share.id));
        }
    }

    public void removeShare(Share share) {
        if(sharedContacts.remove(share)) {
            App.conMan.add(new DeleteShare(share.id));
        }
    }

    public void removeShare(ArrayList<Share> shares) {
        for(Share share : shares) {
            if(sharedContacts.remove(share)) {
                App.conMan.add(new DeleteShare(share.id));
            }
        }
    }

    public void addEntry(String task,boolean state) {
        TaskListEntry ent = new TaskListEntry(task,state);
        entry.add(ent);
        if(id != 0) {
            ent.create(id);
        }
    }

    public void addEntry(int index,String task,boolean state) {
        TaskListEntry ent = new TaskListEntry(task,state);
        entry.add(index,ent);
        if(id != 0) {
            ent.create(id);
        }
    }

    public void change(boolean state) {
        this.state = ( state ? (byte)1 : (byte)0 );
    }

    public void change(String name,boolean state) {
        this.name = name;
        this.state = ( state ? (byte)1 : (byte)0 );
    }

    public boolean isDone(int index) {
        return entry.get(index).isDone();
    }

    public int length() {
        return entry.size();
    }

    public String getTaskName(int index) {
        return entry.get(index).description;
    }

    @Override
    protected boolean mysql_update() {
        String resp = connect("tasklist/create.php", "&name=" + name);
        if(resp.substring(0,3).equals("suc")) {
            id = Integer.parseInt(resp.substring(3));
            for(TaskListEntry e : entry) {
                e.create(id);
            }
            return true;
        }
        return false;
    }

    public void delete() {
        App.TaskLists.remove(this);
        App.conMan.add(new Delete(id));
    }

    public void delete(int index) {
        App.conMan.add(new TaskListEntry.Delete(entry.get(index).id));
        entry.remove(index);
    }

    protected String getEntryStrings() {
        if(entry.size() <= 0) {
            return "n";
        }
        String s = "";
        for(TaskListEntry e : entry) {
            s += e.id + S + e.user_id + S + e.description + S + e.state + S;
        }
        return s;
    }

    protected static class Delete extends Entry {

        protected int table_id;

        protected Delete(int table_id) {
            this.table_id = table_id;
        }

        protected Delete(String line) {
            table_id = Integer.parseInt(line);
        }

        @Override
        protected boolean mysql_update() {
            String resp = connect("tasklist/delete.php", "&table_id=" + table_id);
            return resp.equals("suc");
        }

        @Override
        protected String getConManString() {
            return TYPE_TASKLIST_DELETE + "" + table_id;
        }

    }

    protected static class Update extends Entry {

        protected int table_id;

        protected Update(int table_id) {
            this.table_id = table_id;
        }

        protected Update(String line) {
            table_id = Integer.parseInt(line);
        }

        @Override
        protected boolean mysql_update() {
            TaskList tl = App.taskMan.findTaskListById(table_id);
            if(tl == null) {
                return true;
            }
            String resp = connect("tasklist/update.php", "&table_id=" + table_id + "&name=" + tl.name);
            return resp.equals("suc");
        }

        @Override
        protected String getConManString() {
            return TYPE_TASKLIST_UPDATE + "" + table_id;
        }

    }

    protected class GetShared extends OnlineEntry {

        protected int table_id;

        protected GetShared(int table_id) {
            this.table_id = table_id;
        }

        @Override
        protected boolean mysql_update() {
            String resp = connect("tasklist/share/get.php","&table_id="+table_id);

            if(!resp.substring(0,3).equals("suc")) {
                return false;
            }

            String[] r = resp.substring(3).split(S);
            sharedContacts = new ArrayList<>();
            for(int i=2;i<r.length;i+=3) {
                Contact c = App.conLis.findContactByUserId(Integer.parseInt(r[i-1]));
                if(c != null) {
                    sharedContacts.add(new Share(TYPE_TASKLIST_SHARE_UPDATE,Integer.parseInt(r[i-2]),Byte.parseByte(r[i]),c));
                }
            }

            sharedContacts.get(0).allowEdit(true);

            return true;
        }

    }


    protected static class CreateShare extends OnlineEntry {

        protected int user_id;

        protected int table_id;

        protected byte permission;

        protected CreateShare(int user_id,int table_id,boolean canEdit) {
            this.user_id = user_id;
            this.table_id = table_id;
            permission = ( canEdit ? (byte)1 : (byte)0 );
        }

        @Override
        protected boolean mysql_update() {
            String resp = connect("tasklist/share/create.php", "&user_id_share=" + user_id + "&table_id=" + table_id);
            return ( resp.substring(0,3).equals("suc") || resp.equals("err_tsc2") );
        }

    }

    protected static class DeleteShare extends OnlineEntry {

        protected int share_id;

        protected DeleteShare(int share_id) {
            this.share_id = share_id;
        }

        @Override
        protected boolean mysql_update() {
            String resp = connect("tasklist/share/delete.php", "&share_id="+share_id);
            return resp.equals("suc");
        }

    }

}
