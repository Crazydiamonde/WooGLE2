package com.WooGLEFX.File;
import com.WooGLEFX.File.FileManager;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

public class SimpleHandler extends DefaultHandler {

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        //System.out.println("Started: " + qName);
        if (qName.equals("oldWOG")) {
            if (!attributes.getValue(attributes.getIndex("filepath")).equals("")) {
                FileManager.setOldWOGdir(attributes.getValue(attributes.getIndex("filepath")));
            }
            if (!FileManager.getOldWOGdir().equals("")) {
                FileManager.setHasOldWOG(true);
            }
        } else if (qName.equals("newWOG")) {
            if (!attributes.getValue(attributes.getIndex("filepath")).equals("")) {
                FileManager.setNewWOGdir(attributes.getValue(attributes.getIndex("filepath")));
            }
            if (!FileManager.getNewWOGdir().equals("")) {
                FileManager.setHasNewWOG(true);
            }
        } else if (qName.equals("Ball")) {
            FileManager.getPaletteBalls().add(attributes.getValue(attributes.getIndex("ball")));
            FileManager.getPaletteVersions().add(Double.valueOf(attributes.getValue(attributes.getIndex("version"))));
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