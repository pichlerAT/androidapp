package fry.oldschool;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

/**
 * Created by Stefan on 26.04.2016.
 */
public abstract class MySQL extends AsyncTask<String,String,String> {

    public static final String ADDRESS="http://194.118.34.232/Oldschool/";

    public static int USER_ID = 9;

    protected boolean errorDialog=false;
    protected String errorTitle="ERROR";
    protected String errorMessage="";

    protected HttpURLConnection con;
    protected BufferedReader br;

    @Override
    protected void onPostExecute(String file_url) {
        if(errorDialog) {
            App.errorDialog(errorTitle,errorMessage);
        }
        try {
            if(br != null) {
                br.close();
            }
            if(con != null) {
                con.disconnect();
            }
        } catch (IOException e) {
            error("cannot close connection");
        }
    }

    protected void error(String errorMessage) {
        this.errorMessage = errorMessage;
        errorDialog = true;
    }

    protected void connect(String address,String post) throws IOException {
        URL url=new URL(ADDRESS+address);
        con = (HttpURLConnection)url.openConnection();
        con.setDoOutput(true);
        OutputStreamWriter os = new OutputStreamWriter(con.getOutputStream());
        os.write(post);
        os.flush();
        os.close();
        con.connect();
        br=new BufferedReader(new InputStreamReader(con.getInputStream()));
    }

    protected String response() {
        try {
            return get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class Register extends MySQL {
        @Override
        protected String doInBackground(String... args) {
            try {
                connect("register.php","e="+args[0]+"&p="+args[1]);

                String line;
                while((line=br.readLine())!=null) {
                    String sub=line.substring(0,4);
                    if(sub.equals("Resp") || sub=="Resp") {
                        break;
                    }
                }
                char c=line.charAt(4);
                if(c=='S') {
                    error("SUCCESS");
                }else if(c=='0') {
                    error("missing parameters");
                }else if(c=='1') {
                    error("email already in use");
                }else if(c=='2') {
                    error("cannot insert into table");
                }

            } catch (IOException e) {
                error("cannot connect to server");
            }
            return null;
        }
    }

    public static class Login extends MySQL {
        @Override
        protected String doInBackground(String... args) {
            try {
                connect("login.php","e="+args[0]+"&p="+args[1]);

                String line;
                while((line=br.readLine())!=null) {
                    String sub=line.substring(0,4);
                    if(sub.equals("Resp") || sub=="Resp") {
                        break;
                    }
                }
                char c=line.charAt(4);
                if(c=='S') {
                    error("SUCCESS");
                }else if(c=='0') {
                    error("missing parameters");

                }else if(c=='1') {
                    error("email not found");

                }else if(c=='2') {
                    error("wrong password");
                }

            } catch (IOException e) {
                error("cannot connect to server");
            }
            return null;
        }
    }

    public static class todolist_create extends MySQL {

        public int id;

        @Override
        protected String doInBackground(String... args) {
            try {
                connect("todolist.php","user_id="+USER_ID+"&name="+args[0]);

                String line=br.readLine();
                if(line.substring(0,3).equals("suc")) {
                    id = Integer.parseInt(line.substring(3));
                }else {
                    error(line);
                }

            } catch (IOException e) {
                error("cannot connect to server");
            }
            return null;
        }
    }

}
