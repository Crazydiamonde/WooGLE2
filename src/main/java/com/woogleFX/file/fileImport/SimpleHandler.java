package com.woogleFX.file.fileImport;
import com.woogleFX.file.FileManager;
import com.woogleFX.functions.PaletteManager;
import com.woogleFX.structures.GameVersion;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class SimpleHandler extends DefaultHandler {

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        switch (qName) {
            case "oldWOG" -> {
                if (!attributes.getValue(attributes.getIndex("filepath")).isEmpty()) {
                    FileManager.setOldWOGdir(attributes.getValue(attributes.getIndex("filepath")));
                }
            }
            case "newWOG" -> {
                if (!attributes.getValue(attributes.getIndex("filepath")).isEmpty()) {
                    FileManager.setNewWOGdir(attributes.getValue(attributes.getIndex("filepath")));
                }
            }
            case "Ball" -> {
                PaletteManager.addPaletteBall(attributes.getValue(attributes.getIndex("ball")));
                PaletteManager.addPaletteVersion(attributes.getValue(attributes.getIndex("version")).equals("1.3") ? GameVersion.OLD : GameVersion.NEW);
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