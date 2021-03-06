package com.frysoft.notifry.data;

import com.frysoft.notifry.utils.Date;

import java.util.Arrays;

public class EventIterator {

    protected final Date rangeStart;

    protected final Date rangeEnd;

    protected final TimetableEntry entry;

    protected Ending ending;

    protected Iterator iterator;

    protected Date.Cursor cursor;

    protected Date current;
    
    protected RRule rRule;

    protected int pos;

    protected EventIterator(TimetableEntry entry, Date rangeStart, Date rangeEnd) {
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
        this.entry = entry;
        this.rRule = entry.getRRule();

        if(rRule.isCount()) {
            ending = new EndingCount();

        }else if(rRule.isUntil()) {
            ending = new EndingUntil();

        }else {
            ending = new EndingInfinite();
        }

        if(rRule.frequencyDaily) {
            iterator = new DailyIterator();

        }else if(rRule.frequencyWeekly) {
            if(rRule.isByDay()) {
                iterator = new WeeklyIteratorByDay();

            }else {
                iterator = new WeeklyIterator();
            }

        }else if(rRule.frequencyMonthly) {
            if(rRule.isByMonthDay()) {
                iterator = new MonthlyIteratorByMonthDay();

            }else {
                iterator = new MonthlyIterator();
            }

        }else if(rRule.frequencyYearly) {
            if(rRule.isByYearDay()) {
                iterator = new YearlyIteratorByYearDay();

            }else if(rRule.isByMonthDay()) {
                iterator = new YearlyIteratorByMonthDay();

            }else if(rRule.isByWeekNo()) {
                iterator = new YearlyIteratorByWeekNo();

            }else if(rRule.isByMonth()) {
                iterator = new YearlyIteratorByMonth();

            }else {
                iterator = new YearlyIterator();
            }

        }else {
            iterator = new OnceIterator();
        }

        pos = 0;
        current = iterator.first();
    }

    protected Event next() {
        if(current == null) {
            return null;
        }
        Event e;

        if(pos == 0) {
            if(entry.days == 0) {
                if(rRule.wholeDay) {
                    e = new Event(entry, current, Event.TYPE_WHOLE_DAY);
                }else {
                    e = new Event(entry, current, Event.TYPE_START);
                }
                current = iterator.next();

            }else {
                if(rRule.wholeDay) {
                    e = new Event(entry, current, Event.TYPE_WHOLE_DAY);
                }else {
                    e = new Event(entry, current, Event.TYPE_START);
                }
                current.addDays(1);
                ++pos;

            }

        }else if(pos == entry.days) {
            if(current.isAfterDate(rangeEnd)) {
                current = iterator.next();
                return null;
            }

            if (rRule.wholeDay) {
                e = new Event(entry, current, Event.TYPE_WHOLE_DAY);
            } else {
                e = new Event(entry, current, Event.TYPE_END);
            }

            current = iterator.next();
            pos = 0;

        }else {
            if(current.isAfterDate(rangeEnd)) {
                current = iterator.next();
                return null;
            }

            e = new Event(entry, current, Event.TYPE_WHOLE_DAY);
            current.addDays(1);
            ++pos;
        }

        return e;
    }

    protected class OnceIterator extends Iterator {

        @Override
        protected Date first() {
            Date start = entry.getStart();
            Date end = start.copy();
            end.addDays(entry.days);
            if(end.isBeforeDate(rangeStart) || start.isAfterDate(rangeEnd)) {
                return null;
            }

            pos = start.getDaysUntil(rangeStart);
            if(pos < 0) {
                pos = 0;
                return start;

            }else {
                return rangeStart.copy();
            }
        }

        @Override
        protected Date next() {
            return null;
        }
    }

    protected class DailyIterator extends Iterator {

        protected DailyIterator() {
            super();
        }

