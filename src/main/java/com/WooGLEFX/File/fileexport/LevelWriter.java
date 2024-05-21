package com.WooGLEFX.File.fileexport;

import com.WooGLEFX.File.AESBinFormat;
import com.WooGLEFX.File.FileManager;
import com.WooGLEFX.EditorObjects.EditorObject;
import com.WooGLEFX.Structures.GameVersion;
import com.WooGLEFX.Structures.WorldLevel;
import com.WorldOfGoo.Level.*;
import com.WorldOfGoo.Scene.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;

public class LevelWriter {

    private record LevelInformation(String scene, String level, String resrc, String addin, String text) {

    }


    private static String getScene(WorldLevel worldLevel) {

        /*
            - ForceFields
            - Particles
            - SceneLayers
            - Buttons
            - Labels
            - Static Geometry
            - Dynamic Geometry
            - Geometry Constraints
         */

        ArrayList<EditorObject> forceFields = new ArrayList<>();
        ArrayList<EditorObject> particles = new ArrayList<>();
        ArrayList<EditorObject> sceneLayers = new ArrayList<>();
        ArrayList<EditorObject> buttons = new ArrayList<>();
        ArrayList<EditorObject> labels = new ArrayList<>();
        ArrayList<EditorObject> staticGeometry = new ArrayList<>();
        ArrayList<EditorObject> dynamicGeometry = new ArrayList<>();
        ArrayList<EditorObject> geometryConstraints = new ArrayList<>();

        for (EditorObject object : worldLevel.getSceneObject().getChildren()) {
            if (object instanceof Linearforcefield || object instanceof Radialforcefield) {
                forceFields.add(object);
            } else if (object instanceof Particles) {
                particles.add(object);
            } else if (object instanceof SceneLayer) {
                sceneLayers.add(object);
            } else if (object instanceof Buttongroup || object instanceof Button) {
                buttons.add(object);
            } else if (object instanceof Label) {
                labels.add(object);
            } else if (object instanceof Rectangle ||
                    object instanceof Circle ||
                    object instanceof Compositegeom ||
                    object instanceof Line) {
                if (!(object instanceof Line) && !object.getAttribute("static").booleanValue()) {
                    dynamicGeometry.add(object);
                } else {
                    staticGeometry.add(object);
                }
            } else if (object instanceof Hinge || object instanceof Motor) {
                geometryConstraints.add(object);
            }
        }

        String scene = XMLUtility.recursiveXMLexport("", worldLevel.getSceneObject(), 0, false);
        scene += "\n\t<!-- ForceFields -->\n";
        for (EditorObject object : forceFields) {
            scene = XMLUtility.recursiveXMLexport(scene, object, 1, true) + "\n";
        }
        scene += "\n\t<!-- Particles -->\n";
        for (EditorObject object : particles) {
            scene = XMLUtility.recursiveXMLexport(scene, object, 1, true) + "\n";
        }
        scene += "\n\t<!-- SceneLayers -->\n";
        for (EditorObject object : sceneLayers) {
            scene = XMLUtility.recursiveXMLexport(scene, object, 1, true) + "\n";
        }
        scene += "\n\t<!-- Buttons -->\n";
        for (EditorObject object : buttons) {
            scene = XMLUtility.recursiveXMLexport(scene, object, 1, true) + "\n";
        }
        scene += "\n\t<!-- Labels -->\n";
        for (EditorObject object : labels) {
            scene = XMLUtility.recursiveXMLexport(scene, object, 1, true) + "\n";
        }
        scene += "\n\t<!-- Static Geometry -->\n";
        for (EditorObject object : staticGeometry) {
            scene = XMLUtility.recursiveXMLexport(scene, object, 1, true) + "\n";
        }
        scene += "\n\t<!-- Dynamic Geometry -->\n";
        for (EditorObject object : dynamicGeometry) {
            scene = XMLUtility.recursiveXMLexport(scene, object, 1, true) + "\n";
        }
        scene += "\n\t<!-- Geometry Constraints -->\n";
        for (EditorObject object : geometryConstraints) {
            scene = XMLUtility.recursiveXMLexport(scene, object, 1, true) + "\n";
        }
        scene += "</scene>";

        return scene;

    }


