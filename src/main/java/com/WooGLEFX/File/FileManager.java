package com.WooGLEFX.File;

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

import com.WooGLEFX.Functions.BlankObjectGenerator;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.Functions.PaletteManager;
import com.WooGLEFX.Structures.GameVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.WooGLEFX.EditorObjects._Ball;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.WorldLevel;

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
    public static final String editorLocation = System.getProperty("user.dir") + "\\";




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

    public static Image getObjectIcon(String imageName) throws FileNotFoundException {
        switch (imageName.toLowerCase()) {

            case "addin" -> { return getIcon("ObjectIcons\\addin\\addin.png"); }
            case "addinauthor" -> { return getIcon("ObjectIcons\\addin\\addin.png"); }
            case "addindependencies" -> { return getIcon("ObjectIcons\\addin\\addin.png"); }
            case "addindepends" -> { return getIcon("ObjectIcons\\addin\\addin.png"); }
            case "addindescription" -> { return getIcon("ObjectIcons\\addin\\addin.png"); }
            case "addinid" -> { return getIcon("ObjectIcons\\addin\\addin.png"); }
            case "addinlevel" -> { return getIcon("ObjectIcons\\addin\\addin.png"); }
            case "addinleveldir" -> { return getIcon("ObjectIcons\\addin\\addin.png"); }
            case "addinlevelname" -> { return getIcon("ObjectIcons\\addin\\addin.png"); }
            case "addinlevelocd" -> { return getIcon("ObjectIcons\\addin\\addin.png"); }
            case "addinlevels" -> { return getIcon("ObjectIcons\\addin\\addin.png"); }
            case "addinlevelsubtitle" -> { return getIcon("ObjectIcons\\addin\\addin.png"); }
            case "addinname" -> { return getIcon("ObjectIcons\\addin\\addin.png"); }
            case "addinthumbnail" -> { return getIcon("ObjectIcons\\addin\\addin.png"); }
            case "addintype" -> { return getIcon("ObjectIcons\\addin\\addin.png"); }
            case "addinversion" -> { return getIcon("ObjectIcons\\addin\\addin.png"); }

            case "ballinstance" -> { return getIcon("ObjectIcons\\level\\BallInstance.png"); }
            case "camera" -> { return getIcon("ObjectIcons\\level\\camera.png"); }
            case "endoncollision" -> { return getIcon("ObjectIcons\\level\\endoncollision.png"); }
            case "endonmessage" -> { return getIcon("ObjectIcons\\level\\endonmessage.png"); }
            case "endonnogeom" -> { return getIcon("ObjectIcons\\level\\endonnogeom.png"); }
            case "fire" -> { return getIcon("ObjectIcons\\level\\fire.png"); }
            case "level" -> { return getIcon("ObjectIcons\\level\\level.png"); }
            case "levelexit" -> { return getIcon("ObjectIcons\\level\\levelexit.png"); }
            case "loopsound" -> { return getIcon("ObjectIcons\\level\\loopsound.png"); }
            case "music" -> { return getIcon("ObjectIcons\\level\\music.png"); }
            case "pipe" -> { return getIcon("ObjectIcons\\level\\pipe.png"); }
            case "poi" -> { return getIcon("ObjectIcons\\level\\poi.png"); }
            case "signpost" -> { return getIcon("ObjectIcons\\level\\signpost.png"); }
            case "strand" -> { return getIcon("ObjectIcons\\level\\strand.png"); }
            case "targetheight" -> { return getIcon("ObjectIcons\\level\\targetheight.png"); }
            case "vertex" -> { return getIcon("ObjectIcons\\level\\vertex.png"); }

            case "resourcemanifest" -> { return getIcon("ObjectIcons\\resrc\\resourcemanifest.png"); }
            case "resources" -> { return getIcon("ObjectIcons\\resrc\\resources.png"); }
            case "resrcimage" -> { return getIcon("ObjectIcons\\resrc\\resrcimage.png"); }
            case "setdefaults" -> { return getIcon("ObjectIcons\\resrc\\setdefaults.png"); }
            case "sound" -> { return getIcon("ObjectIcons\\resrc\\sound.png"); }

            case "button" -> { return getIcon("ObjectIcons\\scene\\button.png"); }
            case "buttongroup" -> { return getIcon("ObjectIcons\\scene\\buttongroup.png"); }
            case "circle" -> { return getIcon("ObjectIcons\\scene\\circle.png"); }
            case "compositegeom" -> { return getIcon("ObjectIcons\\scene\\compositegeom.png"); }
            case "hinge" -> { return getIcon("ObjectIcons\\scene\\hinge.png"); }
            case "label" -> { return getIcon("ObjectIcons\\scene\\label.png"); }
            case "line" -> { return getIcon("ObjectIcons\\scene\\line.png"); }
            case "linearforcefield" -> { return getIcon("ObjectIcons\\scene\\linearforcefield.png"); }
            case "motor" -> { return getIcon("ObjectIcons\\scene\\motor.png"); }
            case "particles" -> { return getIcon("ObjectIcons\\scene\\particles.png"); }
            case "radialforcefield" -> { return getIcon("ObjectIcons\\scene\\radialforcefield.png"); }
            case "rectangle" -> { return getIcon("ObjectIcons\\scene\\rectangle.png"); }
            case "scene" -> { return getIcon("ObjectIcons\\scene\\scene.png"); }
            case "scenelayer" -> { return getIcon("ObjectIcons\\scene\\SceneLayer.png"); }
            case "slider" -> { return getIcon("ObjectIcons\\scene\\slider.png"); }

            case "textstring" -> { return getIcon("ObjectIcons\\text\\textstring.png"); }
            case "textstrings" -> { return getIcon("ObjectIcons\\text\\textstrings.png"); }

        }

        throw new FileNotFoundException("What is " + imageName + "???");

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

    public static ArrayList<ArrayList<String>> attributes = new ArrayList<>();
    public static ArrayList<String> objectNames = new ArrayList<>();

}