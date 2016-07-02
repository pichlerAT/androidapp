package fry.oldschool.utils;

import java.util.ArrayList;
import java.util.Arrays;

public class SearchableList<E> {

    protected int length;

    protected int showLength;

    protected String[] searchText;

    protected Searchable[] baseItems;

    protected Searchable[] showItems;

    public SearchableList() {
        this(0);
    }

    public SearchableList(int minLength) {
        length = 0;
        showLength = 0;
        searchText = null;
        baseItems = new Searchable[minLength];
        showItems = new Searchable[minLength];
    }

    public SearchableList(ArrayList<? extends E> items) {
        baseItems = (Searchable[])items.toArray();
        length = baseItems.length;
        showLength = length;
        searchText = null;

        if(baseItems.getClass() != Searchable[].class) {
            baseItems = Arrays.copyOf(baseItems, length, Searchable[].class);
        }

        showItems = Arrays.copyOf(baseItems, baseItems.length);
    }

    public SearchableList(E[] items) {
        length = baseItems.length;
        showLength = length;
        searchText = null;

        if(items.getClass() == Searchable[].class) {
            baseItems = (Searchable[]) items;
        }else {
            baseItems = Arrays.copyOf(items, length, Searchable[].class);
        }

        showItems = Arrays.copyOf(baseItems, baseItems.length);
    }

    protected void ensureCapacity(int minCapacity) {
        if(minCapacity >= baseItems.length) {
            grow(minCapacity);
        }
    }

    protected void grow(int minCapacity) {
        int newCapacity = baseItems.length + (baseItems.length >> 1);
        if(newCapacity < minCapacity) {
            baseItems = Arrays.copyOf(baseItems, minCapacity);
            showItems = Arrays.copyOf(showItems, minCapacity);
        }else {
            baseItems = Arrays.copyOf(baseItems, newCapacity);
            showItems = Arrays.copyOf(showItems, newCapacity);
        }
    }

    protected void rangeCheckBase(int index) {
        if(index < 0 || index >= length) {
            throw new IndexOutOfBoundsException("index out of bounds: index = " + index);
        }
    }

    protected void rangeCheckShow(int index) {
        if(index < 0 || index >= showLength) {
            throw new IndexOutOfBoundsException("index out of bounds: index = " + index);
        }
    }

    @SuppressWarnings("unchecked")
    protected E baseItems(int index) {
        return (E) baseItems[index];
    }

    @SuppressWarnings("unchecked")
    protected E showItems(int index) {
        return (E) showItems[index];
    }

    public int baseLength() {
        return length;
    }

    public int length() {
        return showLength;
    }

    public boolean isEmpty() {
        return (length == 0);
    }

    public boolean baseContains(Object o) {
        return (indexOfBase(o) >= 0);
    }

    public boolean contains(Object o) {
        return (indexOf(o) >= 0);
    }

    public int indexOfBase(Object o) {
        if(o == null) {
            for(int i=0; i<length; ++i) {
                if(baseItems[i] == null) {
                    return i;
                }
            }
        }else {
            for(int i=0; i<length; ++i) {
                if(o.equals(baseItems[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    public int indexOf(Object o) {
        if(o == null) {
            for(int i=0; i<showLength; ++i) {
                if(showItems[i] == null) {
                    return i;
                }
            }
        }else {
            for(int i=0; i<showLength; ++i) {
                if(o.equals(showItems[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    public int lastIndexOfBase(Object o) {
        if(o == null) {
            for(int i=length-1; i>=0; --i) {
                if(baseItems[i] == null) {
                    return i;
                }
            }
        }else {
            for(int i=length-1; i>=0; --i) {
                if(o.equals(baseItems[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    public int lastIndexOf(Object o) {
        if(o == null) {
            for(int i=showLength-1; i>=0; --i) {
                if(showItems[i] == null) {
                    return i;
                }
            }
        }else {
            for(int i=showLength-1; i>=0; --i) {
                if(o.equals(showItems[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    public SearchableList<E> clone() {
        try {
            @SuppressWarnings("unchecked")
            SearchableList<E> list = (SearchableList<E>) super.clone();
            list.length = length;
            list.showLength = showLength;
            list.searchText = Arrays.copyOf(searchText, searchText.length);
            list.baseItems = Arrays.copyOf(baseItems, length);
            list.showItems = Arrays.copyOf(showItems, length);
            return list;
        } catch (CloneNotSupportedException e) {
            //e.printStackTrace();
            throw new InternalError();
        }
    }

    public void trimToSize() {
        baseItems = Arrays.copyOf(baseItems, length);
        showItems = Arrays.copyOf(showItems, length);
    }

    public E getBase(int index) {
        rangeCheckBase(index);

        return baseItems(index);
    }

    public E get(int index) {
        rangeCheckShow(index);

        return showItems(index);
    }

    public void add(E item) {
        ensureCapacity(length + 1);

        baseItems[length] = (Searchable) item;

        ++length;

        updateSearchItems();
    }

    public void addToBase(int index, E item) {
        rangeCheckBase(index);
        ensureCapacity(length + 1);

        System.arraycopy(baseItems, index, baseItems, index + 1, length - index);
        baseItems[index] = (Searchable) item;

        updateSearchItems();
    }

    public void add(int index, E item) {
        rangeCheckShow(index);
        ensureCapacity(length + 1);
        addToBase(indexOfBase(showItems[index]), item);
    }

    public boolean remove(E item) {
        int index = indexOfBase(item);
        if(index < 0) {
            return false;
        }
        System.arraycopy(baseItems, index + 1, baseItems, index, length - index);
        baseItems[length--] = null;

        updateSearchItems();

        return true;
    }
    public E removeFromBase(int index) {
        rangeCheckBase(index);

        E item = baseItems(index);

        System.arraycopy(baseItems, index + 1, baseItems, index, length - index);
        baseItems[length--] = null;

        updateSearchItems();

        return item;
    }

    public E remove(int index) {
        rangeCheckShow(index);
        return removeFromBase(indexOfBase(showItems[index]));
    }

    public E setBase(int index, E item) {
        rangeCheckBase(index);

        E old = baseItems(index);
        baseItems[index] = (Searchable) item;

        updateSearchItems();

        return old;
    }

    public E set(int index, E item) {
        rangeCheckShow(index);
        return setBase(indexOfBase(showItems[index]), item);
    }

    public void swapBase(int index_1, int index_2) {
        rangeCheckBase(index_1);
        rangeCheckBase(index_2);

        Searchable item_1 = baseItems[index_1];
        baseItems[index_1] = baseItems[index_2];
        baseItems[index_2] = item_1;

        updateSearchItems();
    }

    public void swap(int index_1, int index_2) {
        rangeCheckShow(index_1);
        rangeCheckShow(index_2);
        swapBase(indexOfBase(showItems[index_1]), indexOfBase(showItems[index_2]));
    }

    protected void updateSearchItems() {
        search(searchText);
    }

    public void search(final String... keyWords) {
        if(keyWords == null) {
            removeSearch();
            return;
        }

        searchText = Arrays.copyOf(keyWords, keyWords.length);
        showLength = 0;
        for(int i=0; i<length; ++i) {
            if(baseItems[i].search(keyWords)) {
                showItems[showLength++] = baseItems[i];
            }
        }
    }

    public void removeSearch() {
        showItems = Arrays.copyOf(baseItems, baseItems.length);
        searchText = null;
        showLength = length;
    }

}