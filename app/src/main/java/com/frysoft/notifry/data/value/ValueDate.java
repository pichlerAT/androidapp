package com.frysoft.notifry.data.value;

import com.frysoft.notifry.utils.Date;

public class ValueDate extends ValueUnsignedInteger {

    public void setValue(Date value) {
        if(value == null) {
            setValue(0);
        }else {
            setValue(value.getInt());
        }
    }

    public Date getDate() {
        return new Date(value);
    }

}
