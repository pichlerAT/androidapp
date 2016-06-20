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

    public static void synchronizeTasklistsFromMySQL(String... r) {
        int index = 0;
        boolean[] isOnline = new boolean[App.Tasklists.size()];
        while(index < r.length) {
            Tasklist on = new Tasklist(Integer.parseInt(r[index++]),Integer.parseInt(r[index++]),Byte.parseByte(r[index++]),r[index++]);

            int NoEntries = Integer.parseInt(r[index++]);
            for(int i=0; i<NoEntries; ++i) {
                TasklistEntry ent = new TasklistEntry(Integer.parseInt(r[index++]), on.id, Integer.parseInt(r[index++]), Byte.parseByte(r[index++]),r[index++]);
                if(!ConnectionManager.hasEntry(OnlineEntry.TYPE_TASKLIST_ENTRY | OnlineEntry.BASETYPE_DELETE, ent.id)) {
                    on.entries.add(ent);
                }
            }

            int NoShares = Integer.parseInt(r[index++]);
            for(int i=0; i<NoShares; ++i) {
                on.sharedContacts.add(new Share(Integer.parseInt(r[index++]), on.id, Integer.parseInt(r[index++]), Byte.parseByte(r[index++])));
            }

            int off_index = getTasklistIndexById(on.id);

            if(off_index >= 0) {
                isOnline[off_index] = true;
                Tasklist backup = TasklistBackup.remove(findBackupTasklistIndexById(on.id));
                if(on.equals(backup)) {
                    Tasklist off = App.Tasklists.get(off_index);
                    ConnectionManager.add(off);
                    TasklistBackup.add(off);
                }else {
                    App.Tasklists.set(off_index,on);
                    TasklistBackup.add(Tasklist.createBackup(on));
                }
            }else if(!ConnectionManager.hasEntry(OnlineEntry.TYPE_TASKLIST | OnlineEntry.BASETYPE_DELETE, on.id)) {
                App.Tasklists.add(on);
                TasklistBackup.add(Tasklist.createBackup(on));
            }
        }
        for(int i=isOnline.length-1; i>=0; --i) {
            if(!isOnline[i] && App.Tasklists.get(i).id != 0) {
                App.Tasklists.remove(i);
            }
        }
    }

    public static int findBackupTasklistIndexById(int id) {
        for(int i=0; i<TasklistBackup.size(); ++i) {
            if(TasklistBackup.get(i).id == id) {
                return i;
            }
        }
        return -1;
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

    public static int getTasklistIndexById(int id) {
        for(int i=0; i<App.Tasklists.size(); ++i) {
            if(App.Tasklists.get(i).id == id) {
                return i;
            }
        }
        return -1;
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
