package fry.oldschool.data;

import fry.oldschool.utils.FryFile;
import fry.oldschool.utils.Fryable;
import fry.oldschool.utils.Searchable;

public class Contact implements Fryable, Searchable {

    protected int id;

    protected int user_id;

    protected String email;

    protected String name;

    protected Contact() { }

    protected Contact(int id,int user_id,String email,String name) {
        this.id = id;
        this.user_id = user_id;
        this.email = email;
        this.name = name;
    }

    @Override
    public void writeTo(FryFile fry) {
        fry.write(id);
        fry.write(user_id);
        fry.write(email);
        fry.write(name);
    }

    @Override
    public void readFrom(FryFile fry) {
        id = fry.getInt();
        user_id = fry.getInt();
        email = fry.getString();
        name = fry.getString();
    }

    @Override
    public boolean search(String... keyWords) {
        String name = this.name.toLowerCase();
        for(String keyWord : keyWords) {
            if(name.contains(keyWord)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Contact) {
            Contact c = (Contact) o;
            return (c.id == id && c.user_id == user_id && c.email.equals(email) && c.name.equals(name));
        }
        return false;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

}
