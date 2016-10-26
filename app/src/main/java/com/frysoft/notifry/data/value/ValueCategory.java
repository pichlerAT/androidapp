package com.frysoft.notifry.data.value;

import com.frysoft.notifry.data.Category;
import com.frysoft.notifry.data.Data;
import com.frysoft.notifry.utils.FryFile;

public class ValueCategory extends Value {

    private Category original_value;

    private Category value;

    @Override
    public boolean equals(Object o) {
        if(o instanceof ValueCategory) {
            ValueCategory category = (ValueCategory) o;
            return value.equals(category.getValue());
        }
        return false;
    }

    @Override
    public void readOriginalValue(FryFile fry) {
        original_value = Data.Categories.getById(fry.readId());
    }

    @Override
    public void writeOriginalValue(FryFile fry) {
        if(original_value == null) {
            fry.writeId(0);
        }else {
            fry.writeId(original_value.getId());
        }
    }

    @Override
    public void readValue(FryFile fry) {
        value = Data.Categories.getById(fry.readId());
    }

    @Override
    public void writeValue(FryFile fry) {
        if(value == null) {
            fry.writeId(0);
        }else {
            fry.writeId(value.getId());
        }
    }

    @Override
    public String getString() {
        return ("" + getId());
    }

    @Override
    protected void updateOriginalValue() {
        original_value = value;
    }

    @Override
    protected void readChanged(FryFile fry) {
        char c = fry.readChar();
        if(c > 1) {
            value = Data.Categories.getById(fry.readId());
        }else {
            changed = (c == 1);
        }
    }

    @Override
    protected void writeChanged(FryFile fry) {
        if(value != null && value.getUserId() == 0) {
            fry.writeChar((char)value.getId());
        }else {
            super.writeChanged(fry);
        }
    }

    public boolean doUpdate(ValueCategory value) {
        if(changed && value.value.getId() == original_value.getId()) {
            return true;
        }
        this.value = value.value;
        changed = false;
        return false;
    }

    public void setValue(Category value) {
        if(this.value == null) {
            if(value == null) {
                if(original_value == null) {
                    changed = false;
                }

            }else if(!changed) {
                original_value = null;
                changed = true;
            }

        }else if(!this.value.equals(value)) {
            if(!changed) {
                original_value = this.value;
                changed = true;

            }else if(this.original_value == null) {
                if(value == null) {
                    changed = false;
                }

            }else if(this.original_value.equals(value)) {
                changed = false;
            }
        }
        this.value = value;
    }

    public int getId() {
        return (value == null ? 0 : value.getId());
    }

    public Category getValue() {
        return value;
    }

}
