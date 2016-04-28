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

    public static final String ADDRESS="http://212.183.46.2/Oldschool/";

    protected boolean errorDialog=false;
    protected String errorTitle="ERROR";
    protected String errorMessage="";

    private static void msg(String s) {
        System.out.println("----"+s);
    }

    @Override
    protected void onPostExecute(String file_url) {
        if(errorDialog) {
            App.errorDialog(errorTitle,errorMessage);
        }
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
                    // SUCCESS
                }else if(c=='0') {
                    // missing parameters
                }else if(c=='1') {
                    // email already in use
                }else if(c=='2') {
                    // cannot insert into table
                }

                br.close();
                con.disconnect();

            } catch (IOException e) {
                // cannot connect to server
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
                    // SUCCESS
                    msg("SUCCESS");

                }else if(c=='0') {
                    errorDialog=true;
                    errorMessage="missing parameters";
                    msg("missing parameters");

                }else if(c=='1') {
                    errorDialog=true;
                    errorMessage="email not found";
                    msg("email not found");

                }else if(c=='2') {
                    errorDialog=true;
                    errorMessage="wrong password";
                    msg("wrong password");
                }

                br.close();
                con.disconnect();

            } catch (IOException e) {
                // cannot connect to server
            }
            return null;
        }
    }

}
