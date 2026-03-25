package com.worldOfGoo.resrc;

/** Interface for anything with an ID and path in resources.xml files. */
public interface ResourceInterface {

    SetDefaults getSetDefaults();
    void setSetDefaults(SetDefaults setDefaults);
    String getAdjustedID();
    String getAdjustedPath();

}
