package fry.oldschool.utils;

public class TaskListManager {

    protected void update(TaskList tl) {
        if(!has(tl)) {
            App.TaskLists.add(tl);
        }
    }

    protected boolean has(TaskList tl) {
        if(tl.id == 0) {
            for(TaskList tli : App.TaskLists) {
                if(tli.equals(tl)) {
                    return true;
                }
            }
            return false;
        }
        for(TaskList tli : App.TaskLists) {
            if(tli.id == tl.id) {
                return true;
            }
        }
        return false;
    }

    protected TaskList findTaskListById(int table_id) {
        for(TaskList t : App.TaskLists) {
            if(t.id == table_id) {
                return t;
            }
        }
        return null;
    }

    protected TaskListEntry findEntryById(int entry_id) {
        for(TaskList t : App.TaskLists) {
            for(TaskListEntry e : t.entry) {
                if(e.id == entry_id) {
                    return e;
                }
            }
        }
        return null;
    }
}
