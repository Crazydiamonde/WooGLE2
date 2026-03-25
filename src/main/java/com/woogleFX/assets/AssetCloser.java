package com.woogleFX.assets;

import com.woogleFX.engine.fx.assetSelectPane.FXAssetSelectPane;
import com.woogleFX.engine.fx.FXStage;
import com.woogleFX.engine.fx.AssetTab;
import com.woogleFX.engine.gui.alarms.CloseTabAlarm;
import com.woogleFX.engine.gui.alarms.ErrorAlarm;
import javafx.scene.control.TabPane;

public class AssetCloser {

    public static void resumeLevelClosing() {
        TabPane levelSelectPane = FXAssetSelectPane.getAssetSelectPane();
        if (levelSelectPane.getTabs().isEmpty()) {
            FXStage.getStage().close();
        } else {
            try {
                AssetTab assetTab = (AssetTab) levelSelectPane.getTabs().get(levelSelectPane.getTabs().size() - 1);
                if (assetTab.getAsset().getEditingStatus() == AssetTab.UNSAVED_CHANGES) {
                    CloseTabAlarm.showClosingEditor(assetTab, assetTab.getAsset());
                } else {
                    assetTab.getTabPane().getTabs().remove(assetTab);
                    resumeLevelClosing();
                }
            } catch (Exception e) {
                ErrorAlarm.show(e);
            }
        }
    }

}
