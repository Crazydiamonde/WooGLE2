package com.woogleFX.engine.gui.alarms;

import com.woogleFX.assets.Asset;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.engine.fx.assetSelectPane.FXAssetSelectPane;
import com.woogleFX.engine.fx.propertiesView.FXPropertiesView;
import com.woogleFX.engine.fx.hierarchy.FXHierarchy;
import com.woogleFX.assets.AssetCloser;
import com.woogleFX.engine.gui.AssetSelector;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;

public class CloseTabAlarm {

    public static void show(Tab tab, Asset level) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Unsaved Changes");
        alert.setContentText("Ignore unsaved changes?");

        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType.equals(ButtonType.OK)) {
                if (tab.getTabPane().getTabs().size() == 1) {

                    // Clear this asset from the editor cache so changes disappear
                    // This is a really silly way to do it but it DOES work
                    try {
                        AssetSelector<?> assetSelector = (AssetSelector<?>)
                                level.getClass().getField("assetSelector").get(level);
                        assetSelector.removeImportedAsset(level);
                    } catch (IllegalAccessException | NoSuchFieldException e) {
                        ErrorAlarm.show(e);
                    }

                    FXAssetSelectPane.getAssetSelectPane().setMinHeight(0);
                    FXAssetSelectPane.getAssetSelectPane().setMaxHeight(0);
                    FXHierarchy.getHierarchy().setRoot(null);
                    FXPropertiesView.changeTableView(new EditorObject[]{});
                }
                Platform.runLater(() -> tab.getTabPane().getTabs().remove(tab));
            } else if (buttonType.equals(ButtonType.CANCEL)) {
                level.setEditingStatus(level.getEditingStatus(), level == AssetManager.getAsset());
            }
        });
    }

    public static void showClosingEditor(Tab tab, Asset asset) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Unsaved Changes");
        alert.setContentText("Ignore unsaved changes?");

        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType.equals(ButtonType.OK)) {
                if (tab.getTabPane().getTabs().size() == 1) {
                    FXAssetSelectPane.getAssetSelectPane().setMinHeight(0);
                    FXAssetSelectPane.getAssetSelectPane().setMaxHeight(0);
                    FXHierarchy.getHierarchy().setRoot(null);
                    FXPropertiesView.changeTableView(new EditorObject[]{});
                }
                Platform.runLater(() -> {
                    tab.getTabPane().getTabs().remove(tab);
                    AssetCloser.resumeLevelClosing();
                });
            } else if (buttonType.equals(ButtonType.CANCEL)) {
                asset.setEditingStatus(asset.getEditingStatus(), asset == AssetManager.getAsset());
            }
        });
    }

}
