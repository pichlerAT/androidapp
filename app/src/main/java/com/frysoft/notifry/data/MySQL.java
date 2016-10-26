package com.frysoft.notifry.data;

import com.frysoft.notifry.data.value.Value;
import com.frysoft.notifry.utils.FryFile;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MySQL {

    private static final String ADDRESS = "http://www.notifry.com/notifry/";

    private static String LOGIN_DATA = "";

    public static void setLoginData(String email, String password) {
        LOGIN_DATA = "email=" + email + "&password=" + password;
    }

    protected final String path;

    protected String fileName;

    protected String data = "";

    public MySQL(String path, String fileName) {
        this.path = path;
        this.fileName = fileName;
        data = LOGIN_DATA;
    }

    private void add(String name, String value) {
        if(data.length() == 0) {
            data += name + "=" + value;
        }else {
            data += "&" + name + "=" + value;
        }
    }

    public void add(String name, Value value) {
        if(data.length() == 0) {
            data += name + "=" + value.getString();
        }else {
            data += "&" + name + "=" + value.getString();
        }
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void addString(String name, String value) {
        add(name, value.replace('&', (char)1).replace('=', (char)2));
    }

    public void addInteger(String name, int value) {
        add(name, "" + value);
    }

    public void addId(String name, int id) {
        add(name, "" + (id + (id < 0 ? 4294967296L : 0L)));
    }

    public void addUnsigned(String name, byte value) {
        add(name, "" + (value + (value < 0 ? 256 : 0)));
    }

    public void addUnsigned(String name, short value) {
        add(name, "" + (value + (value < 0 ? 65536 : 0)));
    }

    public void addUnsigned(String name, int value) {
        add(name, "" + (value + (value < 0 ? 4294967296L : 0L)));
    }

    public FryFile execute() {
        try {
            URL url = new URL(ADDRESS + path + fileName);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            //con.setRequestProperty("Content-Type", "text/plain; charset=utf-8");

            //if(!data.isEmpty()) {

            //con.setRequestMethod("POST");
            con.setDoOutput(true);
            OutputStreamWriter os = new OutputStreamWriter(con.getOutputStream());
            os.write(data);
            os.flush();
            //os.close();
            //}

            con.connect();

            try {
                FryFile fry = new FryFile.Split("" + (char)0);
                fry.loadFromStream(con.getInputStream());

                os.close();
                con.disconnect();

                String resp = fry.readString();
                if(resp == null) {
                    return null;
                }

                if(resp.equals("suc")) {
                    return fry;
                }

                if(resp.length()>3 && resp.substring(0,4).equals("err_")) {
                    System.out.println("# MYSQLI - ERROR: " + resp);
                    if(fry.hasNext()) {
                        FryFile f = new FryFile.Split("\n");
                        f.loadFromString(fry.readString());
                        while (f.hasNext()) {
                            System.out.println("# " + f.readString());
                        }
                    }
                    return null;
                }

                // TODO REMOVE IN FULL VERSION
                if(resp.contains("<br")) {
                    String newLine;
                    while((newLine = fry.readString())!=null) {
                        System.out.println("# fry: " + newLine);
                    }
                    return null;
                }

                return fry;

            }catch (FileNotFoundException ex) {
                System.out.println("# HttpURLConnection ERROR");

                int code = con.getResponseCode();
                if (code != HttpURLConnection.HTTP_OK) {
                    System.out.println(con.getResponseCode());
                    System.out.println(" # addr = " + path + fileName);
                    InputStream es = con.getErrorStream();
                    if (es != null) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(es));
                        String line;
                        while ((line = br.readLine()) != null) {
                            System.out.println(" # " + line);
                        }
                    }
                }
                return null;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }



}
