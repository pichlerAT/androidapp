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

    public static void save_local() {
        try {
            BufferedWriter bw=new BufferedWriter(new FileWriter(new File(App.getContext().getFilesDir(),App.getContext().getResources().getString(R.string.file_tasklist))));

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

    public static void load_local() {
        try {
            BufferedReader br=new BufferedReader(new FileReader(new File(App.getContext().getFilesDir(),App.getContext().getResources().getString(R.string.file_tasklist))));

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
/*
    protected class Connection extends MySQL {

        @Override
        protected String doInBackground(String... args) {
            switch(args[0]) {
                case "create": mysql_create(); break;
                case "update": mysql_update(); break;
                case "delete": mysql_delete(); break;
                case "entry_create": mysql_entry_create(Integer.parseInt(args[1])); break;
                case "entry_update": mysql_entry_update(Integer.parseInt(args[1])); break;
                case "entry_delete": mysql_entry_delete(args[1]); break;
                default: error("Unknown Command: tdl:"+args[0]);
            }
            return "tdl_"+args[0];
        }

        public void mysql_create() {
            String re = connect("todolist/create.php", "&name=" + name);
            if (re != null) {
                id = Integer.parseInt(re);
            }
        }

        public void mysql_update() {
            if(id == 0) {
                mysql_create();
            }else {
                connect("todolist/update.php", "&table_id=" + id + "&name=" + name);
            }
            for(int i=0;i<entry_id.length;++i) {
                if(entry_id[i]==0) {
                    mysql_entry_create(i);
                }else {
                    mysql_entry_update(i);
                }
            }
        }

        public void mysql_delete() {
            connect("todolist/delete.php", "&table_id=" + id);
        }

        public void mysql_entry_create(int index) {
            String re = connect("todolist/entry/create.php","&table_id="+id+"&description="+task[index]+"&state="+state[index]);
            if(re != null) {
                entry_id[index] = Integer.parseInt(re);
            }
        }

        public void mysql_entry_update(int index) {
            connect("todolist/entry/update.php","&entry_id=" + entry_id[index] + "&description=" + task[index] + "&state=" + state[index]);
        }

        public void mysql_entry_delete(String entry_id) {
            connect("todolist/entry/delete.php", "&entry_id=" + entry_id);
        }
    }

    protected static class Sync extends MySQL {

        @Override
        protected String doInBackground(String... params) {
            for(TaskList tdl : TaskLists) {
                tdl.update();
            }
            mysql_load();
            for(TaskList tdl : deleted_TaskLists) {
                mysql_delete(tdl.id);
            }
            deleted_TaskLists = new ArrayList<>();
            for(int entry_id : deleted_entries) {
                mysql_entry_delete(entry_id);
            }
            deleted_entries = new int[0];
            return "sync_tdl";
        }

        public void mysql_delete(int table_id) {
            connect("todolist/delete.php", "&table_id=" + table_id);
        }

        public void mysql_entry_delete(int entry_id) {
            connect("todolist/entry/delete.php", "&entry_id=" + entry_id);
        }

        public void mysql_load() {
            String lists=connect("todolist/get.php","");

            if(lists == null) {
                return;
            }

            String[] sl=lists.split(";");
            for(String rl : sl) {
                String[] rli=rl.split(",");

                int tdl_id=Integer.parseInt(rli[0]);

                if(findById(tdl_id) != null) {
                    return;
                }

                String entries=connect("todolist/entry/get.php","&table_id=" + tdl_id);

                if(entries == null) {
                    TaskLists.add(new TaskList(tdl_id,rli[1],0));
                    continue;
                }

                String[] re=entries.split(";");
                TaskList tdl=new TaskList(tdl_id,rli[1],re.length);

                for(int j=0;j<re.length;++j) {
                    String[] rei=re[j].split(",");

                    tdl.entry_id[j]=Integer.parseInt(rei[0]);
                    tdl.user_id[j]=Integer.parseInt(rei[1]);
                    tdl.task[j]=rei[2];
                    tdl.state[j]=Byte.parseByte(rei[3]);
                }

                TaskLists.add(tdl);
            }
        }
    }
    */
}
