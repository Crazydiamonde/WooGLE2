package com.woogleFX.engine.fx.editorButtons;

import com.woogleFX.assets.*;
import com.woogleFX.assets.wog2.WOG2Level.WOG2Level;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.engine.fx.FXContainers;
import com.woogleFX.engine.fx.menu.FXMenu;
import com.woogleFX.file.FileManager;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class FXEditorButtons {

    public static void fillShowHideToolbar(ToolBar functionsToolbar) {

        Asset asset = AssetManager.getAsset();
        if (asset == null) return;

        for (String key : asset.getVisibilitySettings().getVisibilityKeys()) {
            EditorButton editorButton = new EditorButton() {
                @Override
                public void updateDisabled() {
                    setDisable(false);
                }
            };
            editorButton.setTooltip(new DelayedTooltip("Show/Hide " + key));
            editorButton.setIcon("/ButtonIcons/ShowHide/showhide_" + key + "_" + asset.getVisibilitySettings().getVisibilityStatus(key) + ".png");
            editorButton.setOnAction(e -> {
                asset.getVisibilitySettings().decrementVisibilityStatus(key);
                editorButton.setIcon("/ButtonIcons/ShowHide/showhide_" + key + "_" + asset.getVisibilitySettings().getVisibilityStatus(key) + ".png");
            });
            functionsToolbar.getItems().add(editorButton);
        }

        if (asset instanceof WOG2Level wog2Level) {
            WOG2Level.updateTerrainGroupSelector(wog2Level);
            functionsToolbar.getItems().add(WOG2Level.getButtonViewTerrainGroup());
        }

    }

    public static abstract class EditorButton extends Button {

        public abstract void updateDisabled();

        public void setIcon(String pathString) {
            setGraphic(new ImageView(FileManager.getIcon(pathString)));
        }

    }


    public static abstract class EditorMenuButton extends MenuButton {

        public EditorMenuButton() {
            super();
            setStyle("-fx-background-insets: 0,0,0; -fx-padding: -4 -8 -4 -8;");
        }

        public abstract void updateDisabled();

        public void setIcon(String pathString) {
            setGraphic(new ImageView(FileManager.getIcon(pathString)));
        }

    }


    public static class DelayedTooltip extends Tooltip {
        // Tooltip with a shorter delay than the default
        public DelayedTooltip(String text) {
            super(text);
            setShowDelay(javafx.util.Duration.millis(150));
        }
    }


    private static ToolBar functionsToolbar;
    public static ToolBar getFunctionsToolbar() {
        return functionsToolbar;
    }

    static ArrayList<Node> stuff = new ArrayList<>();


    public static void init() {

        functionsToolbar = new ToolBar();

        VBox vBox = FXContainers.getvBox();

        functionsToolbar.getItems().clear();

        FXEditorButtons_Asset.asset(functionsToolbar);
        functionsToolbar.getItems().add(new Separator());
        FXEditorButtons_Edit.edit(functionsToolbar);
        functionsToolbar.getItems().add(new Separator());
        FXEditorButtons_Resources.resources(functionsToolbar);
        functionsToolbar.getItems().add(new Separator());
        for (Node node : functionsToolbar.getItems()) node.setDisable(true);
        vBox.getChildren().add(1, functionsToolbar);

        stuff.addAll(functionsToolbar.getItems());

    }


    public static void refreshToolbars() {

        functionsToolbar.getItems().clear();
        functionsToolbar.getItems().addAll(stuff);

        ObservableList<Node> vBoxItems = FXContainers.getvBox().getChildren();
        if (vBoxItems.size() > 3) vBoxItems.subList(2, vBoxItems.size() - 1).clear();

    }


    public static void updateAllButtons() {

        for (Node node : FXContainers.getvBox().getChildren()) {
            if (!(node instanceof ToolBar toolBar)) continue;

            for (Node child : toolBar.getItems()) {

                if (child instanceof EditorButton editorButton) editorButton.updateDisabled();

                if (!(child instanceof EditorMenuButton editorMenuButton)) continue;
                editorMenuButton.updateDisabled();

                for (MenuItem node1 : editorMenuButton.getItems()) {
                    if (!(node1 instanceof FXMenu.EditorMenu menu)) continue;
                    menu.updateDisabled();

                    for (MenuItem node2 : menu.getItems()) {
                        if (!(node2 instanceof FXMenu.EditorMenuItem editorMenuItem)) continue;
                        editorMenuItem.updateDisabled();

                    }
                }
            }
        }
    }

}
