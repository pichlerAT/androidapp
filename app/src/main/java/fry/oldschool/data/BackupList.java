package fry.oldschool.data;

import java.util.ArrayList;
import java.util.Collection;

public class BackupList<E extends MySQL> {

    protected int length;

    protected ArrayList<E> list;

    protected ArrayList<E> backupList;

    public BackupList() {
        list = new ArrayList<>();
        backupList = new ArrayList<>();
    }

    public ArrayList<E> getList() {
        return list;
    }

    protected ArrayList<E> getBackupList() {
        return backupList;
    }

    public int size() {
        return list.size();
    }

    public void add(E element) {
        list.add(element);
    }

    protected void addBackup(E element) {
        backupList.add(element);
    }

    public boolean remove(E element) {
        return (list.remove(element) && backupList.remove(element));
    }

    public E get(int index) {
        return list.get(index);
    }

    public void removeById(int id) {
        for(int i=0; i<list.size(); ++i) {
            if(list.get(i).id == id) {
                list.remove(i);
                break;
            }
        }
        for(int i=0; i<backupList.size(); ++i) {
            if(backupList.get(i).id == id) {
                backupList.remove(i);
                break;
            }
        }
    }

    public E getById(int id) {
        for(E e : list) {
            if(e.id == id) {
                return e;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public void recreateBackup() {
        backupList = new ArrayList<>(list.size());
        for(E e : list) {
            backupList.add((E) e.backup());
        }
    }

    public void synchronizeWith(Collection<E> onlineElements) {
        boolean[] isOnline = new boolean[list.size()];
        for(E onlineElement : onlineElements) {
            int backupIndex = backupList.indexOf(onlineElement);
            int listIndex = list.indexOf(onlineElement);
            boolean sync = synchronize(onlineElement, listIndex, backupIndex);
            if(listIndex >= 0 && listIndex < isOnline.length) {
                isOnline[listIndex] = sync;
            }
        }
        for(int i=isOnline.length-1; i>=0; --i) {
            if(!isOnline[i]) {
                list.remove(i);
            }
        }
        recreateBackup();
    }

    public boolean synchronize(E online, int listIndex, int backupIndex) { // TODO check conMan
        if(listIndex < 0) {
            if(backupIndex < 0) {
                // add online
                list.add(online);

            }else {
                if(online.canEdit()) {
                    // delete online
                    OfflineEntry.delete(online);

                }else {
                    // delete share
                    // TODO delete share

                }
            }
        }else {
            if(backupIndex < 0) {// TODO ASK POSSIBRUU???
                /*
                if(online.isOwner()) {
                    E offline = list.get(listIndex);
                    if(online.equals(offline)) {

                    }else {

                    }
                }else {
                    E offline = list.get(listIndex);
                    if(online.equals(offline)) {

                    }else {

                    }
                }
                */
            }else {
                if(online.canEdit()) {
                    E offline = list.get(listIndex);
                    E backup = backupList.get(backupIndex);

                    if(online.equals(backup)) {
                        // update online from offline
                        ConnectionManager.add(offline);
                        return true;

                    }else {
                        if(offline.equals(backup)) {
                            // update offline from online
                            offline.synchronize(online);
                            return true;

                        }else {
                            // ask which to accept
                            // TODO ASK
                            return true;

                        }
                    }
                }else {
                    E offline = list.get(listIndex);
                    // update offline from online
                    offline.synchronize(online);
                    return true;

                }
            }
        }
        return false;
    }

}
