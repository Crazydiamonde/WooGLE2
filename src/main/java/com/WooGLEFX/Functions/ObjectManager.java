package com.WooGLEFX.Functions;

import com.WooGLEFX.Engine.FX.FXHierarchy;
import com.WooGLEFX.Engine.FX.FXPropertiesView;
import com.WooGLEFX.Engine.Main;
import com.WooGLEFX.Engine.SelectionManager;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.UserActions.ObjectDestructionAction;
import com.WooGLEFX.Structures.WorldLevel;
import com.WorldOfGoo.Level.BallInstance;
import com.WorldOfGoo.Level.Level;
import com.WorldOfGoo.Level.Strand;
import com.WorldOfGoo.Scene.Scene;
import javafx.scene.control.TreeItem;

public class ObjectManager {

    public static void create(WorldLevel level, EditorObject object, int row) {
        object.update();

        if (object.getTreeItem() == null) {
            object.setTreeItem(new TreeItem<>(object));
        }

        if (!object.getParent().getChildren().contains(object)) {
            object.getParent().getChildren().add(object);
        }

        if (!object.getParent().getTreeItem().getChildren().contains(object.getTreeItem())) {
            object.getParent().getTreeItem().getChildren().add(row, object.getTreeItem());
        }

        if (object.getChildren().size() > 0) {
            int i = 0;
            for (EditorObject child : object.getChildren().toArray(new EditorObject[0])) {
                create(level, child, i);
                i++;
            }
        }

        EditorObject absoluteParent = LevelManager.getLevel().getSelected();
        while (absoluteParent.getParent() != null) absoluteParent = absoluteParent.getParent();

        if (absoluteParent instanceof Scene) {
            level.getScene().add(object);
        }
        if (absoluteParent instanceof Level) {
            level.getLevel().add(object);
        }

        if (object instanceof BallInstance ballInstance) {
            for (EditorObject strandObject : level.getLevel()) {
                if (strandObject instanceof Strand strand) {
                    if (ballInstance.getAttribute("id").equals(strand.getAttribute("gb1"))) {
                        strand.setGoo1(ballInstance);
                    }
                    if (ballInstance.getAttribute("id").equals(strand.getAttribute("gb2"))) {
                        strand.setGoo2(ballInstance);
                    }
                }
            }
        }

        if (object instanceof BallInstance ballInstance) {

            String id = ballInstance.getAttribute("id").stringValue();

            for (EditorObject editorObject : level.getLevel()) if (editorObject instanceof Strand strand) {

                String gb1 = strand.getAttribute("gb1").stringValue();
                if (gb1.equals(id)) {
                    strand.update();
                }

                String gb2 = strand.getAttribute("gb2").stringValue();
                if (gb2.equals(id)) {
                    strand.update();
                }

            }

        }

    }


    public static void deleteItem(WorldLevel level, EditorObject item, boolean parentDeleted) {

        for (EditorObject child : item.getChildren().toArray(new EditorObject[0])) {
            deleteItem(level, child, true);
        }

        level.getScene().remove(item);
        level.getLevel().remove(item);
        level.getResources().remove(item);
        level.getAddin().remove(item);
        level.getText().remove(item);

        item.getParent().getTreeItem().getChildren().remove(item.getTreeItem());
        if (!parentDeleted) {
            item.getParent().getChildren().remove(item);
        }

        if (item instanceof BallInstance ballInstance) {

            String id = ballInstance.getAttribute("id").stringValue();

            for (EditorObject editorObject : level.getLevel()) if (editorObject instanceof Strand strand) {

                String gb1 = strand.getAttribute("gb1").stringValue();
                if (gb1.equals(id)) {
                    strand.setGoo1(null);
                    strand.update();
                }

                String gb2 = strand.getAttribute("gb2").stringValue();
                if (gb2.equals(id)) {
                    strand.setGoo2(null);
                    strand.update();
                }

            }

        }

    }


    public static void delete(WorldLevel level) {

        EditorObject selected = level.getSelected();
        if (selected == null) return;

        EditorObject parent = selected.getParent();

        int row = parent.getChildren().indexOf(selected);

        UndoManager.registerChange(new ObjectDestructionAction(selected, row));
        level.redoActions.clear();

        deleteItem(level, selected, false);

        if (row == 0) {
            selected = parent;
        } else {
            selected = parent.getChildren().get(row - 1);
        }

        SelectionManager.setSelected(selected);
        FXPropertiesView.changeTableView(selected);
        // hierarchy.getFocusModel().focus(row);
        FXHierarchy.getHierarchy().getSelectionModel().select(FXHierarchy.getHierarchy().getRow(selected.getTreeItem()));
        FXHierarchy.getHierarchy().refresh();

    }

}
