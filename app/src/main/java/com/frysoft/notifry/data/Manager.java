package com.frysoft.notifry.data;

import com.frysoft.notifry.utils.FryFile;
import com.frysoft.notifry.utils.Fryable;

import java.util.ArrayList;

public class Manager<E extends MySQLEntry> implements Fryable {

    protected int nextId = 0;

    protected BackupList<E> list = new BackupList<>();

    @Override
    public void writeTo(FryFile fry) {
        fry.writeObjects(list.getList());
        fry.writeObjects(list.getBackupList());
    }

    public E get(int index) {
        return list.get(index);
    }

    public E getById(int id) {
        for(E e : list.getList()) {
            if(e.id == id /*&& e.isOnline()*/) {
                return e;
            }
        }
        return null;
    }
/*
    protected E getOfflineById(int id) {
        for(E e : list.getList()) {
            if(e.id == id && e.isOffline()) {
                return e;
            }
        }
        return null;
    }
*/
    public ArrayList<E> getList() {
        return list.getList();
    }

    protected void add(E e) {
        if(e.isOffline()) {

            if(e.id == 0) {
                e.id = nextId++;

            }else {
                nextId = e.id + 1;
            }

        }
        list.add(e);
    }

    protected void addBackup(E e) {
        list.addBackup(e);
    }

    protected boolean remove(E e) {
        return list.remove(e);
    }

    public int size() {
        return list.size();
    }

    protected void synchronizeWith(ArrayList<E> eList) {
        list.synchronizeWith(eList);
    }

    protected void optimizeIds() {
        nextId = 1;
        for(E e : list.getList()) {
            if(e.isOffline()) {
                e.id = nextId++;
            }
        }
    }

}
