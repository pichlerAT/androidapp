package com.frysoft.notifry.data.value;

import com.frysoft.notifry.utils.FryFile;

public class ValueByte extends Value {

    protected byte original_value;

    protected byte value;

    @Override
    public boolean equals(Object o) {
        if(o instanceof ValueByte) {
            ValueByte byt = (ValueByte) o;
            return (value == byt.getValue());
        }
        return false;
    }

    @Override
    public void readOriginalValue(FryFile fry) {
        original_value = fry.readByte();
    }

    @Override
    public void writeOriginalValue(FryFile fry) {
        fry.writeByte(original_value);
    }

    @Override
    public void readValue(FryFile fry) {
        value = fry.readByte();
    }

    @Override
    public void writeValue(FryFile fry) {
        fry.writeByte(value);
    }

    @Override
    public String getString() {
        return ("" + value);
    }

    @Override
    protected void updateOriginalValue() {
        original_value = value;
    }

    public boolean doUpdate(ValueByte value) {
        if(changed && value.value == original_value) {
            return true;
        }
        this.value = value.value;
        changed = false;
        return false;
    }

    public void setValue(byte value) {
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

    public byte getValue() {
        return value;
    }

    public void setBit(byte value) {
        this.value |= value;
    }

    public void unsetBit(byte value) {
        this.value &= ~value;
    }

    public boolean hasBit(byte value) {
        return ((this.value & value) > 0);
    }

}
