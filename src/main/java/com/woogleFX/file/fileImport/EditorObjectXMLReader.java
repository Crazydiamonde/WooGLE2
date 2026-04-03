package com.woogleFX.file.fileImport;

import com.woogleFX.assets.GameVersion;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.objectCreators.ObjectCreator;
import com.woogleFX.file.aesEncryption.AESBinFormat;
import com.worldOfGoo.addin.level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.stream.Collectors;

public class EditorObjectXMLReader {

    private static final Logger logger = LoggerFactory.getLogger(EditorObjectXMLReader.class);


    private static class XMLHandler extends DefaultHandler {

        private final Class<? extends EditorObject> objClass;
        private final EditorObject[] parent;
        private final GameVersion version;

        public XMLHandler(Class<? extends EditorObject> objClass, EditorObject[] parent, GameVersion version) {
            this.objClass = objClass;
            this.parent = parent;
            this.version = version;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {

            EditorObject obj;
            if (parent[0] instanceof level && qName.equals("name")) qName = "_name";
            try {
                //noinspection unchecked
                obj = ObjectCreator.create((Class<? extends EditorObject>) Class.forName(objClass.getPackageName() + "." + qName), parent[0], version);
            } catch (ClassNotFoundException e) {
                return;
            }

            for (int i = 0; i < attributes.getLength(); i++) {
                if (!obj.attributeExists(attributes.getQName(i))) {
                    // shut up! TODO:
                    // logger.warn(obj.getClass().getName() + " does not have attribute " + attributes.getQName(i));
                } else {
                    obj.setAttribute(attributes.getQName(i), attributes.getValue(i));
                }
            }

            parent[0] = obj;

        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            if (parent[0].getParent() != null) parent[0] = parent[0].getParent();
        }

    }

    /** Reads the given XML file and returns its contents as an EditorObject.
     * @param version The version of the game that this file is from.
     * @param sourceFile The file to read from.
     * @param objClass The type of object that this file contains.
     * @return A new EditorObject, created from this file.
     * @throws IOException if anything goes wrong reading the file or creating the object. */
    public static <T extends EditorObject> T readEditorObject(GameVersion version, File sourceFile, Class<T> objClass) throws IOException {

        String sourceText;
        if (sourceFile.getName().endsWith("bin")) {
            sourceText = new String(AESBinFormat.decodeFile(sourceFile));
        } else {
            sourceText = Files.readString(sourceFile.toPath());
        }

        while (sourceText.contains("<?xml")) {
            sourceText = sourceText.lines().skip(1).collect(Collectors.joining());
        }

        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

        SAXParser saxParser;
        try {
            saxParser = saxParserFactory.newSAXParser();
        } catch (ParserConfigurationException | SAXException e) {
            logger.error("", e);
            return null;
        }

        // incredibly hacky workaround to add quotes to xml entries that were missing them
        int idtracker = 0;
        while (idtracker < sourceText.length()) {
            idtracker = sourceText.indexOf("id=", idtracker);
            if (idtracker == -1) break;
            if (sourceText.charAt(idtracker + 3) != '\"')
                sourceText = sourceText.substring(0, idtracker + 3) + '\"' +
                        sourceText.substring(idtracker + 3, sourceText.indexOf('>', idtracker)) + '\"' +
                        sourceText.substring(sourceText.indexOf('>', idtracker));
            idtracker += 3;
        }

        EditorObject[] reference = new EditorObject[] { null };

        try {
            saxParser.parse(new InputSource(new StringReader(sourceText)), new XMLHandler(objClass, reference, version));
        } catch (SAXException e) {
            logger.error("", e);
            return null;
        }

        return objClass.cast(reference[0]);

    }

}
