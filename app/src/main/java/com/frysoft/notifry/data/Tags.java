package com.frysoft.notifry.data;

import com.frysoft.notifry.utils.Date;
import com.frysoft.notifry.utils.DateSpan;
import com.frysoft.notifry.utils.FryFile;
import com.frysoft.notifry.utils.Fryable;
import com.frysoft.notifry.utils.Time;
import com.frysoft.notifry.utils.User;

import java.util.ArrayList;
import java.util.Arrays;

public class Tags extends MySQL {

    protected static final Date DATE_ZERO = new Date(0, 0, 2000);

    protected static Tags manager = new Tags(0);

    protected int id;

    protected ArrayList<Tag> tags = new ArrayList<>();

    public static Tag create(String name) {
        if(manager.getTagByName(name) == null) {
            Tag tag = new Tag(name);
            manager.tags.add(tag);
            return tag;
        }
        return null;
    }

    public static void synchronizeFromMySQL(FryFile fry) {
        manager.id = fry.getInt();

        FryFile comp = ((FryFile.Split)fry).getCompact();
        String s = fry.getString();

        int NoTags = comp.getArrayLength();
        ArrayList<Tag> tags = new ArrayList<>(NoTags);

        for(int i=0; i<NoTags; ++i) {
            tags.add(new Tag(comp));
        }

        // TODO Tag: accept online of offline?
    }

    public static void static_readFrom(FryFile fry) {
        manager = new Tags(fry);
    }

    public static void static_writeTo(FryFile fry) {
        manager.writeTo(fry);
    }

    protected Tags(FryFile fry) {
        super(fry);
        int NoTags = fry.getArrayLength();

        for(int i=0; i<NoTags; ++i) {
            tags.add(new Tag(fry));
        }
    }

    protected Tags(int id) {
        super(TYPE_TAG, id, User.getId());
    }

    @Override
    protected boolean mysql_create() {
        return false;
    }

    @Override
    protected boolean mysql_update() {
        return (executeMySQL(DIR_TAG + "update.php", "&id=" + id + "&data=" + getData()) != null);
    }

    @Override
    protected boolean mysql_delete() {
        return false;
    }

    protected Tag getTagByName(String name) {
        for(Tag tag : tags) {
            if(tag.name.equals(name)) {
                return tag;
            }
        }
        return null;
    }

    protected String getData() {
        FryFile fry = new FryFile.Compact();

        fry.write(tags);

        return fry.getWrittenString();
    }

    @Override
    public void writeTo(FryFile fry) {
        super.writeTo(fry);
        fry.write(tags);
    }

    public static class Tag implements Fryable {

        protected String name;

        protected short additions = 0;

        protected int category_id = 0;

        protected String title = null;

        protected String description = null;

        protected Time time = null;

        protected Integer duration = null;

        protected Integer color = null;

        protected int[] shares = new int[0];

        protected Tag(String name) {
            this.name = name;
        }

        protected Tag(FryFile fry) {
            name = fry.getString();
            additions = fry.getShort();
            category_id = fry.getInt();

            char indicator = fry.getChar();

            if((indicator & 0x0001) > 0) {
                title = fry.getString();
            }
            if((indicator & 0x0002) > 0) {
                description = fry.getString();
            }
            if((indicator & 0x0004) > 0) {
                time = new Time(fry.getShort());
            }
            if((indicator & 0x0008) > 0) {
                duration = fry.getInt();
            }
            if((indicator & 0x0010) > 0) {
                color = fry.getInt();
            }

            shares = fry.getIntArray();
        }

        public void setAdditions(short additions) {
            this.additions = additions;
        }

        public void addAdition(short addition) {
            additions |= addition;
        }

        public void setCategory(Category category) {
            category_id = category.id;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setTime(Time time) {
            this.time = new Time(time);
        }

        public void setDuration(int minutes) {
            duration = minutes;
        }

        public void setDuration(int hours, int minutes) {
            setDuration(hours * 60 + minutes);
        }

        public void setColor(int color) {
            this.color = color;
        }

        public void removeAllShares() {
            shares = new int[0];
        }

        public void addShares(ArrayList<Contact> contacts) {
            if(contacts.size() <= 0) {
                return;
            }

            int index = shares.length;
            shares = Arrays.copyOf(shares, index + contacts.size());

            int i = 0;
            while(index < contacts.size()) {
                shares[index++] = contacts.get(i++).user_id;
            }
        }

        public void setShares(ArrayList<Contact> contacts) {
            shares = new int[contacts.size()];
            for(int i=0; i<shares.length; ++i) {
                shares[i] = contacts.get(i).user_id;
            }
        }

        public String getName() {
            return name;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public boolean[] getAdditions() {
            if(additions == 0) {
                return new boolean[16];
            }

            boolean[] b = new boolean[16];
            b[0] =  (additions & TimetableEntry.REPEAT_MONDAY) > 1;
            b[1] =  (additions & TimetableEntry.REPEAT_TUESDAY) > 1;
            b[2] =  (additions & TimetableEntry.REPEAT_WEDNESDAY) > 1;
            b[3] =  (additions & TimetableEntry.REPEAT_THURSDAY) > 1;
            b[4] =  (additions & TimetableEntry.REPEAT_FRIDAY) > 1;
            b[5] =  (additions & TimetableEntry.REPEAT_SATURDAY) > 1;
            b[6] =  (additions & TimetableEntry.REPEAT_SUNDAY) > 1;
            b[7] =  (additions & TimetableEntry.REPEAT_MONTHLY) > 1;
            b[8] =  (additions & TimetableEntry.REPEAT_ANNUALY) > 1;
            b[9] =  (additions & TimetableEntry.NOTIFY_SELF) > 1;
            b[10] = (additions & TimetableEntry.NOTIFY_ALL) > 1;
            b[11] = false;
            b[12] = false;
            b[13] = false;
            b[14] = false;
            b[15] = false;
            return b;
        }

        public Category getCategory() {
            if(category_id == 0) {
                return null;
            }
            return Timetable.getCategoryById(category_id);
        }

        public DateSpan getDateSpan(Date start) {
            return null; // TODO
        }

        public Integer getColor() {
            return color;
        }

        @Override
        public void writeTo(FryFile fry) {
            fry.write(name);
            fry.write(additions);
            fry.write(category_id);

            char indicator = 0;

            if(title != null) {
                indicator |= 0x0001;
            }
            if(description != null) {
                indicator |= 0x0002;
            }
            if(time != null) {
                indicator |= 0x0004;
            }
            if(duration != null) {
                indicator |= 0x0008;
            }
            if(color != null) {
                indicator |= 0x0010;
            }

            fry.write(indicator);

            if(title != null) {
                fry.write(title);
            }
            if(description != null) {
                fry.write(description);
            }
            if(time != null) {
                fry.write(time.time);
            }
            if(duration != null) {
                fry.write(duration);
            }
            if(color != null) {
                fry.write(color);
            }

            fry.write(shares);
        }

    }

}
