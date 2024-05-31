package com.woogleFX.file.fileImport;

import com.woogleFX.editorObjects.objectCreators.ObjectCreator;
import com.woogleFX.structures.GameVersion;
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

    private final ArrayList<EditorObject> scene;
    private final ArrayList<EditorObject> level;
    private final ArrayList<EditorObject> resrc;
    private final ArrayList<EditorObject> addin;
    private final ArrayList<EditorObject> text;
    private final GameVersion version;

    public ObjectXMLParser(ArrayList<EditorObject> scene,
                           ArrayList<EditorObject> level,
                           ArrayList<EditorObject> resrc,
                           ArrayList<EditorObject> addin,
                           ArrayList<EditorObject> text,
                           GameVersion version) {
        this.scene = scene;
        this.level = level;
        this.resrc = resrc;
        this.addin = addin;
        this.text = text;
        this.version = version;
    }


    private EditorObject currentObject = null;


    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        String zName = qName;

        EditorObject parent = currentObject;

        EditorObject absoluteParent = currentObject;
        while (absoluteParent != null && absoluteParent.getParent() != null) absoluteParent = absoluteParent.getParent();

        if (absoluteParent instanceof Addin) {
            if (parent instanceof AddinLevel && zName.equals("name")) {
                zName = "Addin_wtf_" + zName;
            } else {
                zName = "Addin_" + zName;
            }
        }

        currentObject = ObjectCreator.create(zName, parent, version);

        for (int i = 0; i < attributes.getLength(); i++) {
            String attributeName = attributes.getQName(i);
            String attributeValue = attributes.getValue(i);

            // handle break (set as tag in game but handled as attribute in editor)
            if (attributeName.equals("tag")) {
                String[] tags = attributeValue.split(",");
                for (String tag : tags) if (tag.startsWith("break=")) {
                    attributeValue = attributeValue.replace("," + tag, "").replace(tag, "");
                    currentObject.setAttribute("break", Double.parseDouble(tag.substring(6)));
                }
            }

            currentObject.setAttribute(attributeName, attributeValue);
        }

        if (absoluteParent == null) absoluteParent = currentObject;

        if (absoluteParent instanceof Scene)            scene.add(currentObject);
        if (absoluteParent instanceof Level)            level.add(currentObject);
        if (absoluteParent instanceof ResourceManifest) resrc.add(currentObject);
        if (absoluteParent instanceof Addin)            addin.add(currentObject);
        if (absoluteParent instanceof TextStrings)      text .add(currentObject);

    }


    @Override
    public void endElement(String uri, String localName, String qName) {
        currentObject = currentObject.getParent();
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