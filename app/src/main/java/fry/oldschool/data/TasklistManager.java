package fry.oldschool.data;

import java.util.ArrayList;

import fry.oldschool.utils.App;
import fry.oldschool.utils.FryFile;

public class TasklistManager {

    public static ArrayList<Tasklist> TasklistBackup = new ArrayList<>();

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
                tl.entries.add(new TasklistEntry(file.getInt(),table_id,file.getInt(),file.getByte(),file.getString()));
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
                tl.entries.add(TasklistEntry.createBackup(file.getInt(),table_id,file.getInt(),file.getByte(),file.getString()));
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
            TasklistEntry e = new TasklistEntry(Integer.parseInt(r[i-3]),id,Integer.parseInt(r[i-2]),Byte.parseByte(r[i]),r[i-1]);
            tl_on.entries.add(e);
        }
        if(tl_off == null) {
            App.Tasklists.add(tl_on);
            TasklistBackup.add(Tasklist.createBackup(tl_on));
        }else {
            // TODO Update: take offline or online data?

        }
    }

    public static void addTasklist(Tasklist tl) {
        App.Tasklists.add(tl);
        TasklistBackup.add(Tasklist.createBackup(tl));
    }

    public static void removeTasklist(int id) {
        for(int i=0; i<App.Tasklists.size(); ++i) {
            if(App.Tasklists.get(i).id == id) {
                App.Tasklists.remove(i);
            }
        }
        for(int i=0; i<TasklistBackup.size(); ++i) {
            if(TasklistBackup.get(i).id == id) {
                TasklistBackup.remove(i);
            }
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

    protected static TasklistEntry findTasklistEntryById(int id) {
        for(Tasklist t : App.Tasklists) {
            for(TasklistEntry e : t.entries) {
                if(e.id == id) {
                    return e;
                }
            }
        }
        return null;
    }
}
