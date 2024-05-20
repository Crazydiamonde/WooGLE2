package com.WooGLEFX.File.fileexport;

import com.WooGLEFX.EditorObjects._Ball;
import com.WooGLEFX.File.AESBinFormat;
import com.WooGLEFX.File.FileManager;
import com.WooGLEFX.Structures.GameVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

public class BallWriter {

    private static final Logger logger = LoggerFactory.getLogger(BallWriter.class);


    public static void exportBallAsXML(_Ball ball, String outputPathString, GameVersion version, boolean goomod) throws IOException {
        /* General Parts */
        /* Walking Animations */
        /* Attached Animations */
        /* Dragging Animations */
        /* Climbing Animations */
        /* Tank State Animations */
        /* Sound FX */

        //TODO add XML comments to files

        String ballXML = XMLUtility.recursiveXMLexport("", ball.objects.get(0), 0, true);
        logger.trace(ballXML);

        String resrcXML = XMLUtility.recursiveXMLexport("", ball.resources.get(0), 0, true);
        logger.trace(resrcXML);

        String name = ball.objects.get(0).getAttribute("name").stringValue();

        String ballsPathText = outputPathString + "\\balls.xml";
        String resourcesPathText = outputPathString + "\\resources.xml";

        if (!goomod && version == GameVersion.OLD) {
            ballsPathText += ".bin";
            resourcesPathText += ".bin";
        }

        if (goomod) {
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

        if (version == GameVersion.OLD) {
            AESBinFormat.encodeFile(new File(ballsPathText), ballXML.getBytes());
            AESBinFormat.encodeFile(new File(resourcesPathText), resrcXML.getBytes());
            if (!goomod) {
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
        if (version == GameVersion.NEW) {
            Files.write(ballsPath, Collections.singleton(ballXML), StandardCharsets.UTF_8);
            Files.write(resourcesPath, Collections.singleton(resrcXML), StandardCharsets.UTF_8);
            if (!goomod) {
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

}
