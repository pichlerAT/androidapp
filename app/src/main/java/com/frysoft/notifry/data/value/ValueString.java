package com.frysoft.notifry.data.value;

import com.frysoft.notifry.utils.FryFile;

public class ValueString extends Value {

    private String original_value;

    private String value;

    @Override
    public boolean equals(Object o) {
        if(o instanceof ValueString) {
            ValueString string = (ValueString) o;
            return value.equals(string.getValue());
        }
        return false;
    }

    @Override
    public void readOriginalValue(FryFile fry) {
        original_value = fry.readString();
    }

    @Override
    public void writeOriginalValue(FryFile fry) {
        fry.writeString(original_value);
    }

    @Override
    public void readValue(FryFile fry) {
        value = fry.readString();
    }

    @Override
    public void writeValue(FryFile fry) {
        fry.writeString(value);
    }

    @Override
    public String getString() {
        return value;
    }

    @Override
    protected void updateOriginalValue() {
        original_value = value;
    }

    public boolean doUpdate(ValueString value) {
        if(changed && value.value.equals(original_value)) {
            return true;
        }
        this.value = value.value;
        changed = false;
        return false;
    }

    public void setValue(String value) {
        if((this.value != null && !this.value.equals(value)) || (this.value == null && value != null)) {
            if(!changed) {
                original_value = this.value;
                changed = true;

            }else if((this.original_value != null && this.original_value.equals(value)) || (this.original_value == null && value == null)) {
                changed = false;
            }
            this.value = value;
        }
    }

    public String getValue() {
        return value;
    }

}
