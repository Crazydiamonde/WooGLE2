package com.woogleFX.editorObjects.objectCreators;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.assets.GameVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

public class ObjectCreator {

    private static final Logger logger = LoggerFactory.getLogger(ObjectCreator.class);

    @SuppressWarnings("unchecked")
    public static <T extends EditorObject> T create(Class<? extends EditorObject> tClass, EditorObject parent, String typeId, GameVersion version) {

        T toAdd;
        try {
            toAdd = (T) tClass.getConstructors()[0].newInstance(parent, version);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            logger.error("", e);
            toAdd = null;
        }
        if (toAdd == null) throw new RuntimeException();

        toAdd.setTypeID(typeId);

        if (parent != null) toAdd.setParent(parent);

        return toAdd;

    }


    public static <T extends EditorObject> T create(Class<? extends EditorObject> tClass, EditorObject parent, GameVersion version) {
        return create(tClass, parent, "", version);
    }

}