        @Override
        protected Date next() {

            do {

                cursor.goToNextDay();

                if ((isByMonthDay && !isByMonthDayValid()) ||
                    (isByMonth && !isByMonthValid()) ||
                    (isByYearDay && !isByYearDayValid()) ||
                    (isByWeekNo && !isByWeekNoValid()) ||
                    (isByDay && !isByDayInMonthValid()) ) {
                    continue;
                }

                if(!ending.reached()) {
                    return cursor;
                }

            } while(!ending.reached(cursor));

            return null;
        }

    }

    protected class WeeklyIterator extends Iterator {

        protected int interval = 7 * rRule.interval - 6;

        protected int weekDay = 0;

        protected WeeklyIterator() {
            super();
        }

        @Override
        protected Date next() {

            do {

                if(weekDay == 7) {
                    weekDay = 0;
                    cursor.addDays(interval);

                }else {
                    ++weekDay;
                    cursor.goToNextDay();
                }

                if ((isByMonthDay && !isByMonthDayValid()) ||
                    (isByMonth && !isByMonthValid()) ||
                    (isByYearDay && !isByYearDayValid()) ||
                    (isByWeekNo && !isByWeekNoValid())) {
                    continue;
                }

                if(!ending.reached()) {
                    return cursor;
                }

            } while(!ending.reached(cursor));

            return null;
        }

    }

    protected class WeeklyIteratorByDay extends Iterator {

        protected int[] map;

        protected int index = 0;

        protected WeeklyIteratorByDay() {
            super();

            int[] bufferMap = new int[7];
            int index = 0;
            int firstIndex = -1;
            int lastIndex = 0;

            for(int i=0; i<7; ++i) {

                if(rRule.isByDay(i)) {
                    if (firstIndex == -1) {
                        firstIndex = i;
                    } else {
                        bufferMap[index++] = i - lastIndex;
                    }
                    lastIndex = i;
                }

            }

            map = Arrays.copyOf(bufferMap, index + 1);
            map[index] = 7 * rRule.interval - lastIndex + firstIndex;

        }

        @Override
        protected Date next() {

            do {

                if(index == map.length) {
                    index = 0;
                }

                if(map[index] == 1) {
                    ++index;
                    cursor.goToNextDay();

                }else {
                    cursor.addDays(map[index++]);
                }

                if ((isByMonthDay && !isByMonthDayValid()) ||
                    (isByMonth && !isByMonthValid()) ||
                    (isByYearDay && !isByYearDayValid()) ||
                    (isByWeekNo && !isByWeekNoValid())) {
                    continue;
                }

                if(!ending.reached()) {
                    return cursor;
                }

            } while(!ending.reached(cursor));

            return null;
        }

    }

    protected class MonthlyIterator extends Iterator {

        protected int monthDay = entry.getStart().day;// = 0;

        protected int month = entry.getStart().month;

        protected MonthlyIterator() {
            super();
        }

        @Override
        protected Date next() {

            do {

                if(pos >= entry.days) {
                    cursor.day = monthDay;
                    cursor.month = month;

                    cursor.addMonths(rRule.interval);
                    month = cursor.month;

                }else {
                    cursor.goToNextDay();
                }

                if ((isByMonth && !isByMonthValid()) ||
                    (isByYearDay && !isByYearDayValid()) ||
                    (isByWeekNo && !isByWeekNoValid()) ||
                    (isByDay && !isByDayInMonthValid()) ) {
                    continue;
                }

                if(!ending.reached()) {
                    return cursor;
                }

            } while(!ending.reached(cursor));

            return null;
        }
    }

    protected class MonthlyIteratorByMonthDay extends Iterator {

        protected int[] map;

        protected int index = 0;

        protected MonthlyIteratorByMonthDay() {
            super();

            map = new int[rRule.byMonthDay.length - 1];

            int lastIndex = rRule.byMonthDay[0];

            for(int i=0; i<map.length; ++i) {
                int nextIndex = rRule.byMonthDay[i + 1];
                map[i] = nextIndex - lastIndex;
            }

        }

