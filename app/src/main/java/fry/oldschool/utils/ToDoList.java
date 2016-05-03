package fry.oldschool.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import fry.oldschool.R;

public class ToDoList {

    public static ArrayList<ToDoList> ToDoLists=new ArrayList<>();

    public int id;

    public int owner_id;

    public String name;

    public byte[] state;

    public int[] entry_id;

    public int[] user_id;

    public String[] task;

    public static ToDoList create(String name,int length) {
        ToDoList tdl=new ToDoList(name,length);
        tdl.create();
        return tdl;
    }

    public static void loadToDoLists() {
        MySQL con=new Load();
        con.execute();
    }

    public static void local_save() {
        try {
            FileWriter fw=new FileWriter(new File(App.getContext().getFilesDir(),App.getContext().getResources().getString(R.string.file_todolist)));

            for(ToDoList tdl : ToDoLists) {
                fw.write(tdl.id+","+tdl.name);
                for(int i=0;i<tdl.length();++i) {
                    fw.write(";"+tdl.entry_id[i]+","+tdl.user_id[i]+","+tdl.task[i]+","+tdl.state[i]);
                }
                fw.write(String.format("%n"));
            }

            fw.write("EOF");
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void local_load() {
        try {
            BufferedReader br=new BufferedReader(new FileReader(new File(App.getContext().getFilesDir(),App.getContext().getResources().getString(R.string.file_todolist))));

            ToDoLists=new ArrayList<>();

            String line;
            while(!(line=br.readLine()).equals("EOF")) {
                String[] r=line.split(";");
                String[] ri=r[0].split(",");
                ToDoList tdl=new ToDoList(ri[1],Integer.parseInt(ri[0]),r.length-1);
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

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected ToDoList(String name,int length) {
        this.name = name;
        setLength(length);
    }

    protected ToDoList(String name,int length,int id) {
        this(name,length);
        this.id = id;
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

    public void setAtPosition(int index,String task,byte state) {
        this.task[index] = task;
        this.state[index] = state;
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
        (new Connection()).execute("create");
    }

    public void update() {
        (new Connection()).execute("update");
    }

    public void delete() {
        (new Connection()).execute("delete");
    }

    public void delete(int index) {
        (new Connection()).execute("entry_delete",""+index);
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
                case "entry_delete": mysql_entry_delete(Integer.parseInt(args[1])); break;
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
            connect("todolist/update.php","&table_id="+id+"&name="+name);
            for(int i=0;i<entry_id.length;++i) {
                if(entry_id[i]==0) {
                    mysql_entry_create(i);
                }else if(state[i]==2) {
                    mysql_entry_delete(i);
                }else {
                    mysql_entry_update(i);
                }
            }
        }

        public void mysql_delete() {
            connect("todolist/delete.php", "&table_id=" + id);
            if(!errorDialog) ToDoLists.remove(ToDoList.this);
        }

        public void mysql_entry_create(int index) {
            String re = connect("todolist/entry/create.php","&table_id="+id+"&description="+task[index]+"&state="+state[index]);
            if(re != null) {
                entry_id[index] = Integer.parseInt(re);
                user_id[index] = USER_ID;
            }
        }

        public void mysql_entry_update(int index) {
            connect("todolist/entry/update.php","&entry_id=" + entry_id[index] + "&description=" + task[index] + "&state=" + state[index]);
        }

        public void mysql_entry_delete(int index) {
            connect("todolist/entry/delete.php", "&entry_id=" + index);
            if(!errorDialog) ToDoList.this.removeFromArray(index);
        }
    }

    protected static class Load extends MySQL {

        @Override
        protected String doInBackground(String... args) {
            if(ToDoLists.size() == 0) {
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
                    continue;
                }

                String[] re=entries.split(";");
                ToDoList tdl=new ToDoList(rli[1],re.length,tdl_id);

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
