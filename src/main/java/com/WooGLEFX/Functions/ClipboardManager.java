package com.WooGLEFX.Functions;

import com.WooGLEFX.Engine.ClipboardHandler;
import com.WooGLEFX.Engine.FX.FXHierarchy;
import com.WooGLEFX.Engine.FX.FXPropertiesView;
import com.WooGLEFX.Engine.Main;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.UserActions.ObjectCreationAction;
import com.WooGLEFX.Structures.WorldLevel;
import com.WorldOfGoo.Level.BallInstance;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class ClipboardManager {


    public static void cut() {
        if (LevelManager.getLevel().getSelected() != null) {
            copy();
            ObjectManager.delete(LevelManager.getLevel());
        }
    }

    public static void copy() {
        if (LevelManager.getLevel().getSelected() != null && FXPropertiesView.getPropertiesView().getEditingCell() == null) {
            String clipboard = ClipboardHandler.exportToClipBoardString(LevelManager.getLevel().getSelected());
            ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString(clipboard);
            Clipboard.getSystemClipboard().setContent(clipboardContent);
        }
    }

    public static void paste() {

        WorldLevel level = LevelManager.getLevel();

        if (FXPropertiesView.getPropertiesView().getEditingCell() == null) {
            String clipboard = Clipboard.getSystemClipboard().getString();
            if (clipboard != null) {
                EditorObject object = ClipboardHandler.importFromClipboardString(clipboard);
                if (object != null) {
                    object.setLevel(level);

                    boolean okayToBeChild = level.getSelected() != null && level.getSelected().getParent() != null;

                    if (okayToBeChild) {
                        okayToBeChild = false;
                        for (String possibleChild : level.getSelected().getParent().getPossibleChildren()) {
                            if (possibleChild.equals(object.getRealName())) {
                                okayToBeChild = true;
                                break;
                            }
                        }
                    }

                    if (okayToBeChild) {
                        object.setParent(level.getSelected().getParent());
                    } else {
                        if (object.getClass().getPackage().getName().equals("com.WorldOfGoo.Scene")) {
                            object.setParent(level.getSceneObject());
                        } else if (object.getClass().getPackage().getName().equals("com.WorldOfGoo.Level")) {
                            object.setParent(level.getLevelObject());
                        } else if (object.getClass().getPackage().getName().equals("com.WorldOfGoo.Resrc")) {
                            object.setParent(level.getResourcesObject());
                        }
                    }
                    if (object instanceof BallInstance) {
                        ObjectAdder.fixGooball(object);
                    }
                    // object.getParent().getChildren().add(0, object);
                    ObjectManager.create(level, object, 0);
                    Main.setSelected(object);
                    UndoManager.registerChange(new ObjectCreationAction(object, FXHierarchy.getHierarchy().getRow(object.getTreeItem())
                            - FXHierarchy.getHierarchy().getRow(object.getParent().getTreeItem()) - 1));
                    level.redoActions.clear();
                    FXHierarchy.getHierarchy().refresh();
                }
            }
        }
    }

}
