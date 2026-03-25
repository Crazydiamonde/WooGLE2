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

public class EditorObjectXMLReader {

    private static final Logger logger = LoggerFactory.getLogger(EditorObjectXMLReader.class);


    private static class XMLHandler extends DefaultHandler {

        private final String prefix;
        private final EditorObject[] parent;
        private final GameVersion version;

        public XMLHandler(String prefix, EditorObject[] parent, GameVersion version) {
            this.prefix = prefix;
            this.parent = parent;
            this.version = version;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void startElement(String uri, String localName, String qName, Attributes attributes) {

            EditorObject obj;
            if (parent[0] instanceof level && qName.equals("name")) qName = "_name";
            try {
                obj = ObjectCreator.create((Class<? extends EditorObject>) Class.forName(prefix + "." + qName), parent[0], version);
            } catch (ClassNotFoundException e) {
                logger.error("", e);
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

    public static <T extends EditorObject> T readEditorObject(String prefix, GameVersion version, File sourceFile, Class<T> tClass) {

        String sourceText;
        try {
            if (sourceFile.getName().endsWith("bin")) {
                sourceText = new String(AESBinFormat.decodeFile(sourceFile));
            } else {
                sourceText = Files.readString(sourceFile.toPath());
            }
        } catch (IOException e) {
            logger.error("", e);
            return null;
        }

        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        SAXParser saxParser;
        try {
            saxParser = saxParserFactory.newSAXParser();
        } catch (ParserConfigurationException | SAXException e) {
            logger.error("", e);
            return null;
        }

        EditorObject[] reference = new EditorObject[] { null };

        try {
            saxParser.parse(new InputSource(new StringReader(sourceText)), new XMLHandler(prefix, reference, version));
        } catch (SAXException | IOException e) {
            logger.error("", e);
            return null;
        }

        return tClass.cast(reference[0]);

    }

}
