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

}
