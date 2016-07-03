package fry.oldschool.data;

import java.util.ArrayList;

import fry.oldschool.utils.FryFile;

public class TasklistManager {

    protected static BackupList<Tasklist> Tasklists = new BackupList<>();

    public static void writeTo(FryFile file) {
        file.write(Tasklists.getList());
        file.write(Tasklists.getBackupList());
    }

    public static void readFrom(FryFile fry) {
        Tasklists = new BackupList<>();

        int NoTasklists = fry.getChar();
        for(int i=0; i<NoTasklists; ++i) {
            Tasklists.add(new Tasklist(fry));
        }

        NoTasklists = fry.getChar();
        for(int i=0; i<NoTasklists; ++i) {
            Tasklists.addBackup(new Tasklist(fry));
        }

    }

    public static void synchronizeTasklistsFromMySQL(String... r) {
        ArrayList<Tasklist> list = new ArrayList<>();
        int index = 0;
        while(index < r.length) {
            Tasklist tl = new Tasklist(Integer.parseInt(r[index++]),Integer.parseInt(r[index++]),Byte.parseByte(r[index++]),r[index++]);

            int NoEntries = Integer.parseInt(r[index++]);
            for(int i=0; i<NoEntries; ++i) {
                TasklistEntry ent = new TasklistEntry(Integer.parseInt(r[index++]), Integer.parseInt(r[index++]), Byte.parseByte(r[index++]), tl.id,r[index++]);
                if(!ConnectionManager.hasEntry(MySQL.TYPE_TASKLIST_ENTRY | MySQL.BASETYPE_DELETE, ent.id)) {
                    tl.entries.add(ent);
                }
            }

            int NoShares = Integer.parseInt(r[index++]);
            for(int i=0; i<NoShares; ++i) {
                tl.shareList.add(Byte.parseByte(r[index++]), Integer.parseInt(r[index++]), Integer.parseInt(r[index++]));
            }

            list.add(tl);
        }
        Tasklists.synchronizeWith(list);
    }

    public static int size() {
        return Tasklists.size();
    }

    public static Tasklist get(int index) {
        return Tasklists.get(index);
    }

    public static ArrayList<Tasklist> getTasklists() {
        return Tasklists.getList();
    }

    protected static void removeTasklist(int id) {
        Tasklists.removeById(id);
    }

    protected static Tasklist getTasklistById(int id) {
        return Tasklists.getById(id);
    }

    protected static TasklistEntry getTasklistEntryById(int id) {
        for(Tasklist t : Tasklists.getList()) {
            for(TasklistEntry e : t.entries) {
                if(e.id == id) {
                    return e;
                }
            }
        }
        return null;
    }
}
