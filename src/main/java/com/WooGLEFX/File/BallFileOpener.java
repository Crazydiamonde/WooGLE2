package com.WooGLEFX.File;
import com.WooGLEFX.Structures.EditorAttribute;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WorldOfGoo.Addin.AddinLevel;
import com.WorldOfGoo.Resrc.ResrcImage;
import com.WorldOfGoo.Resrc.SetDefaults;
import com.WorldOfGoo.Resrc.Sound;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

public class BallFileOpener extends DefaultHandler {
    public static EditorObject parent = null;

    public static int mode = 0;

    public static SetDefaults impossible = null;

    public static boolean bruhmode = false;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        //System.out.println("Started: " + qName);
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
            EditorObject obj = EditorObject.create(qName, new EditorAttribute[0], parent);
            for (int i = 0; i < attributes.getLength(); i++){
                obj.setAttribute(attributes.getQName(i), attributes.getValue(i));
            }
            if (qName.equals("ball_particles") || qName.equals("ball_sound")) {
                obj.setRealName(qName.substring(5));
            } else {
                obj.setRealName(qName);
            }
            if (mode == 0) {
                FileManager.commonBallData.add(obj);
            } else if (mode == 1) {
                if (impossible != null && (obj instanceof ResrcImage || obj instanceof Sound)) {
                    obj.setAttribute("REALid", obj.getAttribute("id"));
                    obj.setAttribute("REALpath", obj.getAttribute("path"));
                    obj.setAttribute("id", impossible.getAttribute("idprefix") + obj.getAttribute("id"));
                    obj.setAttribute("path", impossible.getAttribute("path") + obj.getAttribute("path"));
                }
                if (obj instanceof SetDefaults) {
                    impossible = (SetDefaults) obj;
                }
                FileManager.commonBallResrcData.add(obj);
            }
            parent = obj;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        //System.out.println("Ended: " + qName);
        if (!bruhmode) {
            parent = parent.getParent();
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        //System.out.println(Arrays.toString(ch) + start + length);
    }
}