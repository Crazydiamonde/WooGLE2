package com.woogleFX.functions.undoHandling;

import com.woogleFX.engine.fx.*;
import com.woogleFX.functions.LevelManager;
import com.woogleFX.functions.LevelResourceManager;
import com.woogleFX.functions.ObjectManager;
import com.woogleFX.structures.simpleStructures.LevelTab;
import com.woogleFX.functions.undoHandling.userActions.*;
import com.woogleFX.structures.WorldLevel;
import com.worldOfGoo.resrc.ResrcImage;
import com.worldOfGoo.resrc.Sound;

import java.io.File;

public class UndoManager {

    public static void registerChange(UserAction... actions) {
        LevelManager.getLevel().undoActions.add(actions);
        if (LevelManager.getLevel().getEditingStatus() == LevelTab.NO_UNSAVED_CHANGES) {
            LevelManager.getLevel().setEditingStatus(LevelTab.UNSAVED_CHANGES, true);
        }
        FXMenu.undoItem.setDisable(false);
        FXEditorButtons.buttonUndo.setDisable(false);
    }

    public static void undo() {

        WorldLevel level = LevelManager.getLevel();

        if (!level.undoActions.isEmpty()) {
            UserAction[] changes = level.undoActions.pop();

            UserAction[] inverted = new UserAction[changes.length];
            for (int i = 0; i < changes.length; i++) inverted[i] = changes[i].getInverse();
            level.redoActions.add(inverted);

            FXMenu.redoItem.setDisable(false);
            FXEditorButtons.buttonRedo.setDisable(false);

            executeUserActions(level, changes);

        }

        // TODO Undo stack should track if there are any unsaved changes, this isn't always true
        if (level.undoActions.isEmpty()) {
            level.setEditingStatus(LevelTab.NO_UNSAVED_CHANGES, true);
            FXMenu.undoItem.setDisable(true);
            FXEditorButtons.buttonUndo.setDisable(true);
        }

    }

    public static void clearRedoActions() {
        LevelManager.getLevel().redoActions.clear();
        FXMenu.redoItem.setDisable(true);
        FXEditorButtons.buttonRedo.setDisable(true);
    }

    public static void redo() {

        WorldLevel level = LevelManager.getLevel();

        if (!level.redoActions.isEmpty()) {
            UserAction[] changes = level.redoActions.pop();
            registerChange(changes);

            UserAction[] inverted = new UserAction[changes.length];
            for (int i = 0; i < changes.length; i++) inverted[i] = changes[i].getInverse();
            level.undoActions.add(inverted);

            FXMenu.undoItem.setDisable(false);
            FXEditorButtons.buttonUndo.setDisable(false);

            executeUserActions(level, changes);

        }

        if (level.redoActions.isEmpty()) {
            FXMenu.redoItem.setDisable(true);
            FXEditorButtons.buttonRedo.setDisable(true);
        }

    }


    public static void executeUserActions(WorldLevel level, UserAction[] userActions) {
        for (UserAction userAction : userActions) executeUserAction(level, userAction);
    }


    public static void executeUserAction(WorldLevel level, UserAction userAction) {

        if (userAction instanceof AttributeChangeAction) {
            userAction.getObject().setAttribute(((AttributeChangeAction) userAction).getAttributeName(),
                    ((AttributeChangeAction) userAction).getOldValue());
            FXPropertiesView.getPropertiesView().refresh();
        } else if (userAction instanceof ObjectCreationAction) {
            ObjectManager.deleteItem(level, userAction.getObject(), false);
        } else if (userAction instanceof ObjectDestructionAction) {
            ObjectManager.create(level, userAction.getObject(), ((ObjectDestructionAction) userAction).getPosition());
        } else if (userAction instanceof ImportResourceAction) {
            LevelResourceManager.deleteResource(level, userAction.getObject().getAttribute("path").stringValue());
            ObjectManager.deleteItem(level, userAction.getObject(), false);
        } else if (userAction instanceof HierarchyDragAction dragAction) {
            int toIndex = dragAction.getToPosition();
            int fromIndex = dragAction.getFromPosition();
            // Shift all the items opposite of the direction the original item was dragged
            if (toIndex > fromIndex) {
                // Dragged the item downwards; shift all the items down
                while (toIndex > fromIndex) {
                    FXHierarchy.getHierarchy().getTreeItem(toIndex).setValue(FXHierarchy.getHierarchy().getTreeItem(toIndex - 1).getValue());
                    toIndex--;
                }
            } else {
                // Dragged the item upwards; shift all the items up
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
