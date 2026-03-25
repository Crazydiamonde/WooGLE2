package com.woogleFX.assets;

import javafx.scene.Node;
import javafx.scene.layout.HBox;

public class AssetError {

    private final HBox contents;
    public HBox getContents() {
        return contents;
    }

    public AssetError(Node... items) {
        contents = new HBox(items);
    }

}
