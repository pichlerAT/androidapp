package fry.oldschool.data;

public class Contact {

    public int id;

    public int user_id;

    public String email;

    public String name;

    protected Contact(int id,int user_id,String email,String name) {
        this.id = id;
        this.user_id = user_id;
        this.email = email;
        this.name = name;
    }

    public boolean equals(Share share) {
        return ( share.user_id == user_id );
    }

}
