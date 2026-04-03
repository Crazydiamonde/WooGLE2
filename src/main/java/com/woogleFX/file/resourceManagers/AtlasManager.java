package com.woogleFX.file.resourceManagers;

import com.woogleFX.file.FileManager;
import com.woogleFX.file.aesEncryption.KTXFileManager;
import com.woogleFX.assets.GameVersion;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class AtlasManager {

    public static Map<String, Image> atlas = new HashMap<>();


    public static void reloadAtlas() throws IOException {

        atlas.clear();

        File atlasFile = new File(FileManager.getGameDir(GameVersion.VERSION_WOG2) + "/res/balls/_atlas.image.atlas");

        ByteArrayInputStream input = new ByteArrayInputStream(Files.readAllBytes(atlasFile.toPath()));

        // Skip header and endianness
        input.skipNBytes(8);

        // Image count
        int imageCount = ByteBuffer.wrap(input.readNBytes(4)).order(ByteOrder.LITTLE_ENDIAN).getInt(0);

        BufferedImage maybe = ImageIO.read(new File(FileManager.getGameDir(GameVersion.VERSION_WOG2) + "/res/balls/_atlas.image"));

        BufferedImage image = (maybe != null) ? maybe : KTXFileManager.readKTXImage(Path.of(FileManager.getGameDir(GameVersion.VERSION_WOG2) + "/res/balls/_atlas.image"));
        if (image == null) {
            System.err.println("image was null?");
            return;
        }

        for (int i = 0; i < imageCount; i++) {

            // Image name
            String imageName = new String(input.readNBytes(64));
            imageName = imageName.substring(0, imageName.indexOf(0));

            // Image location
            int imageX = ByteBuffer.wrap(input.readNBytes(4)).order(ByteOrder.LITTLE_ENDIAN).getInt(0);
            int imageY = ByteBuffer.wrap(input.readNBytes(4)).order(ByteOrder.LITTLE_ENDIAN).getInt(0);
            int imageWidth = ByteBuffer.wrap(input.readNBytes(4)).order(ByteOrder.LITTLE_ENDIAN).getInt(0);
            int imageHeight = ByteBuffer.wrap(input.readNBytes(4)).order(ByteOrder.LITTLE_ENDIAN).getInt(0);

            atlas.put(imageName, SwingFXUtils.toFXImage(image.getSubimage(imageX, imageY, imageWidth, imageHeight), null));

        }

    }

}
