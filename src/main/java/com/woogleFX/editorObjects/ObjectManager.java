package com.woogleFX.editorObjects;

import com.woogleFX.assets.Asset;
import com.woogleFX.editorObjects.attributes.InputField;
import com.woogleFX.engine.undoHandling.UndoManager;
import com.woogleFX.engine.undoHandling.userActions.ObjectCreationAction;
import com.woogleFX.engine.undoHandling.userActions.ObjectDestructionAction;
import com.woogleFX.engine.undoHandling.userActions.UserAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ObjectManager {

    private static boolean shouldAddTreeItem(EditorObject object, EditorObject parent) {
        if (!parent.attributeExists(object.getTypeID())) return true;
        InputField type = parent.getAttribute(object.getTypeID()).getType();
        return type != InputField._2_CHILD_HIDDEN && type != InputField._2_LIST_CHILD_HIDDEN;
    }


    public static List<ObjectCreationAction> createItem(EditorObject object, EditorObject parent, int position, int treePosition) {

        ArrayList<ObjectCreationAction> childActions = new ArrayList<>(object.onCreate());

        if (!parent.getChildren().contains(object)) {
            parent.getChildren().add(position, object);
            if (shouldAddTreeItem(object, parent))
                parent.getTreeItem().getChildren().add(treePosition, object.getTreeItem());
        }

        for (ObjectCreationAction action : childActions.toArray(ObjectCreationAction[]::new)) {
            childActions.addAll(createItem(action.getObject(), action.getParent(), action.getPosition(), action.getTreePosition()));
        }

        childActions.add(new ObjectCreationAction(object, parent, position, treePosition));

        object.update();

        return childActions;

    }

    /** Deletes an object from an asset.
     * @return A list of all the objects that were destroyed as a result of this. */
    public static List<ObjectDestructionAction> deleteItem(Asset asset, EditorObject object) {

        ArrayList<ObjectDestructionAction> childActions = new ArrayList<>();

        List<ObjectDestructionAction> immediateChildrenActions = object.onDelete();
        for (ObjectDestructionAction action : immediateChildrenActions.toArray(ObjectDestructionAction[]::new)) {
            childActions.addAll(deleteItem(asset, action.getObject()));
        }

        childActions.add(new ObjectDestructionAction(object));
        if (object.getParent() != null) {
            object.getParent().getChildren().remove(object);
            object.getParent().getTreeItem().getChildren().remove(object.getTreeItem());
        }

        return childActions;

    }


    public static void delete(Asset asset) {

        ArrayList<EditorObject> newSelectionBuilder = new ArrayList<>();
        List<ObjectDestructionAction> allActions = new ArrayList<>();

        for (EditorObject selected : asset.getSelectedObjects()) {

            EditorObject parent = selected.getParent();
            if (parent == null) continue;

            // Select the object immediately above this one
            int row = parent.getChildren().indexOf(selected);
            EditorObject parentObject = (row == 0) ? parent : parent.getChildren().get(row - 1);
            if (Arrays.stream(asset.getSelectedComponents()).noneMatch(parentObject::containsObjectComponent)) {
                newSelectionBuilder.add(parentObject);
            }

            allActions.addAll(deleteItem(asset, selected));

        }

        if (allActions.isEmpty()) return;
        UndoManager.registerChange(allActions.toArray(UserAction[]::new));

        EditorObject[] newSelected = newSelectionBuilder.toArray(EditorObject[]::new);
        asset.setSelectedDiscreetly(newSelected);
        asset.setSelectedObjects(newSelected);

    }


    public static void create(EditorObject object, EditorObject parent, int position, int treePosition) {

        ArrayList<EditorObject> newSelectionBuilder = new ArrayList<>();
        List<ObjectCreationAction> allActions = createItem(object, parent, position, treePosition);

        if (allActions.isEmpty()) return;
        UndoManager.registerChange(allActions.toArray(ObjectCreationAction[]::new));

        for (ObjectCreationAction objectCreationAction : allActions) {
            newSelectionBuilder.add(objectCreationAction.getObject());
        }

        EditorObject[] newSelected = newSelectionBuilder.toArray(EditorObject[]::new);
        parent.getAsset().setSelectedDiscreetly(newSelected);
        parent.getAsset().setSelectedObjects(newSelected);

    }

}
