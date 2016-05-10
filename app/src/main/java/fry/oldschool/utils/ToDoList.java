package fry.oldschool.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import fry.oldschool.R;

public class ToDoList {

    public static ArrayList<ToDoList> ToDoLists;

    public static ArrayList<ToDoList> deleted_ToDoLists;
    public static int[] deleted_entries;

    public int id;

    public int owner_id;

    public String name;

    public byte[] state;

    public int[] entry_id;

    public int[] user_id;

    public String[] task;

    public static void load() {
        ToDoLists = new ArrayList<>();
        deleted_ToDoLists = new ArrayList<>();
        deleted_entries = new int[0];
        load_local();
        (new NetworkStateReciever.CheckInternetConnection()).execute();
    }

    public static void unload() {
        save_local();
        ToDoLists = null;
        deleted_ToDoLists = null;
        deleted_entries = null;
    }

    public static ToDoList create(String name,int length) {
        ToDoList tdl=new ToDoList(0,MySQL.USER_ID,name,length);
        tdl.create();
        return tdl;
    }

    public static void save_local() {
        try {
            BufferedWriter bw=new BufferedWriter(new FileWriter(new File(App.getContext().getFilesDir(),App.getContext().getResources().getString(R.string.file_todolist))));

            if(deleted_entries.length>0) {
                bw.write(deleted_entries[0]);
            }
            for(int i=1;i<deleted_entries.length;++i) {
                bw.write(";"+deleted_entries[i]);
            }

            for(ToDoList tdl : ToDoLists) {
                bw.newLine();
                bw.write(tdl.id+","+tdl.owner_id+","+tdl.name);
                for(int i=0;i<tdl.length();++i) {
                    bw.write(";"+tdl.entry_id[i]+","+tdl.user_id[i]+","+tdl.task[i]+","+tdl.state[i]);
                }
            }

            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void load_local() {
        try {
            BufferedReader br=new BufferedReader(new FileReader(new File(App.getContext().getFilesDir(),App.getContext().getResources().getString(R.string.file_todolist))));

            String line=br.readLine();

            if(line!=null && !line.equals("")) {
                String[] r=line.split(";");
                deleted_entries=new int[r.length];
                for(int i=0;i<r.length;++i) {
                    deleted_entries[i]=Integer.parseInt(r[i]);
                }
            }

            while((line=br.readLine()) != null) {
                if(line.equals("")) continue;

                String[] r=line.split(";");
                String[] ri=r[0].split(",");

                int owner_id = Integer.parseInt(ri[1]);
                if(owner_id < 0) {
                    continue;
                }

                ToDoList tdl=new ToDoList(Integer.parseInt(ri[0]),owner_id,ri[2],r.length-1);

                for(int i=1;i<r.length;++i) {
                    ri=r[i].split(",");
                    tdl.entry_id[i-1]=Integer.parseInt(ri[0]);
                    tdl.user_id[i-1]=Integer.parseInt(ri[1]);
                    tdl.task[i-1]=ri[2];
                    tdl.state[i-1]=Byte.parseByte(ri[3]);
                }
                ToDoLists.add(tdl);
            }

            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected ToDoList(int id,int owner_id, String name, int length) {
        this.id = id;
        this.owner_id = owner_id;
        this.name = name;
        setLength(length);
    }

    protected ToDoList(int id, String name, int length) {
        this(id,MySQL.USER_ID,name,length);
    }

    protected void removeFromArray(int index) {
        byte[] s=state;
        int[] ei=entry_id;
        int[] ui=user_id;
        String[] t=task;
        state=new byte[s.length-1];
        entry_id=new int[s.length-1];
        user_id=new int[s.length-1];
        task=new String[s.length-1];
        boolean removed=false;
        for(int i=0;i<s.length;++i) {
            if(i == index) {
                removed = true;
            }else if(removed) {
                state[i-1]=s[i];
                entry_id[i-1]=ei[i];
                user_id[i-1]=ui[i];
                task[i-1]=t[i];
            }else {
                state[i]=s[i];
                entry_id[i]=ei[i];
                user_id[i]=ui[i];
                task[i]=t[i];
            }
        }
    }

    public void setAtPosition(int index,String task,boolean state) {
        this.task[index] = task;
        this.state[index] = ( state ? (byte)0 : (byte)1 );
    }

    public void addEntry(String task,boolean state) {
        byte[] s=this.state;
        int[] ei=entry_id;
        int[] ui=user_id;
        String[] t=this.task;
        this.state=new byte[s.length+1];
        entry_id=new int[s.length+1];
        user_id=new int[s.length+1];
        this.task=new String[s.length+1];
        System.arraycopy(s,0,this.state,0,s.length);
        System.arraycopy(ei,0,entry_id,0,s.length);
        System.arraycopy(ui,0,user_id,0,s.length);
        System.arraycopy(t,0,this.task,0,s.length);
        this.state[s.length]=( state ? (byte)0 : (byte)1 );
        entry_id[s.length]=0;
        user_id[s.length]=MySQL.USER_ID;
        this.task[s.length]=task;
    }

    public boolean done(int index) {
        return ( state[index]==0 );
    }

    public int length() {
        return entry_id.length;
    }

    public void setLength(int length) {
        state = new byte[length];
        entry_id = new int[length];
        user_id = new int[length];
        task = new String[length];
    }

    public void create() {
        ToDoLists.add(this);
        System.out.print("--------------- tdl.create()");
        if(NetworkStateReciever.hasInternetConnection) {
            System.out.println(" - ONLINE");
            (new Connection()).execute("create");
        }else {
            System.out.println(" - OFFLINE");
        }
    }

    public void update() {
        System.out.print("--------------- tdl.update()");
        if(NetworkStateReciever.hasInternetConnection) {
            System.out.println(" - ONLINE");
            (new Connection()).execute("update");
        }else {
            System.out.println(" - OFFLINE");
        }
    }

    public void delete() {
        ToDoLists.remove(this);
        owner_id = -1;
        System.out.print("--------------- tdl.delete()");
        if(NetworkStateReciever.hasInternetConnection) {
            System.out.println(" - ONLINE");
            (new Connection()).execute("delete");
        }else {
            System.out.println(" - OFFLINE");
        }
    }

    public void delete(int index) {
        int[] d=deleted_entries;
        deleted_entries=new int[d.length+1];
        System.arraycopy(d,0,deleted_entries,0,d.length);
        deleted_entries[d.length]=entry_id[index];
        removeFromArray(index);
        System.out.print("--------------- tdl.delete(int)");
        if(NetworkStateReciever.hasInternetConnection) {
            System.out.println(" - ONLINE");
            (new Connection()).execute("entry_delete",""+index);
        }else {
            System.out.println(" - OFFLINE");
        }
    }

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
            for(ToDoList tdl : ToDoLists) {
                tdl.update();
            }
            for(ToDoList tdl : deleted_ToDoLists) {
                mysql_delete(tdl.id);
            }
            deleted_ToDoLists = new ArrayList<>();
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
    }

    protected static class Load extends MySQL {

        @Override
        protected String doInBackground(String... args) {
            if(ToDoLists == null) {
                ToDoLists = new ArrayList<>();
            }
            String lists=connect("todolist/get.php","");

            if(lists == null) {
                return "tdl_load_error";
            }

            String[] sl=lists.split(";");
            for(String rl : sl) {
                String[] rli=rl.split(",");

                int tdl_id=Integer.parseInt(rli[0]);
                String entries=connect("todolist/entry/get.php","&table_id=" + tdl_id);

                if(entries == null) {
                    ToDoLists.add(new ToDoList(tdl_id,rli[1],0));
                    continue;
                }

                String[] re=entries.split(";");
                ToDoList tdl=new ToDoList(tdl_id,rli[1],re.length);

                for(int j=0;j<re.length;++j) {
                    String[] rei=re[j].split(",");

                    tdl.entry_id[j]=Integer.parseInt(rei[0]);
                    tdl.user_id[j]=Integer.parseInt(rei[1]);
                    tdl.task[j]=rei[2];
                    tdl.state[j]=Byte.parseByte(rei[3]);
                }

                ToDoLists.add(tdl);
            }
            return "tdl_load";
        }
    }
}
