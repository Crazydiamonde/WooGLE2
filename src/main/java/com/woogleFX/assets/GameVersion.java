package com.woogleFX.assets;

public enum GameVersion {

    /** The 1.3 version of World of Goo from 2008. */
    VERSION_WOG1_OLD,

    /** The 1.5 version of World of Goo from 2018. */
    VERSION_WOG1_NEW,

    /** World of Goo 2! */
    VERSION_WOG2;


    @Override
    public String toString() {
        return switch (this) {
            case VERSION_WOG1_OLD -> "1.3";
            case VERSION_WOG1_NEW -> "1.5";
            case VERSION_WOG2     -> "2";
        };
    }

}
