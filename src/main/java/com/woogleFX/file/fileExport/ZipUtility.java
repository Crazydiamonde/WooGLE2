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
    public void zip(List<File> listFiles, String destZipFile) throws IOException {
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(destZipFile));
        for (File file : listFiles) {
            if (file.isDirectory()) {
                zipDirectory(file, file.getName(), zos);
            } else {
                zipFile(file, zos);
            }
        }
        zos.flush();
        zos.close();
    }

    /**
     * Adds a directory to the current zip output stream
     *
     * @param folder       the directory to be added
     * @param parentFolder the path of parent directory
     * @param zos          the current zip output stream
     */
    private void zipDirectory(File folder, String parentFolder, ZipOutputStream zos) throws IOException {
        File[] files = folder.listFiles();
        if (files != null) for (File file : files) {
            if (file.isDirectory()) {
                zipDirectory(file, parentFolder + "/" + file.getName(), zos);
                continue;
            }
            zos.putNextEntry(new ZipEntry(parentFolder + "/" + file.getName()));
            BufferedInputStream bis = new BufferedInputStream(
                    new FileInputStream(file));
            byte[] bytesIn = new byte[BUFFER_SIZE];
            int read;
            while ((read = bis.read(bytesIn)) != -1) {
                zos.write(bytesIn, 0, read);
            }
            bis.close();
            zos.closeEntry();
        }
    }

    /**
     * Adds a file to the current zip output stream
     *
     * @param file the file to be added
     * @param zos  the current zip output stream
     */
    private void zipFile(File file, ZipOutputStream zos) throws IOException {
        zos.putNextEntry(new ZipEntry(file.getName()));
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read;
        while ((read = bis.read(bytesIn)) != -1) {
            zos.write(bytesIn, 0, read);
        }
        bis.close();
        zos.closeEntry();
    }
}