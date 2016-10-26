package com.frysoft.notifry.data.value;

import com.frysoft.notifry.data.RRule;
import com.frysoft.notifry.utils.FryFile;

public class ValueRRule extends Value {

    private RRule original_value;

    private RRule value;

    public ValueRRule() {
        original_value = new RRule();
        value = new RRule();
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof ValueRRule) {
            ValueRRule rRule = (ValueRRule) o;
            return value.equals(rRule.getValue());
        }
        return false;
    }

    @Override
    public void readOriginalValue(FryFile fry) {
        original_value = new RRule(fry.readString());
    }

    @Override
    public void writeOriginalValue(FryFile fry) {
        fry.writeString(original_value.getString());
    }

    @Override
    public void readValue(FryFile fry) {
        value = new RRule(fry.readString());
    }

    @Override
    public void writeValue(FryFile fry) {
        fry.writeString(value.getString());
    }

    @Override
    public String getString() {
        return value.getString();
    }

    @Override
    protected void updateOriginalValue() {
        original_value = new RRule(value.getString());
    }

    public boolean doUpdate(ValueRRule value) {
        if(changed && value.value.equals(original_value)) {
            return true;
        }
        this.value = value.value;
        changed = false;
        return false;
    }

    public void setValue(RRule value) {
        if(value == null) {
            value = new RRule();
        }
        if(!this.value.equals(value)) {
            if(!changed) {
                original_value = this.value;
                changed = true;

            }else if(this.original_value.equals(value)) {
                changed = false;
            }
            this.value = value;
        }
    }

    public RRule getValue() {
        return value;
    }
}
