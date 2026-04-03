package com.woogleFX.file;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.stream.Stream;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.SupremeMain;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.objectCreators.ObjectCreator;
import com.woogleFX.engine.gui.alarms.ErrorAlarm;
import com.woogleFX.file.fileImport.EditorObjectXMLReader;
import com.woogleFX.file.fileImport.ObjectGOOParser;
import com.woogleFX.file.fileImport.PropertiesOpener;
import com.woogleFX.engine.fx.PaletteManager;
import com.woogleFX.assets.GameVersion;
import com.worldOfGoo.resrc.ResourceManifest;
import com.worldOfGoo.resrc.Resources;
import com.worldOfGoo.text.strings;
import com.worldOfGoo2.items._2_Item_Collection;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javafx.scene.image.Image;


public class FileManager {

    private static final Logger logger = LoggerFactory.getLogger(FileManager.class);


    private static String oldWOG1dir = "";
    public static void setOldWOG1dir(String oldWOG1dir) {
        FileManager.oldWOG1dir = oldWOG1dir;
    }


    private static String newWOG1dir = "";
    public static void setNewWOG1dir(String newWOG1dir) {
        FileManager.newWOG1dir = newWOG1dir;
    }


    private static String wog2dir = "";
    public static void setWog2dir(String WOG2dir) {
        FileManager.wog2dir = WOG2dir;
    }


    public static String getGameDir(GameVersion version) {
        return switch (version) {
            case VERSION_WOG1_OLD -> oldWOG1dir;
            case VERSION_WOG1_NEW -> newWOG1dir;
            case VERSION_WOG2 -> wog2dir;
        };
    }


    private static Image failedImage;
    public static Image getFailedImage() {
        return failedImage;
    }
    public static void openFailedImage() throws IOException {
        failedImage = openImageFromFilePath(editorLocation + "ObjectIcons/failed.png");
    }


    // Editor location should be the current folder
    private static final String editorLocation;
    static {
        String editorLocation1;
        try {
            editorLocation1 = new File(SupremeMain.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent() + "/";
            if (!Files.exists(Path.of(editorLocation1 + "/src"))) {
                editorLocation1 = new File(SupremeMain.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getParent() + "/";
            }
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
        PropertiesOpener defaultHandler = new PropertiesOpener();
        File properties = new File(editorLocation + "properties.xml");
        if (!properties.exists()) {
            oldWOG1dir = "";
            newWOG1dir = "";
            wog2dir = "";
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


    public static ArrayList<ResourceManifest> openResources(GameVersion version) throws ParserConfigurationException, SAXException, IOException {

        // search EVERY SINGLE FILE for any resources.xml
        ArrayList<ResourceManifest> resourceManifests = new ArrayList<>();
        try (Stream<Path> paths = Files.find(Path.of(FileManager.getGameDir(version)), 24,
                (val, val2) -> val.endsWith("resources.xml") || val.endsWith("resources.xml.bin") || val.endsWith("_resources.xml") || val.endsWith("manifest.resrc"))) {
            paths.forEach(path -> {
                ResourceManifest resourceManifest;
                try {
                    resourceManifest = EditorObjectXMLReader.readEditorObject(
                            version, path.toFile(), ResourceManifest.class);
                } catch (IOException e) {
                    resourceManifest = null;
                } catch (ClassCastException e) {
                    // oops.. was it a Resources instead?
                    // TODO: do this better
                    try {
                        Resources resources = EditorObjectXMLReader.readEditorObject(
                                version, path.toFile(), Resources.class);
                        resourceManifest = ObjectCreator.create(ResourceManifest.class, null, version);
                        resourceManifest.getChildren().add(resources);
                    } catch (IOException e2) {
                        resourceManifest = null;
                    }
                }
                if (resourceManifest == null) {
                    ErrorAlarm.show("failed to parse resource file: " + path);
                } else {
                    resourceManifests.add(resourceManifest);
                }
            });
        }
        return resourceManifests;

    }


    public static ArrayList<EditorObject> openWog2ResourceFile(String path) throws ParserConfigurationException, SAXException, IOException {

        // TODO2: this whole function
        return new ArrayList<>();

    }


    public static strings openText(GameVersion version) throws ParserConfigurationException, SAXException, IOException {
        String suffix = (version == GameVersion.VERSION_WOG1_OLD) ? ".xml.bin" : ".xml";
        File textFile = new File(getGameDir(version) + "/properties/text" + suffix);
        return EditorObjectXMLReader.readEditorObject(version, textFile, strings.class);
    }


    public static ArrayList<EditorObject> openMaterials(GameVersion version) throws ParserConfigurationException, SAXException, IOException {

        String suffix = (version == GameVersion.VERSION_WOG1_OLD) ? ".xml.bin" : ".xml";
        File materialsFile = new File(getGameDir(version) + "/properties/materials" + suffix);
        return new ArrayList<>(); // TODO: parse materials file into an ArrayList?

    }


    public static ArrayList<EditorObject> openItems(GameVersion version) throws IOException {

        ArrayList<EditorObject> items = new ArrayList<>();

        for (File itemFile : new File(wog2dir + "/res/items").listFiles()) {

            if (itemFile.getName().endsWith(".wog2")) {
                items.addAll(ObjectGOOParser.read(_2_Item_Collection.class, Files.readString(itemFile.toPath()), "items").getChildren());
            }

        }

        return items;

    }


    public static void saveProperties() throws IOException {
        StringBuilder export = new StringBuilder("<properties>\n" +
                "\n<oldWOG filepath=\"" + oldWOG1dir + "\"/>" +
                "\n<newWOG filepath=\"" + newWOG1dir + "\"/>" +
                "\n<WOG2 filepath=\"" + wog2dir + "\"/>" +
                "\n<gooBallPalette>\n");
        for (int i = 0; i < PaletteManager.getPaletteBalls().size(); i++) {
            export.append("\t<Ball ball=\"").append(PaletteManager.getPaletteBalls().get(i)).append("\" version=\"").append(PaletteManager.getPaletteVersions().get(i).toString()).append("\"/>\n");
        }
        export.append("</gooBallPalette>\n<recentlyOpened>");
        export.append("</recentlyOpened>\n</properties>");

        Files.write(Paths.get(editorLocation + "properties.xml"), Collections.singleton(export.toString()), StandardCharsets.UTF_8);
    }


    public static FileChooser.ExtensionFilter get2ExtensionFilter() {

        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            return new FileChooser.ExtensionFilter("World of Goo 2 executable", "World Of Goo 2.exe");
        } else if (os.contains("mac")) {
            return new FileChooser.ExtensionFilter("World of Goo 2 executable", "WorldOfGoo2.app");
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            return new FileChooser.ExtensionFilter("World of Goo 2 executable", "WorldOfGoo2");
        } else {
            throw new RuntimeException("Unsupported OS: " + os);
        }

    }

}