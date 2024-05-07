package com.WooGLEFX.Functions;

import com.WooGLEFX.Engine.FX.FXHierarchy;
import com.WooGLEFX.Engine.Main;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.UserActions.ObjectDestructionAction;
import com.WooGLEFX.Structures.WorldLevel;
import com.WorldOfGoo.Level.BallInstance;
import com.WorldOfGoo.Level.Strand;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.List;

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
            ArrayList<EditorObject> children = object.getChildren();
            EditorObject[] dontBreak = new EditorObject[children.size()];
            int i = 0;
            for (EditorObject child : children) {
                dontBreak[i] = child;
                i++;
            }
            i = 0;
            for (EditorObject child : dontBreak) {
                create(level, child, i);
                i++;
            }
        }

        TreeItem<EditorObject> thing = object.getTreeItem();
        while (thing.getParent() != null) {
            thing = thing.getParent();
        }
        if (thing.getValue().getRealName().equals("scene")) {
            level.getScene().add(object);
        }
        if (thing.getValue().getRealName().equals("level")) {
            level.getLevel().add(object);
        }

        if (object instanceof BallInstance) {
            for (EditorObject strand : level.getLevel()) {
                if (strand instanceof Strand) {
                    if (object.getAttribute("id").equals(strand.getAttribute("gb1"))) {
                        ((Strand) strand).setGoo1((BallInstance) object);
                    }
                    if (object.getAttribute("id").equals(strand.getAttribute("gb2"))) {
                        ((Strand) strand).setGoo2((BallInstance) object);
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
        if (item.getChildren().size() > 0) {
            List<EditorObject> children = item.getChildren();
            EditorObject[] dontBreak = new EditorObject[children.size()];
            int i = 0;
            for (EditorObject child : children) {
                dontBreak[i] = child;
                i++;
            }
            for (EditorObject child : dontBreak) {
                deleteItem(level, child, true);
            }
        }
        if (level.getScene().contains(item)) {
            level.getScene().remove(item);
        } else if (level.getLevel().contains(item)) {
            level.getLevel().remove(item);
        } else if (level.getResources().contains(item)) {
            level.getResources().remove(item);
        } else if (level.getAddin().contains(item)) {
            level.getAddin().remove(item);
        } else {
            level.getText().remove(item);
        }
        item.getParent().getTreeItem().getChildren().remove(item.getTreeItem());
        if (!parentDeleted) {
            item.getParent().getChildren().remove(item);
        }
    }


    public static void delete(WorldLevel level) {
        if (level.getSelected() != null) {
            EditorObject parent = level.getSelected().getParent();
            int row = parent.getChildren().indexOf(level.getSelected());
            UndoManager.registerChange(new ObjectDestructionAction(level.getSelected(), row));
            level.redoActions.clear();
            deleteItem(level, level.getSelected(), false);
            if (row == 0) {
                Main.setSelected(parent);
            } else {
                Main.setSelected(parent.getChildren().get(row - 1));
            }
            Main.changeTableView(level.getSelected());
            // hierarchy.getFocusModel().focus(row);
            FXHierarchy.getHierarchy().getSelectionModel().select(FXHierarchy.getHierarchy().getRow(level.getSelected().getTreeItem()));
            FXHierarchy.getHierarchy().refresh();
        }
    }

}
