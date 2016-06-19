package fry.oldschool.data;

import java.util.ArrayList;

import fry.oldschool.utils.App;

public class TasklistManager {

    public static String getLocalSaveString() {
        String line = "" + (char)App.Tasklists.size();

        for(Tasklist tl : App.Tasklists) {
            line += (char)(tl.id & 65535) + "" + (char)((tl.id >> 16) & 65535);
            line += (char)(tl.user_id & 65535) + "" + (char)((tl.user_id >> 16) & 65535);
            line += (char)tl.state;
            line += (char)tl.name.length() + tl.name;
            line += (char)tl.entries.size();
            for(TasklistEntry ent : tl.entries) {
                line += (char)(ent.id & 65535) + "" + (char)((ent.id >> 16) & 65535);
                line += (char)(ent.user_id & 65535) + "" + (char)((ent.user_id >> 16) & 65535);
                line += (char)ent.state;
                line += (char)ent.description.length() + ent.description;
            }
        }

        return line;
    }

    public static void recieveLocalSaveString(String line) {
        App.Tasklists = new ArrayList<>();

        if(line == null) {
            return;
        }
        char[] charArray = line.toCharArray();

        int index = 0;
        int numberOfTasklists = charArray[index++];

        for(int i=0; i<numberOfTasklists; ++i) {
            int table_id = charArray[index++] | (charArray[index++] << 16);
            int user_id = charArray[index++] | (charArray[index++] << 16);
            byte state = (byte)charArray[index++];
            int toIndex = charArray[index++] + index;
            String name = "";
            while(index < toIndex) {
                name += charArray[index++];
            }

            Tasklist tl = new Tasklist(table_id,user_id,state,name);
            int numberOfEntries = charArray[index++];

            for(int j=0; j<numberOfEntries; ++j) {
                int id = charArray[index++] | (charArray[index++] << 16);
                user_id = charArray[index++] | (charArray[index++] << 16);
                state = (byte)charArray[index++];
                toIndex = charArray[index++] + index;
                name = "";
                while(index < toIndex) {
                    name += charArray[index++];
                }
                tl.entries.add(new TasklistEntry(id,table_id,user_id,name,state));
            }

            App.Tasklists.add(tl);
        }
    }

    protected static void updateTasklists(String... r) {
        int id = Integer.parseInt(r[0]);
        if(hasTasklistDeleted(id)) {
            return;
        }
        Tasklist tl = findTasklistById(id);
        if(tl == null) {
            tl = new Tasklist(id,Integer.parseInt(r[1]),Byte.parseByte(r[2]),r[3]);
            for(int i=7; i<r.length; i+=4) {
                TasklistEntry e = new TasklistEntry(Integer.parseInt(r[i-3]),id,Integer.parseInt(r[i-2]),r[i-1],Byte.parseByte(r[i]));
                tl.entries.add(e);
            }
            App.Tasklists.add(tl);
        }else {
            // TODO Update: take offline or online data?

        }
    }

    protected static boolean hasTasklistDeleted(int id) {
        for(Entry ent : ConnectionManager.entries) {
            if(ent instanceof Delete) {
                Delete del = (Delete)ent;
                if((del.type & Entry.TYPE_TASKLIST) > 0 && del.id == id) {
                    return true;
                }
            }
        }
        return false;
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