    private static LevelInformation getAllLevelInformation(WorldLevel worldLevel) {

        /*
            - Camera
            - Music
            - Fire
            - Signposts
            - Pipes
            - Balls
            - Arms
            - Level Exit
         */

        ArrayList<EditorObject> camera = new ArrayList<>();
        ArrayList<EditorObject> music = new ArrayList<>();
        ArrayList<EditorObject> loopsound = new ArrayList<>();
        ArrayList<EditorObject> fire = new ArrayList<>();
        ArrayList<EditorObject> signposts = new ArrayList<>();
        ArrayList<EditorObject> pipes = new ArrayList<>();
        ArrayList<EditorObject> balls = new ArrayList<>();
        ArrayList<EditorObject> arms = new ArrayList<>();
        ArrayList<EditorObject> levelExit = new ArrayList<>();

        for (EditorObject object : worldLevel.getLevelObject().getChildren()) {
            if (object instanceof Camera ||
                    object instanceof Poi) {
                camera.add(object);
            } else if (object instanceof Music) {
                music.add(object);
            } else if (object instanceof Loopsound) {
                loopsound.add(object);
            } else if (object instanceof Fire) {
                fire.add(object);
            } else if (object instanceof Signpost) {
                signposts.add(object);
            } else if (object instanceof Pipe ||
                    object instanceof Vertex) {
                pipes.add(object);
            } else if (object instanceof BallInstance) {
                balls.add(object);
            } else if (object instanceof Strand) {
                arms.add(object);
            } else if (object instanceof Levelexit ||
                    object instanceof Endoncollision ||
                    object instanceof Endonnogeom ||
                    object instanceof Endonmessage ||
                    object instanceof Targetheight) {
                levelExit.add(object);
            }
        }

        String level = XMLUtility.recursiveXMLexport("", worldLevel.getLevelObject(), 0, false);
        level += "\n\t<!-- Camera -->\n";
        for (EditorObject object : camera) {
            level = XMLUtility.recursiveXMLexport(level, object, 1, true) + "\n";
        }
        level += "\n\t<!-- Music -->\n";
        for (EditorObject object : music) {
            level = XMLUtility.recursiveXMLexport(level, object, 1, true) + "\n";
        }
        if (!loopsound.isEmpty()) {
            level += "\n\t<!-- Loop Sound -->\n";
            for (EditorObject object : loopsound) {
                level = XMLUtility.recursiveXMLexport(level, object, 1, true) + "\n";
            }
        }
        level += "\n\t<!-- Fire -->\n";
        for (EditorObject object : fire) {
            level = XMLUtility.recursiveXMLexport(level, object, 1, true) + "\n";
        }
        level += "\n\t<!-- Signposts -->\n";
        for (EditorObject object : signposts) {
            level = XMLUtility.recursiveXMLexport(level, object, 1, true) + "\n";
        }
        level += "\n\t<!-- Pipes -->\n";
        for (EditorObject object : pipes) {
            level = XMLUtility.recursiveXMLexport(level, object, 1, true) + "\n";
        }
        level += "\n\t<!-- Balls -->\n";
        for (EditorObject object : balls) {
            level = XMLUtility.recursiveXMLexport(level, object, 1, true) + "\n";
        }
        level += "\n\t<!-- Arms -->\n";
        for (EditorObject object : arms) {
            level = XMLUtility.recursiveXMLexport(level, object, 1, true) + "\n";
        }
        level += "\n\t<!-- Level Exit -->\n";
        for (EditorObject object : levelExit) {
            level = XMLUtility.recursiveXMLexport(level, object, 1, true) + "\n";
        }
        level += "\n</level>";

        String scene = getScene(worldLevel);

        EditorObject addinObject = worldLevel.getAddinObject();
        String addin = XMLUtility.fullAddinXMLExport("", addinObject, 0);

        EditorObject textObject = worldLevel.getTextObject();
        String text = XMLUtility.recursiveXMLexport("", textObject, 0, true);

        EditorObject resourcesObject = worldLevel.getResourcesObject();
        String resrc = XMLUtility.recursiveXMLexport("", resourcesObject, 0, true);


        return new LevelInformation(scene, level, resrc, addin, text);


    }


