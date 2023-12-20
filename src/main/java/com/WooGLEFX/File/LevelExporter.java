package com.WooGLEFX.File;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import javax.imageio.ImageIO;

import com.WooGLEFX.EditorObjects._Ball;
import com.WooGLEFX.GUI.Alarms;
import com.WooGLEFX.Structures.EditorAttribute;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.WorldLevel;
import com.WorldOfGoo.Addin.AddinLevelOCD;
import com.WorldOfGoo.Level.BallInstance;
import com.WorldOfGoo.Level.Camera;
import com.WorldOfGoo.Level.Endoncollision;
import com.WorldOfGoo.Level.Endonmessage;
import com.WorldOfGoo.Level.Endonnogeom;
import com.WorldOfGoo.Level.Fire;
import com.WorldOfGoo.Level.Level;
import com.WorldOfGoo.Level.Levelexit;
import com.WorldOfGoo.Level.Loopsound;
import com.WorldOfGoo.Level.Music;
import com.WorldOfGoo.Level.Pipe;
import com.WorldOfGoo.Level.Poi;
import com.WorldOfGoo.Level.Signpost;
import com.WorldOfGoo.Level.Strand;
import com.WorldOfGoo.Level.Targetheight;
import com.WorldOfGoo.Level.Vertex;
import com.WorldOfGoo.Resrc.ResourceManifest;
import com.WorldOfGoo.Resrc.Resources;
import com.WorldOfGoo.Resrc.ResrcImage;
import com.WorldOfGoo.Resrc.SetDefaults;
import com.WorldOfGoo.Resrc.Sound;
import com.WorldOfGoo.Scene.Button;
import com.WorldOfGoo.Scene.Buttongroup;
import com.WorldOfGoo.Scene.Circle;
import com.WorldOfGoo.Scene.Compositegeom;
import com.WorldOfGoo.Scene.Hinge;
import com.WorldOfGoo.Scene.Label;
import com.WorldOfGoo.Scene.Line;
import com.WorldOfGoo.Scene.Linearforcefield;
import com.WorldOfGoo.Scene.Motor;
import com.WorldOfGoo.Scene.Particles;
import com.WorldOfGoo.Scene.Radialforcefield;
import com.WorldOfGoo.Scene.Rectangle;
import com.WorldOfGoo.Scene.Scene;
import com.WorldOfGoo.Scene.SceneLayer;
import com.WorldOfGoo.Text.TextStrings;

import javafx.embed.swing.SwingFXUtils;

public class LevelExporter {

    public static boolean ding = false;

    public static String recursiveXMLexport(String export, EditorObject object, int spaces, boolean children) {
        return recursiveXMLexport(export, object, spaces, children, "", "");
    }

