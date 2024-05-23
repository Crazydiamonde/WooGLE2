package com.woogleFX.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

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
    public static String getOldWOGdir() {
        return oldWOGdir;
    }
    public static void setOldWOGdir(String oldWOGdir) {
        logger.debug("setting to " + oldWOGdir);
        FileManager.oldWOGdir = oldWOGdir;
    }


    private static boolean hasOldWOG = false;
    public static boolean hasOldWOG() {
        return hasOldWOG;
    }
    public static void setHasOldWOG(boolean hasOldWOG) {
        FileManager.hasOldWOG = hasOldWOG;
    }


    private static String newWOGdir = "";
    public static String getNewWOGdir() {
        return newWOGdir;
    }
    public static void setNewWOGdir(String newWOGdir) {
        FileManager.newWOGdir = newWOGdir;
    }


    private static boolean hasNewWOG = false;
    public static boolean hasNewWOG() {
        return hasNewWOG;
    }
    public static void setHasNewWOG(boolean hasNewWOG) {
        FileManager.hasNewWOG = hasNewWOG;
    }


    private static Image failedImage;
    public static Image getFailedImage() {
        return failedImage;
    }
    public static void openFailedImage() throws FileNotFoundException {
        failedImage = openImageFromFilePath(editorLocation + "ObjectIcons\\failed.png");
    }


    // Editor location should be the current folder
    private static final String editorLocation = System.getProperty("user.dir") + "\\";
    public static String getEditorLocation() {
        return editorLocation;
    }


    public static void readWOGdirs() throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        SimpleHandler defaultHandler = new SimpleHandler();
        File properties = new File(editorLocation + "properties.xml");
        if (!properties.exists()) {
            FileManager.setHasOldWOG(false);
            FileManager.setOldWOGdir("");
            FileManager.setHasNewWOG(false);
            FileManager.setNewWOGdir("");
            return;
        }
        saxParser.parse(properties, defaultHandler);
    }

    public static Image openImageFromFilePath(String file_path) throws FileNotFoundException {
        return new Image(new FileInputStream(file_path));
    }

    public static Image getIcon(String imagePath) throws FileNotFoundException {
        return new Image(new FileInputStream(editorLocation + imagePath));
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


    public static ArrayList<EditorObject> commonBallData;
    public static ArrayList<EditorObject> commonBallResrcData;

    public static WorldLevel openLevel(String levelName, GameVersion version) throws ParserConfigurationException, SAXException, IOException {

        LevelManager.setLevel(null);

        ArrayList<EditorObject> scene = new ArrayList<>();
        ArrayList<EditorObject> level = new ArrayList<>();
        ArrayList<EditorObject> resrc = new ArrayList<>();
        ArrayList<EditorObject> addin = new ArrayList<>();
        ArrayList<EditorObject> text = new ArrayList<>();

        SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
        ObjectXMLParser defaultHandler = new ObjectXMLParser(scene, level, resrc, addin, text);

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
            else supremeAddToList(addin, BlankObjectGenerator.generateBlankAddinObject(levelName));

            File textF = new File(levelDir + ".text.xml");
            if (textF.exists()) saxParser.parse(textF, defaultHandler);
            else supremeAddToList(text, BlankObjectGenerator.generateBlankTextObject());

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
            else supremeAddToList(addin, BlankObjectGenerator.generateBlankAddinObject(levelName));

            File textF = new File(levelDir + ".text.xml");
            if (textF.exists()) saxParser.parse(textF, defaultHandler);
            else supremeAddToList(text, BlankObjectGenerator.generateBlankTextObject());

        }

        return new WorldLevel(scene, level, resrc, addin, text, version);

    }

    public static _Ball openBall(String ballName, GameVersion version) throws ParserConfigurationException, SAXException, IOException {

        /* Make sure a ball from an invalid version isn't being opened (possible because of properties.xml) */
        if (version == GameVersion.OLD && oldWOGdir.isEmpty() ||
            version == GameVersion.NEW && newWOGdir.isEmpty()) {
            return null;
        }

        commonBallData = new ArrayList<>();
        commonBallResrcData = new ArrayList<>();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        BallFileOpener defaultHandler = new BallFileOpener();
        if (version == GameVersion.OLD) {
            File ballFile = new File(oldWOGdir + "\\res\\balls\\" + ballName + "\\balls.xml.bin");
            File ballFileR = new File(oldWOGdir + "\\res\\balls\\" + ballName + "\\resources.xml.bin");
            BallFileOpener.mode = 0;
            saxParser.parse(new InputSource(new StringReader(bytesToString(AESBinFormat.decodeFile(ballFile)))), defaultHandler);
            BallFileOpener.mode = 1;
            saxParser.parse(new InputSource(new StringReader(bytesToString(AESBinFormat.decodeFile(ballFileR)))), defaultHandler);
            return new _Ball(commonBallData, commonBallResrcData);
        } else if (version == GameVersion.NEW) {
            File ballFile = new File(newWOGdir + "\\res\\balls\\" + ballName + "\\balls.xml");
            File ballFileR = new File(newWOGdir + "\\res\\balls\\" + ballName + "\\resources.xml");
            BallFileOpener.mode = 0;
            saxParser.parse(ballFile, defaultHandler);
            BallFileOpener.mode = 1;
            saxParser.parse(ballFileR, defaultHandler);
            return new _Ball(commonBallData, commonBallResrcData);
        } else {
            return null;
        }
    }

    public static ArrayList<EditorObject> openResources(GameVersion version) throws ParserConfigurationException, SAXException, IOException {
        commonBallResrcData = new ArrayList<>();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        BallFileOpener defaultHandler = new BallFileOpener();
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
        return commonBallResrcData;
    }

    public static void openParticles(GameVersion version) throws ParserConfigurationException, SAXException, IOException {
        commonBallData = new ArrayList<>();
        commonBallResrcData = new ArrayList<>();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        BallFileOpener defaultHandler = new BallFileOpener();
        BallFileOpener.mode = 0;
        if (version == GameVersion.OLD && hasOldWOG) {
            File ballFile = new File(FileManager.getOldWOGdir() + "\\properties\\fx.xml.bin");
            saxParser.parse(new InputSource(new StringReader(bytesToString(AESBinFormat.decodeFile(ballFile)))), defaultHandler);
        } else if (version == GameVersion.NEW && hasNewWOG) {
            File ballFile2 = new File(FileManager.getNewWOGdir() + "\\properties\\fx.xml");
            saxParser.parse(ballFile2, defaultHandler);
        }
    }

    public static ArrayList<EditorObject> openText(GameVersion version) throws ParserConfigurationException, SAXException, IOException {
        commonBallData = new ArrayList<>();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        BallFileOpener defaultHandler = new BallFileOpener();
        BallFileOpener.mode = 0;
        if (version == GameVersion.OLD && hasOldWOG) {
            File ballFile = new File(FileManager.getOldWOGdir() + "\\properties\\text.xml.bin");
            byte[] bytes = AESBinFormat.decodeFile(ballFile);
            // If the file starts with EF BB BF, strip these three bytes (not sure why it does this)
            if (bytes[0] == (byte)0xEF && bytes[1] == (byte)0xBB && bytes[2] == (byte)0xBF) {
                byte[] newBytes = new byte[bytes.length - 3];
                System.arraycopy(bytes, 3, newBytes, 0, bytes.length - 3);
                bytes = newBytes;
            }
            String stringBytes = bytesToString(bytes);
            saxParser.parse(new InputSource(new StringReader(stringBytes)), defaultHandler);
        } else if (version == GameVersion.NEW && hasNewWOG) {
            File ballFile2 = new File(FileManager.getNewWOGdir() + "\\properties\\text.xml");
            saxParser.parse(ballFile2, defaultHandler);
        }
        return commonBallData;
    }

    public static void saveProperties() throws IOException {
        StringBuilder export = new StringBuilder("<properties>\n\n<oldWOG filepath=\"" + oldWOGdir + "\"/>\n<newWOG filepath=\"" + newWOGdir + "\"/>\n<gooBallPalette>\n");
        for (int i = 0; i < PaletteManager.getPaletteBalls().size(); i++) {
            export.append("\t<Ball ball=\"").append(PaletteManager.getPaletteBalls().get(i)).append("\" version=\"").append(PaletteManager.getPaletteVersions().get(i) == GameVersion.OLD ? "1.3" : "1.5").append("\"/>\n");
        }
        export.append("</gooBallPalette>\n</properties>");

        Files.write(Paths.get(editorLocation + "properties.xml"), Collections.singleton(export.toString()), StandardCharsets.UTF_8);
    }

    public static final ArrayList<ArrayList<String>> attributes = new ArrayList<>();
    public static final ArrayList<String> objectNames = new ArrayList<>();

}