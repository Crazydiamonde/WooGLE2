package com.woogleFX.file.resourceManagers;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.engine.gui.alarms.ErrorAlarm;
import com.woogleFX.engine.gui.alarms.LoadingResourcesAlarm;
import com.woogleFX.file.FileManager;
import com.woogleFX.assets.GameVersion;
import com.worldOfGoo.resrc.*;
import com.worldOfGoo.text.strings;
import com.worldOfGoo2.util.BallInstanceHelper;
import com.worldOfGoo2.util.ItemHelper;
import com.worldOfGoo2.util.TerrainHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.*;

/** Stores global resources (those specified in properties/resources.xml). */
public class GlobalResourceManager {

    private static final Logger logger = LoggerFactory.getLogger(GlobalResourceManager.class);


    private static final Map<String, ResourceInterface> oldResources = new HashMap<>();
    public static Map<String, ResourceInterface> getOldResources() {
        return oldResources;
    }


    private static final Map<String, ResourceInterface> newResources = new HashMap<>();
    public static Map<String, ResourceInterface> getNewResources() {
        return newResources;
    }


    private static final Map<String, ResourceInterface> sequelResources = new HashMap<>();
    public static Map<String, ResourceInterface> getSequelResources() {
        return sequelResources;
    }

    private static final Map<String, Sound> sequelMusic = new HashMap<>();
    public static Map<String, Sound> getSequelMusic() {
        return sequelMusic;
    }

    private static final Map<String, EditorObject> sequelAmbience = new HashMap<>();
    public static Map<String, EditorObject> getSequelAmbience() {
        return sequelAmbience;
    }


    private static final ArrayList<String> allFailedResources = new ArrayList<>();


    public static void init() {

        allFailedResources.clear();

        oldResources.clear();
        if (!FileManager.getGameDir(GameVersion.VERSION_WOG1_OLD).isEmpty()) {
            openResources(GameVersion.VERSION_WOG1_OLD);
            openText(GameVersion.VERSION_WOG1_OLD);
            openMaterials(GameVersion.VERSION_WOG1_OLD);
        }

        newResources.clear();
        if (!FileManager.getGameDir(GameVersion.VERSION_WOG1_NEW).isEmpty()) {
            openResources(GameVersion.VERSION_WOG1_NEW);
            openText(GameVersion.VERSION_WOG1_NEW);
            openMaterials(GameVersion.VERSION_WOG1_NEW);
        }

        sequelResources.clear();
        if (!FileManager.getGameDir(GameVersion.VERSION_WOG2).isEmpty()) {
            openResources(GameVersion.VERSION_WOG2);
            openItems();
            try {
                AtlasManager.reloadAtlas();
            } catch (IOException e) {
                logger.error("", e);
            }
            openBallTable();
            new Thread(() -> {
                try {
                    ResourceManager.findTerrainTypes(null, GameVersion.VERSION_WOG2);
                    TerrainHelper.buildImageMap();
                } catch (FileNotFoundException e) {
                    logger.error("", e);
                }
            }).start();
            //openText(GameVersion.VERSION_WOG2);
            //openMaterials(GameVersion.VERSION_WOG2);
        }

        if (!allFailedResources.isEmpty()) {
            StringBuilder fullError = new StringBuilder();
            for (String resource : allFailedResources) {
                fullError.append("\n").append(resource);
            }
            LoadingResourcesAlarm.showInitial(fullError.substring(1));
        }

    }


    private static void openItems() {

        new Thread(() -> {
            for (File itemFile : new File(FileManager.getGameDir(GameVersion.VERSION_WOG2) + "/res/items").listFiles())
                if (itemFile.getName().endsWith(".wog2"))
                    ItemHelper.getItemActualName(itemFile.getName().substring(0, itemFile.getName().length() - 5));
        }).start();

    }
    
    private static void openBallTable() {

        new Thread(() -> {  
            File ballTableFile = new File(FileManager.getGameDir(GameVersion.VERSION_WOG2) + "/fisty/ballTable.ini");
            if (ballTableFile.exists()) {
                try {
                    String ballTable = Files.readString(ballTableFile.toPath());
                    parseBallTable(ballTable);
                } catch (IOException | ParseException e) {
                    ErrorAlarm.show(e);
                }
            }
        }).start();

    }
    
