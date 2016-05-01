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

    public static ToDoList create(String name,int length) {
        return new ToDoList(0,MySQL.USER_ID,name,length);
    }

    public static ToDoList create(String name,String[] task,byte[] state) {
        ToDoList tdl=new ToDoList(0,MySQL.USER_ID,name,0);
        tdl.update(task,state);

        return tdl;
    }

    protected ToDoList(int id,int owner_id,String name,int length) {
        this.id = id;
        this.owner_id = owner_id;
        this.name = name;
        state = new byte[length];
        entry_id = new int[length];
        user_id = new int[length];
        task = new String[length];
    }

    protected ToDoList(int id,int owner_id,String name,int[] entry_id,int[] user_id,byte[] state,String[] task) {
        this(id,owner_id,name,0);
        this.entry_id = entry_id;
        this.user_id = user_id;
        this.state = state;
        this.task = task;
    }

    public void setAtPosition(int index,String task,byte state) {
        this.task[index] = task;
        this.state[index] = state;
    }

    public void update(String[] task,byte[] state) {
        System.arraycopy(task,0,this.task,0,task.length);
        System.arraycopy(state,0,this.state,0,state.length);
    }

    public boolean done(int index) {
        return ( state[index]==0 );
    }
}
