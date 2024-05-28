package com.woogleFX.functions;

import com.woogleFX.editorObjects.objectCreators.ObjectCreator;
import com.woogleFX.engine.fx.FXHierarchy;
import com.woogleFX.engine.fx.FXPropertiesView;
import com.woogleFX.engine.SelectionManager;
import com.woogleFX.functions.undoHandling.UndoManager;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.functions.undoHandling.userActions.ObjectDestructionAction;
import com.woogleFX.structures.WorldLevel;
import com.worldOfGoo.level.BallInstance;
import com.worldOfGoo.level.Level;
import com.worldOfGoo.level.Strand;
import com.worldOfGoo.level.Vertex;
import com.worldOfGoo.scene.Scene;

public class ObjectManager {

    public static void create(WorldLevel level, EditorObject object, int row) {
        object.update();

        if (!object.getParent().getChildren().contains(object)) {
            object.getParent().getChildren().add(row, object);
            object.getParent().getTreeItem().getChildren().add(row, object.getTreeItem());
        }

        if (!object.getChildren().isEmpty()) {
            int i = 0;
            for (EditorObject child : object.getChildren().toArray(new EditorObject[0])) {
                create(level, child, i);
                i++;
            }
        }

        EditorObject absoluteParent = LevelManager.getLevel().getSelected();
        if (absoluteParent == null) absoluteParent = ObjectCreator.getDefaultParent(object.getType());
        else while (absoluteParent.getParent() != null) absoluteParent = absoluteParent.getParent();

        if (absoluteParent instanceof Scene) level.getScene().add(object);
        if (absoluteParent instanceof Level) level.getLevel().add(object);

        if (object instanceof BallInstance ballInstance) {

            String id = ballInstance.getAttribute("id").stringValue();

            for (EditorObject editorObject : level.getLevel()) if (editorObject instanceof Strand strand) {

                String gb1 = strand.getAttribute("gb1").stringValue();
                if (gb1.equals(id)) {
                    strand.setGoo1(ballInstance);
                    strand.update();
                }

                String gb2 = strand.getAttribute("gb2").stringValue();
                if (gb2.equals(id)) {
                    strand.setGoo2(ballInstance);
                    strand.update();
                }

            }

        } else if (object instanceof Vertex vertex) vertex.getParent().update();

    }


    public static void deleteItem(WorldLevel level, EditorObject item, boolean parentDeleted) {

        for (EditorObject child : item.getChildren().toArray(new EditorObject[0])) {
            deleteItem(level, child, true);
        }

        level.getScene().remove(item);
        level.getLevel().remove(item);
        level.getResrc().remove(item);
        level.getAddin().remove(item);
        level.getText().remove(item);

        if (!parentDeleted) {
            item.getParent().getChildren().remove(item);
            item.getParent().getTreeItem().getChildren().remove(item.getTreeItem());
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

        } else if (item instanceof Vertex vertex) vertex.getParent().update();

    }


    public static void delete(WorldLevel level) {

        EditorObject selected = level.getSelected();
        if (selected == null) return;

        EditorObject parent = selected.getParent();

        int row = parent.getChildren().indexOf(selected);

        UndoManager.registerChange(new ObjectDestructionAction(selected, row));

        deleteItem(level, selected, false);

        if (row == 0) {
            selected = parent;
        } else {
            selected = parent.getChildren().get(row - 1);
        }

        SelectionManager.setSelected(selected);
        FXPropertiesView.changeTableView(selected);
        // hierarchy.getFocusModel().focus(row);
        FXHierarchy.getHierarchy().getSelectionModel().select(selected.getTreeItem());
        FXHierarchy.getHierarchy().refresh();

    }

}
