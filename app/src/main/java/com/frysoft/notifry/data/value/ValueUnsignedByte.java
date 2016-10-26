package com.frysoft.notifry.data.value;

import com.frysoft.notifry.utils.FryFile;

public class ValueUnsignedByte extends ValueByte {

    @Override
    public void readOriginalValue(FryFile fry) {
        original_value = fry.readUnsignedByte();
    }

    @Override
    public void writeOriginalValue(FryFile fry) {
        fry.writeUnsignedByte(original_value);
    }

    @Override
    public void readValue(FryFile fry) {
        value = fry.readUnsignedByte();
    }

    @Override
    public void writeValue(FryFile fry) {
        fry.writeUnsignedByte(value);
    }

    @Override
    public String getString() {
        return ("" + (value + (value < 0 ? 256 : 0)));
    }

}
