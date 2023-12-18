package com.WooGLEFX.File;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.nio.file.Files;
import java.nio.file.Path;

public class SimpleHandler extends DefaultHandler {

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        //System.out.println("Started: " + qName);
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
                FileManager.getPaletteBalls().add(attributes.getValue(attributes.getIndex("ball")));
                FileManager.getPaletteVersions().add(Double.valueOf(attributes.getValue(attributes.getIndex("version"))));
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        //System.out.println("Ended: " + qName);
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        //System.out.println(Arrays.toString(ch) + start + length);
    }
}