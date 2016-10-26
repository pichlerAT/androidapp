package com.frysoft.notifry.data;

import com.frysoft.notifry.utils.FryFile;

class Delete extends MySQLEntry {

    protected char type;

    static void create(char type, int id) {
        Delete del = new Delete(type, id);
        ConnectionManager.add(del);
    }

    private Delete(char type, int id) {
        super(id, User.getId(), 0);
        this.type = type;
    }

    @Override
    public void writeTo(FryFile fry) {
        super.writeTo(fry);
        fry.writeChar(getType());
    }

    @Override
    protected void remove() {
    }

    @Override
    protected void addData(MySQL mysql) {
    }

    @Override
    public boolean canEdit() {
        return false;
    }

    @Override
    public int getShareId() {
        return 0;
    }

    @Override
    protected void sync(MySQLEntry entry) {
    }

    @Override
    protected char getType() {
        return type;
    }

    @Override
    protected boolean mysql() {
        MySQL mysql = new MySQL(getPath(), PHP_DELETE);
        mysql.addId("id", id);
        FryFile fry = mysql.execute();
        return (fry != null);
    }

}
