package fry.oldschool;

/**
 * Created by Stefan on 30.04.2016.
 */
public class ToDoList {

    public int id;

    public int owner_id;

    public String name;

    public byte[] state;

    public int[] entry_id;

    public int[] user_id;

    public String[] task;

    public ToDoList(int id,int owner_id,String name) {
        this.id = id;
        this.owner_id = owner_id;
        this.name = name;
    }

    public ToDoList(int id,int owner_id,String name,int[] entry_id,int[] user_id,byte[] state,String[] task) {
        this(id,owner_id,name);
        this.entry_id = entry_id;
        this.user_id = user_id;
        this.state = state;
        this.task = task;
    }

    public boolean done(int index) {
        return ( state[index]==0 ? true : false );
    }
}
