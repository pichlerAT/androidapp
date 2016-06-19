package fry.oldschool.data;

import java.util.ArrayList;

import fry.oldschool.utils.App;
import fry.oldschool.utils.FryFile;

public class TaskListManager {

    public static ArrayList<TaskList> TasklistBackup;

    public static void writeTo(FryFile file) {
        file.write(App.Tasklists.toArray());
        file.write(TasklistBackup.toArray());
    }

    public static void readFrom(FryFile file) {
        App.Tasklists = new ArrayList<>();

        int NoTasklists = file.getChar();
        for(int i=0; i<NoTasklists; ++i) {
            int table_id = file.getInt();
            TaskList tl = new TaskList(table_id,file.getInt(),file.getByte(),file.getString());

            int NoEntries = file.getChar();
            for(int j=0; j<NoEntries; ++j) {
                tl.entries.add(new TaskListEntry(file.getInt(),table_id,file.getInt(),file.getByte(),file.getString()));
            }

            App.Tasklists.add(tl);
        }

        TasklistBackup = new ArrayList<>();

        NoTasklists = file.getChar();
        for(int i=0; i<NoTasklists; ++i) {
            int table_id = file.getInt();
            TaskList tl = TaskList.createBackup(table_id,file.getInt(),file.getByte(),file.getString());

            int NoEntries = file.getChar();
            for(int j=0; j<NoEntries; ++j) {
                tl.entries.add(TaskListEntry.createBackup(file.getInt(),table_id,file.getInt(),file.getByte(),file.getString()));
            }

            TasklistBackup.add(tl);
        }
    }

    protected static void updateTasklists(String... r) {
        int id = Integer.parseInt(r[0]);
        if(ConnectionManager.hasEntry(OnlineEntry.TYPE_TASKLIST | OnlineEntry.BASETYPE_DELETE, id)) {
            return;
        }
        TaskList tl_off = findTasklistById(id);

        TaskList tl_on = new TaskList(id,Integer.parseInt(r[1]),Byte.parseByte(r[2]),r[3]);
        for(int i=7; i<r.length; i+=4) {
            TaskListEntry e = new TaskListEntry(Integer.parseInt(r[i-3]),id,Integer.parseInt(r[i-2]),Byte.parseByte(r[i]),r[i-1]);
            tl_on.entries.add(e);
        }
        if(tl_off == null) {
            App.Tasklists.add(tl_on);
        }else {
            // TODO Update: take offline or online data?

        }
    }

    protected static TaskList findTasklistById(int id) {
        for(TaskList t : App.Tasklists) {
            if(t.id == id) {
                return t;
            }
        }
        return null;
    }

    protected static TaskListEntry findTasklistEntryById(int id) {
        for(TaskList t : App.Tasklists) {
            for(TaskListEntry e : t.entries) {
                if(e.id == id) {
                    return e;
                }
            }
        }
        return null;
    }
}