    public static String recursiveXMLexport(String export, EditorObject object, int spaces, boolean children, String defaultsPath, String defaultsPrefix) {
        export += "\t".repeat(spaces) + "<" + object.getRealName() + " ";
        StringBuilder exportBuilder = new StringBuilder(export);
        for (EditorAttribute attribute : object.getAttributes()) {
            String attributeName = attribute.getName();
            if (object instanceof Sound || object instanceof ResrcImage) {
                if (attributeName.equals("id") || attributeName.equals("path")) {
                    continue;
                }
                if (attributeName.equals("REALid")) {
                    attributeName = "id";
                }
                if (attributeName.equals("REALpath")) {
                    attributeName = "path";
                }
            }
            if (!attribute.getValue().equals("") || attribute.getRequiredInFile()) {
                if (ding && (attributeName.equals("up") || attributeName.equals("over") || attributeName.equals("disabled"))) {
                    exportBuilder.append(attributeName).append("=\"\" ");
                } else {
                    if (object instanceof Sound || object instanceof ResrcImage) {
                        // Remove prefixes from id and path on save
                        String attributeValue = attribute.getValue();
                        if (attributeName.equals("id")) {
                            // Remove defaultsPrefix from id
                            if (attributeValue.startsWith(defaultsPrefix)) {
                                attributeValue = attributeValue.substring(defaultsPrefix.length());
                            }
                        }
                        if (attributeName.equals("path")) {
                            // Remove defaultsPath from path
                            if (attributeValue.startsWith(defaultsPath)) {
                                attributeValue = attributeValue.substring(defaultsPath.length());
                            }
                        }
                        exportBuilder.append(attributeName).append("=\"").append(attributeValue).append("\" ");
                    } else {
                        if (attribute.getValue().equals("") && attribute.getRequiredInFile()) {
                            exportBuilder.append(attributeName).append("=\"").append(attribute.getDefaultValue()).append("\" ");
                        } else {
                            exportBuilder.append(attributeName).append("=\"").append(attribute.getValue()).append("\" ");
                        }
                    }
                }
            }
        }
        export = exportBuilder.toString();
        export = export.substring(0, export.length() - 1);

        if (object.getChildren().size() > 0 || object instanceof Levelexit || object instanceof Scene || object instanceof Level || object instanceof ResourceManifest || object instanceof Resources || object instanceof TextStrings) {
            export += " >\n";
            if (children) {
                for (EditorObject child : object.getChildren()) {
                    String curDefaultsPath = "";
                    String curDefaultsPrefix = "";
                    if(child instanceof SetDefaults) {
                        curDefaultsPath = child.getAttribute("path");
                        curDefaultsPrefix = child.getAttribute("idprefix");
                    }
                    export = recursiveXMLexport(export, child, spaces + 1, true, curDefaultsPath, curDefaultsPrefix) + "\n";
                }
                export += "\t".repeat(spaces) + "</" + object.getRealName() + ">";
            }
        } else {
            export += " />";
        }
        return export;
    }

    public static String fullAddinXMLExport(String export, EditorObject object, int spaces) {
        if (object instanceof AddinLevelOCD) {
            if (object.getAttribute("type").equals("") && object.getAttribute("value").equals("")) {
                export += "\t".repeat(spaces) + "<ocd />";
            } else {
                export += "\t".repeat(spaces) + "<ocd>" + object.getAttribute("type") + "," + object.getAttribute("value") + "</ocd>";
            }
        } else if (object.getAttributes().length == 1 && object.getChildren().size() == 0) {
            if (object.getAttributes()[0].getValue().equals("")) {
                export += "\t".repeat(spaces) + "<" + object.getRealName() + " />";
            } else {
                export += "\t".repeat(spaces) + "<" + object.getRealName() + ">" + object.getAttributes()[0].getValue() + "</" + object.getRealName() + ">";
            }
        } else {
            export += "\t".repeat(spaces) + "<" + object.getRealName() + " ";
            StringBuilder exportBuilder = new StringBuilder(export);
            for (EditorAttribute attribute : object.getAttributes()) {
                if (!attribute.getValue().equals("") || attribute.getRequiredInFile()) {
                    if (ding && (attribute.getName().equals("up") || attribute.getName().equals("over") || attribute.getName().equals("disabled"))) {
                        exportBuilder.append(attribute.getName()).append("=\"\" ");
                    } else {
                        exportBuilder.append(attribute.getName()).append("=\"").append(attribute.getValue()).append("\" ");
                    }
                }
            }
            export = exportBuilder.toString();
            export = export.substring(0, export.length() - 1);

            if (object.getChildren().size() > 0) {
                export += ">\n";
                for (EditorObject child : object.getChildren()) {
                    export = fullAddinXMLExport(export, child, spaces + 1) + "\n";
                }
                export += "\t".repeat(spaces) + "</" + object.getRealName() + ">";
            } else {
                export += " />";
            }
        }
        return export;
    }

