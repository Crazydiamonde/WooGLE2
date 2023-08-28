package com.WooGLEFX.File;

import com.WooGLEFX.Structures.Resources.Resource;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.Resources.ImageResource;
import com.WooGLEFX.Structures.Resources.TextResource;
import com.WorldOfGoo.Resrc.ResrcImage;
import com.WorldOfGoo.Text.TextString;
import javafx.scene.image.Image;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class GlobalResourceManager {

    private static final ArrayList<Resource> oldResources = new ArrayList<>();
    private static final ArrayList<Resource> newResources = new ArrayList<>();

    public static ArrayList<Resource> getOldResources() {
        return oldResources;
    }
    public static ArrayList<Resource> getNewResources() {
        return newResources;
    }

    /**
     * @param id The id (all caps property) of the resource.
     * @param version Specifies which version of WoG (1.3 or 1.5).
     * @return The image associated with the resource.
     * @throws FileNotFoundException If there is no resource matching the id, or if the resource path does not lead to an image.
     */
    public static Image getImage(String id, double version) throws FileNotFoundException {

        if (version == 1.3) {
            for (Resource resource : oldResources) {
                if (resource instanceof ImageResource imageResource && resource.getId().equals(id)) {

                    if (imageResource.getImage() == null) {
                        imageResource.setImage(FileManager.openImageFromFilePath(FileManager.getOldWOGdir() + "\\" + ((ImageResource) resource).getPath() + ".png"));
                    }

                    return imageResource.getImage();
                }
            }
        } else if (version == 1.5) {
            for (Resource resource : newResources) {
                if (resource instanceof ImageResource imageResource && resource.getId().equals(id)) {

                    if (imageResource.getImage() == null) {
                        imageResource.setImage(FileManager.openImageFromFilePath(FileManager.getNewWOGdir() + "\\" + ((ImageResource) resource).getPath() + ".png"));
                    }

                    return imageResource.getImage();
                }
            }
        }

        throw new FileNotFoundException("Invalid image resource ID: \"" + id + "\" (version " + version + ")");

    }

    public static void updateResource(String id, double version) throws FileNotFoundException {
        if (version == 1.3) {
            for (Resource resource : oldResources) {
                if (resource instanceof ImageResource imageResource && resource.getId().equals(id)) {
                    imageResource.setImage(FileManager.openImageFromFilePath(FileManager.getOldWOGdir() + "\\" + ((ImageResource) resource).getPath() + ".png"));
                }
            }
        } else if (version == 1.5) {
            for (Resource resource : newResources) {
                if (resource instanceof ImageResource imageResource && resource.getId().equals(id)) {
                    imageResource.setImage(FileManager.openImageFromFilePath(FileManager.getNewWOGdir() + "\\" + ((ImageResource) resource).getPath() + ".png"));
                }
            }
        }
    }

    public static TextString getText(String id, double version) throws FileNotFoundException {

        if (version == 1.3) {
            for (Resource resource : oldResources) {
                if (resource instanceof TextResource textResource && resource.getId().equals(id)) {
                    return textResource.getText();
                }
            }
        } else if (version == 1.5) {
            for (Resource resource : newResources) {
                if (resource instanceof TextResource textResource && resource.getId().equals(id)) {
                    return textResource.getText();
                }
            }
        }

        throw new FileNotFoundException("Invalid text resource ID: \"" + id + "\" (version " + version + ")");
    }

    public static void addResource(EditorObject resource, double version) {
        if (version == 1.3) {
            if (resource instanceof ResrcImage) {
                oldResources.add(new ImageResource(resource.getAttribute("id"), resource.getAttribute("path"), null));
            } else if (resource instanceof TextString textString) {
                oldResources.add(new TextResource(resource.getAttribute("id"), textString));
            }
        } else if (version == 1.5) {
            if (resource instanceof ResrcImage) {
                newResources.add(new ImageResource(resource.getAttribute("id"), resource.getAttribute("path"), null));
            } else if (resource instanceof TextString textString) {
                newResources.add(new TextResource(resource.getAttribute("id"), textString));
            }
        }
    }

}
