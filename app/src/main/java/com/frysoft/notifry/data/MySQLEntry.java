package com.frysoft.notifry.data;

import com.frysoft.notifry.utils.FryFile;
import com.frysoft.notifry.utils.Logger;

public abstract class MySQLEntry extends MySQL {

    protected MySQLEntry(FryFile fry) {
        super(fry);
        Logger.Log("MySQLEntry", "MySQLEntry(FryFile)");
    }

    protected MySQLEntry(char type, int id, int user_id) {
        super(type, id, user_id);
        Logger.Log("MySQLEntry", "MySQLEntry(char,int,int)");
    }
/*
    @Override
    public abstract boolean equals(Object o);
*/
    public abstract Object backup();

    protected abstract void synchronize(MySQL mysql);

    public abstract boolean canEdit();

    public abstract int getShareId();

}