    public static void saveAsXML(WorldLevel worldLevel, String outputPathString, GameVersion version,
                                 boolean exportingGoomod, boolean includeAddinInfo) throws IOException {


        LevelInformation levelInformation = getAllLevelInformation(worldLevel);

        String levelName = outputPathString.substring(outputPathString.lastIndexOf("\\") + 1);

        String scenePathText = outputPathString + "\\" + levelName + ".scene";
        String levelPathText = outputPathString + "\\" + levelName + ".level";
        String resrcPathText = outputPathString + "\\" + levelName + ".resrc";

        if (exportingGoomod) {
            //If exporting to goomod, add .xml to the end of each path.
            scenePathText += ".xml";
            levelPathText += ".xml";
            resrcPathText += ".xml";
        } else if (version == GameVersion.OLD) {
            //If not exporting and on the older version, add .bin to the end of each path.
            scenePathText += ".bin";
            levelPathText += ".bin";
            resrcPathText += ".bin";
        }

        String addinPathText = "addin.xml";
        String textPathText = "text.xml";

        if (!exportingGoomod) {
            addinPathText = outputPathString + "\\" + levelName + "." + addinPathText;
            textPathText = outputPathString + "\\" + levelName + "." + textPathText;
        } else {
            String WoGDir = version == GameVersion.OLD ? FileManager.getOldWOGdir() : FileManager.getNewWOGdir();
            String initialPath = WoGDir + "\\res\\levels\\" + levelName + "\\goomod";
            addinPathText = initialPath + "\\" + addinPathText;
            textPathText = initialPath + "\\" + textPathText;
        }

        Path outputPath = Path.of(outputPathString);

        Path scenePath = Path.of(scenePathText);
        Path levelPath = Path.of(levelPathText);
        Path resrcPath = Path.of(resrcPathText);
        Path addinPath = Path.of(addinPathText);
        Path textPath = Path.of(textPathText);

        if (!Files.exists(outputPath)) Files.createDirectories(outputPath);

        if (!Files.exists(scenePath)) Files.createFile(scenePath);
        if (!Files.exists(levelPath)) Files.createFile(levelPath);
        if (!Files.exists(resrcPath)) Files.createFile(resrcPath);
        if (!Files.exists(addinPath)) Files.createFile(addinPath);
        if (!Files.exists(textPath)) Files.createFile(textPath);

        String otherDir;

        if (version == GameVersion.OLD) {
            otherDir = FileManager.getNewWOGdir();
            AESBinFormat.encodeFile(new File(scenePathText), levelInformation.scene.getBytes());
            AESBinFormat.encodeFile(new File(levelPathText), levelInformation.level.getBytes());
            AESBinFormat.encodeFile(new File(resrcPathText), levelInformation.resrc.getBytes());
        } else {
            otherDir = FileManager.getOldWOGdir();
            Files.write(scenePath, Collections.singleton(levelInformation.scene), StandardCharsets.UTF_8);
            Files.write(levelPath, Collections.singleton(levelInformation.level), StandardCharsets.UTF_8);
            Files.write(resrcPath, Collections.singleton(levelInformation.resrc), StandardCharsets.UTF_8);
        }

        if (!exportingGoomod && Files.exists(Path.of(otherDir + "\\res\\levels\\" + levelName))) {
            File[] images = new File(otherDir + "\\res\\levels\\" + levelName).listFiles();
            if (images != null) for (File imageFile : images) {
                /* Make sure that we're not saving a "goomod" folder or something */
                if (imageFile.getName().contains(".png") || imageFile.getName().contains(".ogg")) {
                    if (imageFile.getPath().substring(imageFile.getPath().lastIndexOf(".")).equals(".png") ||
                            imageFile.getPath().substring(imageFile.getPath().lastIndexOf(".")).equals(".ogg")) {
                        if (!Files.exists(Path.of(outputPath + "\\" + imageFile.getName()))) {
                            Files.copy(imageFile.toPath(), Path.of(outputPath + "\\" + imageFile.getName()));
                        }
                    }
                }
            }
        }

        if (includeAddinInfo) {
            Files.write(addinPath, Collections.singleton(levelInformation.addin), StandardCharsets.UTF_8);
        }
        Files.write(textPath, Collections.singleton(levelInformation.text), StandardCharsets.UTF_8);
    }

}
