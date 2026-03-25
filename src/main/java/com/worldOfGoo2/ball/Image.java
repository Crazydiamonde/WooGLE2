package com.worldOfGoo2.ball;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.attributes.AttributeAdapter;
import com.woogleFX.editorObjects.attributes.InputField;
import com.woogleFX.editorObjects.objectCreators.ObjectCreator;
import com.woogleFX.assets.GameVersion;
import com.worldOfGoo2.misc.ImageID;

public class Image extends EditorObject {

    public Image(EditorObject parent, GameVersion version) {
        super(parent, version);

        addAttributeAdapter("imageId", AttributeAdapter.childAttributeAdapter(this, "imageId", "imageId", InputField.STRING));
        addAttributeAdapter("imageMaskId", AttributeAdapter.childAttributeAdapter(this, "imageMaskId", "imageMaskId", InputField.STRING));

    }

    @Override
    public String getName() {
        return getAttribute("imageId").stringValue();
    }

    @Override
    public void createRequiredChildren() {
        ObjectCreator.create(ImageID.class, this, "imageId", GameVersion.VERSION_WOG2);
        ObjectCreator.create(ImageID.class, this, "imageMaskId", GameVersion.VERSION_WOG2);
    }

}
