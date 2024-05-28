package com.woogleFX.file.fileImport;
import java.util.ArrayList;

import com.woogleFX.editorObjects.objectCreators.ObjectCreator;
import com.woogleFX.structures.GameVersion;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import com.woogleFX.editorObjects.EditorObject;

public class BallFileOpener extends DefaultHandler {
    public static EditorObject parent = null;

    public static int mode = 0;


    private final ArrayList<EditorObject> objects;
    private final ArrayList<EditorObject> resources;
    private final GameVersion version;
    public BallFileOpener(ArrayList<EditorObject> objects,
                          ArrayList<EditorObject> resources,
                          GameVersion  version) {
        this.objects = objects;
        this.resources = resources;
        this.version = version;
    }


    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {

        if (qName.equals("particles") || qName.equals("sound")) qName = "ball_" + qName;

        EditorObject obj = ObjectCreator.create(qName, parent, version);

        for (int i = 0; i < attributes.getLength(); i++) obj.setAttribute(attributes.getQName(i), attributes.getValue(i));

        if (mode == 0) objects.add(obj);
        else if (mode == 1) resources.add(obj);

        parent = obj;

    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        //logger.debug("Ended: " + qName);
        parent = parent.getParent();
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        //logger.debug(Arrays.toString(ch) + start + length);
    }
}