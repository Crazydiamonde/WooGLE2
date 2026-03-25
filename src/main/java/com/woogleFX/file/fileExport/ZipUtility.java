package com.woogleFX.file.fileExport;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * This utility compresses a list of files to standard ZIP format file.
 * It is able to compress all sub files and subdirectories, recursively.
 *
 * @author www.codejava.net
 *
 */
public class ZipUtility {
    /**
     * A constants for buffer size used to read/write data
     */
    private static final int BUFFER_SIZE = 4096;

    /**
     * Compresses a list of files to a destination zip file
     *
     * @param listFiles   A collection of files and directories
     * @param destZipFile The path of the destination zip file
     */
    public void zip(File root, List<File> listFiles, String destZipFile) throws IOException {
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(destZipFile));
        for (File file : listFiles) zipFile(root, file, zos);
        zos.flush();
        zos.close();
    }

    /**
     * Adds a file to the current zip output stream
     *
     * @param file the file to be added
     * @param zos  the current zip output stream
     */
    private void zipFile(File root, File file, ZipOutputStream zos) throws IOException {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) return;
            for (File child : files) zipFile(root, child, zos);
        } else {
            zos.putNextEntry(new ZipEntry(file.getPath().substring(root.getPath().length() + 1)));
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            byte[] bytesIn = new byte[BUFFER_SIZE];
            int read;
            while ((read = bis.read(bytesIn)) != -1) zos.write(bytesIn, 0, read);
            bis.close();
            zos.closeEntry();
        }
    }
}