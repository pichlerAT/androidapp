package fry.oldschool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Stefan on 30.04.2016.
 */
public class ToDoList {

    public int id;

    //public int owner_id;

    public String name;

    public byte[] state;

    public int[] entry_id;

    //public int[] user_id;

    public String[] task;

    public static ToDoList create(String name,int length) {
        ToDoList tdl=new ToDoList(name,length);
        tdl.create();
        return tdl;
    }

    protected ToDoList(String name,int length) {
        this.name = name;
        state = new byte[length];
        entry_id = new int[length];
        task = new String[length];
    }

    protected ToDoList(int id,String name,int[] entry_id,byte[] state,String[] task) {
        this(name,0);
        this.entry_id = entry_id;
        this.state = state;
        this.task = task;
    }

    public void setAtPosition(int index,String task,byte state) {
        this.task[index] = task;
        this.state[index] = state;
    }

    public boolean done(int index) {
        return ( state[index]==0 );
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
                URL url=new URL(ADDRESS+"todolist.php?user_id="+USER_ID+"&name="+name);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.connect();
                BufferedReader br=new BufferedReader(new InputStreamReader(con.getInputStream()));

                String line=br.readLine();
                if(line.substring(0,3).equals("suc")) {
                    id = Integer.parseInt(line.substring(3));
                }else {
                    error(line);
                }

                br.close();
                con.disconnect();

            } catch (IOException e) {
                error("cannot connect to server");
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
                URL url=new URL(ADDRESS+"todolist_entries_create.php?table_id="+id+"&user_id="+USER_ID+"&description="+task[index]+"&state="+state[index]);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.connect();
                BufferedReader br=new BufferedReader(new InputStreamReader(con.getInputStream()));

                String line=br.readLine();
                if(line.substring(0,3).equals("suc")) {
                    entry_id[index] = Integer.parseInt(line.substring(3));
                    //user_id[index] = USER_ID;
                }else {
                    error(line);
                }

                br.close();
                con.disconnect();

            } catch (IOException e) {
                error("cannot connect to server");
            }
            return null;
        }
    }

    protected class Update extends MySQL {
        @Override
        protected String doInBackground(String... args) {
            try {
                URL url=new URL(ADDRESS+"todolist_update.php?table_id="+id+"&name="+name);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.connect();
                BufferedReader br=new BufferedReader(new InputStreamReader(con.getInputStream()));

                String line=br.readLine();
                if(line.substring(0,3).equals("err")) {
                    error(line);
                }

                br.close();
                con.disconnect();

            } catch (IOException e) {
                error("cannot connect to server");
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
                    URL url = new URL(ADDRESS + "todolist_entries_update.php?entry_id=" + entry_id[index] + "&user_id=" + USER_ID + "&description=" + task[index] + "&state=" + state[index]);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.connect();
                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String line = br.readLine();
                    if (line.substring(0, 3).equals("err")) {
                        error(line);
                    }

                    br.close();
                    con.disconnect();

                } catch (IOException e) {
                    error("cannot connect to server");
                }
            return null;
        }
    }
}
