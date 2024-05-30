package com.woogleFX.file.resourceManagers;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects._Font;
import com.woogleFX.file.FileManager;
import com.woogleFX.file.fileImport.FontReader;
import com.woogleFX.structures.GameVersion;
import com.worldOfGoo.resrc.Material;
import com.worldOfGoo.resrc.Font;
import com.worldOfGoo.resrc.ResrcImage;
import com.worldOfGoo.resrc.Sound;
import com.worldOfGoo.text.TextString;
import javafx.scene.image.Image;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class ResourceManager {

    private static boolean checkSingleResource(EditorObject resource, String id) {

        if (resource instanceof ResrcImage resrcImage) return resrcImage.getAdjustedID().equals(id);
        else if (resource instanceof Sound sound) return sound.getAdjustedID().equals(id);
        else if (resource instanceof Font font) return font.getAdjustedID().equals(id);
        else if (resource instanceof TextString text) return text.getAttribute("id").stringValue().equals(id);
        else if (resource instanceof Material mat) return mat.getAttribute("id").stringValue().equals(id);
        else return false;

    }

    /** Attempts to locate a resource with the given ID from the given resources.
     * Looks in the given level's resources first, then checks the global resources.
     * Returns null if there isn't any resource with the given ID. */
    private static EditorObject findResource(ArrayList<EditorObject> resources, String id, GameVersion version) {

        if (resources != null) for (EditorObject resource : resources)
            if (checkSingleResource(resource, id)) return resource;

        ArrayList<EditorObject> globalResources;
        if (version == GameVersion.OLD) globalResources = GlobalResourceManager.getOldResources();
        else globalResources = GlobalResourceManager.getNewResources();

        for (EditorObject resource : globalResources)
            if (checkSingleResource(resource, id)) return resource;

        return null;

    }


    /** Returns an image corresponding with the given ID. */
    public static Image getImage(ArrayList<EditorObject> resources, String id, GameVersion version) throws FileNotFoundException {
        EditorObject resource = findResource(resources, id, version);
        if (resource instanceof ResrcImage resrcImage) {
            if (resrcImage.getImage() == null) updateResource(resrcImage, version);
            return resrcImage.getImage();
        }
        else throw new FileNotFoundException("Invalid image resource ID: \"" + id + "\" (version " + version + ")");
    }


    /** Returns a font corresponding with the given ID. */
    public static TextString getText(ArrayList<EditorObject> resources, String id, GameVersion version) throws FileNotFoundException {
        EditorObject resource = findResource(resources, id, version);
        if (resource instanceof TextString textString) return textString;
        else throw new FileNotFoundException("Invalid text resource ID: \"" + id + "\" (version " + version + ")");
    }


    /** Returns a font corresponding with the given ID. */
    public static _Font getFont(ArrayList<EditorObject> resources, String id, GameVersion version) throws FileNotFoundException {
        EditorObject resource = findResource(resources, id, version);
        if (resource instanceof Font font) {
            if (font.getFont() == null) updateResource(font, version);
            return font.getFont();
        }
        else throw new FileNotFoundException("Invalid text resource ID: \"" + id + "\" (version " + version + ")");
    }


    /** Returns a material corresponding with the given ID. */
    public static Material getMaterial(ArrayList<EditorObject> resources, String id, GameVersion version) throws FileNotFoundException {
        EditorObject resource = findResource(resources, id, version);
        if (resource instanceof Material material) return material;
        else throw new FileNotFoundException("Invalid material resource ID: \"" + id + "\" (version " + version + ")");
    }


    public static boolean updateResource(EditorObject resource, GameVersion version) {
        String dir = FileManager.getGameDir(version);
        if (resource instanceof ResrcImage resrcImage) {
            try {
                resrcImage.setImage(FileManager.openImageFromFilePath(dir + "\\" + resrcImage.getAdjustedPath() + ".png"));
                return true;
            } catch (IOException ignored) {
                return false;
            }
        } else if (resource instanceof Sound sound) {
            return true;
        } else if (resource instanceof TextString textString) {
            //textResource.setText("");
            return true;
        } else if (resource instanceof Font font) {
            font.setFont(FontReader.read(font.getAdjustedPath(), version));
            return true;
        } else return false;
    }

}
