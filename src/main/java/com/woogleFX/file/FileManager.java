package com.woogleFX.file;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.SupremeMain;
import com.woogleFX.editorObjects.objectCreators.BlankObjectGenerator;
import com.woogleFX.file.fileImport.BallFileOpener;
import com.woogleFX.file.fileImport.ObjectXMLParser;
import com.woogleFX.file.fileImport.SimpleHandler;
import com.woogleFX.functions.LevelManager;
import com.woogleFX.functions.PaletteManager;
import com.woogleFX.structures.GameVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.woogleFX.editorObjects._Ball;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.structures.WorldLevel;

import javafx.scene.image.Image;


public class FileManager {

    private static final Logger logger = LoggerFactory.getLogger(FileManager.class);


    private static String oldWOGdir = "";
    public static void setOldWOGdir(String oldWOGdir) {
        FileManager.oldWOGdir = oldWOGdir;
    }


    private static String newWOGdir = "";
    public static void setNewWOGdir(String newWOGdir) {
        FileManager.newWOGdir = newWOGdir;
    }


    public static String getGameDir(GameVersion version) {
        if (version == GameVersion.OLD) return oldWOGdir;
        if (version == GameVersion.NEW) return newWOGdir;
        throw new RuntimeException("Invalid game version: " + version);
    }


    private static Image failedImage;
    public static Image getFailedImage() {
        return failedImage;
    }
    public static void openFailedImage() throws IOException {
        failedImage = openImageFromFilePath(editorLocation + "ObjectIcons\\failed.png");
    }


    // Editor location should be the current folder
    private static final String editorLocation;
    static {
        String editorLocation1;
        try {
            editorLocation1 = new File(SupremeMain.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent() + "\\";
            logger.debug(editorLocation1);
        } catch (URISyntaxException ignored) {
            editorLocation1 = "";
        }
        editorLocation = editorLocation1;
    }
    public static String getEditorLocation() {
        return editorLocation;
    }


    public static void readWOGdirs() throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        SimpleHandler defaultHandler = new SimpleHandler();
        File properties = new File(editorLocation + "properties.xml");
        if (!properties.exists()) {
            oldWOGdir = "";
            newWOGdir = "";
            return;
        }
        saxParser.parse(properties, defaultHandler);
    }


    public static Image openImageFromFilePath(String file_path) throws IOException {
        InputStream inputStream = new FileInputStream(file_path);
        Image image = new Image(inputStream);
        inputStream.close();
        return image;
    }


    public static Image getIcon(String imagePath) {
        try {
            InputStream inputStream = new FileInputStream(editorLocation + imagePath);
            Image iconImage = new Image(inputStream);
            inputStream.close();
            return iconImage;
        } catch (IOException e) {
            logger.error("", e);
            return null;
        }
    }

    private static String bytesToString(byte[] input) {
        return new String(input, StandardCharsets.UTF_8);
    }


    public static void supremeAddToList(ArrayList<EditorObject> list, EditorObject object) {
        list.add(object);
        for (EditorObject child : object.getChildren()) {
            supremeAddToList(list, child);
        }
    }


