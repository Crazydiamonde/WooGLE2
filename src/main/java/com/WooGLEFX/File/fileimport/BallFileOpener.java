package com.WooGLEFX.File.fileimport;
import java.util.ArrayList;

import com.WooGLEFX.EditorObjects.ObjectCreators.ObjectCreator;
import com.WooGLEFX.File.FileManager;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import com.WooGLEFX.EditorObjects.EditorObject;

public class BallFileOpener extends DefaultHandler {
    public static EditorObject parent = null;

    public static int mode = 0;

    public static boolean bruhmode = false;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        //logger.debug("Started: " + qName);
        if (bruhmode) {
            if (!FileManager.objectNames.contains(qName)) {
                FileManager.objectNames.add(qName);
                FileManager.attributes.add(new ArrayList<>());
                int index = FileManager.objectNames.indexOf(qName);
                for (int i = 0; i < attributes.getLength(); i++) {
                    FileManager.attributes.get(index).add(attributes.getQName(i));
                }
            } else {
                int index = FileManager.objectNames.indexOf(qName);
                for (String attribute : FileManager.attributes.get(index).toArray(new String[0])) {
                    boolean ok = false;
                    for (int i = 0; i < attributes.getLength(); i++) {
                        if (attribute.equals(attributes.getQName(i))) {
                            ok = true;
                            break;
                        }
                    }
                    if (!ok) {
                        FileManager.attributes.get(index).remove(attribute);
                    }
                }
                for (int i = 0; i < attributes.getLength(); i++) {
                    if (!FileManager.attributes.get(index).contains(attributes.getQName(i))) {
                        FileManager.attributes.get(index).remove(attributes.getQName(i));
                    }
                }
            }
        } else {
            if (qName.equals("particles") || qName.equals("sound")) {
                qName = "ball_" + qName;
            }
            EditorObject obj = ObjectCreator.create(qName, parent);
            for (int i = 0; i < attributes.getLength(); i++) {
                obj.setAttribute(attributes.getQName(i), attributes.getValue(i));
            }
            if (mode == 0) {
                FileManager.commonBallData.add(obj);
            } else if (mode == 1) {
                FileManager.commonBallResrcData.add(obj);
            }
            parent = obj;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        //logger.debug("Ended: " + qName);
        if (!bruhmode) {
            parent = parent.getParent();
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        //logger.debug(Arrays.toString(ch) + start + length);
    }
}