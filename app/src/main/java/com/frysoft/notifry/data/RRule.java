package com.frysoft.notifry.data;

import com.frysoft.notifry.utils.Date;

import java.util.Arrays;

public class RRule {

    protected static final char RRULE_BYDAY_CHAR_OFFSET = 127;
    protected static final char RRULE_CHAR_OFFSET = 255;

    public static final char RRULE_FREQUENCY     =  1 ;
    public static final char RRULE_COUNT         =  2 ;
    public static final char RRULE_UNTIL         =  3 ;
    public static final char RRULE_INTERVAL      =  4 ;
    public static final char RRULE_BYDAY         =  5 ;
    public static final char RRULE_BYMONTH       =  6 ;
    public static final char RRULE_BYMONTHDAY    =  7 ;
    public static final char RRULE_BYYEARDAY     =  8 ;
    public static final char RRULE_BYWEEKNO      =  9 ;
    public static final char RRULE_BYSETPOS      = 10 ;
    public static final char RRULE_WHOLEDAY      = 11 ;

    public static final char RRULE_FREQUENCY_DAILY   = 1 ;
    public static final char RRULE_FREQUENCY_WEEKLY  = 2 ;
    public static final char RRULE_FREQUENCY_MONTHLY = 3 ;
    public static final char RRULE_FREQUENCY_YEARLY  = 4 ;

    public static final char RRULE_BYDAY_MONDAY      = 0x01 ;
    public static final char RRULE_BYDAY_TUESDAY     = 0x02 ;
    public static final char RRULE_BYDAY_WEDNESDAY   = 0x04 ;
    public static final char RRULE_BYDAY_THURSDAY    = 0x08 ;
    public static final char RRULE_BYDAY_FRIDAY      = 0x10 ;
    public static final char RRULE_BYDAY_SATURDAY    = 0x20 ;
    public static final char RRULE_BYDAY_SUNDAY      = 0x40 ;

    public static final char[] RRULE_BYDAY_ = { RRULE_BYDAY_MONDAY,
                                                RRULE_BYDAY_TUESDAY,
                                                RRULE_BYDAY_WEDNESDAY,
                                                RRULE_BYDAY_THURSDAY,
                                                RRULE_BYDAY_FRIDAY,
                                                RRULE_BYDAY_SATURDAY,
                                                RRULE_BYDAY_SUNDAY    };

    protected boolean frequencyDaily   = false;
    protected boolean frequencyWeekly  = false;
    protected boolean frequencyMonthly = false;
    protected boolean frequencyYearly  = false;

    protected short count    = 0;
    protected short until    = 0;
    protected short interval = 0;

    protected byte[][] byDay    = null;

    protected byte[] byWeekNo   = null;
    protected byte[] byMonth    = null;
    protected byte[] byMonthDay = null;

    protected short[] byYearDay = null;

    protected short bySetPos  = 0;

    protected boolean wholeDay = false;

    public RRule() { }

