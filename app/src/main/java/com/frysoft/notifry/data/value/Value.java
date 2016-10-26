package com.frysoft.notifry.data.value;

import com.frysoft.notifry.utils.FryFile;

public abstract class Value {

    protected boolean changed;

    protected void readChanged(FryFile fry) {
        changed = (fry.readChar() == 1);
    }

    protected void writeChanged(FryFile fry) {
        fry.writeChar((changed ? (char)1 : (char)0));
    }

    public final void setAsOriginal() {
        updateOriginalValue();
        changed = false;
    }

    public final boolean isChanged() {
        return changed;
    }

    public final void readFrom(FryFile fry) {
        readValue(fry);
        if(fry instanceof FryFile.Compact) {
            readChanged(fry);
            if(changed) {
                readOriginalValue(fry);
            }
        }else {
            setAsOriginal();
        }
    }

    public final void writeTo(FryFile fry) {
        writeValue(fry);
        if(fry instanceof FryFile.Compact) {
            writeChanged(fry);
            if(changed) {
                writeOriginalValue(fry);
            }
        }
    }

    protected abstract void readOriginalValue(FryFile fry);

    protected abstract void writeOriginalValue(FryFile fry);

    protected abstract void readValue(FryFile fry);

    protected abstract void writeValue(FryFile fry);

    public abstract String getString();

    protected abstract void updateOriginalValue();

}
