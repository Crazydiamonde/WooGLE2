package com.woogleFX.assets;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.attributes.EditorAttribute;
import com.woogleFX.engine.SelectionManager;
import javafx.geometry.Insets;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AssetVerifier {

    public static AssetError buildFromText(EditorObject object, String part1, String part2, String part3) {
        Label first = new Label(part1);

        Hyperlink second = new Hyperlink(part2);
        second.setPadding(new Insets(-1, 0, 0, 0));
        second.setOnAction(actionEvent -> {
            object.getAsset().setSelectedLoudly(new EditorObject[]{ object });
            second.setVisited(false);
        });

        Label third = new Label(part3);

        return new AssetError(first, second, third);

    }

    private static List<AssetError> verifySingleObject(EditorObject object) {
        List<AssetError> assetErrors = new ArrayList<>();
        for (EditorAttribute attribute : object.getAttributes())
            if (!attribute.getType().verify(object, attribute.stringValue(), attribute.getRequired())) {

                assetErrors.add(buildFromText(object,
                        "Attribute \"" + attribute.getName() + "\" of object ",
                        object.getType() + " (\"" + object.getName() + "\")",
                        " has invalid value \"" + attribute.stringValue() + "\""));

            }
        return assetErrors;
    }


    public static List<AssetError> verifyAllObjects(Collection<EditorObject> EditorObjects) {
        List<AssetError> assetErrors = new ArrayList<>();
        for (EditorObject object : EditorObjects) assetErrors.addAll(verifySingleObject(object));
        return assetErrors;
    }

}
