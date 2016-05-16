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

    public static ArrayList<TaskList> TaskLists;

    public String name;

    public ArrayList<TaskListEntry> entry = new ArrayList<>();

    public static void load() {
        TaskLists = new ArrayList<>();
        load_local();
    }

    public static void unload() {
        save_local();
        TaskLists = null;
    }

    public static TaskList create(String name, int length) {
        TaskList tdl=new TaskList(0, MySQL.USER_ID,name);
        tdl.create();
        return tdl;
    }

    public static TaskList findById(int id) {
        for(TaskList tdl : TaskLists) {
            if(tdl.id == id) {
                return tdl;
            }
        }
        return null;
    }

    public static void load_local() {
        try {
            BufferedReader br=new BufferedReader(new FileReader(new File(App.mContext.getFilesDir(),App.mContext.getResources().getString(R.string.file_tasklist))));

            String line;

            while((line=br.readLine()) != null) {
                if(line.equals("")) continue;

                String[] r=line.split(";");
                String[] ri=r[0].split(",");

                TaskList tdl=new TaskList(Integer.parseInt(ri[0]),Integer.parseInt(ri[1]),ri[2]);

                for(int i=1;i<r.length;++i) {
                    ri=r[i].split(",");
                    int entry_id=Integer.parseInt(ri[0]);
                    int user_id=Integer.parseInt(ri[1]);
                    byte state=Byte.parseByte(ri[3]);
                    tdl.entry.add(new TaskListEntry(entry_id,tdl.id,user_id,ri[2],state));
                }
                TaskLists.add(tdl);
            }

            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save_local() {
        try {
            BufferedWriter bw=new BufferedWriter(new FileWriter(new File(App.mContext.getFilesDir(),App.mContext.getResources().getString(R.string.file_tasklist))));

            Iterator<TaskList> it = TaskLists.iterator();
            TaskList tdl;

            if(it.hasNext()) {
                tdl = it.next();
                bw.write(tdl.id+","+tdl.user_id+","+tdl.name);
                for(TaskListEntry ent : tdl.entry) {
                    bw.write(";"+ent.id+","+ent.user_id+","+ent.description+","+ent.state);
                }
            }

            while(it.hasNext()) {
                tdl = it.next();
                bw.newLine();
                bw.write(tdl.id+","+tdl.user_id+","+tdl.name);
                for(TaskListEntry ent : tdl.entry) {
                    bw.write(";"+ent.id+","+ent.user_id+","+ent.description+","+ent.state);
                }
            }

            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected TaskList(int id, int user_id, String name) {
        super(id,user_id);
        this.name = name;
    }

    @Override
    protected void setId(int id) {
        super.setId(id);
        for(TaskListEntry ent : entry) {
            ent.table_id = id;
            ent.create();
        }
    }

    @Override
    protected String getString() {
        return super.getString() + SEP_1 + name ;
    }

    @Override
    protected byte getType() {
        return 0;
    }

    @Override
    protected String[] getCreate() {
        return new String[]{"todolist/create.php", "&name=" + name};
    }

    @Override
    protected String[] getUpdate() {
        return new String[]{"todolist/update.php", "&table_id=" + id + "&name=" + name};
    }

    @Override
    protected String[] getDelete() {
        return new String[]{"todolist/delete.php", "&table_id=" + id};
    }

    public void set(String name) {
        this.name = name;
    }

    public void setAtPosition(int index,String task,boolean state) {
        entry.get(index).set(task,state);
    }

    public void addEntry(String task,boolean state) {
        TaskListEntry ent = new TaskListEntry(id,task,( state ? (byte)0 : (byte)1 ));
        entry.add(ent);
        if(id != 0) {
            ent.create();
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

    public void create() {
        super.create();
        TaskLists.add(this);
    }

    public void delete() {
        super.delete();
        TaskLists.remove(this);
        entry = null;
    }

    public void delete(int index) {
        entry.get(index).delete();
        entry.remove(index);
    }

}
