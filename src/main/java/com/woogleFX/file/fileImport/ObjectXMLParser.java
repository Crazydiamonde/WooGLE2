package com.woogleFX.file.fileImport;

import com.woogleFX.editorObjects.objectCreators.ObjectCreator;
import com.worldOfGoo.level.Level;
import com.worldOfGoo.resrc.ResourceManifest;
import com.worldOfGoo.scene.Scene;
import com.worldOfGoo.text.TextStrings;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import com.woogleFX.editorObjects.EditorObject;
import com.worldOfGoo.addin.Addin;
import com.worldOfGoo.addin.AddinLevel;
import com.worldOfGoo.addin.AddinLevelOCD;

import java.util.ArrayList;

public class ObjectXMLParser extends DefaultHandler {

    public static EditorObject parent = null;

    private EditorObject currentObject = null;

    private final ArrayList<EditorObject> scene;
    private final ArrayList<EditorObject> level;
    private final ArrayList<EditorObject> resrc;
    private final ArrayList<EditorObject> addin;
    private final ArrayList<EditorObject> text;

    public ObjectXMLParser(ArrayList<EditorObject> scene,
                           ArrayList<EditorObject> level,
                           ArrayList<EditorObject> resrc,
                           ArrayList<EditorObject> addin,
                           ArrayList<EditorObject> text) {
        this.scene = scene;
        this.level = level;
        this.resrc = resrc;
        this.addin = addin;
        this.text = text;
    }


    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        String zName = qName;

        EditorObject absoluteParent2 = parent;
        while (absoluteParent2 != null && absoluteParent2.getParent() != null) absoluteParent2 = absoluteParent2.getParent();

        if (absoluteParent2 instanceof Addin) {
            if (parent instanceof AddinLevel && zName.equals("name")) {
                zName = "Addin_wtf_" + zName;
            } else {
                zName = "Addin_" + zName;
            }
        }

        EditorObject obj = ObjectCreator.create(zName, parent);

        for (int i = 0; i < attributes.getLength(); i++) {
            String attributeName = attributes.getQName(i);
            String attributeValue = attributes.getValue(i);
            obj.setAttribute(attributeName, attributeValue);
        }

        currentObject = obj;

        EditorObject absoluteParent = obj;
        while (absoluteParent.getParent() != null) absoluteParent = absoluteParent.getParent();

        if (absoluteParent instanceof Scene)            scene.add(obj);
        if (absoluteParent instanceof Level)            level.add(obj);
        if (absoluteParent instanceof ResourceManifest) resrc.add(obj);
        if (absoluteParent instanceof Addin)            addin.add(obj);
        if (absoluteParent instanceof TextStrings)      text .add(obj);

        parent = obj;

    }


    @Override
    public void endElement(String uri, String localName, String qName) {
        parent = parent.getParent();
    }


    @Override
    public void characters(char[] ch, int start, int length) {
        if ((currentObject.getParent() instanceof Addin || currentObject.getParent() instanceof AddinLevel) && currentObject.getChildren().isEmpty() && currentObject.getAttributes().length == 1) {
            StringBuilder total = new StringBuilder();
            for (int i = start; i < start + length; i++) {
                total.append(ch[i]);
            }
            if (!total.toString().equals("\n\t") && !total.toString().equals("\n") && !total.toString().equals("\n\t\t") && !total.toString().equals("\n\t\t\t") && !total.toString().isEmpty()) {
                currentObject.getAttributes()[0].setValue(total.toString());
            }
        }
        // Sort OCD into type and value
        if (currentObject instanceof AddinLevelOCD) {
            String total = new String(ch, start, length);
            if (!total.equals("\n\t") && !total.equals("\n") && !total.equals("\n\t\t") && !total.equals("\n\t\t\t") && !total.isEmpty()) {
                // Split the string by the first comma
                String[] splitString = total.split(",", 2);
                // Set the type and value attributes
                currentObject.setAttribute("type", splitString[0]);
                currentObject.setAttribute("value", splitString[1]);
            }
        }
    }

}