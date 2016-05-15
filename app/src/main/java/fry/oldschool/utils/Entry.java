package fry.oldschool.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class Entry extends MySQL {

    protected int id;

    protected int user_id;

    protected Entry(int id,int user_id) {
        this.id = id;
        this.user_id = user_id;
    }

    public void create() {
        App.conMan.add(this);
    }

    public void update() {
        App.conMan.add(this);
    }

    public void delete() {
        user_id = -1;
        App.conMan.add(this);
    }

    protected String[] getAddress() {
        if(user_id<0) {
            return getDelete();
        }
        if(id == 0) {
            return getCreate();
        }
        return getUpdate();
    }

    protected String getString() {
        return ( getType() + SEP_1 + id + SEP_1 + user_id );
    }

    protected void setId(int id) {
        this.id = id;
    }

    protected abstract byte getType();

    protected abstract String[] getCreate();

    protected abstract String[] getUpdate();

    protected abstract String[] getDelete();

    protected boolean mysql_update() {
        try {
            String[] addr = getAddress();
            URL url = new URL(ADDRESS + addr[0]);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            OutputStreamWriter os = new OutputStreamWriter(con.getOutputStream());

            os.write("user_id=" + USER_ID + "&password=" + USER_PASSWORD + addr[1]);
            os.flush();
            os.close();
            con.connect();

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line=br.readLine();
            br.close();
            con.disconnect();

            if(line.substring(0,3).equals("suc")) {
                App.conMan.remove(this);
                if(id == 0) {
                    setId(Integer.parseInt(line.substring(3)));
                }
                System.out.println("----- Entry#mysql_update: "+addr[0]);
                return false;
            }else {
                System.out.println("----- Entry#mysql_update: "+line);
                return true;
            }

        }catch(IOException ex) {
            ex.printStackTrace();
        }
        return true;
    }
}