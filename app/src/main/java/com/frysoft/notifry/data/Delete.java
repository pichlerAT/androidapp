package com.frysoft.notifry.data;

class Delete extends MySQL {

    private byte deleteType;

    static void create(byte type, int id) {
        Delete del = new Delete(type, id);
        del.delete();
    }

    private Delete(byte type, int id) {
        super(id, 0);
        deleteType = type;
    }

    @Override
    protected boolean mysql_create() {
        return true;
    }

    @Override
    protected boolean mysql_update() {
        return true;
    }

    @Override
    protected boolean mysql_delete() {
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