        @Override
        protected Date next() {

            do {

                if(index == map.length) {
                    index = 0;
                    cursor.addMonths(rRule.interval);
                    if(!cursor.goToMonthDay(rRule.byMonthDay[0])) {
                        continue;
                    }

                }else {
                    cursor.addDays(map[index++]);
                }

                if ((isByMonth && !isByMonthValid()) ||
                    (isByYearDay && !isByYearDayValid()) ||
                    (isByWeekNo && !isByWeekNoValid()) ||
                    (isByDay && !isByDayInMonthValid()) ) {
                    continue;
                }

                if(!ending.reached()) {
                    return cursor;
                }

            } while(!ending.reached(cursor));

            return null;
        }

    }
    protected class YearlyIteratorByYearDay extends Iterator {

        protected int[] map;

        protected int index;

        protected YearlyIteratorByYearDay() {
            super();

            map = new int[rRule.byYearDay.length - 1];

            int lastDay = rRule.byYearDay[0];
            for(int i=0; i<map.length; ++i) {
                int nextDay = rRule.byYearDay[i + 1];
                map[i] = nextDay - lastDay;
                lastDay = nextDay;
            }

        }

        @Override
        protected Date next() {

            do {

                if(index == map.length) {
                    index = 0;
                    cursor.addYears(rRule.interval);
                    if(!cursor.goToYearDay(rRule.byYearDay[0])) {
                        continue;
                    }

                }else {
                    cursor.addDays(map[index++]);
                }

                if ((isByMonthDay && !isByMonthDayValid()) ||
                    (isByMonth && !isByMonthValid()) ||
                    (isByWeekNo && !isByWeekNoValid()) ||
                    (isByDay && !isByDayInMonthValid()) ) {
                    continue;
                }

                if(!ending.reached()) {
                    return cursor;
                }

            } while(!ending.reached(cursor));

            return null;
        }

    }

    protected class YearlyIteratorByMonthDay extends Iterator {

        protected int[] map;

        protected int index = 0;

        protected YearlyIteratorByMonthDay() {
            super();

            map = new int[rRule.byMonthDay.length - 1];

            int lastIndex = rRule.byMonthDay[0];

            for(int i=0; i<map.length; ++i) {
                int nextIndex = rRule.byMonthDay[i + 1];
                map[i] = nextIndex - lastIndex;
            }

        }

        @Override
        protected Date next() {

            do {

                if(index == map.length) {
                    index = 0;
                    cursor.addYears(rRule.interval);
                    if(!cursor.goToMonthDay(rRule.byMonthDay[0])) {
                        continue;
                    }

                }else {
                    cursor.addDays(map[index++]);
                }

                if ((isByMonth && !isByMonthValid()) ||
                    (isByWeekNo && !isByWeekNoValid()) ||
                    (isByDay && !isByDayInYearValid()) ) {
                    continue;
                }

                if(!ending.reached()) {
                    return cursor;
                }

            } while(!ending.reached(cursor));

            return null;
        }
    }

    protected class YearlyIteratorByWeekNo extends Iterator {

        protected int firstWeek;

        protected int[] map;

        protected int index = 0;

        protected int weekDayIndex = 0;

        protected int currentYear;

        protected YearlyIteratorByWeekNo() {
            super();

            firstWeek = (rRule.byWeekNo[0] - 1 ) * 7;
            map = new int[rRule.byWeekNo.length - 1];

            int lastIndex = rRule.byWeekNo[0];

            for(int i=0; i<map.length; ++i) {
                int nextIndex = rRule.byWeekNo[i + 1];
                map[i] = (nextIndex - lastIndex) * 7 - 6;
            }

            currentYear = entry.getStart().year;
        }

