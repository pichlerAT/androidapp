package fry.oldschool.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import fry.oldschool.R;

public class TaskList extends Entry {

    protected int id;

    protected int user_id;

    public String name;

    public ArrayList<TaskListEntry> entry = new ArrayList<>();

    public static TaskList create(String name) {
        TaskList tl=new TaskList(name);
        App.conMan.add(tl);
        return tl;
    }

    public static void load() {
        try {
            File file = new File(App.mContext.getFilesDir(),App.mContext.getResources().getString(R.string.file_tasklist));
            if(!file.exists()) {
                return;
            }

            BufferedReader br=new BufferedReader(new FileReader(file));

            String line;

            while((line=br.readLine()) != null) {
                TaskList tl = new TaskList(line);
                App.TaskLists.add(tl);
            }

            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try {
            BufferedWriter bw=new BufferedWriter(new FileWriter(new File(App.mContext.getFilesDir(),App.mContext.getResources().getString(R.string.file_tasklist))));
            Iterator<TaskList> it = App.TaskLists.iterator();

            if(it.hasNext()) {
                TaskList tl = it.next();
                bw.write(tl.id + ";" + tl.user_id + ";" + tl.name + ";" + tl.getEntryStrings());
                while(it.hasNext()) {
                    tl = it.next();
                    bw.newLine();
                    bw.write(tl.id + ";" + tl.user_id + ";" + tl.name + ";" + tl.getEntryStrings());
                }
            }

            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected TaskList(String line) {
        String[] r = line.split(";");
        if(r.length == 1) {
            user_id = USER_ID;
            name = line;
            return;
        }
        id = Integer.parseInt(r[0]);
        user_id = Integer.parseInt(r[1]);
        name = r[2];
        for(int i=6;i<r.length;i+=4) {
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

    public boolean owner() {
        return ( user_id == Entry.USER_ID );
    }

    public Contact getOwner() {
        if(owner()) {
            return null;
        }
        return App.conLis.findContactById(user_id);
    }

    public void rename(String name) {
        this.name = name;
        App.conMan.add(new Update(id));
    }

    public void addShare(Contact contact) {
        App.conMan.add(new Share.Create(contact.id,id));
    }

    public void addShare(ArrayList<Contact> contacts) {
        for(Contact contact : contacts) {
            App.conMan.add(new Share.Create(contact.id,id));
        }
    }

    public void removeShare(Contact cont) {
        App.conMan.add(new Share.Delete(cont.id,id));
    }

    public void removeShare(ArrayList<Contact> contacts) {
        for(Contact contact : contacts) {
            App.conMan.add(new Share.Delete(contact.id,id));
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

    public boolean done(int index) {
        return entry.get(index).done();
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

    @Override
    protected String getConManString() {
        return null;
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
            s += e.id + ";" + e.user_id + ";" + e.description + ";" + e.state + ";";
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

    protected static abstract class Share extends Entry {

        protected int contact_id;

        protected int table_id;

        protected Share(int contact_id,int table_id) {
            this.contact_id = contact_id;
            this.table_id = table_id;
        }

        protected Share(String line) {
            String[] r = line.split(";");
            contact_id = Integer.parseInt(r[0]);
            table_id = Integer.parseInt(r[1]);
        }

        protected static class Create extends Share {

            protected Create(int contact_id,int table_id) {
                super(contact_id,table_id);
            }

            protected Create(String line) {
                super(line);
            }

            @Override
            protected boolean mysql_update() {
                String resp = connect("tasklist/share/create.php", "&contact_id=" + contact_id + "&table_id=" + table_id);
                return ( resp.equals("suc") || resp.equals("err_tsc2") );
            }

            @Override
            protected String getConManString() {
                return TYPE_TASKLIST_SHARE_CREATE + "" + contact_id + ";" + table_id;
            }
        }

        protected static class Delete extends Share {

            protected Delete(int contact_id,int table_id) {
                super(contact_id,table_id);
            }

            protected Delete(String line) {
                super(line);
            }

            @Override
            protected boolean mysql_update() {
                String resp = connect("tasklist/share/delete.php", "&contact_id=" + contact_id + "&table_id=" + table_id);
                return ( resp.equals("suc") );
            }

            @Override
            protected String getConManString() {
                return TYPE_TASKLIST_SHARE_DELETE + "" + contact_id + ";" + table_id;
            }
        }

    }

}
