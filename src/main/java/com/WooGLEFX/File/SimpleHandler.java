package com.WooGLEFX.File;
import com.WooGLEFX.Functions.PaletteManager;
import com.WooGLEFX.Structures.GameVersion;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.nio.file.Files;
import java.nio.file.Path;

public class SimpleHandler extends DefaultHandler {

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        switch (qName) {
            case "oldWOG" -> {
                if (!attributes.getValue(attributes.getIndex("filepath")).equals("")) {
                    FileManager.setOldWOGdir(attributes.getValue(attributes.getIndex("filepath")));
                }
                if (!FileManager.getOldWOGdir().equals("") && Files.exists(Path.of(FileManager.getOldWOGdir()))) {
                    FileManager.setHasOldWOG(true);
                }
            }
            case "newWOG" -> {
                if (!attributes.getValue(attributes.getIndex("filepath")).equals("")) {
                    FileManager.setNewWOGdir(attributes.getValue(attributes.getIndex("filepath")));
                }
                if (!FileManager.getNewWOGdir().equals("") && Files.exists(Path.of(FileManager.getNewWOGdir()))) {
                    FileManager.setHasNewWOG(true);
                }
            }
            case "Ball" -> {
                PaletteManager.getPaletteBalls().add(attributes.getValue(attributes.getIndex("ball")));
                PaletteManager.getPaletteVersions().add(attributes.getValue(attributes.getIndex("version")).equals("1.3") ? GameVersion.OLD : GameVersion.NEW);
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
    }

    @Override
    public void characters(char[] ch, int start, int length) {
    }
}