        @Override
        protected Date next() {

            do {

                if(weekDayIndex == 7) {

                    if (index == map.length) {
                        index = 0;
                        cursor.addYears(rRule.interval);
                        cursor.goToYearDate(1, 1);
                        cursor.addDays(firstWeek);
                        cursor.goToFirstDayOfWeek();
                        currentYear = cursor.year;

                    } else {
                        cursor.addDays(map[index++]);

                        if(cursor.year > currentYear) {
                            cursor.goToDate(currentYear, 1, 1);
                            cursor.addYears(rRule.interval);
                            cursor.addDays(firstWeek);
                            cursor.goToFirstDayOfWeek();
                            currentYear = cursor.year;
                        }
                    }

                }else {
                    ++weekDayIndex;
                    cursor.goToNextDay();

                    if(cursor.year > currentYear) {
                        cursor.goToDate(currentYear, 1, 1);
                        cursor.addYears(rRule.interval);
                        cursor.addDays(firstWeek);
                        cursor.goToFirstDayOfWeek();
                        currentYear = cursor.year;
                    }
                }

                if ((isByMonth && !isByMonthValid()) ||
                    (isByDay && !isByDayInYearValid()) ) {
                    continue;
                }

                if(!ending.reached()) {
                    return cursor;
                }

            } while(!ending.reached(cursor));

            return null;
        }
    }

    protected class YearlyIteratorByMonth extends Iterator {

        protected int[] map;

        protected int index = 0;

        protected int monthDayIndex = 0;

        protected int daysOfMonth;

        protected int currentYear;

        protected YearlyIteratorByMonth() {
            super();

            map = new int[rRule.byMonth.length - 1];

            int lastIndex = rRule.byMonth[0];

            for(int i=0; i<map.length; ++i) {
                int nextIndex = rRule.byMonth[i + 1];
                map[i] = nextIndex - lastIndex;
            }

            Date start = entry.getStart();
            daysOfMonth = start.getDaysOfMonth();
            currentYear = start.year;
        }

        @Override
        protected Date next() {

            do {

                if(monthDayIndex == daysOfMonth) {

                    if(index == map.length) {
                        index = 0;
                        cursor.addYears(rRule.interval);
                        currentYear = cursor.year;
                        if(!cursor.goToMonth(rRule.byMonth[0])) {
                            continue;
                        }

                    }else {
                        cursor.goToMonthDay(1);
                        cursor.addMonths(map[index++]);

                        if(cursor.year > currentYear) {
                            cursor.goToDate(currentYear, rRule.byMonth[0], 1);
                            cursor.addYears(rRule.interval);
                            currentYear = cursor.year;
                        }
                    }

                }else {
                    ++monthDayIndex;
                    cursor.goToNextDay();

                    if(cursor.year > currentYear) {
                        cursor.goToDate(currentYear, rRule.byMonth[0], 1);
                        cursor.addYears(rRule.interval);
                        currentYear = cursor.year;
                    }
                }

                if ((isByDay && !isByDayInYearValid()) ) {
                    continue;
                }

                if(!ending.reached()) {
                    return cursor;
                }

            } while(!ending.reached(cursor));

            return null;
        }
    }

    protected class YearlyIterator extends Iterator {

        protected int year;

        protected YearlyIterator() {
            super();
            year = entry.getStart().year;
        }

        @Override
        protected Date next() {

            do {

                if(pos >= entry.days) {
                    cursor.goToDate(entry.getStart());
                    year += rRule.interval;
                    cursor.year = year;

                }else {
                    cursor.goToNextDay();
                }

                if ((isByDay && !isByDayInYearValid()) ) {
                    continue;
                }

                if(!ending.reached()) {
                    return cursor;
                }

            } while(!ending.reached(cursor));

            return null;
        }
    }


    protected abstract class Iterator {

        protected boolean isByMonthDay = rRule.isByMonthDay();

        protected boolean isByMonth = rRule.isByMonth();

        protected boolean isByYearDay = rRule.isByYearDay();

        protected boolean isByWeekNo = rRule.isByWeekNo();

        protected boolean isByDay = rRule.isByDay();

        protected boolean isByDayZero = false;

        protected boolean[] isByDayMap;

        protected boolean[] isByDayZeroMap;

