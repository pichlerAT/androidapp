package fry.oldschool.utils;

public interface MySQLListener {

    /**
     * @param arg
     * "tdl_create" a ToDoList has been created
     * "tdl_update" a ToDoList has been updated ( includes all entries )
     * "tdl_delete" a ToDoList has been deleted ( includes all entries )
     * "tdl_entry_create" a ToDoList entry has been created
     * "tdl_entry_update" a ToDoList entry has been updated
     * "tdl_entry_delete" a ToDoList entry has been deleted
     * "tdl_load" the ToDoLists have been loaded from the server
     * "tdl_load_error" an error occured when trying to load the ToDoLists from the server
     * "sync_all" all Lists have been synchronized
     * "sync_tdl" the ToDoLists have been synchronized
     * "Unknown Command: ..." well, an unknown command...
     */
    void mysql_finished(String arg);

}
