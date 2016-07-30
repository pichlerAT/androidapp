package com.frysoft.notifry.data;

import com.frysoft.notifry.utils.Date;
import com.frysoft.notifry.utils.DateSpan;
import com.frysoft.notifry.utils.Time;

public class Tag {

    protected static final Date DATE_ZERO = new Date(0, 0, 2000);

    protected short addition;

    protected String name;

    protected String title;

    protected String description;

    protected DateSpan span;


    public static Tag create(String name, String title, String description, Time timeFrom, Time timeTo, int days, short... additions) {
        Date dateTo = DATE_ZERO.copy();
        dateTo.addDays(days);

        short addition = 0;
        for(short add : additions) {
            addition |= add;
        }

        Tag tag = new Tag(addition, name, title, description, DATE_ZERO, timeFrom, dateTo, timeTo);

        return tag;
    }

    protected Tag(short addition, String name, String title, String description,Date dateFrom, Time timeFrom, Date dateTo, Time timeTo) {
        this.name = name;
        this.title = title;
        this.description = description;
        this.addition = addition;
        span = new DateSpan(dateFrom, timeFrom, dateTo, timeTo);
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

    public Time getTimeFrom() {
        return span.getTimeStart();
    }

    public Time getTimeTo() {
        return span.getTimeEnd();
    }

    public int getDays() {
        return span.getDateStart().getDaysUntil(span.getDateEnd());
    }

}