    private static void parseBallTable(String ballTable) throws ParseException {
        String[] lines = ballTable.split("\n");
        Map<Integer, String> typeEnumToTypeMap = new HashMap<>();
        
        int lineNumber = -1;
        for (String line : lines) {
            lineNumber++;
            
            int lineCommentStart = line.indexOf(';');
            if (lineCommentStart != -1)
                line = line.substring(0, lineCommentStart);
            
            if (line.isBlank())
                continue;
            
            int equalsSignPos = line.indexOf('=');
            
            if (equalsSignPos == -1)
                throw new ParseException(null, lineNumber);
            
            String keyStr = line.substring(0, equalsSignPos).trim();
            String value = line.substring(equalsSignPos + 1).trim();
            
            try {
                int key = Integer.parseInt(keyStr);
                typeEnumToTypeMap.put(key, value);
            } catch (NumberFormatException e) {
                throw new ParseException(null, lineNumber);
            }
        }
        
        BallInstanceHelper.setTypeEnumMaps(typeEnumToTypeMap);
    }


    private static void openResources(GameVersion version) {

        Map<String, ResourceInterface> toAddTo = switch (version) {
            case VERSION_WOG1_OLD -> oldResources;
            case VERSION_WOG1_NEW -> newResources;
            case VERSION_WOG2 -> sequelResources;
        };

        ArrayList<ResourceManifest> resourceManifests;
        try {
            resourceManifests = FileManager.openResources(version);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            ErrorAlarm.show(e);
            return;
        }

        SetDefaults currentSetDefaults = null;

        for (ResourceManifest resourceManifest : resourceManifests) {

            Stack<EditorObject> stack = new Stack<>();
            stack.push(resourceManifest);

            while (!stack.empty()) {
                EditorObject resourceObject = stack.pop();
                List<EditorObject> children = resourceObject.getChildren();
                for (int i = 0; i < children.size(); i++)
                    stack.push(children.get(children.size() - i - 1));

                if (resourceObject instanceof SetDefaults setDefaults) {
                    currentSetDefaults = setDefaults;
                    } else if (resourceObject instanceof Resources) {
                    currentSetDefaults = null;
                }

                else if (resourceObject instanceof ResourceInterface resourceInterface) {
                    resourceInterface.setSetDefaults(currentSetDefaults);
                    toAddTo.put(resourceInterface.getAdjustedID(), resourceInterface);
                }

            }

        }
        
        if (version == GameVersion.VERSION_WOG2 && false) {
            try {

                Set<String> globalResourceDirs = new HashSet<>();
                BaseGameResources.loadFileIntoSet(FileManager.getEditorLocation() + "/BaseGameResources/2/GlobalResourceDirs.txt", globalResourceDirs);

                ArrayList<ResourceManifest> resourceManifests1 = FileManager.openResources(GameVersion.VERSION_WOG2);

                for (EditorObject resourceManifest : resourceManifests1) {
                    currentSetDefaults = null;
                    //ArrayList<EditorObject> ambience = FileManager.openWog2ResourceFile("/" + dir);

                    Stack<EditorObject> stack = new Stack<>();
                    stack.add(resourceManifest);

                    while (!stack.empty()) {
                        EditorObject resourceObject = stack.pop();
                        stack.addAll(resourceObject.getChildren());

                        if (resourceObject instanceof SetDefaults setDefaults) {
                            currentSetDefaults = setDefaults;
                        } else if (resourceObject instanceof ResourceInterface resourceInterface) {
                            resourceInterface.setSetDefaults(currentSetDefaults);
                            sequelResources.put(resourceInterface.getAdjustedID(), resourceInterface);
                        } else if (resourceObject instanceof Resources) {
                            currentSetDefaults = null;
                        }
                    }
                }

            } catch (ParserConfigurationException | SAXException | IOException e) {
                ErrorAlarm.show(e);
            }
        }

    }


    private static void openText(GameVersion version) {

        Map<String, ResourceInterface> toAddTo = switch (version) {
            case VERSION_WOG1_OLD -> oldResources;
            case VERSION_WOG1_NEW -> newResources;
            case VERSION_WOG2 -> sequelResources;
        };

        strings textList;
        try {
            textList = FileManager.openText(version);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            ErrorAlarm.show(e);
            return;
        }

        for (EditorObject string : textList.getChildren()) if (string instanceof ResourceInterface resourceInterface) {
            toAddTo.put(string.getAttribute("id").stringValue(), resourceInterface);
        }

    }


    private static void openMaterials(GameVersion version) {

        Map<String, ResourceInterface> toAddTo = version == GameVersion.VERSION_WOG1_OLD ? oldResources : newResources;

        ArrayList<EditorObject> materialList;
        try {
            materialList = FileManager.openMaterials(version);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            ErrorAlarm.show(e);
            return;
        }

        for (EditorObject editorObject : materialList) {
            if (editorObject instanceof material material) {
                toAddTo.put("", (ResourceInterface) material);
            }
        }

    }

}
