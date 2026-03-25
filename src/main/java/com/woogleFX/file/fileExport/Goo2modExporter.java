package com.woogleFX.file.fileExport;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.assets.wog2.WOG2Level.WOG2Level;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Goo2modExporter {

    public static void exportGoo2mod(WOG2Level level, boolean includeAddinInfo) throws IOException {

        FileChooser destinationDialog = new FileChooser();
        File destinationFile = destinationDialog.showSaveDialog(new Stage());

        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(destinationFile));

        zipOutputStream.putNextEntry(new ZipEntry("addin.xml"));
        EditorObject addinObject = level.getAddin();
        zipOutputStream.write(XMLUtility.fullAddinXMLExport("", addinObject, 0).getBytes());
        zipOutputStream.closeEntry();

        // Empty compile folder
        zipOutputStream.putNextEntry(new ZipEntry("compile/"));
        zipOutputStream.closeEntry();

        // Empty merge folder
        zipOutputStream.putNextEntry(new ZipEntry("merge/"));
        zipOutputStream.closeEntry();

        zipOutputStream.putNextEntry(new ZipEntry("override/"));
        zipOutputStream.closeEntry();
        zipOutputStream.putNextEntry(new ZipEntry("override/res/"));
        zipOutputStream.closeEntry();
        zipOutputStream.putNextEntry(new ZipEntry("override/res/levels/"));
        zipOutputStream.closeEntry();
        zipOutputStream.putNextEntry(new ZipEntry("override/res/levels/" + level.getName() + ".wog2"));
        StringBuilder exportBuilder = new StringBuilder();
        GOOWriter.recursiveGOOExport(exportBuilder, level.getLevel(), 0);
        zipOutputStream.write(exportBuilder.toString().getBytes());
        zipOutputStream.closeEntry();



        zipOutputStream.close();

    }

}
