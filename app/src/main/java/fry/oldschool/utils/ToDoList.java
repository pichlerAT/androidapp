package fry.oldschool.utils;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Stefan on 30.04.2016.
 *
 */
public class ToDoList {

    public int id;

    //public int owner_id;

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

    public static ArrayList<ToDoList> getToDoLists() {
        MySQL con=new Get_Lists();
        con.execute();

        ArrayList<ToDoList> tdls=new ArrayList<ToDoList>();

        String response=con.response();
        if(response == null) {
            return tdls;
        }

        String[] s=response.split(";");
        for(String si : s) {
            String[] r = si.split(",");
            int id = Integer.parseInt(r[0]);
            ToDoList tdl=new ToDoList(r[1], 0, id);

            con=new Get_Entries(id);
            con.execute();
            String resp=con.response();
            if(resp != null) {
                r = resp.split(";");
                tdl.setLength(r.length);
                for(int j=0;j<r.length;++j) {
                    String[] ri=r[j].split(",");
                    tdl.entry_id[j]=Integer.parseInt(ri[0]);
                    tdl.user_id[j]=Integer.parseInt(ri[1]);
                    tdl.task[j]=ri[2];
                    tdl.state[j]=Byte.parseByte(ri[3]);
                }
            }

            tdls.add(tdl);
        }

        return tdls;
    }

    protected ToDoList(String name,int length) {
        this.name = name;
        setLength(length);
    }

    protected ToDoList(String name,int length,int id) {
        this(name,length);
        this.id = id;
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
        MySQL con=new Create();
        con.execute();
    }

    public void update() {
        MySQL con=new Update();
        con.execute();
        for(int i=0;i<entry_id.length;++i) {
            if(entry_id[i]==0) {
                con=new Entry(i);
            }else {
                con=new Update_Entry(i);
            }
            con.execute();
        }
    }

    protected class Create extends MySQL {
        @Override
        protected String doInBackground(String... args) {
            try {
                connect("todolist/create.php","&name="+name);

                String line=br.readLine();
                if(line.substring(0,3).equals("suc")) {
                    id = Integer.parseInt(line.substring(3));
                }else {
                    error(line);
                }

            } catch (IOException e) {
                error("cannot connect to server");
                e.printStackTrace();
            }
            return null;
        }
    }

    protected class Entry extends MySQL {

        protected int index;

        protected Entry(int index) {
            this.index = index;
        }

        @Override
        protected String doInBackground(String... args) {
            try {
                connect("todolist/entries/create.php","&table_id="+id+"&description="+task[index]+"&state="+state[index]);

                String line=br.readLine();
                if(line.substring(0,3).equals("suc")) {
                    entry_id[index] = Integer.parseInt(line.substring(3));
                    user_id[index] = USER_ID;
                }else {
                    error(line);
                }

            } catch (IOException e) {
                error("cannot connect to server");
                e.printStackTrace();
            }
            return null;
        }
    }

    protected class Update extends MySQL {
        @Override
        protected String doInBackground(String... args) {
            try {
                connect("todolist/update.php","&table_id="+id+"&name="+name);

                String line=br.readLine();
                if(line.substring(0,3).equals("err")) {
                    error(line);
                }

            } catch (IOException e) {
                error("cannot connect to server");
                e.printStackTrace();
            }
            return null;
        }
    }

    protected class Update_Entry extends MySQL {

        protected int index;

        protected Update_Entry(int index) {
            this.index = index;
        }

        @Override
        protected String doInBackground(String... params) {
                try {
                    connect("todolist/entries/update.php","&entry_id=" + entry_id[index] + "&description=" + task[index] + "&state=" + state[index]);

                    String line = br.readLine();
                    if (line.substring(0, 3).equals("err")) {
                        error(line);
                    }

                } catch (IOException e) {
                    error("cannot connect to server");
                    e.printStackTrace();
                }
            return null;
        }
    }

    protected static class Get_Lists extends MySQL {
        @Override
        protected String doInBackground(String... params) {
            try {
                connect("todolist/get.php","");

                String line=br.readLine();
                System.out.println("------------------"+line);
                String li;
                while((li=br.readLine())!=null) {
                    System.out.println("------------------"+li);
                }

                if(line.substring(0,3).equals("suc")) {
                    return line.substring(3);
                }else {
                    error(line);
                }

            } catch (IOException e) {
                error("cannot connect to server");
                e.printStackTrace();
            }
            return null;
        }
    }

    protected static class Get_Entries extends MySQL {

        protected int id;

        protected Get_Entries(int id) {
            this.id = id;
        }

        @Override
        protected String doInBackground(String... args) {
            try {
                connect("todolist/entries/get.php","&table_id=" + id);

                String line=br.readLine();
                if(line.substring(0,3).equals("suc")) {
                    return line.substring(3);
                }else {
                    error(line);
                }

            } catch (IOException e) {
                error("cannot connect to server");
                e.printStackTrace();
            }
            return null;
        }
    }
}