        protected Iterator() {
            cursor = new Date.Cursor(entry.start);

            if(isByDay) {
                isByDayMap = new boolean[7];
                isByDayZeroMap = new boolean[7];

                for(int i=0; i<7; ++i) {
                    if(rRule.isByDay(i)) {
                        isByDayMap[i] = true;
                        for(byte b : rRule.byDay[i]) {
                            if(b == 0) {
                                isByDayZeroMap[i] = true;
                                isByDayZero = true;
                            }
                        }
                    }
                }

                if(!isByDayZero) {
                    isByDayZeroMap = null;
                }
            }
        }


        protected abstract Date next();

        protected Date first() {
            Date d;
            while((d = next()) != null) {
                Date.Cursor c = new Date.Cursor(d);
                Date end = c.copy();
                end.addDays(entry.days);
                if(!end.isBeforeDate(rangeStart)) {

                    pos = 0;
                    do {

                        if(!c.isBeforeDate(rangeStart)) {
                            return c;
                        }

                        c.goToNextDay();
                        ++pos;

                    } while(pos <= entry.days);

                }

            }
            return null;
        }

        protected boolean isByMonthDayValid() {
            for(byte day : rRule.byMonthDay) {
                if(day == cursor.day) {
                    return true;
                }
            }
            return false;
        }

        protected boolean isByMonthValid() {
            for(byte month : rRule.byMonth) {
                if(month == cursor.month) {
                    return true;
                }
            }
            return false;
        }

        protected boolean isByWeekNoValid() {
            int weekNo = cursor.getWeekOfYear();
            for(byte week : rRule.byWeekNo) {
                if(week == weekNo) {
                    return true;
                }
            }
            return false;
        }

        protected boolean isByYearDayValid() {
            int yearDay = cursor.getDayOfYear();
            for(short day : rRule.byYearDay) {
                if(day == yearDay) {
                    return true;
                }
            }
            return false;
        }

        protected boolean isByDayInMonthValid() {
            int dayOfWeek = cursor.getDayOfWeek();

            if(isByDayZero) {
                if(isByDayMap[dayOfWeek]) {
                    return true;
                }
            }

            if(isByDayMap[dayOfWeek]) {
                int tof = (cursor.day - 1) / 7 + 1;
                int tol = -((cursor.getDaysOfMonth() - cursor.day) / 7 + 1);
                for (byte b : rRule.byDay[dayOfWeek]) {
                    if (b == tof || b == tol) {
                        return true;
                    }
                }
            }

            return false;
        }

        protected boolean isByDayInYearValid() {
            int dayOfWeek = cursor.getDayOfWeek();

            if(isByDayZero) {
                if(isByDayMap[dayOfWeek]) {
                    return true;
                }
            }

            int day = cursor.getDayOfYear();
            int tof = (day - 1) / 7 + 1;
            int tol = -((cursor.getDaysOfYear() - day) / 7 + 1);
            for(byte b : rRule.byDay[dayOfWeek]) {
                if(b == tof || b == tol) {
                    return true;
                }
            }

            return false;
        }

    }

    protected class EndingUntil extends Ending {

        private Date until;

        protected EndingUntil() {
            until = rRule.getUntil();
            if(rangeEnd.isBeforeDate(until)) {
                until = rangeEnd;
            }
        }

        @Override
        protected boolean reached() {
            return cursor.isAfterDate(until);
        }

        @Override
        protected boolean reached(Date date) {
            return date.isAfterDate(until);
        }

    }

    protected class EndingCount extends Ending {

        private int count = 0;

        @Override
        protected boolean reached() {
            return (count++ > rRule.count || cursor.isAfterDate(rangeEnd));
        }

        @Override
        protected boolean reached(Date date) {
            return (count > rRule.count || date.isAfterDate(rangeEnd));
        }
    }

    protected class EndingInfinite extends Ending {

        @Override
        protected boolean reached() {
            return cursor.isAfterDate(rangeEnd);
        }

        @Override
        protected boolean reached(Date date) {
            return date.isAfterDate(rangeEnd);
        }

    }

    protected abstract class Ending {

        protected abstract boolean reached();

        protected abstract boolean reached(Date date);

    }

}