    public static WorldLevel openLevel(String levelName, GameVersion version) throws ParserConfigurationException, SAXException, IOException {

        LevelManager.setLevel(null);

        ArrayList<EditorObject> scene = new ArrayList<>();
        ArrayList<EditorObject> level = new ArrayList<>();
        ArrayList<EditorObject> resrc = new ArrayList<>();
        ArrayList<EditorObject> addin = new ArrayList<>();
        ArrayList<EditorObject> text = new ArrayList<>();

        SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
        ObjectXMLParser defaultHandler = new ObjectXMLParser(scene, level, resrc, addin, text, version);

        if (version == GameVersion.OLD) {

            String levelDir = oldWOGdir + "\\res\\levels\\" + levelName + "\\" + levelName;

            File sceneF = new File(levelDir + ".scene.bin");
            saxParser.parse(new InputSource(new StringReader(bytesToString(AESBinFormat.decodeFile(sceneF)))), defaultHandler);

            File levelF = new File(levelDir + ".level.bin");
            saxParser.parse(new InputSource(new StringReader(bytesToString(AESBinFormat.decodeFile(levelF)))), defaultHandler);

            File resrcF = new File(levelDir + ".resrc.bin");
            saxParser.parse(new InputSource(new StringReader(bytesToString(AESBinFormat.decodeFile(resrcF)))), defaultHandler);

            File addinF = new File(levelDir + ".addin.xml");
            if (addinF.exists()) saxParser.parse(addinF, defaultHandler);
            else supremeAddToList(addin, BlankObjectGenerator.generateBlankAddinObject(levelName, version));

            File textF = new File(levelDir + ".text.xml");
            if (textF.exists()) saxParser.parse(textF, defaultHandler);
            else supremeAddToList(text, BlankObjectGenerator.generateBlankTextObject(version));

        } else if (version == GameVersion.NEW) {

            String levelDir = newWOGdir + "\\res\\levels\\" + levelName + "\\" + levelName;

            File sceneF = new File(levelDir + ".scene");
            saxParser.parse(sceneF, defaultHandler);

            File levelF = new File(levelDir + ".level");
            saxParser.parse(levelF, defaultHandler);

            File resrcF = new File(levelDir + ".resrc");
            saxParser.parse(resrcF, defaultHandler);

            File addinF = new File(levelDir + ".addin.xml");
            if (addinF.exists()) saxParser.parse(addinF, defaultHandler);
            else supremeAddToList(addin, BlankObjectGenerator.generateBlankAddinObject(levelName, version));

            File textF = new File(levelDir + ".text.xml");
            if (textF.exists()) saxParser.parse(textF, defaultHandler);
            else supremeAddToList(text, BlankObjectGenerator.generateBlankTextObject(version));

        }

        return new WorldLevel(scene, level, resrc, addin, text, version);

    }


    public static _Ball openBall(String ballName, GameVersion version) throws ParserConfigurationException, SAXException, IOException {

        /* Make sure a ball from an invalid version isn't being opened (possible because of properties.xml) */
        if (version == GameVersion.OLD && oldWOGdir.isEmpty() ||
            version == GameVersion.NEW && newWOGdir.isEmpty()) {
            return null;
        }

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();

        ArrayList<EditorObject> objects = new ArrayList<>();
        ArrayList<EditorObject> resources = new ArrayList<>();

        BallFileOpener defaultHandler = new BallFileOpener(objects, resources, version);
        if (version == GameVersion.OLD) {
            File ballFile = new File(oldWOGdir + "\\res\\balls\\" + ballName + "\\balls.xml.bin");
            File ballFileR = new File(oldWOGdir + "\\res\\balls\\" + ballName + "\\resources.xml.bin");
            BallFileOpener.mode = 0;
            saxParser.parse(new InputSource(new StringReader(bytesToString(AESBinFormat.decodeFile(ballFile)))), defaultHandler);
            BallFileOpener.mode = 1;
            saxParser.parse(new InputSource(new StringReader(bytesToString(AESBinFormat.decodeFile(ballFileR)))), defaultHandler);
            return new _Ball(objects, resources);
        } else if (version == GameVersion.NEW) {
            File ballFile = new File(newWOGdir + "\\res\\balls\\" + ballName + "\\balls.xml");
            File ballFileR = new File(newWOGdir + "\\res\\balls\\" + ballName + "\\resources.xml");
            BallFileOpener.mode = 0;
            saxParser.parse(ballFile, defaultHandler);
            BallFileOpener.mode = 1;
            saxParser.parse(ballFileR, defaultHandler);
            return new _Ball(objects, resources);
        } else {
            return null;
        }
    }


