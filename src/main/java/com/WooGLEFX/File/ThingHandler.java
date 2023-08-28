package com.WooGLEFX.File;
import com.WooGLEFX.Structures.EditorAttribute;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WorldOfGoo.Addin.Addin;
import com.WorldOfGoo.Addin.AddinLevel;
import com.WorldOfGoo.Resrc.ResrcImage;
import com.WorldOfGoo.Resrc.SetDefaults;
import com.WorldOfGoo.Resrc.Sound;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class ThingHandler extends DefaultHandler {
    public static EditorObject parent = null;

    public int mode = 0;

    private EditorObject currentObject = null;

    public static SetDefaults impossible = null;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        String zName = qName;
        if (mode == 3) {
            if (parent instanceof AddinLevel && zName.equals("name")) {
                zName = "Addin_wtf_" + zName;
            } else {
                zName = "Addin_" + zName;
            }
        }
        EditorObject obj = EditorObject.create(zName, new EditorAttribute[0], parent);
        for (int i = 0; i < attributes.getLength(); i++) {
            obj.setAttribute(attributes.getQName(i), attributes.getValue(i));
        }
        currentObject = obj;
        obj.setRealName(qName);
        if (mode == 0) {
            FileManager.level.add(obj);
        } else if (mode == 1) {
            FileManager.resources.add(obj);
            if (impossible != null && (obj instanceof ResrcImage || obj instanceof Sound)) {
                obj.setAttribute("REALid", impossible.getAttribute("idprefix") + obj.getAttribute("id"));
                obj.setAttribute("REALpath", impossible.getAttribute("path") + obj.getAttribute("path"));
                obj.setAttribute("id", obj.getAttribute("id"));
                obj.setAttribute("path", obj.getAttribute("path"));
            }
            if (obj instanceof SetDefaults) {
                impossible = (SetDefaults) obj;
            }
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
        if ((currentObject.getParent() instanceof Addin || currentObject.getParent() instanceof AddinLevel) && currentObject.getChildren().size() == 0 && currentObject.getAttributes().length == 1) {
            StringBuilder total = new StringBuilder();
            for (int i = start; i < start + length; i++) {
                total.append(ch[i]);
            }
            if (!total.toString().equals("\n\t") && !total.toString().equals("\n") && !total.toString().equals("\n\t\t") && !total.toString().equals("\n\t\t\t") && !total.toString().equals("")) {
                currentObject.getAttributes()[0].setValue(total.toString());
            }
        }
    }
}