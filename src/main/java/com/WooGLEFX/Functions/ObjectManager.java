package com.WooGLEFX.Functions;

import com.WooGLEFX.Engine.FX.FXHierarchy;
import com.WooGLEFX.Engine.Main;
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

        if (object.getAbsoluteParent() instanceof Scene) {
            level.getScene().add(object);
        }
        if (object.getAbsoluteParent() instanceof Level) {
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
    }


    public static void deleteItem(WorldLevel level, EditorObject item, boolean parentDeleted) {

        if (item instanceof Strand) {
            for (EditorObject ball : level.getLevel()) {
                if (ball instanceof BallInstance) {
                    ((BallInstance) ball).getStrands().remove(item);
                }
            }
        }

        if (item instanceof BallInstance) {
            for (Strand strand : ((BallInstance) item).getStrands()) {
                if (strand.getGoo1() == item) {
                    strand.setGoo1(null);
                }
                if (strand.getGoo2() == item) {
                    strand.setGoo2(null);
                }
            }
        }

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

        Main.setSelected(selected);
        Main.changeTableView(selected);
        // hierarchy.getFocusModel().focus(row);
        FXHierarchy.getHierarchy().getSelectionModel().select(FXHierarchy.getHierarchy().getRow(selected.getTreeItem()));
        FXHierarchy.getHierarchy().refresh();

    }

}