    public static ArrayList<EditorObject> openResources(GameVersion version) throws ParserConfigurationException, SAXException, IOException {

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();

        ArrayList<EditorObject> objects = new ArrayList<>();
        ArrayList<EditorObject> resources = new ArrayList<>();

        BallFileOpener defaultHandler = new BallFileOpener(objects, resources, version);
        BallFileOpener.mode = 1;
        File ballFile;
        String dir;
        if (version == GameVersion.OLD) {
            dir = oldWOGdir;
            ballFile = new File(dir + "\\properties\\resources.xml.bin");
            saxParser.parse(new InputSource(new StringReader(bytesToString(AESBinFormat.decodeFile(ballFile)))), defaultHandler);
        } else if (version == GameVersion.NEW) {
            dir = newWOGdir;
            ballFile = new File(dir + "\\properties\\resources.xml");
            saxParser.parse(ballFile, defaultHandler);
        }
        return resources;
    }


    public static ArrayList<EditorObject> openParticles(GameVersion version) throws ParserConfigurationException, SAXException, IOException {

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();

        ArrayList<EditorObject> objects = new ArrayList<>();
        ArrayList<EditorObject> resources = new ArrayList<>();

        BallFileOpener defaultHandler = new BallFileOpener(objects, resources, version);

        BallFileOpener.mode = 0;
        if (version == GameVersion.OLD && !oldWOGdir.isEmpty()) {
            File ballFile = new File(oldWOGdir + "\\properties\\fx.xml.bin");
            saxParser.parse(new InputSource(new StringReader(bytesToString(AESBinFormat.decodeFile(ballFile)))), defaultHandler);
        } else if (version == GameVersion.NEW && !newWOGdir.isEmpty()) {
            File ballFile2 = new File(newWOGdir + "\\properties\\fx.xml");
            saxParser.parse(ballFile2, defaultHandler);
        }
        return objects;
    }


    public static ArrayList<EditorObject> openText(GameVersion version) throws ParserConfigurationException, SAXException, IOException {

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();

        ArrayList<EditorObject> objects = new ArrayList<>();
        ArrayList<EditorObject> resources = new ArrayList<>();

        BallFileOpener defaultHandler = new BallFileOpener(objects, resources, version);
        BallFileOpener.mode = 0;
        if (version == GameVersion.OLD && !oldWOGdir.isEmpty()) {
            File ballFile = new File(oldWOGdir + "\\properties\\text.xml.bin");
            byte[] bytes = AESBinFormat.decodeFile(ballFile);
            // If the file starts with EF BB BF, strip these three bytes (not sure why it does this)
            if (bytes[0] == (byte)0xEF && bytes[1] == (byte)0xBB && bytes[2] == (byte)0xBF) {
                byte[] newBytes = new byte[bytes.length - 3];
                System.arraycopy(bytes, 3, newBytes, 0, bytes.length - 3);
                bytes = newBytes;
            }
            String stringBytes = bytesToString(bytes);
            saxParser.parse(new InputSource(new StringReader(stringBytes)), defaultHandler);
        } else if (version == GameVersion.NEW && !newWOGdir.isEmpty()) {
            File ballFile2 = new File(newWOGdir + "\\properties\\text.xml");
            saxParser.parse(ballFile2, defaultHandler);
        }
        return objects;
    }


    public static void saveProperties() throws IOException {
        StringBuilder export = new StringBuilder("<properties>\n\n<oldWOG filepath=\"" + oldWOGdir + "\"/>\n<newWOG filepath=\"" + newWOGdir + "\"/>\n<gooBallPalette>\n");
        for (int i = 0; i < PaletteManager.getPaletteBalls().size(); i++) {
            export.append("\t<Ball ball=\"").append(PaletteManager.getPaletteBalls().get(i)).append("\" version=\"").append(PaletteManager.getPaletteVersions().get(i).toString()).append("\"/>\n");
        }
        export.append("</gooBallPalette>\n</properties>");

        Files.write(Paths.get(editorLocation + "properties.xml"), Collections.singleton(export.toString()), StandardCharsets.UTF_8);
    }

}