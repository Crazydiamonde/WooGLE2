package com.woogleFX.file.fileExport;

import com.woogleFX.editorObjects._Ball;
import com.woogleFX.file.AESBinFormat;
import com.woogleFX.file.FileManager;
import com.woogleFX.structures.GameVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class BallWriter {

    private static final Logger logger = LoggerFactory.getLogger(BallWriter.class);


    public static void saveAsXML(_Ball ball, String outputPathString, GameVersion version, boolean goomod) throws IOException {
        /* General Parts */
        /* Walking Animations */
        /* Attached Animations */
        /* Dragging Animations */
        /* Climbing Animations */
        /* Tank State Animations */
        /* Sound FX */

        //TODO add XML comments to files

        String ballXML = XMLUtility.XMLExport(ball.objects.get(0));
        logger.trace(ballXML);

        String resrcXML = XMLUtility.XMLExport(ball.resources.get(0));
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

        String otherDir = FileManager.getGameDir(version == GameVersion.OLD ? GameVersion.NEW : GameVersion.OLD);
        if (!goomod && version == GameVersion.OLD) {
            AESBinFormat.encodeFile(new File(ballsPathText), ballXML.getBytes());
            AESBinFormat.encodeFile(new File(resourcesPathText), resrcXML.getBytes());
        } else {
            Files.write(ballsPath, ballXML.getBytes());
            Files.write(resourcesPath, resrcXML.getBytes());
        }

        if (goomod) return;

        if (!Files.exists(Path.of(otherDir + "\\res\\balls\\" + name))) return;

        File[] images = new File(otherDir + "\\res\\balls\\" + name).listFiles();
        if (images == null) return;

        for (File imageFile : images) {
            if (imageFile.getPath().substring(imageFile.getPath().lastIndexOf(".")).equals(".png") || imageFile.getPath().substring(imageFile.getPath().lastIndexOf(".")).equals(".ogg")) {
                if (Files.exists(Path.of(outputPath + "\\" + imageFile.getName()))) continue;

                Files.copy(imageFile.toPath(), Path.of(outputPath + "\\" + imageFile.getName()));

            }
        }

    }

}
