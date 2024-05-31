package com.woogleFX.functions;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.engine.fx.*;
import com.woogleFX.engine.Renderer;
import com.woogleFX.engine.SelectionManager;
import com.woogleFX.structures.GameVersion;
import com.woogleFX.structures.WorldLevel;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Affine;

public class LevelManager {

    private static WorldLevel level = null;
    public static WorldLevel getLevel() {
        return level;
    }
    public static void setLevel(WorldLevel _level) {
        level = _level;
        FXEditorButtons.updateAllButtons();
        FXMenu.updateAllButtons();

        if (level != null) {

            // Transform the canvas according to the updated translation and scale.
            Renderer.t = new Affine();
            Renderer.t.appendTranslation(level.getOffsetX(), level.getOffsetY());
            Renderer.t.appendScale(level.getZoom(), level.getZoom());

        }

        onSetLevel(level);
        Renderer.draw();
    }

    public static void onSetLevel(WorldLevel level) {

        VBox vBox = FXContainers.getvBox();

        vBox.getChildren().remove(2);

        if (level == null) {
            FXStage.getStage().setTitle("World of Goo Anniversary Editor");
            vBox.getChildren().add(2, FXEditorButtons.getNullGooballsToolbar());
            return;
        }

        if (level.getVersion() == GameVersion.OLD) {
            vBox.getChildren().add(2, FXEditorButtons.getOldGooballsToolbar());
        } else {
            vBox.getChildren().add(2, FXEditorButtons.getNewGooballsToolbar());
        }

        String levelName = level.getLevelName() + " (version " + level.getVersion() + ")";
        FXStage.getStage().setTitle(levelName + " — World of Goo Anniversary Editor");

        level.getVisibilitySettings().updateButtons();

        // TODO: implement multiple-object handling in the properties view. good luck
        if (level.getSelected().length == 0) FXPropertiesView.changeTableView(new EditorObject[]{});
        else FXPropertiesView.changeTableView(level.getSelected());

        SelectionManager.goToSelectedInHierarchy();

    }

}
