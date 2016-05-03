package fry.oldschool.utils;

public interface MySQLListener {

    /**
     * @param arg
     * "tdl_create" a ToDoList has been created
     * "tdl_update" a ToDoList has been updated
     * "tdl_delete" a ToDoList has been deleted
     * "tdl_entry_create" an ToDoList entry has been created
     * "tdl_entry_update" an ToDoList entry has been updated
     * "tdl_entry_delete" an ToDoList entry has been deleted
     * "tdl_load" the ToDoLists have been loaded from the server
     * "sync_all" all Lists have been synchronized
     * "sync_tdl" the ToDoLists have been synchronized
     */
    void mysql_finished(String arg);

}
