package com.frysoft.notifry.data;

import com.frysoft.notifry.utils.FryFile;
import com.frysoft.notifry.utils.Fryable;

import java.util.ArrayList;

public class Manager<E extends MySQLEntry> implements Fryable {

    protected ArrayList<E> list = new ArrayList<>();

    @Override
    public void writeTo(FryFile fry) {
        fry.writeObjects(list);
    }

    public E get(int index) {
        return list.get(index);
    }

    public E getById(int id) {
        for(E e : list) {
            if(e.id == id) {
                return e;
            }
        }
        return null;
    }

    public ArrayList<E> getList() {
        return list;
    }

    protected void add(int index, E e) {
        list.add(index, e);
    }

    protected void add(E e) {
        list.add(e);
    }

    protected boolean remove(E e) {
        return list.remove(e);
    }

    public int size() {
        return list.size();
    }

    protected void synchronizeWith(E[] onList) {
        boolean[] online = new boolean[list.size()];

        for(int i=0; i<onList.length; ++i) {
            int index = indexOf(onList[i]);

            if(index < 0) {
                if(!ConnectionManager.hasEntry(onList[i], MySQLEntry.BASETYPE_DELETE)) {
                    list.add(onList[i]);
                }

            }else {
                online[index] = true;
                list.get(index).synchronize(onList[i]);
            }
        }

        for(int i=online.length-1; i>=0; --i) {
            if(!online[i]) {
                list.remove(i);
            }
        }
    }

    protected int indexOf(E e) {
        for(int i=0; i<list.size(); ++i) {
            if(list.get(i).equals(e)) {
                return i;
            }
        }
        return -1;
    }

}
