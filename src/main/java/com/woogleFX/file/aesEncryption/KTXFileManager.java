package com.woogleFX.file.aesEncryption;

import com.github.luben.zstd.Zstd;

import java.awt.*;
import java.awt.image.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;

public class KTXFileManager {

    public static BufferedImage readKTXImage(Path ktxImagePath) {

        // TODO: figure this out

        try {

            ByteArrayInputStream inputStream = new ByteArrayInputStream(Files.readAllBytes(ktxImagePath));

            String header = new String(inputStream.readNBytes(4));

            int version = ByteBuffer.wrap(inputStream.readNBytes(4)).order(ByteOrder.LITTLE_ENDIAN).getInt();
            int width = ByteBuffer.wrap(inputStream.readNBytes(2)).order(ByteOrder.LITTLE_ENDIAN).getShort();
            int height = ByteBuffer.wrap(inputStream.readNBytes(2)).order(ByteOrder.LITTLE_ENDIAN).getShort();
            int width2 = ByteBuffer.wrap(inputStream.readNBytes(2)).order(ByteOrder.LITTLE_ENDIAN).getShort();
            int height2 = ByteBuffer.wrap(inputStream.readNBytes(2)).order(ByteOrder.LITTLE_ENDIAN).getShort();
            int compressedSize = ByteBuffer.wrap(inputStream.readNBytes(4)).order(ByteOrder.LITTLE_ENDIAN).getInt();
            int uncompressedSize = ByteBuffer.wrap(inputStream.readNBytes(4)).order(ByteOrder.LITTLE_ENDIAN).getInt();
            int maskWidth = ByteBuffer.wrap(inputStream.readNBytes(2)).order(ByteOrder.LITTLE_ENDIAN).getShort();
            int maskHeight = ByteBuffer.wrap(inputStream.readNBytes(2)).order(ByteOrder.LITTLE_ENDIAN).getShort();
            int maskCompressedSize = ByteBuffer.wrap(inputStream.readNBytes(4)).order(ByteOrder.LITTLE_ENDIAN).getInt();
            int maskUncompressedSize = ByteBuffer.wrap(inputStream.readNBytes(4)).order(ByteOrder.LITTLE_ENDIAN).getInt();

            byte[] compressedData = inputStream.readNBytes(compressedSize);

            byte[] output1 = new byte[uncompressedSize];
            Zstd.decompress(output1, compressedData);
            byte[] output = new byte[uncompressedSize - 64];
            System.arraycopy(output1, 64, output, 0, output.length);
            DataBuffer buffer = new DataBufferByte(output, output.length);
            WritableRaster raster = Raster.createInterleavedRaster(buffer, width2, height2, 4 * width, 4, new int[]{ 0, 1, 2, 3}, null);
            ColorModel colorModel = new ComponentColorModel(ColorModel.getRGBdefault().getColorSpace(), true, false, Transparency.TRANSLUCENT, DataBuffer.TYPE_BYTE);

            return new BufferedImage(colorModel, raster, true, null);

        } catch (IOException e) {
            return null;
        }

    }


    public static void writeKTXImage(Path ktxImagePath, BufferedImage bufferedImage) {

    }

}