    public RRule(String rRule) {
        setRRule(rRule);
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof RRule) {
            RRule r = (RRule)o;
            return (
                    r.frequencyDaily == frequencyDaily &&
                    r.frequencyWeekly == frequencyWeekly &&
                    r.frequencyMonthly == frequencyMonthly &&
                    r.frequencyYearly == frequencyYearly &&
                    r.count == count &&
                    r.until == until &&
                    r.interval == interval &&
                    Arrays.equals(r.byDay, byDay) &&
                    Arrays.equals(r.byWeekNo, byWeekNo) &&
                    Arrays.equals(r.byMonth, byMonth) &&
                    Arrays.equals(r.byMonthDay, byMonthDay) &&
                    Arrays.equals(r.byYearDay, byYearDay) &&
                    r.bySetPos == bySetPos &&
                    r.wholeDay == wholeDay
                   );
        }
        return false;
    }

    public boolean isWholeDay() {
        return wholeDay;
    }

    public boolean isFrequencyDaily() {
        return frequencyDaily;
    }

    public boolean isFrequencyWeekly() {
        return frequencyWeekly;
    }

    public boolean isFrequencyMonthly() {
        return frequencyMonthly;
    }

    public boolean isFrequencyYearly() {
        return frequencyYearly;
    }

    public boolean isCount() {
        return (count != 0);
    }

    public boolean isUntil() {
        return (until != 0);
    }

    public boolean isInterval() {
        return (interval != 0);
    }

    public short getCount() {
        return count;
    }

    public Date getUntil() {
        return new Date(until);
    }

    public short getInterval() {
        return interval;
    }

    public boolean isByDay() {
        return (byDay != null);
    }

    public boolean isByDay(int weekDay) {
        return (byDay != null && byDay[weekDay] != null);
    }

    public boolean isByWeekNo() {
        return (byWeekNo != null);
    }

    public boolean isByMonth() {
        return (byMonth != null);
    }

    public boolean isByMonthDay() {
        return (byMonthDay != null);
    }

    public boolean isByYearDay() {
        return (byYearDay != null);
    }

    public byte[][] getByDay() {
        return byDay;
    }

    public byte[] getByDay(int weekday) {
        if(byDay == null) {
            return null;
        }
        return byDay[weekday];
    }

    public byte[] getByWeekNo() {
        return byWeekNo;
    }

    public byte[] getByMonth() {
        return byMonth;
    }

    public byte[] getByMonthDay() {
        return byMonthDay;
    }

    public short[] getByYearDay() {
        return byYearDay;
    }

    public short getBySetPos() {
        return bySetPos;
    }

    public void setWholeDay(boolean active) {
        wholeDay = active;
    }

    public void setFrequencyDaily(boolean active) {
        frequencyDaily = active;
        if(active) {
            frequencyWeekly = false;
            frequencyMonthly = false;
            frequencyYearly = false;
        }
    }

    public void setFrequencyWeekly(boolean active) {
        frequencyWeekly = active;
        if(active) {
            frequencyDaily = false;
            frequencyMonthly = false;
            frequencyYearly = false;
        }
    }

    public void setFrequencyMonthly(boolean active) {
        frequencyMonthly = active;
        if(active) {
            frequencyDaily = false;
            frequencyWeekly = false;
            frequencyYearly = false;
        }
    }

    public void setFrequencyYearly(boolean active) {
        frequencyYearly = active;
        if(active) {
            frequencyDaily = false;
            frequencyWeekly = false;
            frequencyMonthly = false;
        }
    }

    public void setCount(int count) {
        if(count > Short.MAX_VALUE) {
            throw new IllegalArgumentException("count must be smaller then " + (Short.MAX_VALUE + 1));
        }
        this.count = (short)count;
        this.until = 0;
    }

    public void setUntil(Date until) {
        this.until = until.getShort();
        this.count = 0;
    }

    public void setInterval(int interval) {
        if(interval > Short.MAX_VALUE) {
            throw new IllegalArgumentException("interval must be smaller then " + (Short.MAX_VALUE + 1));
        }
        this.interval = (short)interval;
    }

    /**
     * @param weekDay range: 0 - 6
     * @param offsets ex: weekDay = 0; offsets = 1, -1; freq = monthly; every first and last monday of a month
     */
    public void setByDay(int weekDay, int[] offsets) {
        if(byDay == null) {
            byDay = new byte[7][];
        }

        byDay[weekDay] = new byte[offsets.length];
        for(int i=0; i<offsets.length; ++i) {
            if (offsets[i] < -52 || offsets[i] > 52) {
                throw new IllegalArgumentException("offset must be in range of -52 to 52");
            }
            byDay[weekDay][i] = (byte)offsets[i];
        }
    }

    public void setMonday(int[] offsets) {
        setByDay(0, offsets);
    }

    public void setTuesday(int[] offsets) {
        setByDay(1, offsets);
    }

    public void setWednesday(int[] offsets) {
        setByDay(2, offsets);
    }

    public void setThursday(int[] offsets) {
        setByDay(3, offsets);
    }

    public void setFriday(int[] offsets) {
        setByDay(4, offsets);
    }

    public void setSaturday(int[] offsets) {
        setByDay(5, offsets);
    }

    public void setSunday(int[] offsets) {
        setByDay(6, offsets);
    }

    public void removeFrequency() {
        frequencyDaily = false;
        frequencyWeekly = false;
        frequencyMonthly = false;
        frequencyYearly = false;
    }

    public void removeCount() {
        count = 0;
    }

    public void removeUntil() {
        until = 0;
    }

    public void removeInterval() {
        interval = 0;
    }

    public void removeByDay() {
        byDay = null;
    }

    public void removeByDay(int weekday) {
        if(byDay != null) {
            byDay[weekday] = null;
        }
    }

    public void removeByWeekNo() {
        byWeekNo = null;
    }

    public void removeByMonth() {
        byMonth = null;
    }

    public void removeByMonthDay() {
        byMonthDay = null;
    }

    public void removeByYearDay() {
        byYearDay = null;
    }

    public void setByWeekNo(int[] weeks) {
        if(weeks.length < 1) {
            throw new IllegalArgumentException("weeks must have at least one item");
        }
        byWeekNo = new byte[weeks.length];
        for(int i=0; i<weeks.length; ++i) {
            if(weeks[i] < 1 || weeks[i] > 52) {
                throw new IllegalArgumentException("week must be in range of 1 to 52");
            }
            byWeekNo[i] = (byte)weeks[i];
        }
    }

    public void setByMonth(int[] months) {
        if(months.length < 1) {
            throw new IllegalArgumentException("months must have at least one item");
        }
        byMonth = new byte[months.length];
        for(int i=0; i<months.length; ++i) {
            if(months[i] < 1 || months[i] > 12) {
                throw new IllegalArgumentException("month must be in range of 1 to 12");
            }
            byMonth[i] = (byte)months[i];
        }
    }

    public void setByMonthDay(int[] monthDays) {
        if(monthDays.length < 1) {
            throw new IllegalArgumentException("monthDays must have at least one item");
        }
        byMonthDay = new byte[monthDays.length];
        for(int i=0; i<monthDays.length; ++i) {
            if(monthDays[i] < 1 || monthDays[i] > 31) {
                throw new IllegalArgumentException("monthDay must be in range of 1 to 31");
            }
            byMonthDay[i] = (byte)monthDays[i];
        }
    }

    public void setByYearDay(int[] yearDays) {
        if(yearDays.length < 1) {
            throw new IllegalArgumentException("yearDays must have at least one item");
        }
        byYearDay = new short[yearDays.length];
        for(int i=0; i<yearDays.length; ++i) {
            if(yearDays[i] < 1 || yearDays[i] > 366) {
                throw new IllegalArgumentException("monthDay must be in range of 1 to 366");
            }
            byYearDay[i] = (short)yearDays[i];
        }
    }

    public void setBySetPos(int setPos) {
        if(setPos < 1 || setPos > 31) {
            throw new IllegalArgumentException("monthDay must be in range of 1 to 366");
        }
        bySetPos = (short)setPos;
    }


    protected void setRRule(String rRule) {
        if(rRule == null || rRule.length() < 2) {
            throw new IllegalArgumentException("rRule must contain at least 2 characters");
        }

        int index = 0;
        while(index < rRule.length()) {

            switch(rRule.charAt(index++)) {

                case RRULE_FREQUENCY:
                    switch(rRule.charAt(index++)) {
                        case RRULE_FREQUENCY_DAILY:      frequencyDaily = true;      break;
                        case RRULE_FREQUENCY_WEEKLY:     frequencyWeekly = true;     break;
                        case RRULE_FREQUENCY_MONTHLY:    frequencyMonthly = true;    break;
                        case RRULE_FREQUENCY_YEARLY:     frequencyYearly = true;     break;
                    }
                    break;


                case RRULE_COUNT:
                    count = (short)(rRule.charAt(index++) - RRULE_CHAR_OFFSET);
                    break;


                case RRULE_UNTIL:
                    until = (short)(rRule.charAt(index++) - RRULE_CHAR_OFFSET);
                    break;


                case RRULE_INTERVAL:
                    interval = (short)(rRule.charAt(index++) - RRULE_CHAR_OFFSET);
                    break;


                case RRULE_BYDAY:
                    char day = (char)(rRule.charAt(index++) - RRULE_BYDAY_CHAR_OFFSET);
                    byDay = new byte[7][];
                    for(int i=0; i<7; ++i) {
                        if((day & RRULE_BYDAY_[i]) > 0) {
                            int length = rRule.charAt(index++) - RRULE_CHAR_OFFSET;
                            byDay[i] = new byte[length];
                            for(int j=0; j<length; ++j) {
                                byDay[i][j] = (byte)(rRule.charAt(index++) - RRULE_CHAR_OFFSET);
                            }
                        }
                    }
                    break;


                case RRULE_BYMONTH:
                    int byMonthLength = rRule.charAt(index++) - RRULE_CHAR_OFFSET;
                    byMonth = new byte[byMonthLength];
                    for(int i=0; i<byMonthLength; ++i) {
                        byMonth[i] = (byte)(rRule.charAt(index++) - RRULE_CHAR_OFFSET);
                    }
                    break;


                case RRULE_BYMONTHDAY:
                    int byMonthDayLength = rRule.charAt(index++) - RRULE_CHAR_OFFSET;
                    byMonthDay = new byte[byMonthDayLength];
                    for(int i=0; i<byMonthDayLength; ++i) {
                        byMonthDay[i] = (byte)(rRule.charAt(index++) - RRULE_CHAR_OFFSET);
                    }
                    break;


                case RRULE_BYYEARDAY:
                    int byYearDayLength = rRule.charAt(index++) - RRULE_CHAR_OFFSET;
                    byYearDay = new short[byYearDayLength];
                    for(int i=0; i<byYearDayLength; ++i) {
                        byYearDay[i] = (short)(rRule.charAt(index++) - RRULE_CHAR_OFFSET);
                    }
                    break;


                case RRULE_BYWEEKNO:
                    int byWeekLength = rRule.charAt(index++) - RRULE_CHAR_OFFSET;
                    byWeekNo = new byte[byWeekLength];
                    for (int i = 0; i < byWeekLength; ++i) {
                        byWeekNo[i] = (byte) (rRule.charAt(index++) - RRULE_CHAR_OFFSET);
                    }
                    break;


                case RRULE_BYSETPOS:
                    bySetPos = (short)(rRule.charAt(index++) - RRULE_CHAR_OFFSET);
                    break;

                case RRULE_WHOLEDAY:
                    wholeDay = (rRule.charAt(index++) == 'T');
                    break;

            }

        }
    }

    public String getString() {
        String rRule = "";

        if(frequencyDaily) {
            rRule += RRULE_FREQUENCY + "" + RRULE_FREQUENCY_DAILY;

        }else if(frequencyWeekly) {
            rRule += RRULE_FREQUENCY + "" + RRULE_FREQUENCY_WEEKLY;

        }else if(frequencyMonthly) {
            rRule += RRULE_FREQUENCY + "" + RRULE_FREQUENCY_MONTHLY;

        }else if(frequencyYearly) {
            rRule += RRULE_FREQUENCY + "" + RRULE_FREQUENCY_YEARLY;

        }

        if(count > 0) {
            rRule += RRULE_COUNT + "" + (char)(count + RRULE_CHAR_OFFSET);
        }
        if(until != 0) {
            rRule += RRULE_UNTIL + "" + (char)(until + RRULE_CHAR_OFFSET);
        }
        if(interval > 0) {
            rRule += RRULE_INTERVAL + "" + (char)(interval + RRULE_CHAR_OFFSET);
        }

        //char byDay = 0;
        //String values = "";

        if(byDay != null) {
            char code = 0;
            String buffer = "";

            for(int i=0; i<7; ++i) {
                if(byDay[i] != null) {
                    code |= RRULE_BYDAY_[i];
                    buffer += (char)(byDay[i].length + RRULE_CHAR_OFFSET);
                    for(byte b : byDay[i]) {
                        buffer += (char)(b + RRULE_CHAR_OFFSET);
                    }
                }
            }

            rRule += RRULE_BYDAY + "" + (char)(code + RRULE_BYDAY_CHAR_OFFSET) + buffer;
        }

        if(byMonth != null) {
            rRule += RRULE_BYMONTH + "" + (char)(byMonth.length + RRULE_CHAR_OFFSET);
            for(byte b : byMonth) {
                rRule += (char)(b + RRULE_CHAR_OFFSET);
            }
        }
        if(byMonthDay != null) {
            rRule += RRULE_BYMONTHDAY + "" + (char)(byMonthDay.length + RRULE_CHAR_OFFSET);
            for(byte b : byMonthDay) {
                rRule += (char)(b + RRULE_CHAR_OFFSET);
            }
        }
        if(byYearDay != null) {
            rRule += RRULE_BYYEARDAY + "" + (char)(byYearDay.length + RRULE_CHAR_OFFSET);
            for(short s : byYearDay) {
                rRule += (char)(s + RRULE_CHAR_OFFSET);
            }
        }
        if(byWeekNo != null) {
            rRule += RRULE_BYWEEKNO + "" + (char)(byWeekNo.length + RRULE_CHAR_OFFSET);
            for(byte b : byWeekNo) {
                rRule += (char)(b + RRULE_CHAR_OFFSET);
            }
        }
        if(bySetPos > 0) {
            rRule += RRULE_BYSETPOS + "" + (char)(bySetPos + RRULE_CHAR_OFFSET);
        }

        rRule += RRULE_WHOLEDAY + "" + (wholeDay ? 'T' : 'F');

        return rRule;
    }

    public static RRule convertIOracleToFrySoft(String str) {
        if(!str.substring(0, 6).equals("RRULE:")) {
            throw new IllegalArgumentException("no valid RRULE");
        }

        RRule rRule = new RRule();
        String[] rules = str.substring(6).split(";");

        for(String rule : rules) {
            String[] args = rule.split("=");
            switch(args[0]) {

                case "FREQ":
                    switch(args[1]) {
                        case "DAILY":   rRule.setFrequencyDaily(true);   break;
                        case "WEEKLY":  rRule.setFrequencyWeekly(true);  break;
                        case "MONTHLY": rRule.setFrequencyMonthly(true); break;
                        case "YEARLY":  rRule.setFrequencyYearly(true);  break;
                    }
                    break;

                case "COUNT":
                    rRule.setCount(Integer.parseInt(args[1]));
                    break;

                case "UNTIL":
                    rRule.setUntil(Date.getDateFromISOString(args[1]));
                    break;

                case "INTERVAL":
                    rRule.setInterval(Integer.parseInt(args[1]));
                    break;

                case "BYDAY": {
                    String params[] = args[1].split(",");
                    int[][] w = new int[8][7];
                    for(String param : params) {
                        int v = 0;
                        String day = param;
                        if(param.length() != 2) {
                            v = Integer.parseInt(param.substring(0, param.length() - 2));
                            day = param.substring(param.length() - 2);
                        }
                        switch(day) {
                            case "MO": w[1][w[0][0]++] = v; break;
                            case "TU": w[2][w[0][1]++] = v; break;
                            case "WE": w[3][w[0][2]++] = v; break;
                            case "TH": w[4][w[0][3]++] = v; break;
                            case "FR": w[5][w[0][4]++] = v; break;
                            case "SA": w[6][w[0][5]++] = v; break;
                            case "SU": w[7][w[0][6]++] = v; break;
                        }
                    }
                    rRule.setByDay(0, Arrays.copyOf(w[1], w[0][0]));
                    rRule.setByDay(1, Arrays.copyOf(w[2], w[0][1]));
                    rRule.setByDay(2, Arrays.copyOf(w[3], w[0][2]));
                    rRule.setByDay(3, Arrays.copyOf(w[4], w[0][3]));
                    rRule.setByDay(4, Arrays.copyOf(w[5], w[0][4]));
                    rRule.setByDay(5, Arrays.copyOf(w[6], w[0][5]));
                    rRule.setByDay(6, Arrays.copyOf(w[7], w[0][6]));
                } break;

                case "BYMONTH": {
                    String params[] = args[1].split(",");
                    int[] b = new int[params.length];
                    for (int i = 0; i < params.length; ++i) {
                        b[i] = Integer.parseInt(params[i]);
                    }
                    rRule.setByMonth(b);
                } break;

                case "BYMONTHDAY": {
                    String params[] = args[1].split(",");
                    int[] b = new int[params.length];
                    for (int i = 0; i < params.length; ++i) {
                        b[i] = Integer.parseInt(params[i]);
                    }
                    rRule.setByMonthDay(b);
                } break;

                case "BYYEARDAY": {
                    String params[] = args[1].split(",");
                    int[] b = new int[params.length];
                    for (int i = 0; i < params.length; ++i) {
                        b[i] = Integer.parseInt(params[i]);
                    }
                    rRule.setByYearDay(b);
                } break;

                case "BYWEEKNO": {
                    String params[] = args[1].split(",");
                    int[] b = new int[params.length];
                    for (int i = 0; i < params.length; ++i) {
                        b[i] = Integer.parseInt(params[i]);
                    }
                    rRule.setByWeekNo(b);
                } break;

                case "BYSETPOS":
                    rRule.setBySetPos(Integer.parseInt(args[1]));
                    break;

            }
        }

        return rRule;
    }

    public static String convertFrySoftToOracle(RRule r) {
        String rRule = "RRULE:";

        if(r.frequencyDaily) {
            rRule += "FREQ=DAILY";

        }else if(r.frequencyWeekly) {
            rRule += "FREQ=WEEKLY";

        }else if(r.frequencyMonthly) {
            rRule += "FREQ=MONTHLY";

        }else if(r.frequencyYearly) {
            rRule += "FREQ=YEARLY";

        }else {
            return null;
        }

        if(r.count > 0) {
            rRule += ";COUNT=" + r.count;
        }
        if(r.until > 0) {
            rRule += ";UNTIL=" + r.getUntil().getISOString();
        }
        if(r.interval > 0) {
            rRule += ";INTERVAL=" + r.interval;
        }

        if(r.byDay != null) {
            String[] D = {"MO", "TU", "WE", "TH", "FR", "SA", "SU"};
            String buffer = "";

            for (int i = 0; i < 7; ++i) {
                if(r.byDay[i] != null) {
                    for(byte b : r.byDay[i]) {
                        if(b == 0) {
                            buffer += "," + D[i];
                        }else {
                            buffer += "," + b + "" + D[i];
                        }
                    }
                }
            }

            rRule += ";BYDAY=" + buffer.substring(1);
        }

        if(r.byMonth != null) {
            rRule += ";BYMONTH=" + r.byMonth[0];
            for(int i=1; i<r.byMonth.length; ++i) {
                rRule += "," + r.byMonth[i];
            }
        }
        if(r.byMonthDay != null) {
            rRule += ";BYMONTHDAY=" + r.byMonthDay[0];
            for(int i=1; i<r.byMonthDay.length; ++i) {
                rRule += "," + r.byMonthDay[i];
            }
        }
        if(r.byYearDay != null) {
            rRule += ";BYYEARDAY=" + r.byYearDay[0];
            for(int i=1; i<r.byYearDay.length; ++i) {
                rRule += "," + r.byYearDay[i];
            }
        }
        if(r.byWeekNo != null) {
            rRule += ";BYWEEKNO=" + r.byWeekNo[0];
            for(int i = 1; i<r.byWeekNo.length; ++i) {
                rRule += "," + r.byWeekNo[i];
            }
        }
        if(r.bySetPos > 0) {
            rRule += ";BYSETPOS=" + r.bySetPos;
        }

        return rRule;
    }

}
