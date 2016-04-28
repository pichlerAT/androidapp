package fry.oldschool;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Stefan on 26.04.2016.
 */
public class MySQL {

    public static final String ADDRESS="http://193.81.253.221/Oldschool/";

    private static void msg(String s) {
        System.out.println("----    _"+s);
    }

    public static byte register(String email,String password) {
        msg("C1C");
        try {
            URL url=new URL(ADDRESS+"register.php?e="+email+"&p="+password);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.connect();
            msg("C2C");
            InputStream is=con.getInputStream();
            BufferedReader br=new BufferedReader(new InputStreamReader(is));
            msg("C3C");
            String line;
            while((line=br.readLine())!=null) {
                msg(line);
            }
            msg("C4C");
            br.close();
            is.close();
            con.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static byte login(String email,String password) {

        return 0;
    }

}
