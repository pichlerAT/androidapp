package fry.oldschool.data;

import java.util.ArrayList;

import fry.oldschool.utils.App;
import fry.oldschool.utils.FryFile;

public class TaskistManager {

    public static ArrayList<Tasklist> TasklistBackup;

    public static void writeTo(FryFile file) {
        file.write(App.Tasklists.toArray());
        file.write(TasklistBackup.toArray());
    }

    public static void readFrom(FryFile file) {
        App.Tasklists = new ArrayList<>();

        int NoTasklists = file.getChar();
        for(int i=0; i<NoTasklists; ++i) {
            int table_id = file.getInt();
            Tasklist tl = new Tasklist(table_id,file.getInt(),file.getByte(),file.getString());

            int NoEntries = file.getChar();
            for(int j=0; j<NoEntries; ++j) {
                tl.entries.add(new TaskistEntry(file.getInt(),table_id,file.getInt(),file.getByte(),file.getString()));
            }

            App.Tasklists.add(tl);
        }

        TasklistBackup = new ArrayList<>();

        NoTasklists = file.getChar();
        for(int i=0; i<NoTasklists; ++i) {
            int table_id = file.getInt();
            Tasklist tl = Tasklist.createBackup(table_id,file.getInt(),file.getByte(),file.getString());

            int NoEntries = file.getChar();
            for(int j=0; j<NoEntries; ++j) {
                tl.entries.add(TaskistEntry.createBackup(file.getInt(),table_id,file.getInt(),file.getByte(),file.getString()));
            }

            TasklistBackup.add(tl);
        }
    }

    protected static void updateTasklists(String... r) {
        int id = Integer.parseInt(r[0]);
        if(ConnectionManager.hasEntry(OnlineEntry.TYPE_TASKLIST | OnlineEntry.BASETYPE_DELETE, id)) {
            return;
        }
        Tasklist tl_off = findTasklistById(id);

        Tasklist tl_on = new Tasklist(id,Integer.parseInt(r[1]),Byte.parseByte(r[2]),r[3]);
        for(int i=7; i<r.length; i+=4) {
            TaskistEntry e = new TaskistEntry(Integer.parseInt(r[i-3]),id,Integer.parseInt(r[i-2]),Byte.parseByte(r[i]),r[i-1]);
            tl_on.entries.add(e);
        }
        if(tl_off == null) {
            App.Tasklists.add(tl_on);
        }else {
            // TODO Update: take offline or online data?

        }
    }

    protected static Tasklist findTasklistById(int id) {
        for(Tasklist t : App.Tasklists) {
            if(t.id == id) {
                return t;
            }
        }
        return null;
    }

    protected static TaskistEntry findTasklistEntryById(int id) {
        for(Tasklist t : App.Tasklists) {
            for(TaskistEntry e : t.entries) {
                if(e.id == id) {
                    return e;
                }
            }
        }
        return null;
    }
}
