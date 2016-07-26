package com.frysoft.notifry.data;

import java.util.ArrayList;
import java.util.Collection;

import com.frysoft.notifry.utils.Logger;

public class BackupList<E extends MySQLEntry> {

    protected int length;

    protected ArrayList<E> list;

    protected ArrayList<E> backupList;

    public BackupList() {
        Logger.Log("BackupList", "BackupList()");
        list = new ArrayList<>();
        backupList = new ArrayList<>();
    }

    public ArrayList<E> getList() {
        Logger.Log("BackupList", "getList()");
        return list;
    }

    protected ArrayList<E> getBackupList() {
        Logger.Log("BackupList", "getBackupList()");
        return backupList;
    }

    public int size() {
        Logger.Log("BackupList", "size()");
        return list.size();
    }

    public void add(E element) {
        Logger.Log("BackupList", "add(E)");
        list.add(element);
    }

    protected void addBackup(E element) {
        Logger.Log("BackupList", "addBackup(E)");
        backupList.add(element);
    }

    public boolean remove(E element) {
        Logger.Log("BackupList", "remove(E)");
        return (list.remove(element) && backupList.remove(element));
    }

    public E get(int index) {
        Logger.Log("BackupList", "get(int)");
        return list.get(index);
    }

    public void removeById(int id) {
        Logger.Log("BackupList", "removeById(int)");
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
        Logger.Log("BackupList", "getById(int)");
        for(E e : list) {
            if(e.id == id) {
                return e;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public void recreateBackup() {
        Logger.Log("BackupList", "recreateBackup()");
        backupList = new ArrayList<>(list.size());
        for(E e : list) {
            backupList.add((E) e.backup());
        }
    }

    protected int indexOfBackup(E searchElement) {
        for(int i=0; i<backupList.size(); ++i) {
            E listElement = backupList.get(i);
            if(listElement.getType() == searchElement.getType() && listElement.id == searchElement.id) {
                return i;
            }
        }
        return -1;
    }

    protected int indexOfList(E searchElement) {
        for(int i=0; i<list.size(); ++i) {
            E listElement = list.get(i);
            if(listElement.getType() == searchElement.getType() && listElement.id == searchElement.id) {
                return i;
            }
        }
        return -1;
    }

    public void synchronizeWith(Collection<E> onlineElements) {
        Logger.Log("BackupList", "synchronizeWith(Collection<E>)");
        boolean[] isOnline = new boolean[list.size()];
        for(E onlineElement : onlineElements) {
            int backupIndex = indexOfBackup(onlineElement);
            int listIndex = indexOfList(onlineElement);
            boolean sync = synchronize(onlineElement, listIndex, backupIndex);
            if(listIndex >= 0 && listIndex < isOnline.length) {
                isOnline[listIndex] = sync;
            }
        }
        for(int i=isOnline.length-1; i>=0; --i) {
            if(!isOnline[i]) {
                E element = list.get(i);
                if(element.id == 0) {
                    element.create();
                }else {
                    list.remove(i);
                }
            }
        }
        recreateBackup();
    }

    public boolean synchronize(E online, int listIndex, int backupIndex) { // TODO check conMan
        Logger.Log("BackupList", "synchronize(E,int,int)");
        if(listIndex < 0) {
            if(backupIndex < 0) {
                // add online
                list.add(online);

            }else {
                if(online.canEdit()) {
                    // delete online
                    online.delete();

                }else {
                    // delete share
                    (new Share(online.getType(), 0, MySQL.USER_ID, (byte)0, online.id, null, null)).deleteWithoutId();

                }
            }
        }else {
            if(backupIndex < 0) {
                // TODO BackupList: ???
                System.out.println("----- backupIndex < 0 : id="+online.id+", listIndex="+listIndex);

            }else {
                if (online.canEdit()) {
                    E offline = list.get(listIndex);
                    E backup = backupList.get(backupIndex);

                    if (online.equals(backup)) {
                        // update online from offline
                        offline.update();
                        return true;

                    } else {
                        if (offline.equals(backup)) {
                            // update offline from online
                            offline.synchronize(online);
                            return true;

                        } else {
                            // ask which to accept
                            // TODO ASK
                            return true;

                        }
                    }
                } else {
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
