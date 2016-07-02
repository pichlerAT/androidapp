package fry.oldschool.data;

import fry.oldschool.utils.FryFile;
import fry.oldschool.utils.Fryable;
import fry.oldschool.utils.Searchable;

public class Contact implements Fryable, Searchable {

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

    @Override
    public void writeTo(FryFile file) {
        file.write(id);
        file.write(user_id);
        file.write(email);
        file.write(name);
    }

    @Override
    public boolean search(String... keyWords) {
        for(String keyWord : keyWords) {
            if(name.contains(keyWord)) {
                return true;
            }
        }
        return false;
    }
}
