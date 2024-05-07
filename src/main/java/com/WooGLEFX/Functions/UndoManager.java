package com.WooGLEFX.Functions;

import com.WooGLEFX.Engine.FX.FXHierarchy;
import com.WooGLEFX.Engine.FX.FXPropertiesView;
import com.WooGLEFX.Engine.FX.FXCreator;
import com.WooGLEFX.Engine.Main;
import com.WooGLEFX.Structures.UserActions.*;
import com.WooGLEFX.Structures.WorldLevel;
import com.WorldOfGoo.Resrc.ResrcImage;
import com.WorldOfGoo.Resrc.Sound;

import java.io.File;

public class UndoManager {

    public static void registerChange(UserAction... actions) {
        LevelManager.getLevel().undoActions.add(actions);
        if (LevelManager.getLevel().getEditingStatus() == WorldLevel.NO_UNSAVED_CHANGES) {
            LevelManager.getLevel().setEditingStatus(WorldLevel.UNSAVED_CHANGES, true);
        }
        FXCreator.undoItem.setDisable(false);
        FXCreator.buttonUndo.setDisable(false);
    }

    public static void undo() {

        WorldLevel level = LevelManager.getLevel();

        if (level.undoActions.size() != 0) {
            UserAction[] changes = level.undoActions.pop();
            level.redoActions.add(changes);
            FXCreator.redoItem.setDisable(false);
            FXCreator.buttonRedo.setDisable(false);
            for (UserAction change : changes) {
                if (change instanceof AttributeChangeAction) {
                    change.getObject().setAttribute(((AttributeChangeAction) change).getAttributeName(),
                            ((AttributeChangeAction) change).getOldValue());
                    FXPropertiesView.getPropertiesView().refresh();
                } else if (change instanceof ObjectCreationAction) {
                    ObjectManager.deleteItem(level, change.getObject(), false);
                } else if (change instanceof ObjectDestructionAction) {
                    ObjectManager.create(level, change.getObject(), ((ObjectDestructionAction) change).getPosition());
                } else if (change instanceof ImportResourceAction) {
                    LevelResourceManager.deleteResource(level, change.getObject().getAttribute("path"));
                    ObjectManager.deleteItem(level, change.getObject(), false);
                } else if (change instanceof HierarchyDragAction) {
                    HierarchyDragAction dragAction = (HierarchyDragAction) change;
                    int toIndex = dragAction.getToPosition();
                    int fromIndex = dragAction.getFromPosition();
                    // Shift all the items opposite of the direction the original item was dragged
                    if (toIndex > fromIndex) {
                        // Dragged the item downwards; shift all of the items down
                        while (toIndex > fromIndex) {
                            FXHierarchy.getHierarchy().getTreeItem(toIndex).setValue(FXHierarchy.getHierarchy().getTreeItem(toIndex - 1).getValue());
                            toIndex--;
                        }
                    } else {
                        // Dragged the item upwards; shift all of the items up
                        while (fromIndex > toIndex) {
                            FXHierarchy.getHierarchy().getTreeItem(toIndex).setValue(FXHierarchy.getHierarchy().getTreeItem(toIndex + 1).getValue());
                            toIndex++;
                        }
                    }
                    FXHierarchy.getHierarchy().getTreeItem(dragAction.getFromPosition()).setValue(dragAction.getObject());
                    FXHierarchy.getHierarchy().getSelectionModel().select(dragAction.getFromPosition());
                }
                FXHierarchy.getHierarchy().refresh();
            }
        }
        // TODO Undo stack should track if there are any unsaved changes, this isn't always true
        if (level.undoActions.size() == 0) {
            level.setEditingStatus(WorldLevel.NO_UNSAVED_CHANGES, true);
            FXCreator.undoItem.setDisable(true);
            FXCreator.buttonUndo.setDisable(true);
        }
    }

    public static void clearRedoActions() {
        LevelManager.getLevel().redoActions.clear();
        FXCreator.redoItem.setDisable(true);
        FXCreator.buttonRedo.setDisable(true);
    }

    public static void redo() {

        WorldLevel level = LevelManager.getLevel();

        if (level.redoActions.size() != 0) {
            UserAction[] changes = level.redoActions.pop();
            registerChange(changes);
            for (UserAction change : changes) {
                if (change instanceof AttributeChangeAction) {
                    change.getObject().setAttribute(((AttributeChangeAction) change).getAttributeName(),
                            ((AttributeChangeAction) change).getNewValue());
                } else if (change instanceof ObjectCreationAction) {
                    // TODO bug here with the index being out of bounds
                    ObjectManager.create(level, change.getObject(), ((ObjectCreationAction) change).getPosition());
                } else if (change instanceof ObjectDestructionAction) {
                    ObjectManager.deleteItem(level, change.getObject(), false);
                } else if (change instanceof ImportResourceAction) {
                    if (change.getObject() instanceof ResrcImage) {
                        LevelResourceManager.importImage(level, new File(((ImportResourceAction) change).getPath()));
                    } else if (change.getObject() instanceof Sound) {
                        // TODO make this work with loopsounds instead of just music
                        LevelResourceManager.importMusic(level, new File(((ImportResourceAction) change).getPath()), false);
                    }
                } else if (change instanceof HierarchyDragAction) {
                    HierarchyDragAction dragAction = (HierarchyDragAction) change;
                    int toIndex = dragAction.getToPosition();
                    int fromIndex = dragAction.getFromPosition();
                    if (toIndex > fromIndex) {
                        // Dragged the item downwards; shift all of the items up
                        while (fromIndex < toIndex) {
                            FXHierarchy.getHierarchy().getTreeItem(fromIndex).setValue(FXHierarchy.getHierarchy().getTreeItem(fromIndex + 1).getValue());
                            fromIndex++;
                        }
                    } else {
                        // Dragged the item upwards; shift all of the items down
                        while (fromIndex > toIndex) {
                            FXHierarchy.getHierarchy().getTreeItem(fromIndex).setValue(FXHierarchy.getHierarchy().getTreeItem(fromIndex - 1).getValue());
                            fromIndex--;
                        }
                    }
                    FXHierarchy.getHierarchy().getTreeItem(dragAction.getToPosition()).setValue(dragAction.getObject());
                    FXHierarchy.getHierarchy().getSelectionModel().select(dragAction.getToPosition());
                }
                FXHierarchy.getHierarchy().refresh();
            }
        }
        if (level.redoActions.size() == 0) {
            FXCreator.redoItem.setDisable(true);
            FXCreator.buttonRedo.setDisable(true);
        }
    }

}
