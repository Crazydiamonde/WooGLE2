package com.woogleFX.gameData.level;

import com.woogleFX.engine.fx.hierarchy.FXHierarchy;
import com.woogleFX.file.FileManager;
import com.woogleFX.editorObjects.EditorObject;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

public class LevelTab extends Tab {

    private final _Level level;
    public _Level getLevel() {
        return level;
    }


    public LevelTab(String text, _Level level) {
        super(text);
        this.level = level;
    }

    public static final int NO_UNSAVED_CHANGES = 0;
    public static final int UNSAVED_CHANGES = 1;
    public static final int UNSAVED_CHANGES_UNMODIFIABLE = 2;

    private static final Image noChangesImageOld = FileManager.getIcon("ButtonIcons\\Level\\no_unsaved_changes_old.png");
    private static final Image changesImageOld = FileManager.getIcon("ButtonIcons\\Level\\unsaved_changes_old.png");
    private static final Image changesUnmodifiableImageOld = FileManager.getIcon("ButtonIcons\\Level\\unsaved_changes_unmodifiable_old.png");

    private static final Image noChangesImageNew = FileManager.getIcon("ButtonIcons\\Level\\no_unsaved_changes_new.png");
    private static final Image changesImageNew = FileManager.getIcon("ButtonIcons\\Level\\unsaved_changes_new.png");
    private static final Image changesUnmodifiableImageNew = FileManager.getIcon("ButtonIcons\\Level\\unsaved_changes_unmodifiable_new.png");


    public void update(int editingStatus, boolean shouldSelect) {

        Image currentStatusImage = null;

        if (editingStatus == NO_UNSAVED_CHANGES) {
            currentStatusImage = level.getVersion() == GameVersion.OLD ? noChangesImageOld : noChangesImageNew;
        } else if (editingStatus == UNSAVED_CHANGES) {
            currentStatusImage = level.getVersion() == GameVersion.OLD ? changesImageOld : changesImageNew;
        } else if (editingStatus == UNSAVED_CHANGES_UNMODIFIABLE) {
            currentStatusImage = level.getVersion() == GameVersion.OLD ? changesUnmodifiableImageOld : changesUnmodifiableImageNew;
        }

        AnchorPane pane = new AnchorPane();

        pane.getChildren().add(new ImageView(currentStatusImage));

        TreeItem<EditorObject> root = FXHierarchy.getHierarchy().getRoot();

        StackPane graphicContainer = new StackPane();
        graphicContainer.prefWidthProperty().bind(getTabPane().tabMaxWidthProperty());
        StackPane.setAlignment(pane, Pos.CENTER_LEFT);
        graphicContainer.getChildren().addAll(pane, new Label(level.getLevelName()));
        setGraphic(graphicContainer);
        if (shouldSelect) {
            getTabPane().getSelectionModel().select(this);
            FXHierarchy.getHierarchy().setRoot(root);
        }
    }

}
