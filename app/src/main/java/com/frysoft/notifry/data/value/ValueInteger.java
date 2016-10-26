package com.frysoft.notifry.data.value;

import com.frysoft.notifry.utils.FryFile;

public class ValueInteger extends Value {

    protected int original_value;

    protected int value;

    @Override
    public boolean equals(Object o) {
        if(o instanceof ValueInteger) {
            ValueInteger integer = (ValueInteger) o;
            return (value == integer.getValue());
        }
        return false;
    }

    @Override
    public void readOriginalValue(FryFile fry) {
        original_value = fry.readInt();
    }

    @Override
    public void writeOriginalValue(FryFile fry) {
        fry.writeInt(original_value);
    }

    @Override
    public void readValue(FryFile fry) {
        value = fry.readInt();
    }

    @Override
    public void writeValue(FryFile fry) {
        fry.writeInt(value);
    }

    @Override
    public String getString() {
        return ("" + value);
    }

    @Override
    protected void updateOriginalValue() {
        original_value = value;
    }

    public boolean doUpdate(ValueInteger value) {
        if(changed && value.value == original_value) {
            return true;
        }
        this.value = value.value;
        changed = false;
        return false;
    }

    public void setValue(int value) {
        if(this.value != value) {
            if(!changed) {
                original_value = this.value;
                changed = true;

            }else if(this.original_value == value) {
                changed = false;
            }
            this.value = value;
        }
    }

    public int getValue() {
        return value;
    }

}
