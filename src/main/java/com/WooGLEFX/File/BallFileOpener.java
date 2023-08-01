package com.WooGLEFX.File;
import com.WooGLEFX.Structures.EditorAttribute;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WorldOfGoo.Resrc.ResrcImage;
import com.WorldOfGoo.Resrc.SetDefaults;
import com.WorldOfGoo.Resrc.Sound;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class BallFileOpener extends DefaultHandler {
    public static EditorObject parent = null;

    public static int mode = 0;

    public static SetDefaults impossible = null;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        //System.out.println("Started: " + qName);
        EditorAttribute[] glumbus = new EditorAttribute[attributes.getLength()];
        for (int i = 0; i < attributes.getLength(); i++){
            glumbus[i] = new EditorAttribute(attributes.getQName(i), attributes.getValue(i), "", new InputField(attributes.getQName(i), InputField.NULL), false);
        }
        EditorObject obj = EditorObject.create(qName, glumbus, parent);
        obj.setRealName(qName);
        if (mode == 0) {
            FileManager.commonBallData.add(obj);
        } else if (mode == 1) {
            if (impossible != null && (obj instanceof ResrcImage || obj instanceof Sound)){
                obj.setAttribute("id", impossible.getAttribute("idprefix") + obj.getAttribute("id"));
                obj.setAttribute("path", impossible.getAttribute("path") + obj.getAttribute("path"));
            }
            if (obj instanceof SetDefaults) {
                impossible = (SetDefaults)obj;
            }
            FileManager.commonBallResrcData.add(obj);
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
        //System.out.println(Arrays.toString(ch) + start + length);
    }
}