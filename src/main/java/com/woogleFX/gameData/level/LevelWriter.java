package com.woogleFX.gameData.level;

import com.woogleFX.file.aesEncryption.AESBinFormat;
import com.woogleFX.file.FileManager;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.file.fileExport.XMLUtility;

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


    private static String getScene(_Level level) {

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

        for (EditorObject object : level.getSceneObject().getChildren()) switch (object.getType()) {

            case "linearforcefield", "radialforcefield" -> forceFields.add(object);
            case "particles" -> particles.add(object);
            case "SceneLayer" -> sceneLayers.add(object);
            case "buttongroup", "button" -> buttons.add(object);
            case "label" -> labels.add(object);
            case "rectangle", "circle", "compositegeom" -> {
                if (!object.getAttribute("static").booleanValue()) dynamicGeometry.add(object);
                else staticGeometry.add(object);
            }
            case "line" -> staticGeometry.add(object);
            case "hinge", "motor" -> geometryConstraints.add(object);

        }

        StringBuilder scene = new StringBuilder();
        scene.append(XMLUtility.XMLExport(level.getSceneObject()));

        scene.append("\n\t<!-- ForceFields -->\n");
        for (EditorObject object : forceFields) scene.append(XMLUtility.XMLExport(object)).append("\n");

        scene.append("\n\t<!-- Particles -->\n");
        for (EditorObject object : particles) scene.append(XMLUtility.XMLExport(object)).append("\n");

        scene.append("\n\t<!-- SceneLayers -->\n");
        for (EditorObject object : sceneLayers) scene.append(XMLUtility.XMLExport(object)).append("\n");

        scene.append("\n\t<!-- Buttons -->\n");
        for (EditorObject object : buttons) scene.append(XMLUtility.XMLExport(object)).append("\n");

        scene.append("\n\t<!-- Labels -->\n");
        for (EditorObject object : labels) scene.append(XMLUtility.XMLExport(object)).append("\n");

        scene.append("\n\t<!-- Static Geometry -->\n");
        for (EditorObject object : staticGeometry) scene.append(XMLUtility.XMLExport(object)).append("\n");

        scene.append("\n\t<!-- Dynamic Geometry -->\n");
        for (EditorObject object : dynamicGeometry) scene.append(XMLUtility.XMLExport(object)).append("\n");

        scene.append("\n\t<!-- Geometry Constraints -->\n");
        for (EditorObject object : geometryConstraints) scene.append(XMLUtility.XMLExport(object)).append("\n");

        scene.append("</scene>");

        return scene.toString();

    }


    private static String getLevel(_Level _level) {

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

        for (EditorObject object : _level.getLevelObject().getChildren()) switch (object.getType()) {

            case "camera" -> camera.add(object);
            case "music" -> music.add(object);
            case "loopsound" -> loopsound.add(object);
            case "fire" -> fire.add(object);
            case "signpost" -> signposts.add(object);
            case "pipe", "Vertex" -> pipes.add(object);
            case "BallInstance" -> balls.add(object);
            case "Strand" -> arms.add(object);
            case "levelexit", "endoncollision", "endonnogeom",
                    "endonmessage", "targetheight" -> levelExit.add(object);

        }

        StringBuilder level = new StringBuilder();
        level.append(XMLUtility.XMLExport(_level.getLevelObject()));

        level.append("\n\t<!-- Camera -->\n");
        for (EditorObject object : camera) level.append(XMLUtility.XMLExport(object)).append("\n");

        level.append("\n\t<!-- Music -->\n");
        for (EditorObject object : music) level.append(XMLUtility.XMLExport(object)).append("\n");

        level.append("\n\t<!-- Loop Sound -->\n");
        for (EditorObject object : loopsound) level.append(XMLUtility.XMLExport(object)).append("\n");

        level.append("\n\t<!-- Fire -->\n");
        for (EditorObject object : fire) level.append(XMLUtility.XMLExport(object)).append("\n");

        level.append("\n\t<!-- Signposts -->\n");
        for (EditorObject object : signposts) level.append(XMLUtility.XMLExport(object)).append("\n");

        level.append("\n\t<!-- Pipes -->\n");
        for (EditorObject object : pipes) level.append(XMLUtility.XMLExport(object)).append("\n");

        level.append("\n\t<!-- Balls -->\n");
        for (EditorObject object : balls) level.append(XMLUtility.XMLExport(object)).append("\n");

        level.append("\n\t<!-- Arms -->\n");
        for (EditorObject object : arms) level.append(XMLUtility.XMLExport(object)).append("\n");

        level.append("\n\t<!-- Level Exit -->\n");
        for (EditorObject object : levelExit) level.append(XMLUtility.XMLExport(object)).append("\n");

        level.append("\n</level>");

        return level.toString();

    }


    private static LevelInformation getAllLevelInformation(_Level _level) {

        String scene = getScene(_level);

        String level = getLevel(_level);

        EditorObject addinObject = _level.getAddinObject();
        String addin = XMLUtility.fullAddinXMLExport("", addinObject, 0);

        EditorObject textObject = _level.getTextObject();
        String text = XMLUtility.XMLExport(textObject);

        EditorObject resourcesObject = _level.getResrcObject();
        String resrc = XMLUtility.XMLExport(resourcesObject);

        return new LevelInformation(scene, level, resrc, addin, text);

    }


    public static void saveAsXML(_Level level, String outputPathString, GameVersion version,
                                 boolean exportingGoomod, boolean includeAddinInfo) throws IOException {


        LevelInformation levelInformation = getAllLevelInformation(level);

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
            String WoGDir = FileManager.getGameDir(version);
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

        if (version == GameVersion.OLD && !exportingGoomod) {
            AESBinFormat.encodeFile(new File(scenePathText), levelInformation.scene.getBytes());
            AESBinFormat.encodeFile(new File(levelPathText), levelInformation.level.getBytes());
            AESBinFormat.encodeFile(new File(resrcPathText), levelInformation.resrc.getBytes());
        } else {
            Files.write(scenePath, Collections.singleton(levelInformation.scene), StandardCharsets.UTF_8);
            Files.write(levelPath, Collections.singleton(levelInformation.level), StandardCharsets.UTF_8);
            Files.write(resrcPath, Collections.singleton(levelInformation.resrc), StandardCharsets.UTF_8);
        }

        String otherDir = FileManager.getGameDir(version == GameVersion.NEW ? GameVersion.OLD : GameVersion.NEW);
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
