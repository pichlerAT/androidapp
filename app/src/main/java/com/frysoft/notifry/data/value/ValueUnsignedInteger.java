package com.frysoft.notifry.data.value;

import com.frysoft.notifry.utils.FryFile;

public class ValueUnsignedInteger extends ValueInteger {

    @Override
    public void readOriginalValue(FryFile fry) {
        original_value = fry.readUnsignedInt();
    }

    @Override
    public void writeOriginalValue(FryFile fry) {
        fry.writeUnsignedInt(original_value);
    }

    @Override
    public void readValue(FryFile fry) {
        value = fry.readUnsignedInt();
    }

    @Override
    public void writeValue(FryFile fry) {
        fry.writeUnsignedInt(value);
    }

    @Override
    public String getString() {
        return ("" + (value + (value < 0 ? 4294967296L : 0L)));
    }

}
