package com.worldOfGoo.resrc;

import com.woogleFX.assets.Asset;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.assets.GameVersion;
import com.woogleFX.file.resourceManagers.ResourceManager;

public class Image extends EditorObject implements ResourceInterface {

    private javafx.scene.image.Image image;
    public javafx.scene.image.Image getImage() {
        return image;
    }
    public void setImage(javafx.scene.image.Image image) {
        this.image = image;
    }


    private SetDefaults setDefaults;
    public SetDefaults getSetDefaults() {
        return setDefaults;
    }
    public void setSetDefaults(SetDefaults setDefaults) {
        this.setDefaults = setDefaults;
    }


    public String getAdjustedID() {
        if (setDefaults == null) return getAttribute("id").stringValue();
        else return setDefaults.getAttribute("idprefix").stringValue() + getAttribute("id").stringValue();
    }


    public String getAdjustedPath() {
        if (setDefaults == null) return getAttribute("path").stringValue();
        else return setDefaults.getAttribute("path").stringValue() + getAttribute("path").stringValue();
    }


    public Image(EditorObject _parent, GameVersion version) {
        super(_parent, version);
    }


    @Override
    public void onLoaded(Asset asset) {
        super.onLoaded(asset);

        getAttribute("path").addChangeListener((observableValue, s, t1) -> ResourceManager.updateResource(this, getVersion()));

    }

    @Override
    public String getName() {
        return getAttribute("id").stringValue();
    }

}
