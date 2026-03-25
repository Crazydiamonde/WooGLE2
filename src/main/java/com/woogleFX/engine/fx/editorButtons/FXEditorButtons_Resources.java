package com.woogleFX.engine.fx.editorButtons;

import com.woogleFX.assets.Asset;
import com.woogleFX.assets.wog1.animation.WOG1Animation;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.file.resourceManagers.LevelResourceImporter;
import com.woogleFX.file.resourceManagers.LevelResourceManager;
import javafx.scene.control.*;

public class FXEditorButtons_Resources {

    private static final FXEditorButtons.EditorButton buttonReloadResources = new FXEditorButtons.EditorButton() {
        @Override
        public void updateDisabled() {
            Asset asset = AssetManager.getAsset();
            setDisable(asset == null || asset instanceof WOG1Animation);
        }
    };
    private static final FXEditorButtons.EditorButton buttonImportResources = new FXEditorButtons.EditorButton() {
        @Override
        public void updateDisabled() {
            Asset asset = AssetManager.getAsset();
            setDisable(asset == null || asset instanceof WOG1Animation);
        }
    };
    private static final FXEditorButtons.EditorButton buttonAddTextResource = new FXEditorButtons.EditorButton() {
        @Override
           public void updateDisabled() {
            Asset asset = AssetManager.getAsset();
            setDisable(asset == null || asset.getStrings() == null);
        }
    };
    private static final FXEditorButtons.EditorButton buttonCleanResources = new FXEditorButtons.EditorButton() {
        @Override
        public void updateDisabled() {
            Asset asset = AssetManager.getAsset();
            setDisable(asset == null || asset instanceof WOG1Animation);
        }
    };

    public static void resources(ToolBar toolBar) {

        String prefix = "ButtonIcons/Resources/";

        buttonReloadResources.setIcon(prefix + "update_level_resources.png");
        buttonReloadResources.setOnAction(e -> LevelResourceManager.updateLevelResources(AssetManager.getAsset()));
        buttonReloadResources.setTooltip(new FXEditorButtons.DelayedTooltip("Reload Resources"));
        toolBar.getItems().add(buttonReloadResources);

        buttonImportResources.setIcon(prefix + "import_img.png");
        buttonImportResources.setOnAction(e -> LevelResourceImporter.importResources(AssetManager.getAsset()));
        buttonImportResources.setTooltip(new FXEditorButtons.DelayedTooltip("Import Resources"));
        toolBar.getItems().add(buttonImportResources);

        buttonAddTextResource.setIcon(prefix + "add_text_resource.png");
        buttonAddTextResource.setOnAction(e -> LevelResourceManager.newTextResource(AssetManager.getAsset()));
        buttonAddTextResource.setTooltip(new FXEditorButtons.DelayedTooltip("Add Text Resource"));
        toolBar.getItems().add(buttonAddTextResource);

        toolBar.getItems().add(new Separator());

        buttonCleanResources.setIcon(prefix + "clean_level_resources.png");
        buttonCleanResources.setOnAction(e -> LevelResourceManager.cleanLevelResources(AssetManager.getAsset()));
        buttonCleanResources.setTooltip(new FXEditorButtons.DelayedTooltip("Clean Resources"));
        toolBar.getItems().add(buttonCleanResources);

    }

}
