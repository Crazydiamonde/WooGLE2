package com.woogleFX.editorObjects;

import com.woogleFX.editorObjects.objectCreators.ObjectCreator;
import com.woogleFX.engine.LevelManager;
import com.woogleFX.engine.fx.hierarchy.FXHierarchy;
import com.woogleFX.engine.fx.FXPropertiesView;
import com.woogleFX.engine.undoHandling.UndoManager;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.engine.undoHandling.userActions.ObjectDestructionAction;
import com.woogleFX.engine.undoHandling.userActions.UserAction;
import com.woogleFX.gameData.level._Level;
import com.worldOfGoo.level.BallInstance;
import com.worldOfGoo.level.Level;
import com.worldOfGoo.level.Strand;
import com.worldOfGoo.level.Vertex;
import com.worldOfGoo.scene.Scene;

import java.util.ArrayList;
import java.util.Arrays;

public class ObjectManager {

    public static void create(_Level level, EditorObject object, int row) {
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

        EditorObject absoluteParent = LevelManager.getLevel().getSelected().length == 0 ? null : LevelManager.getLevel().getSelected()[0];
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


    public static void deleteItem(_Level level, EditorObject item, boolean parentDeleted) {

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


    public static void delete(_Level level) {

        ArrayList<UserAction> objectDestructionActions = new ArrayList<>();

        ArrayList<EditorObject> newSelectionBuilder = new ArrayList<>();
        for (EditorObject selected : level.getSelected()) {

            EditorObject parent = selected.getParent();

            int row = parent.getChildren().indexOf(selected);

            objectDestructionActions.add(new ObjectDestructionAction(selected, Math.max(row, 0)));

            deleteItem(level, selected, false);

            EditorObject parentObject = (row <= 0) ? parent : parent.getChildren().get(row - 1);
            if (Arrays.stream(level.getSelected()).noneMatch(e -> e == parentObject)) newSelectionBuilder.add(parentObject);
        }

        UndoManager.registerChange(objectDestructionActions.toArray(new UserAction[0]));

        EditorObject[] newSelected = newSelectionBuilder.toArray(new EditorObject[0]);
        level.setSelected(newSelected);
        FXPropertiesView.changeTableView(newSelected);
        if (newSelected.length != 0) {
            int[] indices = new int[newSelected.length - 1];
            for (int i = 0; i < newSelected.length - 1; i++)
                indices[i] = FXHierarchy.getHierarchy().getRow(newSelected[i + 1].getTreeItem());
            FXHierarchy.getHierarchy().getSelectionModel().selectIndices(FXHierarchy.getHierarchy().getRow(newSelected[0].getTreeItem()), indices);
        } else FXHierarchy.getHierarchy().getSelectionModel().clearSelection();
        FXHierarchy.getHierarchy().refresh();

    }

}
