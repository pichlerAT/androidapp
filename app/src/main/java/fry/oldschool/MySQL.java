package fry.oldschool;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Stefan on 26.04.2016.
 */
public abstract class MySQL extends AsyncTask<String,String,String> {

    public static final String ADDRESS="http://193.81.75.135/Oldschool/";

    public static int USER_ID;

    protected boolean errorDialog=false;
    protected String errorTitle="ERROR";
    protected String errorMessage="";

    @Override
    protected void onPostExecute(String file_url) {
        if(errorDialog) {
            App.errorDialog(errorTitle,errorMessage);
        }
    }

    protected void error(String errorMessage) {
        this.errorMessage = errorMessage;
        errorDialog = true;
    }

    public static class Register extends MySQL {
        @Override
        protected String doInBackground(String... args) {
            try {
                URL url=new URL(ADDRESS+"register.php?e="+args[0]+"&p="+args[1]);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.connect();
                BufferedReader br=new BufferedReader(new InputStreamReader(con.getInputStream()));

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

                br.close();
                con.disconnect();

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
                URL url=new URL(ADDRESS+"login.php?e="+args[0]+"&p="+args[1]);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.connect();
                BufferedReader br=new BufferedReader(new InputStreamReader(con.getInputStream()));

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

                br.close();
                con.disconnect();

            } catch (IOException e) {
                error("cannot connect to server");
            }
            return null;
        }
    }

    public static class Todolist extends MySQL {
        @Override
        protected String doInBackground(String... args) {
            try {
                URL url=new URL(ADDRESS+"todolist.php?i="+args[0]+"&n="+args[1]);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.connect();
                BufferedReader br=new BufferedReader(new InputStreamReader(con.getInputStream()));

                String line=br.readLine();
                error(line);

                br.close();
                con.disconnect();

            } catch (IOException e) {
                error("cannot connect to server");
            }
            return null;
        }
    }

}
