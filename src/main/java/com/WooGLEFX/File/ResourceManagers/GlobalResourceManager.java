package com.WooGLEFX.File.ResourceManagers;

import com.WooGLEFX.File.FileManager;
import com.WooGLEFX.Structures.GameVersion;
import com.WooGLEFX.File.ResourceManagers.Resources.Resource;
import com.WooGLEFX.EditorObjects.EditorObject;
import com.WooGLEFX.File.ResourceManagers.Resources.ImageResource;
import com.WooGLEFX.File.ResourceManagers.Resources.TextResource;
import com.WorldOfGoo.Resrc.Resources;
import com.WorldOfGoo.Resrc.ResrcImage;
import com.WorldOfGoo.Resrc.SetDefaults;
import com.WorldOfGoo.Text.TextString;
import javafx.scene.image.Image;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class GlobalResourceManager {

    private static final ArrayList<Resource> oldResources = new ArrayList<>();


    private static final ArrayList<Resource> newResources = new ArrayList<>();


    /**
     * @param id The id (all caps property) of the resource.
     * @param version Specifies which version of WoG (1.3 or 1.5).
     * @return The image associated with the resource.
     * @throws FileNotFoundException If there is no resource matching the id, or if the resource path does not lead to an image.
     */
    public static Image getImage(String id, GameVersion version) throws FileNotFoundException {

        if (version == GameVersion.OLD) {
            for (Resource resource : oldResources) {
                if (resource instanceof ImageResource imageResource && resource.getId().equals(id)) {

                    if (imageResource.getImage() == null) {
                        imageResource.setImage(FileManager.openImageFromFilePath(FileManager.getOldWOGdir() + "\\" + ((ImageResource) resource).getPath() + ".png"));
                    }

                    return imageResource.getImage();
                }
            }
        } else if (version == GameVersion.NEW) {
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


    public static void updateResource(String id, GameVersion version) throws FileNotFoundException {
        if (version == GameVersion.OLD) {
            for (Resource resource : oldResources) {
                if (resource instanceof ImageResource imageResource && resource.getId().equals(id)) {
                    imageResource.setImage(FileManager.openImageFromFilePath(FileManager.getOldWOGdir() + "\\" + ((ImageResource) resource).getPath() + ".png"));
                }
            }
        } else if (version == GameVersion.NEW) {
            for (Resource resource : newResources) {
                if (resource instanceof ImageResource imageResource && resource.getId().equals(id)) {
                    imageResource.setImage(FileManager.openImageFromFilePath(FileManager.getNewWOGdir() + "\\" + ((ImageResource) resource).getPath() + ".png"));
                }
            }
        }
    }


    public static TextString getText(String id, GameVersion version) throws FileNotFoundException {

        if (version == GameVersion.OLD) {
            for (Resource resource : oldResources) {
                if (resource instanceof TextResource textResource && resource.getId().equals(id)) {
                    return textResource.getText();
                }
            }
        } else if (version == GameVersion.NEW) {
            for (Resource resource : newResources) {
                if (resource instanceof TextResource textResource && resource.getId().equals(id)) {
                    return textResource.getText();
                }
            }
        }

        throw new FileNotFoundException("Invalid text resource ID: \"" + id + "\" (version " + version + ")");
    }


    public static void addResource(EditorObject resource, GameVersion version) {

        EditorObject resourceManifest = resource;
        while (!(resourceManifest instanceof Resources) && resourceManifest.getParent() != null) {
            resourceManifest = resourceManifest.getParent();
        }

        int resrcIndex = resourceManifest.getChildren().indexOf(resource);

        String idprefix = "";
        String pathprefix = "";
        for (int i = resrcIndex - 1; i >= 0; i--) {
            EditorObject editorObject = resourceManifest.getChildren().get(i);
            if (editorObject instanceof SetDefaults setDefaults) {
                idprefix = setDefaults.getAttribute("idprefix").stringValue();
                pathprefix = setDefaults.getAttribute("path").stringValue();
                break;
            }
        }

        if (version == GameVersion.OLD) {
            if (resource instanceof ResrcImage) {
                oldResources.add(new ImageResource(idprefix + resource.getAttribute("id").stringValue(), pathprefix + resource.getAttribute("path").stringValue(), null));
            } else if (resource instanceof TextString textString) {
                oldResources.add(new TextResource(idprefix + resource.getAttribute("id").stringValue(), textString));
            }
        } else if (version == GameVersion.NEW) {
            if (resource instanceof ResrcImage) {
                newResources.add(new ImageResource(idprefix + resource.getAttribute("id").stringValue(), pathprefix + resource.getAttribute("path").stringValue(), null));
            } else if (resource instanceof TextString textString) {
                newResources.add(new TextResource(idprefix + resource.getAttribute("id").stringValue(), textString));
            }
        }
    }

}
