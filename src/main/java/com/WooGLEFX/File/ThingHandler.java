package com.WooGLEFX.File;
import com.WooGLEFX.Structures.EditorAttribute;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WorldOfGoo.Addin.Addin;
import com.WorldOfGoo.Addin.AddinLevel;
import com.WorldOfGoo.Addin.AddinLevels;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Arrays;

public class ThingHandler extends DefaultHandler {
    public static EditorObject parent = null;

    public int mode = 0;

    private EditorObject currentObject = null;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        EditorAttribute[] glumbus = new EditorAttribute[attributes.getLength()];
        for (int i = 0; i < attributes.getLength(); i++){
            glumbus[i] = new EditorAttribute(attributes.getQName(i), attributes.getValue(i), "", new InputField(attributes.getQName(i), InputField.NULL), false);
        }
        String zName = qName;
        if (mode == 3) {
            if (parent instanceof AddinLevel && zName.equals("name")) {
                zName = "Addin_wtf_" + zName;
            } else {
                zName = "Addin_" + zName;
            }
        }
        EditorObject obj = EditorObject.create(zName, glumbus, parent);
        currentObject = obj;
        obj.setRealName(qName);
        if (mode == 0) {
            FileManager.level.add(obj);
        } else if (mode == 1) {
            FileManager.resources.add(obj);
        } else if (mode == 2) {
            FileManager.scene.add(obj);
        } else if (mode == 3) {
            FileManager.addin.add(obj);
        } else if (mode == 4) {
            FileManager.text.add(obj);
        }
        parent = obj;
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        //System.out.println("Ended: " + qName);
        parent = parent.getParent();
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        if (currentObject.getParent() instanceof Addin && currentObject.getChildren().size() == 0 && currentObject.getAttributes().length == 1) {
            String total = "";
            for (int i = start; i < start + length; i++) {
                total += ch[i];
            }
            if (!total.equals("\n\t")) {
                currentObject.getAttributes()[0].setValue(total);
            }
        }
    }
}