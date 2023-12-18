package com.WooGLEFX.File;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

import com.WooGLEFX.EditorObjects._Ball;
import com.WooGLEFX.Engine.Main;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.WorldLevel;
import javafx.scene.image.Image;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


public class FileManager {

    private static String oldWOGdir = "";
    private static boolean hasOldWOG = false;
    private static String newWOGdir = "";
    private static boolean hasNewWOG = false;

    public static boolean isHasOldWOG() {
        return hasOldWOG;
    }

    public static void setHasOldWOG(boolean hasOldWOG) {
        FileManager.hasOldWOG = hasOldWOG;
    }

    public static boolean isHasNewWOG() {
        return hasNewWOG;
    }

    public static void setHasNewWOG(boolean hasNewWOG) {
        FileManager.hasNewWOG = hasNewWOG;
    }

    private static Image failedImage;

    // Editor location should be the current folder
    public static final String editorLocation = System.getProperty("user.dir") + "\\";

    public static Image getFailedImage() {
        return failedImage;
    }

    public static void openFailedImage() throws FileNotFoundException {
        failedImage = openImageFromFilePath(editorLocation + "ObjectIcons\\failed.png");
    }

    private static ArrayList<String> paletteBalls = new ArrayList<>();

    public static ArrayList<String> getPaletteBalls() {
        return paletteBalls;
    }

    public static void setPaletteBalls(ArrayList<String> paletteBalls) {
        FileManager.paletteBalls = paletteBalls;
    }

    private static ArrayList<Double> paletteVersions = new ArrayList<>();

    public static ArrayList<Double> getPaletteVersions() {
        return paletteVersions;
    }

    public static void setPaletteVersions(ArrayList<Double> paletteVersions) {
        FileManager.paletteVersions = paletteVersions;
    }

    public static String getOldWOGdir() {
        return oldWOGdir;
    }

    public static void setOldWOGdir(String oldWOGdir) {
        System.out.println("setting to " + oldWOGdir);
        FileManager.oldWOGdir = oldWOGdir;
    }

