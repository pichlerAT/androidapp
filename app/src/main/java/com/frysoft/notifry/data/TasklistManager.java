package com.frysoft.notifry.data;

import java.util.ArrayList;

import com.frysoft.notifry.utils.FryFile;
import com.frysoft.notifry.utils.Logger;

public class TasklistManager {

    protected static BackupList<Tasklist> Tasklists = new BackupList<>();

    public static void writeTo(FryFile file) {
        Logger.Log("BackupList", "writeTo(FryFile)");
        file.write(Tasklists.getList());
        file.write(Tasklists.getBackupList());
    }

    public static void readFrom(FryFile fry) {
        Logger.Log("BackupList", "readFrom(FryFile)");
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

    public static void synchronizeTasklistsFromMySQL(FryFile fry) {
        Logger.Log("BackupList", "synchronizeTasklistsFromMySQL(String[])");

        ArrayList<Tasklist> list = new ArrayList<>();

        int NoTasklists = fry.getArrayLength();
        for(int i=0; i<NoTasklists; ++i) {
            Tasklist tl = new Tasklist(fry.getInt(), fry.getInt(), fry.getByte(), fry.getString(), fry.getInt());

            int NoEntries = fry.getArrayLength();
            for(int k=0; k<NoEntries; ++k) {
                TasklistEntry ent = new TasklistEntry(fry.getInt(), fry.getInt(), fry.getByte(), tl.id, fry.getString());
                if(!ConnectionManager.hasEntry((char)(MySQL.TYPE_TASKLIST_ENTRY | MySQL.BASETYPE_DELETE), ent.id)) {
                    tl.entries.add(ent);
                }
            }

            int NoShares = fry.getArrayLength();
            for(int k=0; k<NoShares; ++k) {
                tl.shares.addStorage(fry.getByte(), fry.getInt(), fry.getInt());
            }

            list.add(tl);
        }

        Tasklists.synchronizeWith(list);
    }

    public static int size() {
        Logger.Log("BackupList", "size()");
        return Tasklists.size();
    }

    public static Tasklist get(int index) {
        Logger.Log("BackupList", "get(int)");
        return Tasklists.get(index);
    }

    public static ArrayList<Tasklist> getTasklists() {
        Logger.Log("BackupList", "getTasklists()");
        return Tasklists.getList();
    }

    protected static void removeTasklist(int id) {
        Logger.Log("BackupList", "removeTasklist(int)");
        Tasklists.removeById(id);
    }

    protected static Tasklist getTasklistById(int id) {
        Logger.Log("BackupList", "getTasklistById(int)");
        return Tasklists.getById(id);
    }

    protected static TasklistEntry getTasklistEntryById(int id) {
        Logger.Log("BackupList", "getTasklistEntryById(int)");
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
