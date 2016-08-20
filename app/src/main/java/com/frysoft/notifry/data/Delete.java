package com.frysoft.notifry.data;

class Delete extends MySQL {

    private byte deleteType;

    static void create(byte type, int id) {
        Delete del = new Delete(type, id);
        del.update();
    }

    private Delete(byte type, int id) {
        super(id, User.getId());
        deleteType = type;
    }

    @Override
    protected void remove() {
    }

    @Override
    protected boolean mysql_create() {
        return true;
    }

    @Override
    protected boolean mysql_update() {
        return (executeMySQL(getPath(deleteType) + "delete.php", "&id=" + id) != null);
    }

    @Override
    protected byte getType() {
        return deleteType;
    }

    @Override
    protected String getPath() {
        return getPath(deleteType);
    }
}