    public static void readWOGdirs() throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        SimpleHandler defaultHandler = new SimpleHandler();
        File properties = new File(editorLocation + "properties.xml");
        saxParser.parse(properties, defaultHandler);
    }

    public static String getNewWOGdir() {
        return newWOGdir;
    }

    public static void setNewWOGdir(String newWOGdir) {
        FileManager.newWOGdir = newWOGdir;
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

    public static ArrayList<EditorObject> scene;
    public static ArrayList<EditorObject> level;
    public static ArrayList<EditorObject> resources;
    public static ArrayList<EditorObject> addin;
    public static ArrayList<EditorObject> text;

    private static String bytesToString(byte[] input) {
        return new String(input, StandardCharsets.UTF_8);
    }


    public static ArrayList<EditorObject> commonBallData;
    public static ArrayList<EditorObject> commonBallResrcData;

    public static WorldLevel openLevel(String levelName, double version) throws ParserConfigurationException, SAXException, IOException {
        scene = new ArrayList<>();
        level = new ArrayList<>();
        resources = new ArrayList<>();
        addin = new ArrayList<>();
        text = new ArrayList<>();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        ThingHandler defaultHandler = new ThingHandler();

        if (version == 1.3) {
            File levelF = new File(oldWOGdir + "\\res\\levels\\" + levelName + "\\" + levelName + ".level.bin");
            File resrcF = new File(oldWOGdir + "\\res\\levels\\" + levelName + "\\" + levelName + ".resrc.bin");
            File sceneF = new File(oldWOGdir + "\\res\\levels\\" + levelName + "\\" + levelName + ".scene.bin");
            File addinF = new File(oldWOGdir + "\\res\\levels\\" + levelName + "\\" + levelName + ".addin.xml");
            File textF = new File(newWOGdir + "\\res\\levels\\" + levelName + "\\" + levelName + ".text.xml");

            saxParser.parse(new InputSource(new StringReader(bytesToString(AESBinFormat.decodeFile(levelF)))), defaultHandler);
            defaultHandler.mode = 1;
            saxParser.parse(new InputSource(new StringReader(bytesToString(AESBinFormat.decodeFile(resrcF)))), defaultHandler);
            defaultHandler.mode = 2;
            saxParser.parse(new InputSource(new StringReader(bytesToString(AESBinFormat.decodeFile(sceneF)))), defaultHandler);
            if (addinF.exists()) {
                defaultHandler.mode = 3;
                saxParser.parse(addinF, defaultHandler);
            } else {
                Main.supremeAddToList(addin, Main.generateBlankAddinObject(levelName));
            }
            if (textF.exists()) {
                defaultHandler.mode = 4;
                saxParser.parse(textF, defaultHandler);
            } else {
                Main.supremeAddToList(text, Main.generateBlankTextObject());
            }
        } else if (version == 1.5) {
            File levelF = new File(newWOGdir + "\\res\\levels\\" + levelName + "\\" + levelName + ".level");
            File resrcF = new File(newWOGdir + "\\res\\levels\\" + levelName + "\\" + levelName + ".resrc");
            File sceneF = new File(newWOGdir + "\\res\\levels\\" + levelName + "\\" + levelName + ".scene");
            File addinF = new File(newWOGdir + "\\res\\levels\\" + levelName + "\\" + levelName + ".addin.xml");
            File textF = new File(newWOGdir + "\\res\\levels\\" + levelName + "\\" + levelName + ".text.xml");
            saxParser.parse(levelF, defaultHandler);
            defaultHandler.mode = 1;
            saxParser.parse(resrcF, defaultHandler);
            defaultHandler.mode = 2;
            saxParser.parse(sceneF, defaultHandler);
            if (addinF.exists()) {
                defaultHandler.mode = 3;
                saxParser.parse(addinF, defaultHandler);
            } else {
                Main.supremeAddToList(addin, Main.generateBlankAddinObject(levelName));
            }
            if (textF.exists()) {
                defaultHandler.mode = 4;
                saxParser.parse(textF, defaultHandler);
            } else {
                Main.supremeAddToList(text, Main.generateBlankTextObject());
            }
        }
        return new WorldLevel(scene, level, resources, addin, text, version);
    }

    public static _Ball openBall(String ballName, double version) throws ParserConfigurationException, SAXException, IOException {

        /* Make sure a ball from an invalid version isn't being opened (possible because of properties.xml) */
        if (version == 1.3 && oldWOGdir.equals("") ||
            version == 1.5 && newWOGdir.equals("")) {
            return null;
        }

        commonBallData = new ArrayList<>();
        commonBallResrcData = new ArrayList<>();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        BallFileOpener defaultHandler = new BallFileOpener();
        if (version == 1.3) {
            File ballFile = new File(oldWOGdir + "\\res\\balls\\" + ballName + "\\balls.xml.bin");
            File ballFileR = new File(oldWOGdir + "\\res\\balls\\" + ballName + "\\resources.xml.bin");
            BallFileOpener.impossible = null;
            BallFileOpener.mode = 0;
            saxParser.parse(new InputSource(new StringReader(bytesToString(AESBinFormat.decodeFile(ballFile)))), defaultHandler);
            BallFileOpener.mode = 1;
            BallFileOpener.impossible = null;
            saxParser.parse(new InputSource(new StringReader(bytesToString(AESBinFormat.decodeFile(ballFileR)))), defaultHandler);
            return new _Ball(commonBallData, commonBallResrcData);
        } else if (version == 1.5) {
            File ballFile = new File(newWOGdir + "\\res\\balls\\" + ballName + "\\balls.xml");
            File ballFileR = new File(newWOGdir + "\\res\\balls\\" + ballName + "\\resources.xml");
            BallFileOpener.impossible = null;
            BallFileOpener.mode = 0;
            saxParser.parse(ballFile, defaultHandler);
            BallFileOpener.mode = 1;
            BallFileOpener.impossible = null;
            saxParser.parse(ballFileR, defaultHandler);
            return new _Ball(commonBallData, commonBallResrcData);
        } else {
            return null;
        }
    }

    public static ArrayList<EditorObject> openResources(double version) throws ParserConfigurationException, SAXException, IOException {
        commonBallResrcData = new ArrayList<>();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        BallFileOpener defaultHandler = new BallFileOpener();
        BallFileOpener.impossible = null;
        BallFileOpener.mode = 1;
        File ballFile;
        String dir;
        if (version == 1.3) {
            dir = oldWOGdir;
            ballFile = new File(dir + "\\properties\\resources.xml.bin");
            saxParser.parse(new InputSource(new StringReader(bytesToString(AESBinFormat.decodeFile(ballFile)))), defaultHandler);
        } else if (version == 1.5) {
            dir = newWOGdir;
            ballFile = new File(dir + "\\properties\\resources.xml");
            saxParser.parse(ballFile, defaultHandler);
        }
        return commonBallResrcData;
    }

    public static void openParticles(double version) throws ParserConfigurationException, SAXException, IOException {
        commonBallData = new ArrayList<>();
        commonBallResrcData = new ArrayList<>();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        BallFileOpener defaultHandler = new BallFileOpener();
        BallFileOpener.impossible = null;
        BallFileOpener.mode = 0;
        if (version == 1.3 && hasOldWOG) {
            File ballFile = new File(FileManager.getOldWOGdir() + "\\properties\\fx.xml.bin");
            saxParser.parse(new InputSource(new StringReader(bytesToString(AESBinFormat.decodeFile(ballFile)))), defaultHandler);
        } else if (version == 1.5 && hasNewWOG) {
            File ballFile2 = new File(FileManager.getNewWOGdir() + "\\properties\\fx.xml");
            saxParser.parse(ballFile2, defaultHandler);
        }
    }

    public static ArrayList<EditorObject> openText(double version) throws ParserConfigurationException, SAXException, IOException {
        commonBallData = new ArrayList<>();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        BallFileOpener defaultHandler = new BallFileOpener();
        BallFileOpener.impossible = null;
        BallFileOpener.mode = 0;
        if (version == 1.3 && hasOldWOG) {
            File ballFile = new File(FileManager.getOldWOGdir() + "\\properties\\text.xml.bin");
            byte[] bytes = AESBinFormat.decodeFile(ballFile);
            // If the file starts with EF BB BF, strip these three bytes (not sure why it does this)
            if (bytes[0] == -17 && bytes[1] == -69 && bytes[2] == -65) {
                byte[] newBytes = new byte[bytes.length - 3];
                System.arraycopy(bytes, 3, newBytes, 0, bytes.length - 3);
                bytes = newBytes;
            }
            String stringBytes = bytesToString(bytes);
            saxParser.parse(new InputSource(new StringReader(stringBytes)), defaultHandler);
        } else if (version == 1.5 && hasNewWOG) {
            File ballFile2 = new File(FileManager.getNewWOGdir() + "\\properties\\text.xml");
            saxParser.parse(ballFile2, defaultHandler);
        }
        return commonBallData;
    }

    public static void saveProperties() throws IOException {
        StringBuilder export = new StringBuilder("<properties>\n\n<oldWOG filepath=\"" + oldWOGdir + "\"/>\n<newWOG filepath=\"" + newWOGdir + "\"/>\n<gooBallPalette>\n");
        for (int i = 0; i < paletteBalls.size(); i++) {
            export.append("\t<Ball ball=\"").append(paletteBalls.get(i)).append("\" version=\"").append(paletteVersions.get(i)).append("\"/>\n");
        }
        export.append("</gooBallPalette>\n</properties>");

        Files.write(Paths.get(editorLocation + "properties.xml"), Collections.singleton(export.toString()), StandardCharsets.UTF_8);
    }

    public static ArrayList<ArrayList<String>> attributes = new ArrayList<>();
    public static ArrayList<String> objectNames = new ArrayList<>();

    public static void main(String[] args) {
        newWOGdir = "D:\\Steam\\steamapps\\common\\World of Goo\\game";
        for (File ballFile : new File("D:\\Steam\\steamapps\\common\\World of Goo\\game\\res\\balls").listFiles()) {
            if (!ballFile.getName().equals("generic")) {
                try {
                    System.out.println("opening " + ballFile.getName());
                    openBall(ballFile.getName(), 1.5);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        int i = 0;
        for (String objectName : objectNames) {
            System.out.print(objectName + ": {");
            for (String attributeName : attributes.get(i)) {
                System.out.print(attributeName);
                if (attributes.get(i).indexOf(attributeName) != attributes.get(i).size() - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println("}");
            i++;
        }
    }
}