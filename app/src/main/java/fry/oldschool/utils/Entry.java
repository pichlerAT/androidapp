package fry.oldschool.utils;

public abstract class Entry extends MySQL {

    protected int id;

    protected int user_id;

    protected Entry(int id, int user_id) {
        this.id = id;
        this.user_id = user_id;
    }

    public void create() {
        App.conMan.add(this);
    }

    public void update() {
        App.conMan.add(this);
    }

    public void delete() {
        user_id = -1;
        App.conMan.add(this);
    }

    protected String[] getAddress() {
        if(user_id<0) {
            return getDelete();
        }
        if(id == 0) {
            return getCreate();
        }
        return getUpdate();
    }

    @Override
    protected String getString() {
        return ( super.getString() + SEP_1 + id + SEP_1 + user_id );
    }

    @Override
    protected boolean mysql_update() {
        String[] addr = getAddress();
        String resp = connect(addr[0],addr[1]);

        if(resp == null) {
            return true;
        }

        App.conMan.remove(this);
        if(id == 0) {
            setId(Integer.parseInt(resp));
        }

        return false;
    }

    protected void setId(int id) {
        this.id = id;
    }

    protected abstract String[] getCreate();

    protected abstract String[] getUpdate();

    protected abstract String[] getDelete();
}