    public static void saveAsXML(WorldLevel worldLevel, String outputPathString, double version, boolean chumbusMode, boolean includeAddinInfo) throws IOException {

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
            } else if (object instanceof Rectangle || object instanceof Circle || object instanceof Compositegeom || object instanceof Line) {
                if (!(object instanceof Line) && object.getAttribute("static").equals("false")) {
                    dynamicGeometry.add(object);
                } else {
                    staticGeometry.add(object);
                }
            } else if (object instanceof Hinge || object instanceof Motor) {
                geometryConstraints.add(object);
            }
        }

        String scene = recursiveXMLexport("", worldLevel.getSceneObject(), 0, false);
        scene += "\n\t<!-- ForceFields -->\n";
        for (EditorObject object : forceFields) {
            scene = recursiveXMLexport(scene, object, 1, true) + "\n";
        }
        scene += "\n\t<!-- Particles -->\n";
        for (EditorObject object : particles) {
            scene = recursiveXMLexport(scene, object, 1, true) + "\n";
        }
        scene += "\n\t<!-- SceneLayers -->\n";
        for (EditorObject object : sceneLayers) {
            scene = recursiveXMLexport(scene, object, 1, true) + "\n";
        }
        scene += "\n\t<!-- Buttons -->\n";
        for (EditorObject object : buttons) {
            scene = recursiveXMLexport(scene, object, 1, true) + "\n";
        }
        scene += "\n\t<!-- Labels -->\n";
        for (EditorObject object : labels) {
            scene = recursiveXMLexport(scene, object, 1, true) + "\n";
        }
        scene += "\n\t<!-- Static Geometry -->\n";
        for (EditorObject object : staticGeometry) {
            scene = recursiveXMLexport(scene, object, 1, true) + "\n";
        }
        scene += "\n\t<!-- Dynamic Geometry -->\n";
        for (EditorObject object : dynamicGeometry) {
            scene = recursiveXMLexport(scene, object, 1, true) + "\n";
        }
        scene += "\n\t<!-- Geometry Constraints -->\n";
        for (EditorObject object : geometryConstraints) {
            scene = recursiveXMLexport(scene, object, 1, true) + "\n";
        }
        scene += "</scene>";

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
            if (object instanceof Camera || object instanceof Poi) {
                camera.add(object);
            } else if (object instanceof Music) {
                music.add(object);
            } else if (object instanceof Loopsound) {
                loopsound.add(object);
            } else if (object instanceof Fire) {
                fire.add(object);
            } else if (object instanceof Signpost) {
                signposts.add(object);
            } else if (object instanceof Pipe || object instanceof Vertex) {
                pipes.add(object);
            } else if (object instanceof BallInstance) {
                balls.add(object);
            } else if (object instanceof Strand) {
                arms.add(object);
            } else if (object instanceof Levelexit || object instanceof Endoncollision || object instanceof Endonnogeom || object instanceof Endonmessage || object instanceof Targetheight) {
                levelExit.add(object);
            }
        }

        String level = recursiveXMLexport("", worldLevel.getLevelObject(), 0, false);
        level += "\n\t<!-- Camera -->\n";
        for (EditorObject object : camera) {
            level = recursiveXMLexport(level, object, 1, true) + "\n";
        }
        level += "\n\t<!-- Music -->\n";
        for (EditorObject object : music) {
            level = recursiveXMLexport(level, object, 1, true) + "\n";
        }
        if (loopsound.size() > 0) {
            level += "\n\t<!-- Loop Sound -->\n";
            for (EditorObject object : loopsound) {
                level = recursiveXMLexport(level, object, 1, true) + "\n";
            }
        }
        level += "\n\t<!-- Fire -->\n";
        for (EditorObject object : fire) {
            level = recursiveXMLexport(level, object, 1, true) + "\n";
        }
        level += "\n\t<!-- Signposts -->\n";
        for (EditorObject object : signposts) {
            level = recursiveXMLexport(level, object, 1, true) + "\n";
        }
        level += "\n\t<!-- Pipes -->\n";
        for (EditorObject object : pipes) {
            level = recursiveXMLexport(level, object, 1, true) + "\n";
        }
        level += "\n\t<!-- Balls -->\n";
        for (EditorObject object : balls) {
            level = recursiveXMLexport(level, object, 1, true) + "\n";
        }
        level += "\n\t<!-- Arms -->\n";
        for (EditorObject object : arms) {
            level = recursiveXMLexport(level, object, 1, true) + "\n";
        }
        level += "\n\t<!-- Level Exit -->\n";
        for (EditorObject object : levelExit) {
            level = recursiveXMLexport(level, object, 1, true) + "\n";
        }
        level += "\n</level>";

        String text = recursiveXMLexport("", worldLevel.getTextObject(), 0, true);
        String levelName = outputPathString.substring(outputPathString.lastIndexOf("\\") + 1);

        String scenePathText = outputPathString + "\\" + levelName + ".scene";
        String levelPathText = outputPathString + "\\" + levelName + ".level";
        String resrcPathText = outputPathString + "\\" + levelName + ".resrc";

        if (chumbusMode) {
            //If exporting to goomod, add .xml to the end of each path.
            scenePathText += ".xml";
            levelPathText += ".xml";
            resrcPathText += ".xml";
        } else if (version == 1.3) {
            //If not exporting and on the older version, add .xml.bin to the end of each path.
            scenePathText += ".bin";
            levelPathText += ".bin";
            resrcPathText += ".bin";
        }

        String addinPathText = "addin.xml";
        String textPathText = "text.xml";

        if (!chumbusMode) {
            addinPathText = outputPathString + "\\" + levelName + "." + addinPathText;
            textPathText = outputPathString + "\\" + levelName + "." + textPathText;
        } else {
            String WoGDir = version == 1.3 ? FileManager.getOldWOGdir() : FileManager.getNewWOGdir();
            String initialPath = WoGDir + "\\res\\levels\\" + levelName + "\\goomod";
            addinPathText = initialPath + "\\" + addinPathText;
            textPathText = initialPath + "\\" + textPathText;
        }

        Path scenePath = Path.of(scenePathText);
        Path levelPath = Path.of(levelPathText);
        Path resrcPath = Path.of(resrcPathText);
        Path addinPath = Path.of(addinPathText);
        Path textPath = Path.of(textPathText);
        Path outputPath = Path.of(outputPathString);

        if (!Files.exists(outputPath)) {
            Files.createDirectories(outputPath);
        }
        if (!Files.exists(scenePath)) {
            Files.createFile(scenePath);
        }
        if (!Files.exists(levelPath)) {
            Files.createFile(levelPath);
        }
        if (!Files.exists(resrcPath)) {
            Files.createFile(resrcPath);
        }
        if (!Files.exists(addinPath)) {
            Files.createFile(addinPath);
        }
        if (!Files.exists(textPath)) {
            Files.createFile(textPath);
        }
        if (version == 1.3) {
            AESBinFormat.encodeFile(new File(scenePathText), scene.getBytes());
            AESBinFormat.encodeFile(new File(levelPathText), level.getBytes());
            AESBinFormat.encodeFile(new File(resrcPathText), recursiveXMLexport("", worldLevel.getResourcesObject(), 0, true).getBytes());
            if (!chumbusMode && Files.exists(Path.of(FileManager.getNewWOGdir() + "\\res\\levels\\" + levelName))) {
                File[] images = new File(FileManager.getNewWOGdir() + "\\res\\levels\\" + levelName).listFiles();
                if (images != null) {
                    for (File imageFile : images) {
                        /* Make sure that we're not saving a "goomod" folder or something */
                        if (imageFile.getName().contains(".png") || imageFile.getName().contains(".ogg")) {
                            if (imageFile.getPath().substring(imageFile.getPath().lastIndexOf(".")).equals(".png") || imageFile.getPath().substring(imageFile.getPath().lastIndexOf(".")).equals(".ogg")) {
                                if (!Files.exists(Path.of(outputPath + "\\" + imageFile.getName()))) {
                                    Files.copy(imageFile.toPath(), Path.of(outputPath + "\\" + imageFile.getName()));
                                }
                            }
                        }
                    }
                }
            }
        }
        if (version == 1.5) {
            Files.write(scenePath, Collections.singleton(scene), StandardCharsets.UTF_8);
            Files.write(levelPath, Collections.singleton(level), StandardCharsets.UTF_8);
            Files.write(resrcPath, Collections.singleton(recursiveXMLexport("", worldLevel.getResourcesObject(), 0, true)), StandardCharsets.UTF_8);
            if (!chumbusMode && Files.exists(Path.of(FileManager.getOldWOGdir() + "\\res\\levels\\" + levelName))) {
                File[] images = new File(FileManager.getOldWOGdir() + "\\res\\levels\\" + levelName).listFiles();
                if (images != null) {
                    for (File imageFile : images) {
                        /* Make sure that we're not saving a "goomod" folder or something */
                        if (imageFile.getName().contains(".png") || imageFile.getName().contains(".ogg")) {
                            if (imageFile.getPath().lastIndexOf(".") != -1 && (imageFile.getPath().substring(imageFile.getPath().lastIndexOf(".")).equals(".png") || imageFile.getPath().substring(imageFile.getPath().lastIndexOf(".")).equals(".ogg"))) {
                                if (!Files.exists(Path.of(outputPath + "\\" + imageFile.getName()))) {
                                    Files.copy(imageFile.toPath(), Path.of(outputPath + "\\" + imageFile.getName()));
                                }
                            }
                        }
                    }
                }
            }
        }
        if (includeAddinInfo) {
            Files.write(addinPath, Collections.singleton(fullAddinXMLExport("", worldLevel.getAddinObject(), 0)), StandardCharsets.UTF_8);
        }
        Files.write(textPath, Collections.singleton(text), StandardCharsets.UTF_8);
    }

    public static void exportBallAsXML(_Ball ball, String outputPathString, double version, boolean exportingInGoomod) throws IOException {
        /* General Parts */
        /* Walking Animations */
        /* Attached Animations */
        /* Dragging Animations */
        /* Climbing Animations */
        /* Tank State Animations */
        /* Sound FX */

        //TODO add XML comments to files

        String ballXML = recursiveXMLexport("", ball.objects.get(0), 0, true);
        // System.out.println(ballXML);

        String resrcXML = recursiveXMLexport("", ball.resources.get(0), 0, true);
        // System.out.println(resrcXML);

        String name = ball.objects.get(0).getAttribute("name");

        String ballsPathText = outputPathString + "\\balls.xml";
        String resourcesPathText = outputPathString + "\\resources.xml";

        if (!exportingInGoomod && version == 1.3) {
            ballsPathText += ".bin";
            resourcesPathText += ".bin";
        }

        if (exportingInGoomod) {
            ballsPathText += ".xml";
            resourcesPathText += ".xml";
        }

        Path ballsPath = Path.of(ballsPathText);
        Path resourcesPath = Path.of(resourcesPathText);
        Path outputPath = Path.of(outputPathString);

        if (!Files.exists(outputPath)) {
            Files.createDirectories(outputPath);
        }
        if (!Files.exists(ballsPath)) {
            Files.createFile(ballsPath);
        }
        if (!Files.exists(resourcesPath)) {
            Files.createFile(resourcesPath);
        }

        if (version == 1.3) {
            AESBinFormat.encodeFile(new File(ballsPathText), ballXML.getBytes());
            AESBinFormat.encodeFile(new File(resourcesPathText), resrcXML.getBytes());
            if (!exportingInGoomod) {
                if (Files.exists(Path.of(FileManager.getNewWOGdir() + "\\res\\balls\\" + name))) {
                    File[] images = new File(FileManager.getNewWOGdir() + "\\res\\balls\\" + name).listFiles();
                    if (images != null) {
                        for (File imageFile : images) {
                            if (imageFile.getPath().substring(imageFile.getPath().lastIndexOf(".")).equals(".png") || imageFile.getPath().substring(imageFile.getPath().lastIndexOf(".")).equals(".ogg")) {
                                if (!Files.exists(Path.of(outputPath + "\\" + imageFile.getName()))) {
                                    Files.copy(imageFile.toPath(), Path.of(outputPath + "\\" + imageFile.getName()));
                                }
                            }
                        }
                    }
                }
            }
        }
        if (version == 1.5) {
            Files.write(ballsPath, Collections.singleton(ballXML), StandardCharsets.UTF_8);
            Files.write(resourcesPath, Collections.singleton(resrcXML), StandardCharsets.UTF_8);
            if (!exportingInGoomod) {
                if (Files.exists(Path.of(FileManager.getOldWOGdir() + "\\res\\balls\\" + name))) {
                    File[] images = new File(FileManager.getOldWOGdir() + "\\res\\balls\\" + name).listFiles();
                    if (images != null) {
                        for (File imageFile : images) {
                            if (imageFile.getPath().substring(imageFile.getPath().lastIndexOf(".")).equals(".png") || imageFile.getPath().substring(imageFile.getPath().lastIndexOf(".")).equals(".ogg")) {
                                if (!Files.exists(Path.of(outputPath + "\\" + imageFile.getName()))) {
                                    Files.copy(imageFile.toPath(), Path.of(outputPath + "\\" + imageFile.getName()));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static void exportGoomod(File file, WorldLevel level, ArrayList<_Ball> balls, boolean includeAddinInfo) {
        String start = level.getVersion() == 1.3 ? FileManager.getOldWOGdir() : FileManager.getNewWOGdir();
        // First things first, delete the goomod folder if it exists
        if (Files.exists(Path.of(start + "\\res\\levels\\" + level.getLevelName() + "\\goomod"))) {
            try {
                Files.walk(Path.of(start + "\\res\\levels\\" + level.getLevelName() + "\\goomod"))
                    .sorted(java.util.Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
            } catch (Exception e) {
                Alarms.errorMessage(e);
            }
        }
        // Delete the goomod zip if it exists
        if (Files.exists(Path.of(start + "\\res\\levels\\" + level.getLevelName() + "\\goomod.zip"))) {
            try {
                Files.delete(Path.of(start + "\\res\\levels\\" + level.getLevelName() + "\\goomod.zip"));
            } catch (Exception e) {
                Alarms.errorMessage(e);
            }
        }
        try {
            saveAsXML(level, start + "\\res\\levels\\" + level.getLevelName() + "\\goomod\\compile\\res\\levels\\" + level.getLevelName(), level.getVersion(), true, includeAddinInfo);
        } catch (Exception e) {
            Alarms.errorMessage(e);
        }
        for (EditorObject resource : level.getResources()) {
            if (resource instanceof ResrcImage) {
                if (BaseGameResources.containsImage(resource.getAttribute("path"))) {
                    // Skip if base game image
                    continue;
                }
                if (!Files.exists(Path.of(start + "\\res\\levels\\" + level.getLevelName() + "\\goomod\\override\\" + resource.getAttribute("REALpath").substring(0, resource.getAttribute("REALpath").lastIndexOf("/"))))) {
                    try {
                        Files.createDirectories(Path.of(start + "\\res\\levels\\" + level.getLevelName() + "\\goomod\\override\\" + resource.getAttribute("REALpath").substring(0, resource.getAttribute("REALpath").lastIndexOf("/"))));
                    } catch (Exception e) {
                        Alarms.errorMessage(e);
                    }
                }
                try {
                    BufferedImage oldImage = SwingFXUtils.fromFXImage(GlobalResourceManager.getImage(resource.getAttribute("REALid"), level.getVersion()), null);
                    File newImageFile = new File(start + "\\res\\levels\\" + level.getLevelName() + "\\goomod\\override\\" + resource.getAttribute("REALpath") + ".png");
                    ImageIO.write(oldImage, "png", newImageFile);
                } catch (Exception e) {
                    Alarms.errorMessage(e);
                }
            } else if (resource instanceof Sound) {
                if (BaseGameResources.containsSound(resource.getAttribute("path"))) {
                    // Skip if base game sound
                    continue;
                }
                if (!Files.exists(Path.of(start + "\\res\\levels\\" + level.getLevelName() + "\\goomod\\override\\" + resource.getAttribute("REALpath").substring(0, resource.getAttribute("REALpath").lastIndexOf("/"))))) {
                    try {
                        Files.createDirectories(Path.of(start + "\\res\\levels\\" + level.getLevelName() + "\\goomod\\override\\" + resource.getAttribute("REALpath").substring(0, resource.getAttribute("REALpath").lastIndexOf("/"))));
                    } catch (Exception e) {
                        Alarms.errorMessage(e);
                    }
                }
                try {
                    Files.copy(
                        Path.of(start + "\\" + resource.getAttribute("REALpath") + ".ogg"),
                        Path.of(start + "\\res\\levels\\" + level.getLevelName() + "\\goomod\\override\\" + resource.getAttribute("REALpath") + ".ogg"),
                        StandardCopyOption.REPLACE_EXISTING
                    );
                } catch (Exception e) {
                    Alarms.errorMessage(e);
                }
            }
        }

        // Keep track of exported balls
        HashSet<String> exportedBalls = new HashSet<>();
        for (_Ball ball : balls) {
            try {
                String ballName = ball.getObjects().get(0).getAttribute("name");
                if (exportedBalls.contains(ballName)) {
                    // Skip if already exported
                    continue;
                }
                if (BaseGameResources.GOO_BALL_TYPES.contains(ballName)) {
                    // Skip if base game ball
                    continue;
                }
                exportBallAsXML(ball, start + "\\res\\levels\\" + level.getLevelName() + "\\goomod\\compile\\res\\balls\\" + ballName, level.getVersion(), true);
                for (EditorObject resource : ball.getResources()) {
                    if (resource instanceof ResrcImage) {
                        String realpath = resource.getAttribute("path");
                        if (BaseGameResources.containsImage(realpath)) {
                            // Skip if base game image
                            continue;
                        }
                        // Create subfolder if required
                        if (realpath.contains("/")) {
                            realpath = realpath.substring(0, realpath.lastIndexOf("/"));
                        }
                        Path subfolder = Path.of(start + "\\res\\levels\\" + level.getLevelName() + "\\goomod\\override\\" + realpath);
                        if (!Files.exists(subfolder)) {
                            Files.createDirectories(subfolder);
                        }
                        BufferedImage oldImage = SwingFXUtils.fromFXImage(GlobalResourceManager.getImage(resource.getAttribute("id"), level.getVersion()), null);
                        File newImageFile = new File(start + "\\res\\levels\\" + level.getLevelName() + "\\goomod\\override\\" + resource.getAttribute("path") + ".png");
                        ImageIO.write(oldImage, "png", newImageFile);
                    }
                }
                exportedBalls.add(ballName);
            } catch (Exception e) {
                Alarms.errorMessage(e);
            }
        }

        try {
            new ZipUtility().zip(new ArrayList<>(List.of(new File(start + "\\res\\levels\\" + level.getLevelName() + "\\goomod").listFiles())), start + "\\res\\levels\\" + level.getLevelName() + "\\goomod.zip");
            new File(start + "\\res\\levels\\" + level.getLevelName() + "\\goomod.zip").renameTo(file);
        } catch (Exception e) {
            Alarms.errorMessage(e);
        }
    }
}
