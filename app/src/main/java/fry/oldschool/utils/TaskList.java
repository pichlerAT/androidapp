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
        TaskList tl=new TaskList(0,Entry.USER_ID,name);
        App.conMan.add(tl);
        return tl;
    }

    public static void load() {
        try {
            BufferedReader br=new BufferedReader(new FileReader(new File(App.mContext.getFilesDir(),App.mContext.getResources().getString(R.string.file_tasklist))));

            String line;

            while((line=br.readLine()) != null) {
                String[] r = line.split(";");
                TaskList tl = new TaskList(Integer.parseInt(r[0]),Integer.parseInt(r[1]),r[2]);
                for(int i=6;i<r.length;i+=4) {
                    tl.entry.add(new TaskListEntry(Integer.parseInt(r[i-3]),tl.id,Integer.parseInt(r[i-2]),r[i-1],Byte.parseByte(r[i])));
                }
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

    protected TaskList(int id, int user_id, String name) {
        this.id = id;
        this.user_id = user_id;
        this.name = name;
    }

    protected TaskList(String line) {
        String[] r = line.split(";");
        id = Integer.parseInt(r[0]);
        user_id = Integer.parseInt(r[1]);
        name = r[2];
        if(r.length > 3) {
            for(int i=6;i<r.length;i+=4) {
                entry.add(new TaskListEntry(Integer.parseInt(r[i-3]),id,Integer.parseInt(r[i-2]),r[i-1],Byte.parseByte(r[i])));
            }
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
        App.conMan.add(this);
    }

    public void addShare(Contact cont) {
        App.conMan.add(new Share.Create(cont.id,id));
    }

    public void removeShare(Contact cont) {
        App.conMan.add(new Share.Delete(cont.id,id));
    }

    public void addEntry(String task,boolean state) {
        entry.add(TaskListEntry.create(id,task,state));
    }

    public void addEntry(int index,String task,boolean state) {
        entry.add(index,TaskListEntry.create(id,task,state));
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
        if(id == 0) {
            String resp = connect("tasklist/create.php", "&name=" + name);
            if(resp.substring(0,3).equals("suc")) {
                id = Integer.parseInt(resp.substring(3));
                return true;
            }
        }else {
            String resp = connect("tasklist/update.php", "&table_id=" + id + "&name=" + name);
            return resp.equals("suc");
        }
        return false;
    }

    @Override
    protected String getConManString() {
        return TYPE_TASKLIST + "" + id + ";" + user_id + ";" + name;
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
