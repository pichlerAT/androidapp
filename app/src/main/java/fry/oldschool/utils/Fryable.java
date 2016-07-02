package fry.oldschool.utils;

public interface Fryable {

    void writeTo(FryFile fry);

    void readFrom(FryFile fry);

}
